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
public class AccountSecurityAnswer implements IPacket {

    public static final transient short PROTOCOL_ID = 1025;

    /**
     * 小于等于0时，表示没有绑定手机
     */
    private long phoneNumber;

    /**
     * 是否绑定了危险
     */
    private boolean weChatBind;

    /**
     * 是否绑定了微博
     */
    private boolean weiBoBind;

    /**
     * 是否设置密码
     */
    private boolean passwordSet;

    public static AccountSecurityAnswer valueOf(long phoneNumber, boolean weChatBind, boolean weiBoBind, boolean passwordSet) {
        var answer = new AccountSecurityAnswer();
        answer.phoneNumber = phoneNumber;
        answer.weChatBind = weChatBind;
        answer.weiBoBind = weiBoBind;
        answer.passwordSet = passwordSet;
        return answer;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isWeChatBind() {
        return weChatBind;
    }

    public void setWeChatBind(boolean weChatBind) {
        this.weChatBind = weChatBind;
    }

    public boolean isWeiBoBind() {
        return weiBoBind;
    }

    public void setWeiBoBind(boolean weiBoBind) {
        this.weiBoBind = weiBoBind;
    }

    public boolean isPasswordSet() {
        return passwordSet;
    }

    public void setPasswordSet(boolean passwordSet) {
        this.passwordSet = passwordSet;
    }
}
