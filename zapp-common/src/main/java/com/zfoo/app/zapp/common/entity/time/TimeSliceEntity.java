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

package com.zfoo.app.zapp.common.entity.time;

import com.zfoo.app.zapp.common.entity.time.model.LoveTrendPO;
import com.zfoo.app.zapp.common.entity.time.model.TimeKeyPO;
import com.zfoo.app.zapp.common.entity.time.model.TimeLinkAlbumPO;
import com.zfoo.app.zapp.common.entity.time.model.VideoLinkPO;
import com.zfoo.orm.model.anno.EntityCache;
import com.zfoo.orm.model.anno.Id;
import com.zfoo.orm.model.anno.Index;
import com.zfoo.orm.model.anno.Persister;
import com.zfoo.orm.model.entity.IEntity;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.StringUtils;

import java.util.List;

/**
 * objective time slice
 * 客观时间片
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-01-27 21:22
 */
@EntityCache(cacheStrategy = "tenThousand", persister = @Persister("time30s"))
public class TimeSliceEntity implements IEntity<Long> {

    @Id
    private long id;
    private long vs;
    @Index(ascending = true, unique = false)
    private long userId;

    /**
     * 点赞喜爱数量
     */
    private long love;
    private LoveTrendPO trend;

    private int type;

    /**
     * 时间片的创建，开始，结束时间
     */
    private long create;
    private long start;
    private long end;

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

    private String content;
    private List<String> images;
    private VideoLinkPO video;
    private TimeKeyPO key;
    private List<TimeLinkAlbumPO> albums;

    public static TimeSliceEntity valueOf(long id, long userId, long create, int type, long start, long end
            , List<Long> locations, List<Long> persons, List<Long> items, String content, List<String> images
            , VideoLinkPO video, TimeKeyPO key, List<TimeLinkAlbumPO> albums) {
        var entity = new TimeSliceEntity();
        entity.id = id;
        entity.userId = userId;
        entity.create = create;
        entity.type = type;
        entity.start = start;
        entity.end = end;
        entity.locations = CollectionUtils.isEmpty(locations) ? null : locations;
        entity.persons = CollectionUtils.isEmpty(persons) ? null : persons;
        entity.items = CollectionUtils.isEmpty(items) ? null : items;
        entity.content = StringUtils.isBlank(content) ? null : content.trim();
        entity.images = CollectionUtils.isEmpty(images) ? null : images;
        entity.video = video;
        entity.key = key;
        entity.albums = CollectionUtils.isEmpty(albums) ? null : albums;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getLove() {
        return love;
    }

    public void setLove(long love) {
        this.love = love;
    }

    public LoveTrendPO getTrend() {
        return trend;
    }

    public void setTrend(LoveTrendPO trend) {
        this.trend = trend;
    }

    public long getCreate() {
        return create;
    }

    public void setCreate(long create) {
        this.create = create;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
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
