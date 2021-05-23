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

package com.zfoo.app.zapp.common.entity.user;

import com.zfoo.app.zapp.common.entity.user.model.GroupTimePO;
import com.zfoo.app.zapp.common.entity.user.model.SettingPO;
import com.zfoo.orm.model.anno.EntityCache;
import com.zfoo.orm.model.anno.Id;
import com.zfoo.orm.model.anno.Index;
import com.zfoo.orm.model.anno.Persister;
import com.zfoo.orm.model.entity.IEntity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-07 21:22
 */
@EntityCache(cacheStrategy = "tenThousand", persister = @Persister("time30s"))
public class UserEntity implements IEntity<Long> {

    @Id
    private long id;

    private long vs;

    private byte adminAuth;

    @Index(ascending = true, unique = false)
    private String name;

    private long coin;
    /**
     * 主观获赞数量，属于开放知识的点赞量
     */
    private long free;
    /**
     * 客观获赞数量，非开放知识的点赞量
     */
    private long normal;

    /**
     * 上一次的登录时间
     */
    private long loginTime;

    /**
     * 头像的url
     */
    private String avatar;
    private String background;

    /**
     * 性别：-1女，0未知，1男
     */
    private byte gender;

    private String signature;
    /**
     * 自定义状态的过期时间
     */
    private long customTime;
    private String custom;

    /**
     * 粉丝数量
     */
    private int fanNum;

    private SettingPO setting = new SettingPO();

    /**
     * 关注了哪些相关的地点标签
     */
    private List<Long> locations = new CopyOnWriteArrayList<>();

    /**
     * 关注了哪些相关的人标签
     */
    private List<Long> persons = new CopyOnWriteArrayList<>();

    /**
     * 关注了哪些相关的物标签
     */
    private List<Long> items = new CopyOnWriteArrayList<>();


    /**
     * 关注的人
     */
    private List<Long> follows = new CopyOnWriteArrayList<>();

    /**
     * 粉丝标签
     */
    private List<Long> fans = new CopyOnWriteArrayList<>();

    /**
     * 收藏的时间片
     */
    private List<Long> stars = new CopyOnWriteArrayList<>();


    /**
     * 好友列表，这个其实可以单独拿出一个服务来做，但是目前这样更加的方便一点
     */
    private List<Long> friends = new CopyOnWriteArrayList<>();
    /**
     * 黑名单
     */
    private List<Long> blacklist = new CopyOnWriteArrayList<>();

    private List<GroupTimePO> groups = new CopyOnWriteArrayList<>();

    public static UserEntity valueOf(long id, String name) {
        var entity = new UserEntity();
        entity.id = id;
        entity.name = name;
        return entity;
    }


    @Override
    public Long id() {
        return id;
    }

    @Override
    public long gvs() {
        return vs;
    }

    @Override
    public void svs(long vs) {
        this.vs = vs;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVs() {
        return vs;
    }

    public void setVs(long vs) {
        this.vs = vs;
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

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
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

    public byte getGender() {
        return gender;
    }

    public void setGender(byte gender) {
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

    public int getFanNum() {
        return fanNum;
    }

    public void setFanNum(int fanNum) {
        this.fanNum = fanNum;
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

    public SettingPO getSetting() {
        return setting;
    }

    public void setSetting(SettingPO setting) {
        this.setting = setting;
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

    public List<Long> getStars() {
        return stars;
    }

    public void setStars(List<Long> stars) {
        this.stars = stars;
    }

    public List<Long> getFollows() {
        return follows;
    }

    public void setFollows(List<Long> follows) {
        this.follows = follows;
    }

    public List<Long> getFans() {
        return fans;
    }

    public void setFans(List<Long> fans) {
        this.fans = fans;
    }

    public List<Long> getFriends() {
        return friends;
    }

    public void setFriends(List<Long> friends) {
        this.friends = friends;
    }

    public List<Long> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<Long> blacklist) {
        this.blacklist = blacklist;
    }

    public List<GroupTimePO> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupTimePO> groups) {
        this.groups = groups;
    }
}
