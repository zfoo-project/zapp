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

package com.zfoo.app.zapp.common.protocol.user.time;

import com.zfoo.app.zapp.common.protocol.common.time.TimeKeyVO;
import com.zfoo.app.zapp.common.protocol.common.time.TimeLinkAlbumVO;
import com.zfoo.app.zapp.common.protocol.common.time.VideoLinkVO;
import com.zfoo.protocol.IPacket;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-16 11:16
 */
public class UpdateTimeSliceAsk implements IPacket {

    public static final transient short PROTOCOL_ID = 1104;

    private long id;
    private int type;
    private long start;
    private long end;

    private List<Long> locations;
    private List<Long> persons;
    private List<Long> items;

    private String content;
    private List<String> images;
    private VideoLinkVO video;
    private TimeKeyVO key;
    private List<TimeLinkAlbumVO> albums;

    public static UpdateTimeSliceAsk valueOf(long id, int type, long start, long end, List<Long> locations, List<Long> persons
            , List<Long> items, String content, List<String> images, VideoLinkVO video, TimeKeyVO key, List<TimeLinkAlbumVO> albums) {
        var ask = new UpdateTimeSliceAsk();
        ask.id = id;
        ask.type = type;
        ask.start = start;
        ask.end = end;
        ask.locations = locations;
        ask.persons = persons;
        ask.items = items;
        ask.content = content;
        ask.images = images;
        ask.video = video;
        ask.key = key;
        ask.albums = albums;
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

    public VideoLinkVO getVideo() {
        return video;
    }

    public void setVideo(VideoLinkVO video) {
        this.video = video;
    }

    public TimeKeyVO getKey() {
        return key;
    }

    public void setKey(TimeKeyVO key) {
        this.key = key;
    }

    public List<TimeLinkAlbumVO> getAlbums() {
        return albums;
    }

    public void setAlbums(List<TimeLinkAlbumVO> albums) {
        this.albums = albums;
    }
}
