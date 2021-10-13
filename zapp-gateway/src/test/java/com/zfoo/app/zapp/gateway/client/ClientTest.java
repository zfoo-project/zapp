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

package com.zfoo.app.zapp.gateway.client;

import com.zfoo.app.zapp.common.constant.ZappDeployEnum;
import com.zfoo.app.zapp.common.protocol.friend.chat.FriendChatRequest;
import com.zfoo.app.zapp.common.protocol.friend.operation.AcceptFriendRequest;
import com.zfoo.app.zapp.common.protocol.friend.operation.ApplyFriendRequest;
import com.zfoo.app.zapp.common.protocol.user.WebsocketSignInRequest;
import com.zfoo.app.zapp.gateway.ApplicationTest;
import com.zfoo.net.NetContext;
import com.zfoo.net.core.tcp.TcpClient;
import com.zfoo.net.session.model.Session;
import com.zfoo.util.ThreadUtils;
import com.zfoo.util.net.HostAndPort;
import com.zfoo.util.net.NetUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Objects;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-10-16 13:25
 */
@Ignore
public class ClientTest {

    private static ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
    private static Session session;

    static {
        ZappDeployEnum.initDevDeploy();

        var hostAndPortStr = (String) System.getProperties().get("hostAndPort");
        var hostAndPort = Objects.isNull(hostAndPortStr) ? HostAndPort.valueOf(NetUtils.LOCAL_LOOPBACK_IP, ApplicationTest.DEFAULT_PORT)
                : HostAndPort.valueOf(hostAndPortStr);
        var client = new TcpClient(hostAndPort);
        session = client.start();
    }

    @Test
    public void aClientTest() {
        var userName = "aaa";
        var friendId = 2;

        // 登录
        var signIn = new WebsocketSignInRequest();
        signIn.setToken("1");
        NetContext.getRouter().send(session, signIn);
        ThreadUtils.sleep(15_000);

        // 同意好友申申请
        var acceptFriend = new AcceptFriendRequest();
        acceptFriend.setFriendId(friendId);
        NetContext.getRouter().send(session, acceptFriend);
        ThreadUtils.sleep(3000);

        // 好友聊天
        var chatRequest = new FriendChatRequest();
        chatRequest.setFriendId(friendId);
        chatRequest.setChatMessage("hello world");
        NetContext.getRouter().send(session, chatRequest);
        ThreadUtils.sleep(3000);

        // 删除好友
//        var deleteRequest = new DeleteFriendRequest();
//        deleteRequest.setFriendId(friendId);
//        NetContext.getDispatcherManager().send(session, deleteRequest);
//        ThreadUtils.sleep(3000);

        // 好友聊天
        NetContext.getRouter().send(session, chatRequest);
        ThreadUtils.sleep(3000);

        ThreadUtils.sleep(Long.MAX_VALUE);
    }


    @Test
    public void bClientTest() {
        var userName = "bbb";
        var friendId = 1;

        // 登录
        var signIn = new WebsocketSignInRequest();
        signIn.setToken("2");
        NetContext.getRouter().send(session, signIn);
        ThreadUtils.sleep(3000);

        // 加A为好友
        var applyFriend = new ApplyFriendRequest();
        NetContext.getRouter().send(session, applyFriend);
        ThreadUtils.sleep(3000);


        ThreadUtils.sleep(Long.MAX_VALUE);
    }
}
