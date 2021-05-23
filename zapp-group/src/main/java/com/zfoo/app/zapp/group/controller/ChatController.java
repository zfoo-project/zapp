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
import com.zfoo.app.zapp.common.entity.common.MessageEnum;
import com.zfoo.app.zapp.common.entity.common.MessagePO;
import com.zfoo.app.zapp.common.entity.group.ChannelEntity;
import com.zfoo.app.zapp.common.entity.group.GroupEntity;
import com.zfoo.app.zapp.common.entity.group.model.OperationEnum;
import com.zfoo.app.zapp.common.model.OssPolicyEnum;
import com.zfoo.app.zapp.common.protocol.group.DeleteGroupMessageNotice;
import com.zfoo.app.zapp.common.protocol.group.EditGroupMessageNotice;
import com.zfoo.app.zapp.common.protocol.group.GroupChatMessageNotice;
import com.zfoo.app.zapp.common.protocol.group.chat.*;
import com.zfoo.app.zapp.common.protocol.push.group.DeleteGroupMessagePush;
import com.zfoo.app.zapp.common.protocol.push.group.EditGroupMessagePush;
import com.zfoo.app.zapp.common.protocol.push.group.GroupChatMessagePush;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.group.service.IGroupService;
import com.zfoo.net.NetContext;
import com.zfoo.net.dispatcher.model.anno.PacketReceiver;
import com.zfoo.net.packet.common.Error;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.packet.model.GatewayPacketAttachment;
import com.zfoo.net.session.model.Session;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-07 14:42
 */
@Component
public class ChatController {

    @EntityCachesInjection
    private IEntityCaches<Long, GroupEntity> groupEntityCaches;

    @EntityCachesInjection
    private IEntityCaches<Long, ChannelEntity> channelEntityCaches;

    @Autowired
    private IGroupService groupService;

    @PacketReceiver
    public void atGroupChatRequest(Session session, GroupChatRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var channelId = cm.getChannelId();
        var type = MessageEnum.getMessageEnumByType(cm.getType());
        var message = cm.getChatMessage();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (!CommonUtils.isChannelIdInRange(List.of(channelId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (type == null) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }

        if (type == MessageEnum.IMAGE) {
            if (!message.startsWith(OssPolicyEnum.GROUP_IMAGE.getUrl())) {
                NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.IMG_PATH_ERROR.getCode()), gatewayAttachment);
                return;
            }
        }

        if (type == MessageEnum.VIDEO) {
            if (!message.startsWith(OssPolicyEnum.GROUP_VIDEO.getUrl())) {
                NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.VIDEO_URL_ERROR.getCode()), gatewayAttachment);
                return;
            }
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxChannelAuth(channelId, userId).hasAuth(OperationEnum.CHANNEL_SEND_NORMAL_MESSAGE)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var channelPO = groupEntity.findChannel(channelId);
        if (channelPO == null) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var now = TimeUtils.now();
        channelPO.setRefreshTime(now);

        var channelEntity = channelEntityCaches.load(channelId);


        var messagePO = MessagePO.valueOf(type.getType(), userId, message, now);
        channelEntity.addMessage(messagePO);

        groupEntityCaches.update(groupEntity);
        channelEntityCaches.update(channelEntity);

        var groupMessageNotice = GroupChatMessageNotice.valueOf(groupId, channelId, groupService.toChatMessages(List.of(messagePO)).get(0));

        // 推送给接收者，考虑到多端登录，还需要发送给自己
        NetContext.getConsumer().send(GroupChatMessagePush.valueOf(new ArrayList<>(groupEntity.getPeople()), groupMessageNotice), groupId);

