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

package com.zfoo.app.zapp.web.coin.model;


import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-23 19:11
 */
public class DownloadTimeSliceResponse {

    /**
     * 时间片的id
     */
    private long id;

    private long love;

    private long coin;

    private List<String> downloadUrls;

    public static DownloadTimeSliceResponse valueOf(long id, long love, long coin, List<String> downloadUrls) {
        var response = new DownloadTimeSliceResponse();
        response.id = id;
        response.love = love;
        response.coin = coin;
        response.downloadUrls = downloadUrls;
        return response;
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

    public long getCoin() {
        return coin;
    }

    public void setCoin(long coin) {
        this.coin = coin;
    }

    public List<String> getDownloadUrls() {
        return downloadUrls;
    }

    public void setDownloadUrls(List<String> downloadUrls) {
        this.downloadUrls = downloadUrls;
    }
}
