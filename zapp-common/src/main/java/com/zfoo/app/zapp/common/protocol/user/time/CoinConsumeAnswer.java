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

package com.zfoo.app.zapp.common.protocol.user.time;

import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.net.packet.common.Message;
import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-16 11:16
 */
public class CoinConsumeAnswer implements IPacket {

    public static final transient short PROTOCOL_ID = 1109;

    private Message message;

    /**
     * 剩余z币
     */
    private long coin;

    public static CoinConsumeAnswer valueOf(CodeEnum codeEnum) {
        var ask = new CoinConsumeAnswer();
        ask.message = Message.valueOf(ask, codeEnum.getCode());
        return ask;
    }

    public static CoinConsumeAnswer valueOf(CodeEnum codeEnum, long coin) {
        var ask = valueOf(codeEnum);
        ask.coin = coin;
        return ask;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public long getCoin() {
        return coin;
    }

    public void setCoin(long coin) {
        this.coin = coin;
    }
}
