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
import com.zfoo.app.zapp.common.entity.group.model.OperationEnum;
import com.zfoo.app.zapp.common.model.OssPolicyEnum;
import com.zfoo.app.zapp.common.protocol.cache.GetUserLatestCacheAnswer;
import com.zfoo.app.zapp.common.protocol.cache.GetUserLatestCacheAsk;
import com.zfoo.app.zapp.common.protocol.cache.WordFilterAnswer;
import com.zfoo.app.zapp.common.protocol.cache.WordFilterAsk;
import com.zfoo.app.zapp.common.protocol.group.GroupUpdateNotice;
import com.zfoo.app.zapp.common.protocol.group.setting.*;
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
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-23 15:31
 */
@Component
public class SettingController {

    @EntityCachesInjection
    private IEntityCaches<Long, GroupEntity> groupEntityCaches;

    @EntityCachesInjection
    private IEntityCaches<Long, ChannelEntity> channelEntityCaches;

    @PacketReceiver
    public void atSaveGroupAvatarRequest(Session session, SaveGroupAvatarRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var avatar = cm.getAvatar();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (StringUtils.isBlank(avatar)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_EMPTY.getCode()), gatewayAttachment);
            return;
        }

        if (!avatar.startsWith(OssPolicyEnum.GROUP_AVATAR.getUrl())) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_URL_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.CHANGE_GROUP_AVATAR)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        groupEntity.setAvatar(avatar);
        groupEntityCaches.update(groupEntity);

        NetContext.getRouter().send(session, SaveGroupAvatarResponse.valueOf(groupId, avatar), gatewayAttachment);
        NetContext.getConsumer().send(GroupUpdatePush.valueOf(groupEntity.getPeople(), GroupUpdateNotice.valueOf(groupEntity.toGroupVO())), groupId);
    }

    @PacketReceiver
    public void atSaveGroupBackgroundRequest(Session session, SaveGroupBackgroundRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var background = cm.getBackground();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (StringUtils.isBlank(background)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_EMPTY.getCode()), gatewayAttachment);
            return;
        }

        if (!background.startsWith(OssPolicyEnum.GROUP_BACKGROUND.getUrl())) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_URL_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.CHANGE_GROUP_BACKGROUND)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        groupEntity.setBackground(background);
        groupEntityCaches.update(groupEntity);

        NetContext.getRouter().send(session, SaveGroupBackgroundResponse.valueOf(groupId, background), gatewayAttachment);
        NetContext.getConsumer().send(GroupUpdatePush.valueOf(groupEntity.getPeople(), GroupUpdateNotice.valueOf(groupEntity.toGroupVO())), groupId);
    }

    @PacketReceiver
    public void atSaveGroupSettingRequest(Session session, SaveGroupSettingRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var groupName = cm.getName();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (StringUtils.isBlank(groupName)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_EMPTY.getCode()), gatewayAttachment);
            return;
        }

        if (groupId == -userId) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NAME_SELF_CHANGE_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.CHANGE_GROUP_NAME)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        NetContext.getConsumer()
                .asyncAsk(WordFilterAsk.valueOf(groupName), WordFilterAnswer.class, userId)
                .whenComplete(new Consumer<>() {
                    @Override
                    public void accept(WordFilterAnswer answer) {
                        // 敏感字符检测
                        if (CollectionUtils.isNotEmpty(answer.getFilterWords())) {
                            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_WORD_FILTER_ERROR.getCode()), gatewayAttachment);
                            return;
                        }

                        groupEntity.setName(groupName);
                        groupEntityCaches.update(groupEntity);

                        NetContext.getRouter().send(session, SaveGroupSettingResponse.valueOf(groupId, groupName), gatewayAttachment);
                        NetContext.getConsumer().send(GroupUpdatePush.valueOf(groupEntity.getPeople(), GroupUpdateNotice.valueOf(groupEntity.toGroupVO())), groupId);
                    }
                });
    }

    @PacketReceiver
    public void atRefreshGroupNameRequest(Session session, RefreshGroupNameRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (groupId != -userId) {
            // do not notice client, because of quiet operation
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.CHANGE_GROUP_NAME)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        NetContext.getConsumer()
                .asyncAsk(GetUserLatestCacheAsk.valueOf(Set.of(userId)), GetUserLatestCacheAnswer.class, groupId)
                .whenComplete(answer -> {
                    var userCacheMap = answer.getUserCacheMap();
                    if (!userCacheMap.containsKey(userId)) {
                        return;
                    }

                    var myGroupName = userCacheMap.get(userId).getName() + AppConstant.GROUP_DEFAULT_GROUP_NAME_SUFFIX;
                    if (myGroupName.equals(groupEntity.getName())) {
                        return;
                    }
                    groupEntity.setName(myGroupName);
                    groupEntityCaches.update(groupEntity);

                    NetContext.getConsumer().send(GroupUpdatePush.valueOf(groupEntity.getPeople(), GroupUpdateNotice.valueOf(groupEntity.toGroupVO())), groupId);
                });
    }
}
