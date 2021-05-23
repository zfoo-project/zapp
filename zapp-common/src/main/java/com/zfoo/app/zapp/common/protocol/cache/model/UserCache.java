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

package com.zfoo.app.zapp.common.protocol.cache.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.zfoo.net.packet.common.PairLS;
import com.zfoo.protocol.IPacket;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-08 11:00
 */
public class UserCache implements IPacket {

    public static final transient short PROTOCOL_ID = 3000;

    @JsonSerialize(using = ToStringSerializer.class)
    private long id;
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
    private int fanNum;
    private int followNum;
    private int starNum;
    @JsonSerialize(using = ToStringSerializer.class)
    private long free;
    @JsonSerialize(using = ToStringSerializer.class)
    private long normal;
    private List<PairLS> locations;
    private List<PairLS> persons;
    private List<PairLS> items;

    public static UserCache valueOf(long id, String name) {
        var cache = new UserCache();
        cache.id = id;
        cache.name = name;
        return cache;
    }

    public static UserCache valueOf(long id, String name, long coin, String avatar, String background, int gender, String signature
            , long customTime, String custom
            , int followNum, int fanNum, int starNum, long free, long normal
            , List<PairLS> locations, List<PairLS> persons, List<PairLS> items) {
        var cache = new UserCache();
        cache.id = id;
        cache.name = name;
        cache.coin = coin;
        cache.avatar = avatar;
        cache.background = background;
        cache.gender = gender;
        cache.signature = signature;
        cache.customTime = customTime;
        cache.custom = custom;
        cache.followNum = followNum;
        cache.fanNum = fanNum;
        cache.starNum = starNum;
        cache.free = free;
        cache.normal = normal;
        cache.locations = locations;
        cache.persons = persons;
        cache.items = items;
        return cache;
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
}
