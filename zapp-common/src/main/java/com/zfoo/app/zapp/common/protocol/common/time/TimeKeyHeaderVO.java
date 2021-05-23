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
import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-22 17:41
 */
public class TimeKeyHeaderVO implements IPacket {

    public static final transient short PROTOCOL_ID = 130;

    private String text;
    private String value;

    public static TimeKeyHeaderVO valueOf(TimeKeyHeaderPO po) {
        var vo = new TimeKeyHeaderVO();
        vo.text = po.getText();
        vo.value = po.getValue();
        return vo;
    }

    public TimeKeyHeaderPO toTimeKeyHeaderPO() {
        return TimeKeyHeaderPO.valueOf(text, value);
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
