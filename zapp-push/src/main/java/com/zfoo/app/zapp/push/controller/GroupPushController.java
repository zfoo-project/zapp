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

import com.zfoo.app.zapp.common.protocol.push.group.*;
import com.zfoo.app.zapp.push.sid.service.ISidSessionService;
import com.zfoo.net.NetContext;
import com.zfoo.net.dispatcher.model.anno.PacketReceiver;
import com.zfoo.net.session.model.Session;
import com.zfoo.protocol.collection.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-27 11:25
 */
@Component
public class GroupPushController {

    @Autowired
    private ISidSessionService sidSessionManager;

    @PacketReceiver
    public void atDeleteGroupPush(Session session, DeleteGroupPush push) {
        var people = push.getPeople();
        var uidMap = sidSessionManager.getSessionByUidSet(people);

        if (CollectionUtils.isEmpty(uidMap)) {
            return;
        }

        for (var entry : uidMap.entrySet()) {
            NetContext.getDispatcher().send(entry.getKey(), DeleteGroupPushToGateway.valueOf(entry.getValue(), push.getNotice()));
        }
    }

    @PacketReceiver
    public void atGroupUpdatePush(Session session, GroupUpdatePush push) {
        var people = push.getPeople();
        var uidMap = sidSessionManager.getSessionByUidSet(people);

        if (CollectionUtils.isEmpty(uidMap)) {
            return;
        }

        for (var entry : uidMap.entrySet()) {
            NetContext.getDispatcher().send(entry.getKey(), GroupUpdatePushToGateway.valueOf(entry.getValue(), push.getNotice()));
        }
    }

    @PacketReceiver
    public void atGroupChatMessagePush(Session session, GroupChatMessagePush push) {
        var targetIdList = push.getTargetIdList();
        if (CollectionUtils.isEmpty(targetIdList)) {
            return;
        }

        var uidMap = sidSessionManager.getSessionByUidSet(new HashSet<>(targetIdList));

        if (CollectionUtils.isEmpty(uidMap)) {
            return;
        }

        for (var entry : uidMap.entrySet()) {
            NetContext.getDispatcher().send(entry.getKey(), GroupChatMessagePushToGateway.valueOf(entry.getValue(), push.getNotice()));
        }
    }

    @PacketReceiver
    public void atMemberGroupAuthIdUpdatePush(Session session, MemberGroupAuthIdUpdatePush push) {
        var memberId = push.getMemberId();
        var uidMap = sidSessionManager.getSessionByUid(memberId);

        if (CollectionUtils.isEmpty(uidMap)) {
            return;
        }

        for (var entry : uidMap.entrySet()) {
            NetContext.getDispatcher().send(entry.getKey(), MemberGroupAuthIdUpdatePushToGateway.valueOf(entry.getValue(), push.getNotice()));
        }
    }

    @PacketReceiver
    public void atKickMemberPush(Session session, KickMemberPush push) {
        var targetId = push.getTargetId();

        var uidMap = sidSessionManager.getSessionByUid(targetId);
        if (CollectionUtils.isEmpty(uidMap)) {
            return;
        }

        for (var entry : uidMap.entrySet()) {
            NetContext.getDispatcher().send(entry.getKey(), KickMemberPushToGateway.valueOf(entry.getValue(), push.getNotice()));
        }
    }

    @PacketReceiver
    public void atDeleteGroupMessagePush(Session session, DeleteGroupMessagePush push) {
        var people = push.getPeople();
        if (CollectionUtils.isEmpty(people)) {
            return;
        }

        var uidMap = sidSessionManager.getSessionByUidSet(new HashSet<>(people));

        if (CollectionUtils.isEmpty(uidMap)) {
            return;
        }

        for (var entry : uidMap.entrySet()) {
            NetContext.getDispatcher().send(entry.getKey(), DeleteGroupMessagePushToGateway.valueOf(entry.getValue(), push.getNotice()));
        }
    }

    @PacketReceiver
    public void atEditGroupMessagePush(Session session, EditGroupMessagePush push) {
        var people = push.getPeople();
        if (CollectionUtils.isEmpty(people)) {
            return;
        }

        var uidMap = sidSessionManager.getSessionByUidSet(new HashSet<>(people));

        if (CollectionUtils.isEmpty(uidMap)) {
            return;
        }

        for (var entry : uidMap.entrySet()) {
            NetContext.getDispatcher().send(entry.getKey(), EditGroupMessagePushToGateway.valueOf(entry.getValue(), push.getNotice()));
        }
    }
}
