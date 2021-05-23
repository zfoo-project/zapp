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

package com.zfoo.app.zapp.common.protocol.user.wechat;

import com.zfoo.protocol.IPacket;


/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-10-15 17:55
 */
public class WeChatUserInfoAsk implements IPacket {

    public static final transient short PROTOCOL_ID = 1062;

    private String unionid;

    private long uid;

    private String openid;

    private String nickname;

    private int sex;

    private String language;

    private String city;

    private String province;

    private String country;

    private String headImgUrl;

    private String[] privilege;

    public static WeChatUserInfoAsk valueOf(String unionid, long uid, String openid, String nickname, int sex
            , String language, String city, String province, String country, String headImgUrl, String[] privilege) {
        var ask = new WeChatUserInfoAsk();
        ask.unionid = unionid;
        ask.uid = uid;
        ask.openid = openid;
        ask.nickname = nickname;
        ask.sex = sex;
        ask.language = language;
        ask.city = city;
        ask.province = province;
        ask.country = country;
        ask.headImgUrl = headImgUrl;
        ask.privilege = privilege;
        return ask;
    }


    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }


    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
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
}
