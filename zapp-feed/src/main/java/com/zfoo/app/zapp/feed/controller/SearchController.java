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

package com.zfoo.app.zapp.feed.controller;

import com.zfoo.app.zapp.common.entity.search.SearchEntity;
import com.zfoo.app.zapp.common.protocol.feed.search.SearchCountAsk;
import com.zfoo.net.router.receiver.PacketReceiver;
import com.zfoo.net.session.model.Session;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-21 00:10
 */
@Component
public class SearchController {

    @EntityCachesInjection
    private IEntityCaches<String, SearchEntity> searchCaches;

    @PacketReceiver
    public void atSearchCountAsk(Session session, SearchCountAsk ask) {
        var query = ask.getQuery();
        if (StringUtils.isBlank(query)) {
            return;
        }

        query = query.trim();

        var entity = searchCaches.load(query);
        // 缓存没有关键词则创建一个关键词
        if (StringUtils.isBlank(entity.getId())) {
            var newEntity = SearchEntity.valueOf(query, TimeUtils.now());
            var trendPO = newEntity.getTrend();
            trendPO.addCount(1);
            newEntity.setScore(trendPO.score());
            newEntity.setCount(entity.getCount() + 1);
            entity = newEntity;
            OrmContext.getAccessor().insert(newEntity);
            searchCaches.invalidate(query);
        }

        var trendPO = entity.getTrend();
        trendPO.addCount(1);
        entity.setScore(trendPO.score());
        entity.setCount(entity.getCount() + 1);
        searchCaches.update(entity);
    }

}
