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

package com.zfoo.app.zapp.gateway;

import com.zfoo.app.zapp.common.protocol.user.WebsocketSignInRequest;
import com.zfoo.net.core.gateway.WebsocketSslGatewayServer;
import com.zfoo.net.session.model.AttributeType;
import com.zfoo.net.session.model.Session;
import com.zfoo.protocol.IPacket;
import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.util.net.HostAndPort;
import com.zfoo.util.net.NetUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.function.BiFunction;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-10 14:21
 */
public class Application {

    public static final int WEBSOCKET_SSL_PORT = 443;

    public static final HostAndPort GATEWAY_HOST_AND_PORT = HostAndPort.valueOf(NetUtils.getLocalhostStr(), WEBSOCKET_SSL_PORT);

    public static final String GATEWAY_HOST_AND_PORT_STR = GATEWAY_HOST_AND_PORT.toHostAndPortStr();

    public static final BiFunction<Session, IPacket, Boolean> packetFilter = (session, packet) -> {
        if (packet.protocolId() == WebsocketSignInRequest.getProtocolId()) {
            if (session.getAttribute(AttributeType.UID) == null) {
                return false;
            } else {
                return true;
            }
        }

        var uid = session.getAttribute(AttributeType.UID);
        if (uid != null) {
            return false;
        }

        return true;
    };

    public static void main(String[] args) throws IOException {
        var context = new ClassPathXmlApplicationContext("application.xml");
        context.registerShutdownHook();

        // 对pfx文件使用如下命令生成netty可用的key
        // openssl pkcs12 -in server.pfx -nocerts -nodes -out server.key
        // websocket ssl网关
        var gateway = new WebsocketSslGatewayServer(GATEWAY_HOST_AND_PORT
                , ClassUtils.getFileFromClassPath("3698574__zfoo.com.pem")
                , ClassUtils.getFileFromClassPath("server.key"), packetFilter);

        gateway.start();
    }

}
