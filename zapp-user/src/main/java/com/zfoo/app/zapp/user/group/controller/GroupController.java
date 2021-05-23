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

package com.zfoo.app.zapp.user.group.controller;

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.entity.group.GroupEntity;
import com.zfoo.app.zapp.common.entity.user.UserEntity;
import com.zfoo.app.zapp.common.entity.user.model.ChannelTimePO;
import com.zfoo.app.zapp.common.entity.user.model.GroupTimePO;
import com.zfoo.app.zapp.common.protocol.cache.WordFilterAnswer;
import com.zfoo.app.zapp.common.protocol.cache.WordFilterAsk;
import com.zfoo.app.zapp.common.protocol.user.group.*;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.user.group.service.IGroupService;
import com.zfoo.net.NetContext;
import com.zfoo.net.dispatcher.model.anno.PacketReceiver;
import com.zfoo.net.packet.common.Error;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.packet.model.GatewayPacketAttachment;
import com.zfoo.net.session.model.Session;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-22 14:14
 */
@Component
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    @EntityCachesInjection
    private IEntityCaches<Long, UserEntity> entityCaches;

    @Autowired
    private IGroupService groupService;

    @PacketReceiver
    public void atCreateGroupRequest(Session session, CreateGroupRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupName = cm.getGroupName();
        var userEntity = entityCaches.load(userId);

        NetContext.getConsumer()
                .asyncAsk(WordFilterAsk.valueOf(groupName), WordFilterAnswer.class, userId)
                .whenComplete(new Consumer<>() {
                    @Override
                    public void accept(WordFilterAnswer answer) {
                        // 敏感字符检测
                        if (CollectionUtils.isNotEmpty(answer.getFilterWords())) {
                            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_WORD_FILTER_ERROR.getCode()), gatewayAttachment);
                            return;
                        }
                        var groupId = MongoIdUtils.getIncrementIdFromMongoDefault(GroupEntity.class);
                        var groupEntity = groupService.createGroup(userEntity, groupId, groupName, null);

                        entityCaches.update(userEntity);

                        NetContext.getDispatcher().send(session, CreateGroupResponse.valueOf(groupEntity.toGroupVO()), gatewayAttachment);
                    }
                });
    }

    @PacketReceiver
    public void atRefreshChannelTimeRequest(Session session, RefreshChannelTimeRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var channelId = cm.getChannelId();
        var refreshTime = cm.getRefreshTime();
        var allChannelIds = cm.getAllChannelIds();

        var userEntity = entityCaches.load(userId);

        var groups = userEntity.getGroups();
        var groupTimeOptional = groups.stream().filter(it -> it.getGroupId() == groupId).findFirst();
        if (groupTimeOptional.isEmpty()) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_JOIN_ERROR.getCode()), gatewayAttachment);
            return;
        }

        groupService.perfectChannelTime(userEntity, groupId, allChannelIds);

        var groupTime = groupTimeOptional.get();
        var channels = groupTime.getChannelTimes();

        if (channels.size() > AppConstant.GROUP_CHANNEL_MAX_SIZE) {
            NetContext.getDispatcher().send(session, RefreshChannelTimeResponse.valueOf(groupId, channelId, refreshTime), gatewayAttachment);
            return;
        }

        var channelTimeOptional = channels.stream().filter(it -> it.getChannelId() == channelId).findFirst();
        if (channelTimeOptional.isEmpty()) {
            channels.add(ChannelTimePO.valueOf(channelId, refreshTime));
        } else {
            channelTimeOptional.get().setRefreshTime(refreshTime);
        }

        entityCaches.update(userEntity);
        NetContext.getDispatcher().send(session, RefreshChannelTimeResponse.valueOf(groupId, channelId, refreshTime), gatewayAttachment);
    }

    @PacketReceiver
    public void atMuteGroupRequest(Session session, MuteGroupRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var mute = cm.isMute();

        var userEntity = entityCaches.load(userId);

        var groups = userEntity.getGroups();
        var groupTimeOptional = groups.stream().filter(it -> it.getGroupId() == groupId).findFirst();
        if (groupTimeOptional.isEmpty()) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_JOIN_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var groupTime = groupTimeOptional.get();
        groupTime.setMute(mute);
        entityCaches.update(userEntity);

        NetContext.getDispatcher().send(session, MuteGroupResponse.valueOf(groupId, mute), gatewayAttachment);
    }

    @PacketReceiver
    public void atMuteChannelRequest(Session session, MuteChannelRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var channelId = cm.getChannelId();
        var mute = cm.isMute();
        var allChannelIds = cm.getAllChannelIds();

        var userEntity = entityCaches.load(userId);

        var groups = userEntity.getGroups();
        var groupTimeOptional = groups.stream().filter(it -> it.getGroupId() == groupId).findFirst();
        if (groupTimeOptional.isEmpty()) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_JOIN_ERROR.getCode()), gatewayAttachment);
            return;
        }

        groupService.perfectChannelTime(userEntity, groupId, allChannelIds);

        var groupTime = groupTimeOptional.get();
        var channels = groupTime.getChannelTimes();

        if (channels.size() >= AppConstant.GROUP_CHANNEL_MAX_SIZE) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_MAX_CHANNEL_LIMIT.getCode()), gatewayAttachment);
            return;
        }

        var channelTimeOptional = channels.stream().filter(it -> it.getChannelId() == channelId).findFirst();
        var refreshTime = TimeUtils.now();
        if (channelTimeOptional.isEmpty()) {
            var channelPO = ChannelTimePO.valueOf(channelId, refreshTime);
            channelPO.setMute(mute);
            channels.add(channelPO);
        } else {
            var channelTimePO = channelTimeOptional.get();
            channelTimePO.setMute(mute);
            refreshTime = channelTimePO.getRefreshTime();
        }
        entityCaches.update(userEntity);

        NetContext.getDispatcher().send(session, MuteChannelResponse.valueOf(groupId, channelId, mute, refreshTime), gatewayAttachment);
    }

    @PacketReceiver
    public void atJoinGroupAsk(Session session, JoinGroupAsk ask) {
        var userId = ask.getUserId();
        var groupId = ask.getGroupId();
        var userEntity = entityCaches.load(userId);
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }

        var groups = userEntity.getGroups();
        var groupOptional = groups.stream().filter(it -> it.getGroupId() == groupId).findFirst();
        if (groupOptional.isEmpty()) {
            userEntity.getGroups().add(GroupTimePO.valueOf(groupId));
            entityCaches.update(userEntity);
        }

        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

    @PacketReceiver
    public void atLeaveGroupAsk(Session session, LeaveGroupAsk ask) {
        var userId = ask.getUserId();
        var groupId = ask.getGroupId();
        var userEntity = entityCaches.load(userId);
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        userEntity.getGroups().removeIf(it -> it.getGroupId() == groupId);
        entityCaches.update(userEntity);

        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

}