        NetContext.getDispatcher().send(session, Message.valueOf(cm, CodeEnum.OK_QUIETLY.getCode()), gatewayAttachment);
    }


    @PacketReceiver
    public void atDeleteGroupMessageRequest(Session session, DeleteGroupMessageRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var channelId = cm.getChannelId();
        var messageId = cm.getMessageId();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (!CommonUtils.isChannelIdInRange(List.of(channelId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxChannelAuth(channelId, userId).hasAuth(OperationEnum.CHANNEL_DELETE_MESSAGE)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var channelPO = groupEntity.findChannel(channelId);
        if (channelPO == null) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var channelEntity = channelEntityCaches.load(channelId);
        channelEntity.getMessages().removeIf(it -> it.getId() == messageId);
        channelEntity.getPins().removeIf(it -> it.getId() == messageId);
        channelEntityCaches.update(channelEntity);

        NetContext.getDispatcher().send(session, Message.valueOf(cm, CodeEnum.OK.getCode()), gatewayAttachment);

        // 推送给接收者，考虑到多端登录，还需要发送给自己
        NetContext.getConsumer().send(DeleteGroupMessagePush.valueOf(groupEntity.getPeople(), DeleteGroupMessageNotice.valueOf(groupId, channelId, messageId)), groupId);
    }

    @PacketReceiver
    public void atEditGroupMessageRequest(Session session, EditGroupMessageRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var channelId = cm.getChannelId();
        var messageId = cm.getMessageId();
        var chatMessage = cm.getChatMessage();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (StringUtils.isBlank(chatMessage)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }

        if (!CommonUtils.isChannelIdInRange(List.of(channelId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxChannelAuth(channelId, userId).hasAuth(OperationEnum.CHANNEL_DELETE_MESSAGE)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var channelPO = groupEntity.findChannel(channelId);
        if (channelPO == null) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var channelEntity = channelEntityCaches.load(channelId);
        var messageOptional = channelEntity.getMessages().stream().filter(it -> it.getId() == messageId).findFirst();
        if (messageOptional.isEmpty()) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_ONE.getCode()), gatewayAttachment);
            return;
        }
        var messagePO = messageOptional.get();
        if (MessageEnum.getMessageEnumByType(messagePO.getType()) != MessageEnum.TEXT) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_TWO.getCode(), null), gatewayAttachment);
            return;
        }
        messagePO.setMessage(chatMessage);

        // 修改pin消息
        var pinMessageOptional = channelEntity.getPins().stream().filter(it -> it.getId() == messageId).findFirst();
        if (pinMessageOptional.isPresent()) {
            var pinMessagePO = pinMessageOptional.get();
            pinMessagePO.setMessage(chatMessage);
        }

        channelEntityCaches.update(channelEntity);

        NetContext.getDispatcher().send(session, Message.valueOf(cm, CodeEnum.OK.getCode()), gatewayAttachment);

        // 推送给接收者，考虑到多端登录，还需要发送给自己
        NetContext.getConsumer().send(EditGroupMessagePush.valueOf(groupEntity.getPeople(), EditGroupMessageNotice.valueOf(groupId, channelId, messageId, chatMessage)), groupId);
    }

    @PacketReceiver
    public void atGroupHistoryMessageRequest(Session session, GroupHistoryMessageRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var channelId = cm.getChannelId();
        var lastMessageId = cm.getLastMessageId();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (!CommonUtils.isChannelIdInRange(List.of(channelId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        // 如果是官方群历史记录则立刻返回，并且将该成员添加到官方群聊中
        if (groupId == AppConstant.ZFOO_GROUP_ID && !groupEntity.getPeople().contains(userId)) {
            groupEntity.getPeople().add(userId);
        }

        if (!groupEntity.memberOfMaxChannelAuth(channelId, userId).hasAuth(OperationEnum.CHANNEL_ACCESS_MESSAGE)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var channelEntity = channelEntityCaches.load(channelId);
        var chatMessages = channelEntity.getMessages();
        var list = lastMessageId > 0
                ? chatMessages.stream().filter(message -> message.getId() < lastMessageId).sorted((a, b) -> Long.compare(b.getId(), a.getId())).limit(AppConstant.FRIEND_HISTORY_MESSAGE_PER_REQUEST_SIZE).collect(Collectors.toList())
                : chatMessages.stream().sorted((a, b) -> Long.compare(b.getId(), a.getId())).limit(AppConstant.FRIEND_HISTORY_MESSAGE_PER_REQUEST_SIZE).collect(Collectors.toList());
        Collections.reverse(list);

        NetContext.getDispatcher().send(session, GroupHistoryMessageResponse.valueOf(groupId, channelId, groupService.toChatMessages(list)), gatewayAttachment);
    }

    @PacketReceiver
    public void atPinGroupMessageRequest(Session session, PinGroupMessageRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var channelId = cm.getChannelId();
        var messageId = cm.getMessageId();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (!CommonUtils.isChannelIdInRange(List.of(channelId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxChannelAuth(channelId, userId).hasAuth(OperationEnum.CHANNEL_PIN_MESSAGE)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var channelPO = groupEntity.findChannel(channelId);
        if (channelPO == null) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var channelEntity = channelEntityCaches.load(channelId);
        var messageOptional = channelEntity.getMessages().stream().filter(it -> it.getId() == messageId).findFirst();
        if (messageOptional.isEmpty()) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_ONE.getCode()), gatewayAttachment);
            return;
        }

        if (channelEntity.getPins().size() >= AppConstant.GROUP_CHAT_PIN_MESSAGE_MAX_SIZE) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_PING_SIZE_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var messagePO = messageOptional.get();
        if (channelEntity.getPins().stream().noneMatch(it -> it.getId() == messageId)) {
            channelEntity.getPins().add(messagePO);
            channelEntity.getPins().sort((a, b) -> Long.compare(a.getId(), b.getId()));
            channelEntityCaches.update(channelEntity);
        }

        NetContext.getDispatcher().send(session, Message.valueOf(cm, CodeEnum.OK.getCode()), gatewayAttachment);
    }

    @PacketReceiver
    public void atDeleteGroupPinMessageRequest(Session session, DeleteGroupPinMessageRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var channelId = cm.getChannelId();
        var messageId = cm.getMessageId();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (!CommonUtils.isChannelIdInRange(List.of(channelId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxChannelAuth(channelId, userId).hasAuth(OperationEnum.CHANNEL_PIN_MESSAGE)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var channelPO = groupEntity.findChannel(channelId);
        if (channelPO == null) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var channelEntity = channelEntityCaches.load(channelId);
        channelEntity.getPins().removeIf(it -> it.getId() == messageId);
        channelEntityCaches.update(channelEntity);

        NetContext.getDispatcher().send(session, DeleteGroupPinMessageResponse.valueOf(groupId, channelId, messageId), gatewayAttachment);
    }

    @PacketReceiver
    public void atGroupHistoryPinMessageRequest(Session session, GroupHistoryPinMessageRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var channelId = cm.getChannelId();
        var lastMessageId = cm.getLastMessageId();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (!CommonUtils.isChannelIdInRange(List.of(channelId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANNEL_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxChannelAuth(channelId, userId).hasAuth(OperationEnum.CHANNEL_ACCESS_MESSAGE)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var channelEntity = channelEntityCaches.load(channelId);
        var pins = channelEntity.getPins();
        var list = lastMessageId > 0
                ? pins.stream().filter(message -> message.getId() < lastMessageId).sorted((a, b) -> Long.compare(b.getId(), a.getId())).limit(AppConstant.FRIEND_HISTORY_MESSAGE_PER_REQUEST_SIZE).collect(Collectors.toList())
                : pins.stream().sorted((a, b) -> Long.compare(b.getId(), a.getId())).limit(AppConstant.FRIEND_HISTORY_MESSAGE_PER_REQUEST_SIZE).collect(Collectors.toList());
        Collections.reverse(list);

        NetContext.getDispatcher().send(session, GroupHistoryPinMessageResponse.valueOf(groupId, channelId, lastMessageId, groupService.toChatMessages(list)), gatewayAttachment);
    }

}
