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

package com.zfoo.app.zapp.gateway.client.controller;

import com.zfoo.app.zapp.common.protocol.common.ChatMessage;
import com.zfoo.app.zapp.common.protocol.friend.NewApplyFriendNotice;
import com.zfoo.app.zapp.common.protocol.friend.NewFriendNotice;
import com.zfoo.app.zapp.common.protocol.friend.operation.DeleteFriendResponse;
import com.zfoo.app.zapp.common.protocol.user.WebsocketSignInResponse;
import com.zfoo.net.packet.common.Error;
import com.zfoo.net.router.receiver.PacketReceiver;
import com.zfoo.net.session.model.Session;
import com.zfoo.protocol.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-10-16 14:06
 */
@Component
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    public static volatile long userId = 0;

    @PacketReceiver
    public void atWebsocketSignInResponse(Session session, WebsocketSignInResponse sm) {
        logger.info(sm.getClass() + "-->" + JsonUtils.object2String(sm));
        userId = sm.getUserId();
    }


    @PacketReceiver
    public void atDeleteFriendResponse(Session session, DeleteFriendResponse sm) {
        logger.info(sm.getClass() + "-->" + JsonUtils.object2String(sm));
    }


    @PacketReceiver
    public void atChatMessage(Session session, ChatMessage sm) {
        logger.info(sm.getClass() + "-->" + JsonUtils.object2String(sm));
    }

    @PacketReceiver
    public void atNewApplyFriendNotice(Session session, NewApplyFriendNotice sm) {
        logger.info(sm.getClass() + "-->" + JsonUtils.object2String(sm));
    }

    @PacketReceiver
    public void atNewFriendNotice(Session session, NewFriendNotice sm) {
        logger.info(sm.getClass() + "-->" + JsonUtils.object2String(sm));
    }

    @PacketReceiver
    public void atError(Session session, Error sm) {
        logger.info(sm.getClass() + "-->" + JsonUtils.object2String(sm));
    }

}
