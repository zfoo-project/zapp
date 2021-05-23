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

package com.zfoo.app.zapp.web.user.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 保存用户自定义状态返回
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-11 11:15
 */
public class SaveCustomStatusResponse {

    /**
     * 自定义状态过期时间
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private long customTime;

    public static SaveCustomStatusResponse valueOf(long customTime) {
        var response = new SaveCustomStatusResponse();
        response.customTime = customTime;
        return response;
    }

    public long getCustomTime() {
        return customTime;
    }

    public void setCustomTime(long customTime) {
        this.customTime = customTime;
    }

}
