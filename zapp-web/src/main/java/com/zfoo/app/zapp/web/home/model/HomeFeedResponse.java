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

package com.zfoo.app.zapp.web.home.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.zfoo.app.zapp.common.model.time.TimeSliceVO;

import java.util.List;

/**
 * 首页推荐内容请求的返回
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-26 11:48
 */
public class HomeFeedResponse {

    /**
     * 推荐种子
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private long feed;

    private boolean newFeed;

    /**
     * 时间片数组
     */
    private List<TimeSliceVO> timeArrays;

    public static HomeFeedResponse valueOf(long feed, boolean newFeed, List<TimeSliceVO> timeArrays) {
        var response = new HomeFeedResponse();
        response.feed = feed;
        response.newFeed = newFeed;
        response.timeArrays = timeArrays;
        return response;
    }

    public long getFeed() {
        return feed;
    }

    public void setFeed(long feed) {
        this.feed = feed;
    }

    public boolean isNewFeed() {
        return newFeed;
    }

    public void setNewFeed(boolean newFeed) {
        this.newFeed = newFeed;
    }

    public List<TimeSliceVO> getTimeArrays() {
        return timeArrays;
    }

    public void setTimeArrays(List<TimeSliceVO> timeArrays) {
        this.timeArrays = timeArrays;
    }
}
