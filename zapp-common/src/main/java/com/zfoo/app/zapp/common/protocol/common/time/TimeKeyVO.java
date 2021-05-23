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

package com.zfoo.app.zapp.common.protocol.common.time;

import com.zfoo.app.zapp.common.entity.time.model.TimeKeyHeaderPO;
import com.zfoo.app.zapp.common.entity.time.model.TimeKeyPO;
import com.zfoo.app.zapp.common.entity.time.model.TimeKeyRowPO;
import com.zfoo.protocol.IPacket;
import com.zfoo.protocol.collection.CollectionUtils;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-22 17:46
 */
public class TimeKeyVO implements IPacket {

    public static final transient short PROTOCOL_ID = 132;

    private List<TimeKeyHeaderVO> headers;
    private List<TimeKeyRowVO> rows;

    @Nullable
    public static TimeKeyVO valueOf(TimeKeyPO po) {
        if (po == null) {
            return null;
        }
        var vo = new TimeKeyVO();
        if (CollectionUtils.isNotEmpty(po.getHeaders())) {
            vo.headers = po.getHeaders().stream().map(it -> TimeKeyHeaderVO.valueOf(it)).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(po.getRows())) {
            vo.rows = po.getRows().stream().map(it -> TimeKeyRowVO.valueOf(it)).collect(Collectors.toList());
        }
        return vo;
    }

    public TimeKeyPO toTimeKeyPO() {
        List<TimeKeyHeaderPO> headers = null;
        if (CollectionUtils.isEmpty(this.headers)) {
            headers = this.headers.stream().map(it -> it.toTimeKeyHeaderPO()).collect(Collectors.toList());
        }

        List<TimeKeyRowPO> rows = null;
        if (CollectionUtils.isEmpty(this.rows)) {
            rows = this.rows.stream().map(it -> it.toTimeKeyRowPO()).collect(Collectors.toList());
        }
        return TimeKeyPO.valueOf(headers, rows);
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public List<TimeKeyHeaderVO> getHeaders() {
        return headers;
    }

    public void setHeaders(List<TimeKeyHeaderVO> headers) {
        this.headers = headers;
    }

    public List<TimeKeyRowVO> getRows() {
        return rows;
    }

    public void setRows(List<TimeKeyRowVO> rows) {
        this.rows = rows;
    }

}
