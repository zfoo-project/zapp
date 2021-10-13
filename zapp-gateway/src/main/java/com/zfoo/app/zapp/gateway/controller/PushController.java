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

package com.zfoo.app.zapp.gateway.controller;

import com.zfoo.app.zapp.common.protocol.push.friend.*;
import com.zfoo.app.zapp.common.protocol.push.group.*;
import com.zfoo.net.NetContext;
import com.zfoo.net.router.receiver.PacketReceiver;
import com.zfoo.net.session.model.Session;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-17 17:14
 */
@Component
public class PushController {

    @PacketReceiver
    public void atApplyFriendPushToGateway(Session session, ApplyFriendPushToGateway push) {
        for (var sid : push.getSidSet()) {
            var pushSession = NetContext.getSessionManager().getServerSession(sid);
            NetContext.getRouter().send(pushSession, push.getNotice());
        }
    }

    @PacketReceiver
    public void atAcceptFriendPushToGateway(Session session, AcceptFriendPushToGateway push) {
        for (var sid : push.getSidSet()) {
            var pushSession = NetContext.getSessionManager().getServerSession(sid);
            NetContext.getRouter().send(pushSession, push.getNotice());
        }
    }

    @PacketReceiver
    public void atFriendChatMessagePushToGateway(Session session, FriendChatMessagePushToGateway push) {
        for (var sid : push.getSidSet()) {
            var pushSession = NetContext.getSessionManager().getServerSession(sid);
            NetContext.getRouter().send(pushSession, push.getNotice());
        }
    }

    @PacketReceiver
    public void atDeleteFriendMessagePushToGateway(Session session, DeleteFriendMessagePushToGateway push) {
        for (var sid : push.getSidSet()) {
            var pushSession = NetContext.getSessionManager().getServerSession(sid);
            NetContext.getRouter().send(pushSession, push.getNotice());
        }
    }

    @PacketReceiver
    public void atEditFriendMessagePushToGateway(Session session, EditFriendMessagePushToGateway push) {
        for (var sid : push.getSidSet()) {
            var pushSession = NetContext.getSessionManager().getServerSession(sid);
            NetContext.getRouter().send(pushSession, push.getNotice());
        }
    }

    @PacketReceiver
    public void atDeleteGroupPushToGateway(Session session, DeleteGroupPushToGateway push) {
        for (var sid : push.getSidSet()) {
            var pushSession = NetContext.getSessionManager().getServerSession(sid);
            NetContext.getRouter().send(pushSession, push.getNotice());
        }
    }

    @PacketReceiver
    public void atGroupUpdatePushToGateway(Session session, GroupUpdatePushToGateway push) {
        for (var sid : push.getSidSet()) {
            var pushSession = NetContext.getSessionManager().getServerSession(sid);
            NetContext.getRouter().send(pushSession, push.getNotice());
        }
    }

    @PacketReceiver
    public void atGroupChatMessagePushToGateway(Session session, GroupChatMessagePushToGateway push) {
        for (var sid : push.getSidSet()) {
            var pushSession = NetContext.getSessionManager().getServerSession(sid);
            NetContext.getRouter().send(pushSession, push.getNotice());
        }
    }

    @PacketReceiver
    public void atMemberGroupAuthIdUpdatePushToGateway(Session session, MemberGroupAuthIdUpdatePushToGateway push) {
        for (var sid : push.getSidSet()) {
            var pushSession = NetContext.getSessionManager().getServerSession(sid);
            NetContext.getRouter().send(pushSession, push.getNotice());
        }
    }

    @PacketReceiver
    public void atKickMemberPushToGateway(Session session, KickMemberPushToGateway push) {
        for (var sid : push.getSidSet()) {
            var pushSession = NetContext.getSessionManager().getServerSession(sid);
            NetContext.getRouter().send(pushSession, push.getNotice());
        }
    }

    @PacketReceiver
    public void atDeleteGroupMessagePushToGateway(Session session, DeleteGroupMessagePushToGateway push) {
        for (var sid : push.getSidSet()) {
            var pushSession = NetContext.getSessionManager().getServerSession(sid);
            NetContext.getRouter().send(pushSession, push.getNotice());
        }
    }

    @PacketReceiver
    public void atEditGroupMessagePushToGateway(Session session, EditGroupMessagePushToGateway push) {
        for (var sid : push.getSidSet()) {
            var pushSession = NetContext.getSessionManager().getServerSession(sid);
            NetContext.getRouter().send(pushSession, push.getNotice());
        }
    }

}
