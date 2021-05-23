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

package com.zfoo.app.zapp.web.login.model;

/**
 * 绑定返回
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-30 22:07
 */
public class BindResponse {

    /**
     * 验证的key
     */
    private String verifyKey;

    public static BindResponse valueOf(String verifyKey) {
        var response = new BindResponse();
        response.verifyKey = verifyKey;
        return response;
    }

    public String getVerifyKey() {
        return verifyKey;
    }

    public void setVerifyKey(String verifyKey) {
        this.verifyKey = verifyKey;
    }
}
