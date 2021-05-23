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

package com.zfoo.app.zapp.common.entity.feed;

import com.zfoo.net.packet.common.PairLong;
import com.zfoo.orm.model.anno.EntityCache;
import com.zfoo.orm.model.anno.Id;
import com.zfoo.orm.model.anno.Persister;
import com.zfoo.orm.model.entity.IEntity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-01-27 21:22
 */
@EntityCache(cacheStrategy = "tenThousand", persister = @Persister("time30s"))
public class FeedItemEntity implements IEntity<Long> {

    /**
     * 对应于CategoryEntity里的id
     */
    @Id
    private long id;

    private long vs;

    /**
     * 喜爱最多的时间片id
     */
    private List<PairLong> topLoveLinks = new CopyOnWriteArrayList<>();

    /**
     * 喜爱的趋势
     */
    private List<PairLong> trendLoveLinks = new CopyOnWriteArrayList<>();

    /**
     * 新的时间片的id
     */
    private List<Long> newLinks = new CopyOnWriteArrayList<>();

    /**
     * 推荐的时间片的id
     */
    private List<Long> recommendLinks = new CopyOnWriteArrayList<>();

    public static FeedItemEntity valueOf(long id) {
        var entity = new FeedItemEntity();
        entity.id = id;
        return entity;
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public long gvs() {
        return vs;
    }

    @Override
    public void svs(long vs) {
        this.vs = vs;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVs() {
        return vs;
    }

    public void setVs(long vs) {
        this.vs = vs;
    }

    public List<PairLong> getTopLoveLinks() {
        return topLoveLinks;
    }

    public void setTopLoveLinks(List<PairLong> topLoveLinks) {
        this.topLoveLinks = topLoveLinks;
    }

    public List<PairLong> getTrendLoveLinks() {
        return trendLoveLinks;
    }

    public void setTrendLoveLinks(List<PairLong> trendLoveLinks) {
        this.trendLoveLinks = trendLoveLinks;
    }

    public List<Long> getNewLinks() {
        return newLinks;
    }

    public void setNewLinks(List<Long> newLinks) {
        this.newLinks = newLinks;
    }

    public List<Long> getRecommendLinks() {
        return recommendLinks;
    }

    public void setRecommendLinks(List<Long> recommendLinks) {
        this.recommendLinks = recommendLinks;
    }
}
