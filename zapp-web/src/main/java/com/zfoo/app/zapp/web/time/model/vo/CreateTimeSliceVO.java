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

package com.zfoo.app.zapp.web.time.model.vo;


import com.zfoo.app.zapp.common.entity.time.model.TimeKeyPO;
import com.zfoo.app.zapp.common.entity.time.model.TimeLinkAlbumPO;
import com.zfoo.app.zapp.common.entity.time.model.VideoLinkPO;

import java.util.List;

/**
 * 用户创建时间片的请求
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-01-27 21:22
 */
public class CreateTimeSliceVO {

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
     * 相关的地点
     */
    private List<Long> locations;
    /**
     * 相关的人
     */
    private List<Long> persons;
    /**
     * 相关的物
     */
    private List<Long> items;
    /**
     * 不存在的物
     */
    private List<String> notExistItems;

    /**
     * 时间片的内容
     */
    private String content;
    /**
     * 图片链接
     */
    private List<String> images;
    /**
     * 诗篇链接
     */
    private VideoLinkPO video;
    /**
     * 键值对
     */
    private TimeKeyPO key;
    /**
     * 相关的时间片
     */
    private List<TimeLinkAlbumPO> albums;

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

    public List<String> getNotExistItems() {
        return notExistItems;
    }

    public void setNotExistItems(List<String> notExistItems) {
        this.notExistItems = notExistItems;
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
