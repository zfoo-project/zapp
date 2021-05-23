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

package com.zfoo.app.zapp.common.protocol.group;

import com.zfoo.app.zapp.common.protocol.common.ChatMessage;
import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-07 10:49
 */
public class GroupChatMessageNotice implements IPacket {

    public static final transient short PROTOCOL_ID = 19006;

    private long groupId;
    private long channelId;

    private ChatMessage chatMessage;

    public static GroupChatMessageNotice valueOf(long groupId, long channelId, ChatMessage chatMessage) {
        var notice = new GroupChatMessageNotice();
        notice.groupId = groupId;
        notice.channelId = channelId;
        notice.chatMessage = chatMessage;
        return notice;
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

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
}
