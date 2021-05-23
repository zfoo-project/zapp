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

package com.zfoo.app.zapp.common.protocol.user.fan;

import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-13 18:41
 */
public class SubscribeItemAsk implements IPacket {

    public static final transient short PROTOCOL_ID = 1201;

    private long userId;
    private long itemId;

    public static SubscribeItemAsk valueOf(long userId, long itemId) {
        var ask = new SubscribeItemAsk();
        ask.userId = userId;
        ask.itemId = itemId;
        return ask;
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

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }
}
