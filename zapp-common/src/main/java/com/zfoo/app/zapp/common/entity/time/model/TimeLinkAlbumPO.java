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

package com.zfoo.app.zapp.common.entity.time.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.zfoo.protocol.util.StringUtils;

import java.util.List;

/**
 * 相关的时间片
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-25 13:12
 */
public class TimeLinkAlbumPO {

    /**
     * 专辑名称
     */
    private String album;

    /**
     * 相关的时间片id
     */
    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<Long> links;

    public static TimeLinkAlbumPO valueOf(String album, List<Long> links) {
        var instance = new TimeLinkAlbumPO();
        instance.album = album;
        instance.links = links;
        return instance;
    }

    public void trim() {
        this.album = StringUtils.trim(this.album);
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public List<Long> getLinks() {
        return links;
    }

    public void setLinks(List<Long> links) {
        this.links = links;
    }
}
