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

package com.zfoo.app.zapp.common.protocol.group.chat;

import com.zfoo.net.core.gateway.IGatewayLoadBalancer;
import com.zfoo.protocol.IPacket;

/**
 * 群组聊天
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-21 18:20
 */
public class GroupChatRequest implements IPacket, IGatewayLoadBalancer {

    public static final transient short PROTOCOL_ID = 18100;

    private long groupId;
    private long channelId;

    private byte type;

    private String chatMessage;

    @Override
    public Object loadBalancerConsistentHashObject() {
        return groupId;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }
}
