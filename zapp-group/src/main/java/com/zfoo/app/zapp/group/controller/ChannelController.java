/*
 * Copyright (C) 2020 The zfoo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.zfoo.app.zapp.group.controller;

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.entity.group.ChannelEntity;
import com.zfoo.app.zapp.common.entity.group.GroupEntity;
import com.zfoo.app.zapp.common.entity.group.model.*;
import com.zfoo.app.zapp.common.protocol.cache.WordFilterAnswer;
import com.zfoo.app.zapp.common.protocol.cache.WordFilterAsk;
import com.zfoo.app.zapp.common.protocol.group.GroupUpdateNotice;
import com.zfoo.app.zapp.common.protocol.group.channel.*;
import com.zfoo.app.zapp.common.protocol.push.group.GroupUpdatePush;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.net.NetContext;
import com.zfoo.net.packet.common.Error;
import com.zfoo.net.packet.model.GatewayPacketAttachment;
import com.zfoo.net.router.receiver.PacketReceiver;
import com.zfoo.net.session.model.Session;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-27 15:13
 */
@Component
public class ChannelController {

    @EntityCachesInjection
    private IEntityCaches<Long, GroupEntity> groupEntityCaches;

    @PacketReceiver
    public void atCreateChannelBoxRequest(Session session, CreateChannelBoxRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var channelBoxName = cm.getChannelBoxName();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (StringUtils.isBlank(channelBoxName)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_EMPTY.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.CREATE_CHANNEL_BOX)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        // 查看新的channelBox是否已经存在
        if (groupEntity.getChannelBoxes().stream().anyMatch(it -> it.getName().equals(channelBoxName))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_BOX_EXIST.getCode()), gatewayAttachment);
            return;
        }

        NetContext.getConsumer()
                .asyncAsk(WordFilterAsk.valueOf(channelBoxName), WordFilterAnswer.class, userId)
                .whenComplete(new Consumer<>() {
                    @Override
                    public void accept(WordFilterAnswer answer) {
                        // 敏感字符检测
                        if (CollectionUtils.isNotEmpty(answer.getFilterWords())) {
                            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_WORD_FILTER_ERROR.getCode()), gatewayAttachment);
                            return;
                        }

                        groupEntity.getChannelBoxes().add(ChannelBoxPO.valueOf(channelBoxName, new ArrayList<>()));
                        groupEntityCaches.update(groupEntity);

                        var groupVO = groupEntity.toGroupVO();
                        NetContext.getRouter().send(session, CreateChannelBoxResponse.valueOf(groupVO), gatewayAttachment);
                        NetContext.getConsumer().send(GroupUpdatePush.valueOf(groupEntity.getPeople(), GroupUpdateNotice.valueOf(groupVO)), groupId);
                    }
                });
    }


    @PacketReceiver
    public void atCreateChannelRequest(Session session, CreateChannelRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var channelBoxName = cm.getChannelBoxName();
        var channelName = cm.getChannelName();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (StringUtils.isBlank(channelBoxName) || StringUtils.isBlank(channelName)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_EMPTY.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.CREATE_CHANNEL)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var channelSize = groupEntity.getChannelBoxes().stream()
                .mapToInt(it -> it.getChannels().size())
                .sum();
        if (channelSize > AppConstant.GROUP_CHANNEL_MAX_SIZE) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_MAX_CHANNEL_LIMIT.getCode()), gatewayAttachment);
            return;
        }

        // 查看新的channelBox是否已经存在
        var channelBoxOptional = groupEntity.getChannelBoxes().stream().filter(it -> it.getName().equals(channelBoxName)).findFirst();
        if (channelBoxOptional.isEmpty()) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_BOX_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var channelBoxPO = channelBoxOptional.get();
        if (channelBoxPO.getChannels().stream().anyMatch(it -> it.getName().equals(channelName))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_EXIST.getCode()), gatewayAttachment);
            return;
        }

        NetContext.getConsumer()
                .asyncAsk(WordFilterAsk.valueOf(channelName), WordFilterAnswer.class, userId)
                .whenComplete(new Consumer<>() {
                    @Override
                    public void accept(WordFilterAnswer answer) {
                        // 敏感字符检测
                        if (CollectionUtils.isNotEmpty(answer.getFilterWords())) {
                            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_WORD_FILTER_ERROR.getCode()), gatewayAttachment);
                            return;
                        }
                        var channelId = MongoIdUtils.getIncrementIdFromMongoDefault(ChannelEntity.class);
                        channelBoxPO.getChannels().add(ChannelPO.valueOf(channelId, channelName
                                , new ArrayList<>(List.of(ChannelAuthPO.valueOf(AppConstant.GROUP_AUTH_DEFAULT_ID, ChannelAuthEnum.BASE_AUTH.getType())))));
                        groupEntityCaches.update(groupEntity);

                        var groupVO = groupEntity.toGroupVO();
                        NetContext.getRouter().send(session, CreateChannelResponse.valueOf(groupVO), gatewayAttachment);
                        NetContext.getConsumer().send(GroupUpdatePush.valueOf(groupEntity.getPeople(), GroupUpdateNotice.valueOf(groupVO)), groupId);
                    }
                });
    }

    @PacketReceiver
    public void atSaveChannelRequest(Session session, SaveChannelRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var channelId = cm.getChannelId();
        var channelName = cm.getChannelName();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (StringUtils.isBlank(channelName)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_EMPTY.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.CHANGE_CHANNEL_NAME)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var channel = groupEntity.findChannel(channelId);
        if (channel == null) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (channel.getName().equals(channelName)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }

        NetContext.getConsumer()
                .asyncAsk(WordFilterAsk.valueOf(channelName), WordFilterAnswer.class, userId)
                .whenComplete(new Consumer<>() {
                    @Override
                    public void accept(WordFilterAnswer answer) {
                        // 敏感字符检测
                        if (CollectionUtils.isNotEmpty(answer.getFilterWords())) {
                            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_WORD_FILTER_ERROR.getCode()), gatewayAttachment);
                            return;
                        }
                        channel.setName(channelName);
                        groupEntityCaches.update(groupEntity);

                        var groupVO = groupEntity.toGroupVO();
                        NetContext.getRouter().send(session, SaveChannelResponse.valueOf(groupVO), gatewayAttachment);
                        NetContext.getConsumer().send(GroupUpdatePush.valueOf(groupEntity.getPeople(), GroupUpdateNotice.valueOf(groupVO)), groupId);
                    }
                });
    }


    @PacketReceiver
    public void atSaveChannelBoxRequest(Session session, SaveChannelBoxRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var oldChannelBoxName = cm.getOldChannelBoxName();
        var newChannelBoxName = cm.getNewChannelBoxName();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (StringUtils.isBlank(newChannelBoxName) || StringUtils.isBlank(oldChannelBoxName)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_EMPTY.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.CHANGE_CHANNEL_BOX_NAME)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var channelBox = groupEntity.findChannelBox(oldChannelBoxName);
        if (channelBox == null) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (channelBox.getName().equals(newChannelBoxName)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }

        NetContext.getConsumer()
                .asyncAsk(WordFilterAsk.valueOf(newChannelBoxName), WordFilterAnswer.class, userId)
                .whenComplete(new Consumer<>() {
                    @Override
                    public void accept(WordFilterAnswer answer) {
                        // 敏感字符检测
                        if (CollectionUtils.isNotEmpty(answer.getFilterWords())) {
                            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_WORD_FILTER_ERROR.getCode()), gatewayAttachment);
                            return;
                        }
                        channelBox.setName(newChannelBoxName);
                        groupEntityCaches.update(groupEntity);

                        var groupVO = groupEntity.toGroupVO();
                        NetContext.getRouter().send(session, SaveChannelBoxResponse.valueOf(groupVO), gatewayAttachment);
                        NetContext.getConsumer().send(GroupUpdatePush.valueOf(groupEntity.getPeople(), GroupUpdateNotice.valueOf(groupVO)), groupId);
                    }
                });
    }

    @PacketReceiver
    public void atDeleteChannelBoxRequest(Session session, DeleteChannelBoxRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var channelBoxName = cm.getChannelBoxName();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (StringUtils.isBlank(channelBoxName)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_EMPTY.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.DELETE_CHANNEL_BOX)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        // 查看新的channelBox是否已经存在
        if (groupEntity.getChannelBoxes().stream().noneMatch(it -> it.getName().equals(channelBoxName))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_BOX_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        groupEntity.getChannelBoxes().removeIf(it -> it.getName().equals(channelBoxName));
        groupEntityCaches.update(groupEntity);

        var groupVO = groupEntity.toGroupVO();
        NetContext.getRouter().send(session, DeleteChannelBoxResponse.valueOf(groupVO), gatewayAttachment);
        NetContext.getConsumer().send(GroupUpdatePush.valueOf(groupEntity.getPeople(), GroupUpdateNotice.valueOf(groupVO)), groupId);
    }


    @PacketReceiver
    public void atDeleteChannelRequest(Session session, DeleteChannelRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var channelId = cm.getChannelId();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (!CommonUtils.isChannelIdInRange(List.of(channelId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }


        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.DELETE_CHANNEL)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var removed = false;
        for (var channelBox : groupEntity.getChannelBoxes()) {
            removed = channelBox.getChannels().removeIf(it -> it.getId() == channelId) || removed;
        }

        if (!removed) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        groupEntityCaches.update(groupEntity);

        var groupVO = groupEntity.toGroupVO();
        NetContext.getRouter().send(session, DeleteChannelResponse.valueOf(groupVO), gatewayAttachment);
        NetContext.getConsumer().send(GroupUpdatePush.valueOf(groupEntity.getPeople(), GroupUpdateNotice.valueOf(groupVO)), groupId);
    }

}
