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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zfoo.app.zapp.common.protocol.user.weibo.WeiBoUserInfoAsk;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-01-14 14:01
 */
//@JsonIgnoreProperties({"id", "class", "screen_name", "province", "city", "description", "url"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeiboUserInfoResponse {

    @JsonProperty("idstr")
    private String idstr;

    @JsonProperty("name")
    private String name;

    @JsonProperty("location")
    private String location;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("lang")
    private String language;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    public WeiBoUserInfoAsk toWeiboUserInfoAsk(long uid) {
        return WeiBoUserInfoAsk.valueOf(idstr, uid, name, location, gender, language, profileImageUrl);
    }

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
