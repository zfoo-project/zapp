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

package com.zfoo.app.zapp.common.entity.common;


/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-17 11:43
 */
public class MessagePO {

    /**
     * 消息的唯一id
     */
    private long id;

    /**
     * 消息的类型
     */
    private byte type;

    /**
     * 发送者的id
     */
    private long sendId;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 接收的人是否已读
     */
    private boolean read;

    /**
     * 发送的时间戳
     */
    private long timestamp;


    public static MessagePO valueOf(byte type, long sendId, String message, long timestamp) {
        var messagePO = new MessagePO();
        messagePO.type = type;
        messagePO.sendId = sendId;
        messagePO.message = message;
        messagePO.timestamp = timestamp;
        return messagePO;
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
