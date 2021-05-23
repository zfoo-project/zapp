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

package com.zfoo.app.zapp.common.protocol.friend.chat;

import com.zfoo.net.core.gateway.IGatewayLoadBalancer;
import com.zfoo.protocol.IPacket;
import com.zfoo.util.security.IdUtils;

/**
 * 好友聊天
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-14 10:54
 */
public class FriendHistoryMessageRequest implements IPacket, IGatewayLoadBalancer {

    public static final transient short PROTOCOL_ID = 15204;

    private long userId;
    private long friendId;

    /**
     * 最老消息的id
     */
    private long lastMessageId;

    @Override
    public Object loadBalancerConsistentHashObject() {
        return IdUtils.generateStringId(userId, friendId);
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getFriendId() {
        return friendId;
    }

    public void setFriendId(long friendId) {
        this.friendId = friendId;
    }

    public long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }
}
