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

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.gateway.Application;
import com.zfoo.event.model.anno.EventReceiver;
import com.zfoo.net.NetContext;
import com.zfoo.net.consumer.event.ConsumerStartEvent;
import com.zfoo.net.core.gateway.model.*;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.session.model.AttributeType;
import com.zfoo.net.session.model.Session;
import com.zfoo.protocol.IPacket;
import com.zfoo.protocol.ProtocolManager;
import com.zfoo.scheduler.model.anno.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-16 16:22
 */
@Component
public class GatewayController {

    private static final Logger logger = LoggerFactory.getLogger(GatewayController.class);

    private List<Session> getAllConsumerSession(IPacket packet) {
        var loadBalancer = NetContext.getConfigManager().consumerLoadBalancer();
        return loadBalancer.getSessionsByModule(ProtocolManager.moduleByProtocolId(packet.protocolId()));
    }

    @EventReceiver
    public void onAuthUidToGatewayEvent(AuthUidToGatewayEvent event) {
        var sid = event.getSid();
        var uid = event.getUid();

        if (uid <= 0) {
            return;
        }

        var ask = AuthUidAsk.valueOf(Application.GATEWAY_HOST_AND_PORT_STR, sid, uid);

        NetContext.getConfigManager().consumerLoadBalancer()
                .getSessionsByModule(ProtocolManager.moduleByModuleId(AppConstant.ZAPP_PUSH_MODULE_ID))
                .forEach(it -> NetContext.getRouter().send(it, ask, null));
    }

    @EventReceiver
    public void onGatewaySessionInactiveEvent(GatewaySessionInactiveEvent event) {
        var sid = event.getSid();
        var uid = event.getUid();

        if (uid <= 0) {
            return;
        }

        var ask = GatewaySessionInactiveAsk.valueOf(Application.GATEWAY_HOST_AND_PORT_STR, sid, uid);

        NetContext.getConfigManager().consumerLoadBalancer()
                .getSessionsByModule(ProtocolManager.moduleByModuleId(AppConstant.ZAPP_PUSH_MODULE_ID))
                .forEach(it -> NetContext.getRouter().send(it, ask, null));
    }

    /**
     * 当push消费者启动，同步网关的信息的push
     *
     * @param event 消费者启动事件
     */
    @EventReceiver
    public void onConsumerStartEvent(ConsumerStartEvent event) {
        var registerVO = event.getRegisterVO();
        var session = event.getSession();

        var module = ProtocolManager.moduleByModuleId(AppConstant.ZAPP_PUSH_MODULE_ID);

        if (!registerVO.getProviderConfig().getModules().contains(module)) {
            return;
        }

        var map = NetContext.getSessionManager().getServerSessionMap().values()
                .stream()
                .filter(it -> it.getAttribute(AttributeType.UID) != null)
                .collect(Collectors.toMap(keySession -> keySession.getSid()
                        , valueSession -> (Long) valueSession.getAttribute(AttributeType.UID)));

        var ask = GatewaySynchronizeSidAsk.valueOf(Application.GATEWAY_HOST_AND_PORT_STR, map);
        NetContext.getRouter()
                .asyncAsk(session, ask, Message.class, null)
                .whenComplete(message -> logger.info("同步网关信息[gateway{}][size:{}]成功", ask.getGatewayHostAndPort(), map.size()));
    }

    /**
     * 每小时更新一下网关会话信息
     */
    @Scheduler(cron = "0 0 * * * ?")
    public void cronHourScheduler() {
        logger.info("网关每小时同步会话信息");

        var map = NetContext.getSessionManager().getServerSessionMap().values()
                .stream()
                .filter(it -> it.getAttribute(AttributeType.UID) != null)
                .collect(Collectors.toMap(keySession -> keySession.getSid()
                        , valueSession -> (Long) valueSession.getAttribute(AttributeType.UID)));

        var ask = GatewaySynchronizeSidAsk.valueOf(Application.GATEWAY_HOST_AND_PORT_STR, map);

        NetContext.getConfigManager().consumerLoadBalancer()
                .getSessionsByModule(ProtocolManager.moduleByModuleId(AppConstant.ZAPP_PUSH_MODULE_ID))
                .forEach(it -> {
                    NetContext.getRouter().asyncAsk(it, ask, Message.class, null)
                            .whenComplete(message -> logger.info("同步网关信息[gateway{}][size:{}]成功", ask.getGatewayHostAndPort(), map.size()));
                });
    }

}
