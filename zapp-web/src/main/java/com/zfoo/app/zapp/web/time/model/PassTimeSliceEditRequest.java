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


/**
 * 审核通过编辑的时间片的请求
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-01-27 21:22
 */
public class PassTimeSliceEditRequest {

    /**
     * 时间片id
     */
    private long tsEditId;

    /**
     * 是否推荐
     */
    private boolean recommend;

    public long getTsEditId() {
        return tsEditId;
    }

    public void setTsEditId(long tsEditId) {
        this.tsEditId = tsEditId;
    }

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }
}
