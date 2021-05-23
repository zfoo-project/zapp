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

package com.zfoo.app.zapp.common.protocol.user.info;

import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-10-15 17:55
 */
public class UpdateSettingAsk implements IPacket {

    public static final transient short PROTOCOL_ID = 1034;

    private long userId;
    private byte theme;
    private byte language;

    public static UpdateSettingAsk valueOf(long userId, byte theme, byte language) {
        var ask = new UpdateSettingAsk();
        ask.userId = userId;
        ask.theme = theme;
        ask.language = language;
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

    public byte getTheme() {
        return theme;
    }

    public void setTheme(byte theme) {
        this.theme = theme;
    }

    public byte getLanguage() {
        return language;
    }

    public void setLanguage(byte language) {
        this.language = language;
    }
}
