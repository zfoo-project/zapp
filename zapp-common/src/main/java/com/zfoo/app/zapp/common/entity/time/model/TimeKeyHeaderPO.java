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

import com.zfoo.protocol.util.StringUtils;

/**
 * 键值对的头部
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-22 17:41
 */
public class TimeKeyHeaderPO {

    /**
     * 键值对的文本显示内容
     */
    private String text;
    /**
     * 键值对的值
     */
    private String value;

    public static TimeKeyHeaderPO valueOf(String text, String value) {
        var po = new TimeKeyHeaderPO();
        po.text = text;
        po.value = value;
        return po;
    }

    public void trim() {
        this.text = StringUtils.trim(this.text);
        this.value = StringUtils.trim(this.value);
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
