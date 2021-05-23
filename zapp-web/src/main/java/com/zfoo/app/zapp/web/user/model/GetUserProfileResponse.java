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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.zfoo.app.zapp.common.protocol.user.info.model.SettingVO;
import com.zfoo.net.packet.common.PairLS;

import java.util.List;

/**
 * 用户的基本信息返回
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-03 16:35
 */
public class GetUserProfileResponse {

    @JsonSerialize(using = ToStringSerializer.class)
    private long id;
    private int adminAuth;
    private String name;
    @JsonSerialize(using = ToStringSerializer.class)
    private long coin;
    private String avatar;
    private String background;
    private int gender;
    private String signature;
    @JsonSerialize(using = ToStringSerializer.class)
    private long customTime;
    private String custom;
    private int followNum;
    private int fanNum;
    private int starNum;
    @JsonSerialize(using = ToStringSerializer.class)
    private long free;
    @JsonSerialize(using = ToStringSerializer.class)
    private long normal;
    private List<PairLS> locations;
    private List<PairLS> persons;
    private List<PairLS> items;
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<Long> follows;
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<Long> stars;
    private SettingVO setting;

    public static GetUserProfileResponse valueOf(long id, int adminAuth, String name, long coin, String avatar, String background
            , int gender, String signature, long customTime, String custom
            , int followNum, int fanNum, int starNum, long free, long normal
            , List<Long> follows, List<Long> stars
            , List<PairLS> locations, List<PairLS> persons, List<PairLS> items, SettingVO setting) {
        var response = new GetUserProfileResponse();
        response.id = id;
        response.adminAuth = adminAuth;
        response.name = name;
        response.coin = coin;
        response.avatar = avatar;
        response.background = background;
        response.gender = gender;
        response.signature = signature;
        response.customTime = customTime;
        response.custom = custom;
        response.followNum = followNum;
        response.fanNum = fanNum;
        response.starNum = starNum;
        response.free = free;
        response.normal = normal;
        response.follows = follows;
        response.stars = stars;
        response.locations = locations;
        response.persons = persons;
        response.items = items;
        response.setting = setting;
        return response;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAdminAuth() {
        return adminAuth;
    }

    public void setAdminAuth(int adminAuth) {
        this.adminAuth = adminAuth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCoin() {
        return coin;
    }

    public void setCoin(long coin) {
        this.coin = coin;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public long getCustomTime() {
        return customTime;
    }

    public void setCustomTime(long customTime) {
        this.customTime = customTime;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public int getFollowNum() {
        return followNum;
    }

    public void setFollowNum(int followNum) {
        this.followNum = followNum;
    }

    public int getFanNum() {
        return fanNum;
    }

    public void setFanNum(int fanNum) {
        this.fanNum = fanNum;
    }

    public int getStarNum() {
        return starNum;
    }

    public void setStarNum(int starNum) {
        this.starNum = starNum;
    }

    public long getFree() {
        return free;
    }

    public void setFree(long free) {
        this.free = free;
    }

    public long getNormal() {
        return normal;
    }

    public void setNormal(long normal) {
        this.normal = normal;
    }

    public List<PairLS> getLocations() {
        return locations;
    }

    public void setLocations(List<PairLS> locations) {
        this.locations = locations;
    }

    public List<PairLS> getPersons() {
        return persons;
    }

    public void setPersons(List<PairLS> persons) {
        this.persons = persons;
    }

    public List<PairLS> getItems() {
        return items;
    }

    public void setItems(List<PairLS> items) {
        this.items = items;
    }

    public List<Long> getFollows() {
        return follows;
    }

    public void setFollows(List<Long> follows) {
        this.follows = follows;
    }

    public List<Long> getStars() {
        return stars;
    }

    public void setStars(List<Long> stars) {
        this.stars = stars;
    }

    public SettingVO getSetting() {
        return setting;
    }

    public void setSetting(SettingVO setting) {
        this.setting = setting;
    }
}
