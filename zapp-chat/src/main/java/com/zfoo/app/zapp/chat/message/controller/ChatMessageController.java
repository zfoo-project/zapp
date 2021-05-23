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

package com.zfoo.app.zapp.chat.message.controller;

import com.zfoo.app.zapp.chat.friend.service.IFriendService;
import com.zfoo.app.zapp.chat.message.service.IChatMessageService;
import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.entity.common.MessageEnum;
import com.zfoo.app.zapp.common.entity.friend.FriendEntity;
import com.zfoo.app.zapp.common.model.OssPolicyEnum;
import com.zfoo.app.zapp.common.protocol.friend.DeleteFriendMessageNotice;
import com.zfoo.app.zapp.common.protocol.friend.EditFriendMessageNotice;
import com.zfoo.app.zapp.common.protocol.friend.chat.*;
import com.zfoo.app.zapp.common.protocol.push.friend.DeleteFriendMessagePush;
import com.zfoo.app.zapp.common.protocol.push.friend.EditFriendMessagePush;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
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
import com.zfoo.util.security.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-17 12:14
 */
@Component
public class ChatMessageController {

    @EntityCachesInjection
    private IEntityCaches<String, FriendEntity> friendEntityCaches;

    @Autowired
    private IChatMessageService chatMessageService;

    @Autowired
    private IFriendService friendService;

    @PacketReceiver
    public void atFriendChatRequest(Session session, FriendChatRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var type = MessageEnum.getMessageEnumByType(cm.getType());
        var userId = gatewayAttachment.getUid();
        var friendId = cm.getFriendId();
        var message = cm.getChatMessage();

        if (userId != cm.getUserId()) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }

