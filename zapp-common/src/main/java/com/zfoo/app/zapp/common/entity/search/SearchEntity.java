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

import com.zfoo.orm.model.anno.EntityCache;
import com.zfoo.orm.model.anno.Id;
import com.zfoo.orm.model.anno.Persister;
import com.zfoo.orm.model.entity.IEntity;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-02 11:16
 */
@EntityCache(cacheStrategy = "tenThousand", persister = @Persister("time30s"))
public class SearchEntity implements IEntity<String> {

    /**
     * 搜索的关键词
     */
    @Id
    private String id;

    /**
     * 搜索次数
     */
    private long count;

    /**
     * 创建时间
     */
    private long time;

    /**
     * 分数
     */
    private long score;

    /**
     * 热搜趋势
     */
    private HotTrendPO trend = new HotTrendPO();

    public static SearchEntity valueOf(String id, long time) {
        var entity = new SearchEntity();
        entity.id = id;
        entity.time = time;
        return entity;
    }

    @Override
    public String id() {
        return id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public HotTrendPO getTrend() {
        return trend;
    }

    public void setTrend(HotTrendPO trend) {
        this.trend = trend;
    }
}
