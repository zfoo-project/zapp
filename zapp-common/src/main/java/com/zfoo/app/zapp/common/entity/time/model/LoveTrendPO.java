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

import com.zfoo.scheduler.util.TimeUtils;

import java.util.Date;

/**
 * 最近七天的每天点赞数量
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-16 11:54
 */
public class LoveTrendPO {

    /**
     * 刷新时间
     */
    private long refresh;

    /**
     * 第一天的点赞数
     */
    private long d1;

    /**
     * 第二天的点赞数
     */
    private long d2;

    /**
     * 第三天的点赞数
     */
    private long d3;

    private long d4;

    private long d5;

    private long d6;

    private long d7;

    /**
     * 超过7天的热搜数量，全部放入d0
     */
    private long d0;

    public static LoveTrendPO valueOf(long refresh) {
        var trend = new LoveTrendPO();
        trend.refresh = refresh;
        return trend;
    }

    public void addCount(long num) {
        var now = TimeUtils.now();
        if (TimeUtils.isSameDay(new Date(now), new Date(refresh))) {
            this.d1 = this.d1 + num;
        } else {
            this.refresh = now;

            d0 = d0 + d7;
            d7 = d6;
            d6 = d5;
            d5 = d4;
            d4 = d3;
            d3 = d2;
            d2 = d1;
            d1 = num;
        }
    }

    public long score() {
        return d1 * 100 + d2 * 80 + d3 * 64 + d4 * 51 + d5 * 41 + d6 * 33 + d7 * 26 + d0 * 21;
    }

    public long getRefresh() {
        return refresh;
    }

    public void setRefresh(long refresh) {
        this.refresh = refresh;
    }

    public long getD1() {
        return d1;
    }

    public void setD1(long d1) {
        this.d1 = d1;
    }

    public long getD2() {
        return d2;
    }

    public void setD2(long d2) {
        this.d2 = d2;
    }

    public long getD3() {
        return d3;
    }

    public void setD3(long d3) {
        this.d3 = d3;
    }

    public long getD4() {
        return d4;
    }

    public void setD4(long d4) {
        this.d4 = d4;
    }

    public long getD5() {
        return d5;
    }

    public void setD5(long d5) {
        this.d5 = d5;
    }

    public long getD6() {
        return d6;
    }

    public void setD6(long d6) {
        this.d6 = d6;
    }

    public long getD7() {
        return d7;
    }

    public void setD7(long d7) {
        this.d7 = d7;
    }

    public long getD0() {
        return d0;
    }

    public void setD0(long d0) {
        this.d0 = d0;
    }
}
