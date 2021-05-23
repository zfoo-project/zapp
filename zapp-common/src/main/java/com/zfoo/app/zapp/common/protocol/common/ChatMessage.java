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

package com.zfoo.app.zapp.common.protocol.common;

import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-17 11:43
 */
public class ChatMessage implements IPacket {

    public static final transient short PROTOCOL_ID = 120;

    private long id;

    private byte type;
    /**
     * 发送者的id
     */
    private long sendId;

    private String avatar;

    private String name;

    /**
     * 消息内容
     */
    private String message;

    private boolean read;

    /**
     * 发送的时间戳
     */
    private long timestamp;

    public static ChatMessage valueOf(long id, byte type, long sendId, String avatar, String name, String message, boolean read, long timestamp) {
        var chatMessage = new ChatMessage();
        chatMessage.id = id;
        chatMessage.type = type;
        chatMessage.sendId = sendId;
        chatMessage.avatar = avatar;
        chatMessage.name = name;
        chatMessage.message = message;
        chatMessage.read = read;
        chatMessage.timestamp = timestamp;
        return chatMessage;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public long getSendId() {
        return sendId;
    }

    public void setSendId(long sendId) {
        this.sendId = sendId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
