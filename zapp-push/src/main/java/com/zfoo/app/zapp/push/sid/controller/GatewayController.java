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

package com.zfoo.app.zapp.push.sid.controller;

import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.push.sid.service.ISidSessionService;
import com.zfoo.net.NetContext;
import com.zfoo.net.core.gateway.model.AuthUidAsk;
import com.zfoo.net.core.gateway.model.GatewaySessionInactiveAsk;
import com.zfoo.net.core.gateway.model.GatewaySynchronizeSidAsk;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.router.receiver.PacketReceiver;
import com.zfoo.net.session.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-16 18:42
 */
@Component
public class GatewayController {

    private static final Logger logger = LoggerFactory.getLogger(GatewayController.class);

    @Autowired
    private ISidSessionService sidSessionService;


    @PacketReceiver
    public void atAuthUidAsk(Session session, AuthUidAsk ask) {
        var gatewayHostAndPort = ask.getGatewayHostAndPort();
        var sid = ask.getSid();
        var uid = ask.getUid();
        logger.info("authUidAsk[gatewayHostAndPort:{}][sid:{}][uid:{}]", gatewayHostAndPort, sid, uid);

        sidSessionService.addSid(session, gatewayHostAndPort, sid, uid);
    }

    @PacketReceiver
    public void atGatewaySessionInactiveAsk(Session session, GatewaySessionInactiveAsk ask) {
        var gatewayHostAndPort = ask.getGatewayHostAndPort();
        var sid = ask.getSid();
        var uid = ask.getUid();
        logger.info("gatewaySessionInactiveAsk[gatewayHostAndPort:{}][sid:{}][uid:{}]", gatewayHostAndPort, sid, uid);

        sidSessionService.removeSid(session, gatewayHostAndPort, sid, uid);
    }

    @PacketReceiver
    public void atGatewaySynchronizeSidAsk(Session session, GatewaySynchronizeSidAsk ask) {
        var gatewayHostAndPort = ask.getGatewayHostAndPort();
        var sidMap = ask.getSidMap();

        logger.info("gatewaySessionInactiveAsk[gatewayHostAndPort:{}]", gatewayHostAndPort);

        NetContext.getRouter().send(session, Message.valueOf(ask, CodeEnum.OK.getCode(), null));

        sidSessionService.synchronizeSid(session, gatewayHostAndPort, sidMap);
    }


}
