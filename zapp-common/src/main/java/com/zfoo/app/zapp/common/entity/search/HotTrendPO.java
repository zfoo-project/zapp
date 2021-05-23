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

package com.zfoo.app.zapp.common.entity.search;

import com.zfoo.scheduler.util.TimeUtils;

import java.util.Date;

/**
 * 热搜趋势
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-16 11:54
 */
public class HotTrendPO {

    /**
     * 刷新时间
     */
    private long refresh;

    /**
     * 第一天的热搜数量
     */
    private int d1;

    /**
     * 第二天的热搜数量
     */
    private int d2;

    /**
     * 第三天的热搜数量
     */
    private int d3;

    private int d4;

    private int d5;

    private int d6;

    private int d7;

    private int d8;

    private int d9;

    private int d10;

    private int d11;

    private int d12;

    private int d13;

    /**
     * 超过13天的热搜数量，全部放入d0
     */
    private long d0;


    public static HotTrendPO valueOf(long refreshTime) {
        var trend = new HotTrendPO();
        trend.refresh = refreshTime;
        return trend;
    }

    public void addCount(int num) {
        var now = TimeUtils.now();
        if (TimeUtils.isSameDay(new Date(now), new Date(refresh))) {
            this.d1 = this.d1 + num;
        } else {
            this.refresh = now;

            d0 = d0 + d13;
            d13 = d12;
            d12 = d11;
            d11 = d10;
            d10 = d9;
            d9 = d8;
            d8 = d7;
            d7 = d6;
            d6 = d5;
            d5 = d4;
            d4 = d2;
            d3 = d2;
            d2 = d1;
            d1 = num;
        }
    }


    /**
     * 二八原则
     */
    public long score() {
        return d1 * 100 + d2 * 80 + d3 * 64 + d4 * 51 + d5 * 41 + d6 * 33 + d7 * 26
                + d8 * 21 + d9 * 17 + d10 * 13 + d11 * 11 + d12 * 9 + d13 * 7 + d0 * 6;
    }

    public long getRefresh() {
        return refresh;
    }

    public void setRefresh(long refresh) {
        this.refresh = refresh;
    }

    public int getD1() {
        return d1;
    }

    public void setD1(int d1) {
        this.d1 = d1;
    }

    public int getD2() {
        return d2;
    }

    public void setD2(int d2) {
        this.d2 = d2;
    }

    public int getD3() {
        return d3;
    }

    public void setD3(int d3) {
        this.d3 = d3;
    }

    public int getD4() {
        return d4;
    }

    public void setD4(int d4) {
        this.d4 = d4;
    }

    public int getD5() {
        return d5;
    }

    public void setD5(int d5) {
        this.d5 = d5;
    }

    public int getD6() {
        return d6;
    }

    public void setD6(int d6) {
        this.d6 = d6;
    }

    public int getD7() {
        return d7;
    }

    public void setD7(int d7) {
        this.d7 = d7;
    }

    public int getD8() {
        return d8;
    }

    public void setD8(int d8) {
        this.d8 = d8;
    }

    public int getD9() {
        return d9;
    }

    public void setD9(int d9) {
        this.d9 = d9;
    }

    public int getD10() {
        return d10;
    }

    public void setD10(int d10) {
        this.d10 = d10;
    }

    public int getD11() {
        return d11;
    }

    public void setD11(int d11) {
        this.d11 = d11;
    }

    public int getD12() {
        return d12;
    }

    public void setD12(int d12) {
        this.d12 = d12;
    }

    public int getD13() {
        return d13;
    }

    public void setD13(int d13) {
        this.d13 = d13;
    }

    public long getD0() {
        return d0;
    }

    public void setD0(long d0) {
        this.d0 = d0;
    }
}
