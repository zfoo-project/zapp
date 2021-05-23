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

import com.zfoo.protocol.collection.CollectionUtils;

import java.util.List;

/**
 * 时间片的键值对
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-22 17:46
 */
public class TimeKeyPO {

    /**
     * 键值对的头部
     */
    private List<TimeKeyHeaderPO> headers;
    /**
     * 键值对的行
     */
    private List<TimeKeyRowPO> rows;

    public static TimeKeyPO valueOf(List<TimeKeyHeaderPO> headers, List<TimeKeyRowPO> rows) {
        var po = new TimeKeyPO();
        po.headers = headers;
        po.rows = rows;
        return po;
    }

    public void trim() {
        if (CollectionUtils.isNotEmpty(headers)) {
            headers.forEach(it -> it.trim());
        }
        if (CollectionUtils.isNotEmpty(rows)) {
            rows.forEach(it -> it.trim());
        }
    }

    public List<TimeKeyHeaderPO> getHeaders() {
        return headers;
    }

    public void setHeaders(List<TimeKeyHeaderPO> headers) {
        this.headers = headers;
    }

    public List<TimeKeyRowPO> getRows() {
        return rows;
    }

    public void setRows(List<TimeKeyRowPO> rows) {
        this.rows = rows;
    }
}
