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

package com.zfoo.app.zapp.web.search.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 搜索提示
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-29 15:30
 */
public class SearchHintVO {

    /**
     * 提示类型，2代表相关的地点提示，3代表相关的人提示，4代表相关的物提示，5代表热点提示
     */
    private int type;
    /**
     * 标识type的唯一id，如果为6，则并没有特殊含义
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private long id;
    /**
     * 提示名称
     */
    private String name;

    public static SearchHintVO valueOf(int type, long id, String name) {
        var instance = new SearchHintVO();
        instance.type = type;
        instance.id = id;
        instance.name = name;
        return instance;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
