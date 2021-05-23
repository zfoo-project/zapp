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
 * 保存手机号码请求
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-30 22:22
 */
public class SavePhoneNumberRequest {

    /**
     * 手机号码
     */
    private long phoneNumber;

    /**
     * 验证码
     */
    private long phoneCode;

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(long phoneCode) {
        this.phoneCode = phoneCode;
    }

}
