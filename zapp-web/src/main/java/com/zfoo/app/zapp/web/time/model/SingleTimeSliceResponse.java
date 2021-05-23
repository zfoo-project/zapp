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

package com.zfoo.app.zapp.web.time.model;

import com.zfoo.app.zapp.common.model.time.TimeSliceVO;
import com.zfoo.scheduler.util.TimeUtils;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-19 17:26
 */
public class SingleTimeSliceResponse {

    private String now;

    private TimeSliceVO rootTimeSlice;

    /**
     * 关联时间片
     */
    private List<TimeSliceVO> dimensionTimeSliceList;

    public static SingleTimeSliceResponse valueOf(TimeSliceVO rootTimeSlice, List<TimeSliceVO> dimensionTimeSliceList) {
        var response = new SingleTimeSliceResponse();
        response.rootTimeSlice = rootTimeSlice;
        response.dimensionTimeSliceList = dimensionTimeSliceList;
        response.now = TimeUtils.timeToString(TimeUtils.now());
        return response;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public TimeSliceVO getRootTimeSlice() {
        return rootTimeSlice;
    }

    public void setRootTimeSlice(TimeSliceVO rootTimeSlice) {
        this.rootTimeSlice = rootTimeSlice;
    }

    public List<TimeSliceVO> getDimensionTimeSliceList() {
        return dimensionTimeSliceList;
    }

    public void setDimensionTimeSliceList(List<TimeSliceVO> dimensionTimeSliceList) {
        this.dimensionTimeSliceList = dimensionTimeSliceList;
    }
}
