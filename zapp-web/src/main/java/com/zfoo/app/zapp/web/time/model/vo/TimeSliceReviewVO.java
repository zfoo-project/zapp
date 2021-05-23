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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.zfoo.app.zapp.common.model.time.TimeSliceVO;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-19 15:17
 */
public class TimeSliceReviewVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private long userId;

    private TimeSliceVO timeSlice;

    public static TimeSliceReviewVO valueOf(long id, long userId, TimeSliceVO timeSlice) {
        var vo = new TimeSliceReviewVO();
        vo.id = id;
        vo.userId = userId;
        vo.timeSlice = timeSlice;
        return vo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public TimeSliceVO getTimeSlice() {
        return timeSlice;
    }

    public void setTimeSlice(TimeSliceVO timeSlice) {
        this.timeSlice = timeSlice;
    }
}
