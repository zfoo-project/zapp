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

package com.zfoo.app.zapp.feed.service;

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.entity.feed.FeedHomeEntity;
import com.zfoo.app.zapp.common.entity.feed.FeedItemEntity;
import com.zfoo.app.zapp.common.entity.feed.FeedLocationEntity;
import com.zfoo.app.zapp.common.entity.feed.FeedPersonEntity;
import com.zfoo.net.packet.common.PairLong;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-16 13:55
 */
@Component
public class FeedService implements IFeedService {

    @EntityCachesInjection
    private IEntityCaches<Long, FeedHomeEntity> feedHomeCaches;

    @EntityCachesInjection
    private IEntityCaches<Long, FeedLocationEntity> feedLocationCaches;

    @EntityCachesInjection
    private IEntityCaches<Long, FeedItemEntity> feedItemCaches;

    @EntityCachesInjection
    private IEntityCaches<Long, FeedPersonEntity> feedPersonCaches;

    @Override
    public void createTs(long tsId, boolean recommend) {
        var id = tsId % AppConstant.HOME_FEED_SIZE + 1;
        var feedEntity = feedHomeCaches.load(id);
        if (feedEntity.id() == 0L) {
            feedEntity.setId(id);
            OrmContext.getAccessor().insert(feedEntity);
            feedHomeCaches.invalidate(id);
        }

        if (recommend) {
            var recommendLinks = feedEntity.getRecommendLinks();
            if (!recommendLinks.contains(tsId)) {
                recommendLinks.add(tsId);
            }

            if (recommendLinks.size() > AppConstant.HOME_RECOMMEND_LINK_SIZE) {
                recommendLinks.remove(0);
            }
        } else {
            var newLinks = feedEntity.getNewLinks();
            if (!newLinks.contains(tsId)) {
                newLinks.add(tsId);
            }

            if (newLinks.size() > AppConstant.HOME_NEW_LINK_SIZE) {
                newLinks.remove(0);
            }
        }


        feedHomeCaches.update(feedEntity);
    }

    @Override
    public void loveTs(long tsId, long love, long score) {
        var id = tsId % AppConstant.HOME_FEED_SIZE + 1;
        var feedEntity = feedHomeCaches.load(id);
        if (feedEntity.id() == 0L) {
            feedEntity.setId(id);
            OrmContext.getAccessor().insert(feedEntity);
            feedHomeCaches.invalidate(id);
        }
        computeLoveFeed(tsId, love, score, feedEntity.getTopLoveLinks(), feedEntity.getTrendLoveLinks()
                , AppConstant.HOME_TOP_LINK_SIZE, AppConstant.HOME_TREND_LINK_SIZE);
        feedHomeCaches.update(feedEntity);
    }

    @Override
    public void createTsWithLocation(long locationId, long tsId, boolean recommend) {
        var feedEntity = feedLocationCaches.load(locationId);
        if (feedEntity.id() == 0L) {
            feedEntity.setId(locationId);
            OrmContext.getAccessor().insert(feedEntity);
            feedLocationCaches.invalidate(locationId);
        }

        if (recommend) {
            var recommendLinks = feedEntity.getRecommendLinks();
            if (!recommendLinks.contains(tsId)) {
                recommendLinks.add(tsId);
            }

            if (recommendLinks.size() > AppConstant.LOCATION_NEW_LINK_SIZE) {
                recommendLinks.remove(0);
            }
        } else {
            var newLinks = feedEntity.getNewLinks();
            if (!newLinks.contains(tsId)) {
                newLinks.add(tsId);
            }

            if (newLinks.size() > AppConstant.LOCATION_RECOMMEND_LINK_SIZE) {
                newLinks.remove(0);
            }
        }

        feedLocationCaches.update(feedEntity);
    }

    @Override
    public void createTsWithItem(long itemId, long tsId, boolean recommend) {
        var feedEntity = feedItemCaches.load(itemId);
        if (feedEntity.id() == 0L) {
            feedEntity.setId(itemId);
            OrmContext.getAccessor().insert(feedEntity);
            feedItemCaches.invalidate(itemId);
        }

        if (recommend) {
            var recommendLinks = feedEntity.getRecommendLinks();
            if (!recommendLinks.contains(tsId)) {
                recommendLinks.add(tsId);
            }
            if (recommendLinks.size() > AppConstant.ITEM_RECOMMEND_LINK_SIZE) {
                recommendLinks.remove(0);
            }
        } else {
            var newLinks = feedEntity.getNewLinks();
            if (!newLinks.contains(tsId)) {
                newLinks.add(tsId);
            }

            if (newLinks.size() > AppConstant.ITEM_NEW_LINK_SIZE) {
                newLinks.remove(0);
            }
        }

        feedItemCaches.update(feedEntity);
    }

