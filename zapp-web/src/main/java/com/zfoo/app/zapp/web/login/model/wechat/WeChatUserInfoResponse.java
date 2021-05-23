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

package com.zfoo.app.zapp.web.login.model.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zfoo.app.zapp.common.protocol.user.wechat.WeChatUserInfoAsk;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-01-14 14:01
 */
public class WeChatUserInfoResponse {

    @JsonProperty("unionid")
    private String unionid;

    @JsonProperty("openid")
    private String openid;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("sex")
    private int sex;

    @JsonProperty("language")
    private String language;

    @JsonProperty("city")
    private String city;

    @JsonProperty("province")
    private String province;

    @JsonProperty("country")
    private String country;

    @JsonProperty("headimgurl")
    private String headImgUrl;

    @JsonProperty("privilege")
    private String[] privilege;

    public WeChatUserInfoAsk toWeChatUserInfoAsk(long uid) {
        return WeChatUserInfoAsk.valueOf(unionid, uid, openid, nickname, sex, language, city, province, country, headImgUrl, privilege);
    }


    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String[] getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String[] privilege) {
        this.privilege = privilege;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
}
