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

package com.zfoo.app.zapp.web.user.model;

/**
 * 用户安全信息返回
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-27 15:02
 */
public class AccountSecurityResponse {

    /**
     * 电话号码
     */
    private String phoneNumber;

    /**
     * 微信绑定状态
     */
    private boolean weChatBind;

    /**
     * 微博绑定状态
     */
    private boolean weiBoBind;

    /**
     * 密码设置状态
     */
    private boolean passwordSet;

    public static AccountSecurityResponse valueOf(String phoneNumber, boolean weChatBind, boolean weiBoBind, boolean passwordSet) {
        var response = new AccountSecurityResponse();
        response.phoneNumber = phoneNumber;
        response.weChatBind = weChatBind;
        response.weiBoBind = weiBoBind;
        response.passwordSet = passwordSet;
        return response;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
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
