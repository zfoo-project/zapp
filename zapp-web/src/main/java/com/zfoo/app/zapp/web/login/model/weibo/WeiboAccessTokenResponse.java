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

package com.zfoo.app.zapp.web.login.model.weibo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-01-14 14:01
 */
public class WeiboAccessTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("remind_in")
    private String remindIn;

    @JsonProperty("expires_in")
    private String expiresIn;

    @JsonProperty("uid")
    private String uid;

    @JsonProperty("isRealName")
    private String isRealName;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRemindIn() {
        return remindIn;
    }

    public void setRemindIn(String remindIn) {
        this.remindIn = remindIn;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIsRealName() {
        return isRealName;
    }

    public void setIsRealName(String isRealName) {
        this.isRealName = isRealName;
    }
}
