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

package com.zfoo.app.zapp.web.search.service;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.constant.LocationConstant;
import com.zfoo.app.zapp.common.entity.core.PersonEntity;
import com.zfoo.app.zapp.common.entity.core.WordEntity;
import com.zfoo.app.zapp.common.entity.feed.FeedItemEntity;
import com.zfoo.app.zapp.common.entity.feed.FeedLocationEntity;
import com.zfoo.app.zapp.common.entity.feed.FeedPersonEntity;
import com.zfoo.app.zapp.common.entity.time.TimeSliceEntity;
import com.zfoo.app.zapp.common.model.time.TimeSliceVO;
import com.zfoo.app.zapp.common.protocol.feed.search.SearchCountAsk;
import com.zfoo.app.zapp.web.time.service.ITimeSliceService;
import com.zfoo.app.zapp.web.word.service.IWordService;
import com.zfoo.net.NetContext;
import com.zfoo.net.util.SimpleCache;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.model.query.Page;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-09-12 13:14
 */
@Component
public class SearchService implements ISearchService {

    public SimpleCache<String, List<Long>> searchCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_0000
            , queries -> {
                var result = new ArrayList<Pair<String, List<Long>>>();
                for (var query : queries) {
                    // TODO:这里的语法没有办法用到最左匹配原则的索引，考虑后期引入es
                    // content匹配
                    var collection = OrmContext.getOrmManager().getCollection(TimeSliceEntity.class);
                    var regex = StringUtils.format("^.*{}.*", query);
                    var contentList = new ArrayList<Long>();
                    collection.find(Filters.regex("content", regex))
                            .projection(Projections.include("_id"))
                            .sort(Sorts.descending("love"))
                            .forEach((Consumer<TimeSliceEntity>) entity -> contentList.add(entity.getId()));

                    // location匹配
                    var locationId = LocationConstant.locationNameMap.get(query);
                    var locationList = new ArrayList<Long>();
                    if (locationId != null) {
                        var locationFeed = OrmContext.getAccessor().load(locationId, FeedLocationEntity.class);
                        if (locationFeed != null) {
                            var joinList = CollectionUtils.listJoinList(true
                                    , new Pair<>(AppConstant.COMMON_FEED_PAGE_TOP_LINK_SIZE, locationFeed.getTopLoveLinks().stream().map(topTsLove -> topTsLove.getKey()).collect(Collectors.toList()))
                                    , new Pair<>(AppConstant.COMMON_FEED_PAGE_TREND_LINK_SIZE, locationFeed.getTrendLoveLinks().stream().map(trendLove -> trendLove.getKey()).collect(Collectors.toList()))
                                    , new Pair<>(AppConstant.COMMON_FEED_PAGE_NEW_LINK_SIZE, locationFeed.getNewLinks())
                                    , new Pair<>(AppConstant.COMMON_FEED_PAGE_RECOMMEND_LINK_SIZE, locationFeed.getRecommendLinks()));
                            locationList.addAll(joinList);
                        }
                    }

                    // item匹配
                    var items = OrmContext.getQuery().queryFieldIn("word", List.of(query, query.toLowerCase(), query.toUpperCase()), WordEntity.class);
                    var itemList = new ArrayList<Long>();
                    if (CollectionUtils.isNotEmpty(items)) {
                        for (var item : items) {
                            var itemFeed = OrmContext.getAccessor().load(item.getId(), FeedItemEntity.class);
                            if (itemFeed != null) {
                                var joinList = CollectionUtils.listJoinList(true
                                        , new Pair<>(AppConstant.COMMON_FEED_PAGE_TOP_LINK_SIZE, itemFeed.getTopLoveLinks().stream().map(topTsLove -> topTsLove.getKey()).collect(Collectors.toList()))
                                        , new Pair<>(AppConstant.COMMON_FEED_PAGE_TREND_LINK_SIZE, itemFeed.getTrendLoveLinks().stream().map(trendLove -> trendLove.getKey()).collect(Collectors.toList()))
                                        , new Pair<>(AppConstant.COMMON_FEED_PAGE_NEW_LINK_SIZE, itemFeed.getNewLinks())
                                        , new Pair<>(AppConstant.COMMON_FEED_PAGE_RECOMMEND_LINK_SIZE, itemFeed.getRecommendLinks()));
                                itemList.addAll(joinList);
                            }
                        }
                    }

                    // person匹配
                    var persons = OrmContext.getQuery().queryFieldIn("word", List.of(query, query.toLowerCase(), query.toUpperCase()), PersonEntity.class);
                    var personList = new ArrayList<Long>();
                    if (CollectionUtils.isNotEmpty(persons)) {
                        for (var person : persons) {
                            var personFeed = OrmContext.getAccessor().load(person.getId(), FeedPersonEntity.class);
                            if (personFeed != null) {
                                var joinList = CollectionUtils.listJoinList(true
                                        , new Pair<>(AppConstant.COMMON_FEED_PAGE_TOP_LINK_SIZE, personFeed.getTopLoveLinks().stream().map(topTsLove -> topTsLove.getKey()).collect(Collectors.toList()))
                                        , new Pair<>(AppConstant.COMMON_FEED_PAGE_TREND_LINK_SIZE, personFeed.getTrendLoveLinks().stream().map(trendLove -> trendLove.getKey()).collect(Collectors.toList()))
                                        , new Pair<>(AppConstant.COMMON_FEED_PAGE_NEW_LINK_SIZE, personFeed.getNewLinks())
                                        , new Pair<>(AppConstant.COMMON_FEED_PAGE_RECOMMEND_LINK_SIZE, personFeed.getRecommendLinks()));
                                personList.addAll(joinList);
                            }
                        }
                    }

                    var list = CollectionUtils.listJoinList(true
                            , new Pair<>(AppConstant.SEARCH_CONTENT_PAGE_SIZE, contentList)
                            , new Pair<>(AppConstant.SEARCH_LOCATION_PAGE_SIZE, locationList)
                            , new Pair<>(AppConstant.SEARCH_PERSON_PAGE_SIZE, personList)
                            , new Pair<>(AppConstant.SEARCH_ITEM_PAGE_SIZE, itemList))
                            .stream()
                            .limit(AppConstant.SEARCH_RESULT_LIMIT_SIZE)
                            .collect(Collectors.toList());

                    result.add(new Pair<>(query, list));
                }

                return result;
            }
            , key -> Collections.emptyList());
    @Autowired
    private ITimeSliceService timeSliceService;
    @Autowired
    private IWordService wordService;

    @Override
    public List<TimeSliceVO> search(String query, int page) {
        if (StringUtils.isBlank(query)) {
            return Collections.EMPTY_LIST;
        }

        var words = ToAnalysis.parse(query)
                .getTerms()
                .stream()
                .filter(it -> CollectionUtils.isNotEmpty(it.termNatures().termNatures))
                .filter(it -> Arrays.stream(it.termNatures().termNatures).anyMatch(nature -> nature.nature.natureStr.contains("n")))
                .map(it -> it.getName().trim())
                .filter(it -> !StringUtils.isBlank(it))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(words)) {
            return Collections.EMPTY_LIST;
        }

        var searchMap = searchCaches.batchGet(words);
        if (CollectionUtils.isEmpty(searchMap)) {
            return Collections.EMPTY_LIST;
        }
        var joinArray = searchMap.values()
                .stream()
                .map(it -> new Pair<>(AppConstant.SEARCH_Analysis_STEP_SIZE, it))
                .collect(Collectors.toList());

        var joinList = CollectionUtils.listJoinList(true, joinArray);

        var homeFeedPage = Page.valueOf(page, AppConstant.SEARCH_PAGE_SIZE, joinList.size());
        var searchList = homeFeedPage.currentPageList(joinList);

        NetContext.getConsumer().send(SearchCountAsk.valueOf(query), query);

        return timeSliceService.existTimeSliceList(searchList);
    }


}
