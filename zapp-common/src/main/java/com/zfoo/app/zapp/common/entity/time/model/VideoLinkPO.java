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

import com.zfoo.protocol.util.StringUtils;

/**
 * 时间片的视频链接
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-18 12:06
 */
public class VideoLinkPO {

    /**
     * 视频封面
     */
    private String poster;
    /**
     * 视频url地址
     */
    private String url;

    public static VideoLinkPO valueOf(String poster, String url) {
        var po = new VideoLinkPO();
        po.poster = poster;
        po.url = url;
        return po;
    }

    public void trim() {
        this.poster = StringUtils.trim(this.poster);
        this.url = StringUtils.trim(this.url);
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
