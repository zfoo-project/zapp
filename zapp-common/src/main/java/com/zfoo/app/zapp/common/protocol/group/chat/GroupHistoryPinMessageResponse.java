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

import com.zfoo.app.zapp.common.protocol.common.ChatMessage;
import com.zfoo.protocol.IPacket;

import java.util.List;

/**
 * 好友聊天
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-14 10:54
 */
public class GroupHistoryPinMessageResponse implements IPacket {

    public static final transient short PROTOCOL_ID = 18113;

    private long groupId;
    private long channelId;

    private long lastMessageId;

    private List<ChatMessage> chatMessages;

    public static GroupHistoryPinMessageResponse valueOf(long groupId, long channelId, long lastMessageId, List<ChatMessage> chatMessages) {
        var response = new GroupHistoryPinMessageResponse();
        response.groupId = groupId;
        response.channelId = channelId;
        response.lastMessageId = lastMessageId;
        response.chatMessages = chatMessages;
        return response;
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

    public long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }
}
