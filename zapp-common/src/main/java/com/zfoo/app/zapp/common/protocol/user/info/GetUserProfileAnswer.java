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

import com.zfoo.app.zapp.common.protocol.user.info.model.SettingVO;
import com.zfoo.protocol.IPacket;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-12-25 17:11
 */
public class GetUserProfileAnswer implements IPacket {

    public static final transient short PROTOCOL_ID = 1021;

    private long id;
    private byte adminAuth;
    private String name;
    private long coin;
    private String avatar;
    private String background;
    private int gender;
    private String signature;
    private long customTime;
    private String custom;
    private int followNum;
    private int fanNum;
    private int starNum;
    private long free;
    private long normal;
    private List<Long> follows;
    private List<Long> stars;
    private List<Long> locations;
    private List<Long> persons;
    private List<Long> items;
    private SettingVO setting;

    public static GetUserProfileAnswer valueOf(long id, byte adminAuth, String name, long coin
            , String avatar, String background, int gender
            , String signature, long customTime, String custom
            , int followNum, int fanNum, int starNum, long free, long normal
            , List<Long> follows, List<Long> stars
            , List<Long> locations, List<Long> persons, List<Long> items, SettingVO setting) {
        var ask = new GetUserProfileAnswer();
        ask.id = id;
        ask.adminAuth = adminAuth;
        ask.name = name;
        ask.coin = coin;
        ask.avatar = avatar;
        ask.background = background;
        ask.gender = gender;
        ask.signature = signature;
        ask.customTime = customTime;
        ask.custom = custom;
        ask.followNum = followNum;
        ask.fanNum = fanNum;
        ask.starNum = starNum;
        ask.free = free;
        ask.normal = normal;
        ask.follows = follows;
        ask.stars = stars;
        ask.locations = locations;
        ask.persons = persons;
        ask.items = items;
        ask.setting = setting;
        return ask;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte getAdminAuth() {
        return adminAuth;
    }

    public void setAdminAuth(byte adminAuth) {
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

    public List<Long> getLocations() {
        return locations;
    }

    public void setLocations(List<Long> locations) {
        this.locations = locations;
    }

    public List<Long> getPersons() {
        return persons;
    }

    public void setPersons(List<Long> persons) {
        this.persons = persons;
    }

    public List<Long> getItems() {
        return items;
    }

    public void setItems(List<Long> items) {
        this.items = items;
    }

    public SettingVO getSetting() {
        return setting;
    }

    public void setSetting(SettingVO setting) {
        this.setting = setting;
    }
}
