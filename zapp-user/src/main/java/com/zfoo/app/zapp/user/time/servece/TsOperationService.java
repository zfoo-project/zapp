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

package com.zfoo.app.zapp.user.time.servece;

import com.zfoo.app.zapp.common.entity.time.TimeSliceEntity;
import com.zfoo.app.zapp.common.entity.time.model.LoveTrendPO;
import com.zfoo.app.zapp.common.protocol.feed.home.LoveTsAsk;
import com.zfoo.app.zapp.common.protocol.feed.item.LoveTsWithItemAsk;
import com.zfoo.app.zapp.common.protocol.feed.location.LoveTsWithLocationAsk;
import com.zfoo.app.zapp.common.protocol.feed.person.LoveTsWithPersonAsk;
import com.zfoo.net.NetContext;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-16 11:26
 */
@Component
public class TsOperationService implements ITsOperationService {

    @EntityCachesInjection
    private IEntityCaches<Long, TimeSliceEntity> tsCaches;


    @Override
    public void loveTimeSlice(long id, long count) {
        var entity = tsCaches.load(id);
        // 如果userId小于等于0，说明这个时间片已经被删除了
        if (entity.id() == 0L) {
            return;
        }
        var love = entity.getLove() + count;
        entity.setLove(love);

        var loveTrend = entity.getTrend();
        if (loveTrend == null) {
            loveTrend = LoveTrendPO.valueOf(TimeUtils.now());
            entity.setTrend(loveTrend);
        }

        loveTrend.addCount(count);
        tsCaches.update(entity);

        var score = loveTrend.score();

        var locations = entity.getLocations();
        if (CollectionUtils.isNotEmpty(locations)) {
            locations.stream().forEach(it -> NetContext.getConsumer().send(LoveTsWithLocationAsk.valueOf(it, id, love, score), it));
        }

        var items = entity.getItems();
        if (CollectionUtils.isNotEmpty(items)) {
            items.stream().forEach(it -> NetContext.getConsumer().send(LoveTsWithItemAsk.valueOf(it, id, love, score), it));
        }

        var persons = entity.getPersons();
        if (CollectionUtils.isNotEmpty(persons)) {
            persons.stream().forEach(it -> NetContext.getConsumer().send(LoveTsWithPersonAsk.valueOf(it, id, love, score), it));
        }

        NetContext.getConsumer().send(LoveTsAsk.valueOf(id, love, score), id);
    }

    @Override
    public void deleteTimeSlice(long id) {
        // 线删除缓存
        tsCaches.invalidate(id);

        // 删除数据库里的时间片
        OrmContext.getAccessor().delete(id, TimeSliceEntity.class);
    }

}
