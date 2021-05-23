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

package com.zfoo.app.zapp.common.model.time;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.zfoo.app.zapp.common.entity.time.model.TimeKeyPO;
import com.zfoo.app.zapp.common.entity.time.model.TimeLinkAlbumPO;
import com.zfoo.app.zapp.common.entity.time.model.VideoLinkPO;
import com.zfoo.app.zapp.common.protocol.cache.model.UserCache;
import com.zfoo.net.packet.common.PairLS;

import java.util.List;
import java.util.Objects;

/**
 * 时间片
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-24 15:37
 */
public class TimeSliceVO {

    /**
     * 用户信息
     */
    private UserCache userInfo;

    /**
     * 时间片的ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private long id;

    /**
     * 获赞的数量
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private long love;

    /**
     * 时间片的类型
     */
    private int type;

    /**
     * 时间片的开始时间
     */
    private String start;
    /**
     * 时间片的结束时间
     */
    private String end;

    /**
     * 相关的地点tag
     */
    private List<PairLS> locations;

    /**
     * 相关的人tag
     */
    private List<PairLS> persons;

    /**
     * 相关的物tag
     */
    private List<PairLS> items;

    /**
     * 时间片的内容
     */
    private String content;

    /**
     * 图片链接
     */
    private List<String> images;

    /**
     * 视频链接
     */
    private VideoLinkPO video;

    /**
     * 键值对
     */
    private TimeKeyPO key;

    /**
     * 链接的时间片
     */
    private List<TimeLinkAlbumPO> albums;

    public static TimeSliceVO valueOf(UserCache userInfo, long id, String content) {
        var vo = new TimeSliceVO();
        vo.userInfo = userInfo;
        vo.id = id;
        vo.content = content;
        return vo;
    }

    public static TimeSliceVO valueOf(UserCache userInfo, long id, long love, int type, String start, String end
            , List<PairLS> locations, List<PairLS> persons, List<PairLS> items
            , String content, List<String> images, VideoLinkPO video, TimeKeyPO key, List<TimeLinkAlbumPO> albums) {
        var vo = new TimeSliceVO();
        vo.userInfo = userInfo;
        vo.id = id;
        vo.love = love;
        vo.type = type;
        vo.start = start;
        vo.end = end;
        vo.locations = locations;
        vo.persons = persons;
        vo.items = items;
        vo.content = content;
        vo.images = images;
        vo.video = video;
        vo.key = key;
        vo.albums = albums;
        return vo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimeSliceVO that = (TimeSliceVO) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public UserCache getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserCache userInfo) {
        this.userInfo = userInfo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLove() {
        return love;
    }

    public void setLove(long love) {
        this.love = love;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public VideoLinkPO getVideo() {
        return video;
    }

    public void setVideo(VideoLinkPO video) {
        this.video = video;
    }

    public TimeKeyPO getKey() {
        return key;
    }

    public void setKey(TimeKeyPO key) {
        this.key = key;
    }

    public List<TimeLinkAlbumPO> getAlbums() {
        return albums;
    }

    public void setAlbums(List<TimeLinkAlbumPO> albums) {
        this.albums = albums;
    }
}