        if (!CommonUtils.isUserIdInRange(List.of(friendId)) || friendId == userId) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.USER_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (type == null) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_ONE.getCode()), gatewayAttachment);
            return;
        }

        if (StringUtils.isBlank(message)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_EMPTY.getCode()), gatewayAttachment);
            return;
        }

        if (type == MessageEnum.IMAGE && !message.startsWith(OssPolicyEnum.CHAT_IMAGE.getUrl())) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.IMG_PATH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        if (type == MessageEnum.VIDEO && !message.startsWith(OssPolicyEnum.CHAT_VIDEO.getUrl())) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.VIDEO_URL_ERROR.getCode()), gatewayAttachment);
            return;
        }

        if (!friendService.connected(userId, friendId)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.FRIEND_NOT_ADDED.getCode()), gatewayAttachment);
            return;
        }
        if (friendService.blacklisted(userId, friendId)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.FRIEND_REJECT_MESSAGE.getCode(), null), gatewayAttachment);
            return;
        }
        if (friendService.blacklisted(friendId, userId)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.FRIEND_REJECT_MESSAGE.getCode(), null), gatewayAttachment);
            return;
        }
        chatMessageService.chatToFriend(userId, friendId, type, message);

        NetContext.getDispatcher().send(session, Message.valueOf(cm, CodeEnum.OK_QUIETLY.getCode()), gatewayAttachment);
    }

    @PacketReceiver
    public void atDeleteFriendMessageRequest(Session session, DeleteFriendMessageRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var friendId = cm.getFriendId();
        var messageId = cm.getMessageId();

        if (userId != cm.getUserId()) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }

        if (!CommonUtils.isUserIdInRange(List.of(friendId)) || friendId == userId) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.USER_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (!friendService.connected(userId, friendId)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.FRIEND_NOT_ADDED.getCode()), gatewayAttachment);
            return;
        }
        if (friendService.blacklisted(userId, friendId)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.FRIEND_REJECT_MESSAGE.getCode(), null), gatewayAttachment);
            return;
        }

        var id = IdUtils.generateStringId(userId, friendId);
        var friendEntity = friendEntityCaches.load(id);
        var removed = friendEntity.getMessages().removeIf(it -> it.getId() == messageId);
        if (removed) {
            friendEntityCaches.update(friendEntity);

            var deleteFriendMessageNotice = DeleteFriendMessageNotice.valueOf(userId, friendId, messageId);
            NetContext.getConsumer().send(DeleteFriendMessagePush.valueOf(List.of(userId, friendId), deleteFriendMessageNotice), IdUtils.generateStringId(userId, friendId));
            NetContext.getDispatcher().send(session, Message.valueOf(cm, CodeEnum.OK_QUIETLY.getCode()), gatewayAttachment);
        }
    }

    @PacketReceiver
    public void atEditFriendMessageRequest(Session session, EditFriendMessageRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var friendId = cm.getFriendId();
        var messageId = cm.getMessageId();
        var chatMessage = cm.getChatMessage();

        if (userId != cm.getUserId()) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }

        if (StringUtils.isBlank(chatMessage)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_ONE.getCode()), gatewayAttachment);
            return;
        }

        if (!CommonUtils.isUserIdInRange(List.of(friendId)) || friendId == userId) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.USER_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (!friendService.connected(userId, friendId)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.FRIEND_NOT_ADDED.getCode()), gatewayAttachment);
            return;
        }
        if (friendService.blacklisted(userId, friendId)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.FRIEND_REJECT_MESSAGE.getCode(), null), gatewayAttachment);
            return;
        }

        var id = IdUtils.generateStringId(userId, friendId);
        var friendEntity = friendEntityCaches.load(id);
        var messageOptional = friendEntity.getMessages().stream().filter(it -> it.getId() == messageId).findFirst();
        if (messageOptional.isEmpty()) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_TWO.getCode(), null), gatewayAttachment);
            return;
        }

        var messagePO = messageOptional.get();
        // 只能编辑文本消息
        if (MessageEnum.getMessageEnumByType(messagePO.getType()) != MessageEnum.TEXT) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_THREE.getCode(), null), gatewayAttachment);
            return;
        }
        // 只能编辑自己发送的消息
        if (messagePO.getSendId() != userId) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_FOUR.getCode(), null), gatewayAttachment);
            return;
        }
        messagePO.setMessage(chatMessage);
        friendEntityCaches.update(friendEntity);

        var editFriendMessageNotice = EditFriendMessageNotice.valueOf(userId, friendId, messageId, chatMessage);
        NetContext.getConsumer().send(EditFriendMessagePush.valueOf(List.of(userId, friendId), editFriendMessageNotice), IdUtils.generateStringId(userId, friendId));
        NetContext.getDispatcher().send(session, Message.valueOf(cm, CodeEnum.OK_QUIETLY.getCode()), gatewayAttachment);
    }

    @PacketReceiver
    public void atReadFriendMessageRequest(Session session, ReadFriendMessageRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var friendId = cm.getFriendId();

        if (userId != cm.getUserId()) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }

        if (!CommonUtils.isUserIdInRange(List.of(friendId)) || friendId == userId) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.USER_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (!friendService.connected(userId, friendId)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.FRIEND_NOT_ADDED.getCode()), gatewayAttachment);
            return;
        }

        var id = IdUtils.generateStringId(userId, friendId);
        var friendEntity = friendEntityCaches.load(id);
        friendEntity.getMessages().stream().forEach(it -> {
            if (it.getSendId() == friendId) {
                it.setRead(true);
            }
        });
        var now = TimeUtils.now();
        friendEntity.readTime(userId, now);

        friendEntityCaches.update(friendEntity);

        NetContext.getDispatcher().send(session, ReadFriendMessageResponse.valueOf(friendId, friendEntity.getRefreshTime(), now), gatewayAttachment);
    }

    @PacketReceiver
    public void atFriendHistoryMessageRequest(Session session, FriendHistoryMessageRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var friendId = cm.getFriendId();
        var lastMessageId = cm.getLastMessageId();

        if (userId != cm.getUserId()) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }

        if (!CommonUtils.isUserIdInRange(List.of(friendId)) || friendId == userId) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.USER_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (!friendService.connected(userId, friendId)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.FRIEND_NOT_ADDED.getCode()), gatewayAttachment);
            return;
        }

        var id = IdUtils.generateStringId(userId, friendId);
        var friendEntity = friendEntityCaches.load(id);

        var uidA = friendEntity.getUidA();
        var uidB = friendEntity.getUidB();
        var chatMessages = friendEntity.getMessages();

        var list = lastMessageId > 0
                ? chatMessages.stream().filter(message -> message.getId() < lastMessageId).sorted((a, b) -> Long.compare(b.getId(), a.getId())).limit(AppConstant.FRIEND_HISTORY_MESSAGE_PER_REQUEST_SIZE).collect(Collectors.toList())
                : chatMessages.stream().sorted((a, b) -> Long.compare(b.getId(), a.getId())).limit(AppConstant.FRIEND_HISTORY_MESSAGE_PER_REQUEST_SIZE).collect(Collectors.toList());
        Collections.reverse(list);

        NetContext.getDispatcher().send(session, FriendHistoryMessageResponse.valueOf(uidA, uidB, chatMessageService.toChatMessages(list)), gatewayAttachment);
    }

}
