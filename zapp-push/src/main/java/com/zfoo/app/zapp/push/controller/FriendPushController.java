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

package com.zfoo.app.zapp.push.controller;

import com.zfoo.app.zapp.common.protocol.push.friend.*;
import com.zfoo.app.zapp.push.sid.service.ISidSessionService;
import com.zfoo.net.NetContext;
import com.zfoo.net.router.receiver.PacketReceiver;
import com.zfoo.net.session.model.Session;
import com.zfoo.protocol.collection.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-17 16:54
 */
@Component
public class FriendPushController {

    @Autowired
    private ISidSessionService sidSessionManager;

    @PacketReceiver
    public void atApplyFriendPush(Session session, ApplyFriendPush push) {
        var targetId = push.getTargetId();

        var uidMap = sidSessionManager.getSessionByUid(targetId);

        if (CollectionUtils.isEmpty(uidMap)) {
            return;
        }

        for (var entry : uidMap.entrySet()) {
            NetContext.getRouter().send(entry.getKey(), ApplyFriendPushToGateway.valueOf(entry.getValue(), push.getNotice()));
        }

    }

    @PacketReceiver
    public void atAcceptFriendPush(Session session, AcceptFriendPush push) {
        var targetIdList = push.getTargetIdList();
        if (CollectionUtils.isEmpty(targetIdList)) {
            return;
        }

        var uidMap = sidSessionManager.getSessionByUidSet(new HashSet<>(targetIdList));

        if (CollectionUtils.isEmpty(uidMap)) {
            return;
        }

        for (var entry : uidMap.entrySet()) {
            NetContext.getRouter().send(entry.getKey(), AcceptFriendPushToGateway.valueOf(entry.getValue(), push.getNotice()));
        }

    }

    @PacketReceiver
    public void atFriendChatMessagePush(Session session, FriendChatMessagePush push) {
        var targetIdList = push.getTargetIdList();
        if (CollectionUtils.isEmpty(targetIdList)) {
            return;
        }

        var uidMap = sidSessionManager.getSessionByUidSet(new HashSet<>(targetIdList));

        if (CollectionUtils.isEmpty(uidMap)) {
            return;
        }

        for (var entry : uidMap.entrySet()) {
            NetContext.getRouter().send(entry.getKey(), FriendChatMessagePushToGateway.valueOf(entry.getValue(), push.getNotice()));
        }
    }

    @PacketReceiver
    public void atDeleteFriendMessagePush(Session session, DeleteFriendMessagePush push) {
        var targetIdList = push.getTargetIdList();
        if (CollectionUtils.isEmpty(targetIdList)) {
            return;
        }

        var uidMap = sidSessionManager.getSessionByUidSet(new HashSet<>(targetIdList));

        if (CollectionUtils.isEmpty(uidMap)) {
            return;
        }

        for (var entry : uidMap.entrySet()) {
            NetContext.getRouter().send(entry.getKey(), DeleteFriendMessagePushToGateway.valueOf(entry.getValue(), push.getNotice()));
        }
    }

    @PacketReceiver
    public void atEditFriendMessagePush(Session session, EditFriendMessagePush push) {
        var targetIdList = push.getTargetIdList();
        if (CollectionUtils.isEmpty(targetIdList)) {
            return;
        }

        var uidMap = sidSessionManager.getSessionByUidSet(new HashSet<>(targetIdList));

        if (CollectionUtils.isEmpty(uidMap)) {
            return;
        }

        for (var entry : uidMap.entrySet()) {
            NetContext.getRouter().send(entry.getKey(), EditFriendMessagePushToGateway.valueOf(entry.getValue(), push.getNotice()));
        }
    }

}
