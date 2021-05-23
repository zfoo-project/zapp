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
 * 热搜
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-23 11:30
 */
public class SearchHotVO {

    private int type;

    @JsonSerialize(using = ToStringSerializer.class)
    private long id;

    private String name;

    @JsonSerialize(using = ToStringSerializer.class)
    private long score;

    public static SearchHotVO valueOf(int type, long id, String name, long score) {
        var instance = new SearchHotVO();
        instance.type = type;
        instance.id = id;
        instance.name = name;
        instance.score = score;
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

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