    @Override
    public void createTsWithPerson(long personId, long tsId, boolean recommend) {
        var feedEntity = feedPersonCaches.load(personId);
        if (feedEntity.id() == 0L) {
            feedEntity.setId(personId);
            OrmContext.getAccessor().insert(feedEntity);
            feedPersonCaches.invalidate(personId);
        }

        if (recommend) {
            var recommendLinks = feedEntity.getRecommendLinks();
            if (!recommendLinks.contains(tsId)) {
                recommendLinks.add(tsId);
            }
            if (recommendLinks.size() > AppConstant.PERSON_RECOMMEND_LINK_SIZE) {
                recommendLinks.remove(0);
            }
        } else {
            var newLinks = feedEntity.getNewLinks();
            if (!newLinks.contains(tsId)) {
                newLinks.add(tsId);
            }
            if (newLinks.size() > AppConstant.PERSON_NEW_LINK_SIZE) {
                newLinks.remove(0);
            }
        }

        feedPersonCaches.update(feedEntity);
    }

    /**
     * @return 是否需要更新数据
     */
    private boolean computeLoveFeed(long tsId, long love, long score, List<PairLong> topLoveLinks, List<PairLong> trendLoveLinks
            , int topLinkSize, int trendLinkSize) {
        var update = false;
        // 计算喜爱的top排序

        var tsLoveOptional = topLoveLinks.stream().filter(it -> it.getKey() == tsId).findFirst();
        if (tsLoveOptional.isPresent()) {
            tsLoveOptional.get().setValue(love);
            Collections.sort(topLoveLinks, PairLong.NATURAL_VALUE_COMPARATOR);
            update = true;
        } else {
            if (topLoveLinks.size() < topLinkSize) {
                topLoveLinks.add(PairLong.valueOf(tsId, love));
                Collections.sort(topLoveLinks, PairLong.NATURAL_VALUE_COMPARATOR);
                update = true;
            } else {
                var firstTsLove = topLoveLinks.get(0);
                if (firstTsLove.getValue() <= love) {
                    topLoveLinks.set(0, PairLong.valueOf(tsId, love));
                    Collections.sort(topLoveLinks, PairLong.NATURAL_VALUE_COMPARATOR);
                    update = true;
                }
            }
        }


        // 计算喜爱的trend排序
        var tsLoveTrendOptional = trendLoveLinks.stream().filter(it -> it.getKey() == tsId).findFirst();
        if (tsLoveTrendOptional.isPresent()) {
            tsLoveTrendOptional.get().setValue(score);
            Collections.sort(trendLoveLinks, PairLong.NATURAL_VALUE_COMPARATOR);
            update = true;
        } else {
            if (trendLoveLinks.size() < trendLinkSize) {
                trendLoveLinks.add(PairLong.valueOf(tsId, score));
                Collections.sort(trendLoveLinks, PairLong.NATURAL_VALUE_COMPARATOR);
                update = true;
            } else {
                var firstTsLoveTrend = trendLoveLinks.get(0);
                if (firstTsLoveTrend.getValue() <= score) {
                    trendLoveLinks.set(0, PairLong.valueOf(tsId, score));
                    Collections.sort(trendLoveLinks, PairLong.NATURAL_VALUE_COMPARATOR);
                    update = true;
                }
            }
        }
        return update;
    }

    @Override
    public void loveTsWithLocation(long locationId, long tsId, long love, long score) {
        var feedEntity = feedLocationCaches.load(locationId);
        if (feedEntity.id() == 0L) {
            feedEntity.setId(locationId);
            OrmContext.getAccessor().insert(feedEntity);
            feedLocationCaches.invalidate(locationId);
        }
        computeLoveFeed(tsId, love, score, feedEntity.getTopLoveLinks(), feedEntity.getTrendLoveLinks()
                , AppConstant.LOCATION_TOP_LINK_SIZE, AppConstant.LOCATION_TREND_LINK_SIZE);
        feedLocationCaches.update(feedEntity);
    }

    @Override
    public void loveTsWithItem(long itemId, long tsId, long love, long score) {
        var feedEntity = feedItemCaches.load(itemId);
        if (feedEntity.id() == 0L) {
            feedEntity.setId(itemId);
            OrmContext.getAccessor().insert(feedEntity);
            feedItemCaches.invalidate(itemId);
        }
        computeLoveFeed(tsId, love, score, feedEntity.getTopLoveLinks(), feedEntity.getTrendLoveLinks()
                , AppConstant.ITEM_TOP_LINK_SIZE, AppConstant.ITEM_TREND_LINK_SIZE);
        feedItemCaches.update(feedEntity);
    }

    @Override
    public void loveTsWithPerson(long personId, long tsId, long love, long score) {
        var feedEntity = feedPersonCaches.load(personId);
        if (feedEntity.id() == 0L) {
            feedEntity.setId(personId);
            OrmContext.getAccessor().insert(feedEntity);
            feedPersonCaches.invalidate(personId);
        }
        computeLoveFeed(tsId, love, score, feedEntity.getTopLoveLinks(), feedEntity.getTrendLoveLinks()
                , AppConstant.PERSON_TOP_LINK_SIZE, AppConstant.PERSON_TREND_LINK_SIZE);
        feedPersonCaches.update(feedEntity);
    }

}
