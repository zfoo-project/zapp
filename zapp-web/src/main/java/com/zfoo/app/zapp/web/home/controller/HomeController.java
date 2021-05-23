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

package com.zfoo.app.zapp.web.home.controller;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.entity.feed.FeedHomeEntity;
import com.zfoo.app.zapp.common.entity.feed.FeedItemEntity;
import com.zfoo.app.zapp.common.entity.feed.FeedLocationEntity;
import com.zfoo.app.zapp.common.entity.feed.FeedPersonEntity;
import com.zfoo.app.zapp.common.model.time.TimeSliceVO;
import com.zfoo.app.zapp.common.protocol.cache.GetUserCacheAnswer;
import com.zfoo.app.zapp.common.protocol.cache.GetUserCacheAsk;
import com.zfoo.app.zapp.common.protocol.feed.home.CreateTsAsk;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.web.home.model.HomeFeedResponse;
import com.zfoo.app.zapp.web.time.model.event.PassEditTimeSliceEvent;
import com.zfoo.app.zapp.web.time.model.event.PassReviewTimeSliceEvent;
import com.zfoo.app.zapp.web.time.service.TimeSliceService;
import com.zfoo.app.zapp.web.util.HttpUtils;
import com.zfoo.event.model.anno.EventReceiver;
import com.zfoo.net.NetContext;
import com.zfoo.net.util.SimpleCache;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.model.query.Page;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.model.Pair;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.util.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 主页相关接口
 * 首页推荐内容，热搜，搜索内容，搜索的提示，用户关注的内容
 *
 * @author jaysunxiao
 * @version 1.0
 * @module Home模块
 * @since 2020-02-24 13:52
 */
@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private TimeSliceService timeSliceService;


    private SimpleCache<Long, List<Long>> homeFeedCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_000
            , ids -> {
                var homeFeeds = OrmContext.getQuery().queryFieldIn("_id", ids, FeedHomeEntity.class);
                var result = new ArrayList<Pair<Long, List<Long>>>();
                for (var feed : homeFeeds) {
                    var list = CollectionUtils.listJoinList(true
                            , new Pair<>(AppConstant.HOME_FEED_PAGE_TOP_LINK_SIZE, feed.getTopLoveLinks().stream().map(topTsLove -> topTsLove.getKey()).collect(Collectors.toList()))
                            , new Pair<>(AppConstant.HOME_FEED_PAGE_TREND_LINK_SIZE, feed.getTrendLoveLinks().stream().map(trendLove -> trendLove.getKey()).collect(Collectors.toList()))
                            , new Pair<>(AppConstant.HOME_FEED_PAGE_NEW_LINK_SIZE, feed.getNewLinks())
                            , new Pair<>(AppConstant.HOME_FEED_PAGE_RECOMMEND_LINK_SIZE, feed.getRecommendLinks()));
                    result.add(new Pair<>(feed.getId(), list));
                }
                return result;
            }
            , key -> Collections.EMPTY_LIST);


    private SimpleCache<Long, List<Long>> subscribeFeedCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_000
            , ids -> {
                try {
                    var answer = NetContext.getConsumer().syncAsk(GetUserCacheAsk.valueOf(new HashSet<>(ids)), GetUserCacheAnswer.class, ids.get(0)).packet();
                    var userCacheMap = answer.getUserCacheMap();
                    if (CollectionUtils.isEmpty(userCacheMap)) {
                        return Collections.EMPTY_LIST;
                    }

                    var locationTsMap = new HashMap<Long, List<Long>>();
                    userCacheMap.values().stream()
                            .filter(it -> CollectionUtils.isNotEmpty(it.getLocations()))
                            .forEach(it -> {
                                it.getLocations().stream().forEach(location -> locationTsMap.put(location.getKey(), Collections.EMPTY_LIST));
                            });
                    if (CollectionUtils.isNotEmpty(locationTsMap)) {
                        OrmContext.getOrmManager()
                                .getCollection(FeedLocationEntity.class)
                                .find(Filters.in("_id", locationTsMap.keySet()))
                                .projection(Projections.include("_id", "newLinks"))
                                .forEach((Consumer<FeedLocationEntity>) locationFeedEntity -> locationTsMap.put(locationFeedEntity.getId(), CollectionUtils.listJoinList(true
                                        , new Pair<>(AppConstant.STAR_FEED_PAGE_NEW_LINK_SIZE, locationFeedEntity.getNewLinks())
                                        , new Pair<>(AppConstant.STAR_FEED_PAGE_RECOMMEND_LINK_SIZE, locationFeedEntity.getRecommendLinks()))));
                    }

                    var personTsMap = new HashMap<Long, List<Long>>();
                    userCacheMap.values().stream()
                            .filter(it -> CollectionUtils.isNotEmpty(it.getPersons()))
                            .forEach(it -> {
                                it.getPersons().stream().forEach(person -> personTsMap.put(person.getKey(), Collections.EMPTY_LIST));
                            });
                    if (CollectionUtils.isNotEmpty(personTsMap)) {
                        OrmContext.getOrmManager()
                                .getCollection(FeedPersonEntity.class)
                                .find(Filters.in("_id", personTsMap.keySet()))
                                .projection(Projections.include("_id", "newLinks"))
                                .forEach((Consumer<FeedPersonEntity>) personFeedEntity -> personTsMap.put(personFeedEntity.getId(), CollectionUtils.listJoinList(true
                                        , new Pair<>(AppConstant.STAR_FEED_PAGE_NEW_LINK_SIZE, personFeedEntity.getNewLinks())
                                        , new Pair<>(AppConstant.STAR_FEED_PAGE_RECOMMEND_LINK_SIZE, personFeedEntity.getRecommendLinks()))));
                    }

                    var itemTsMap = new HashMap<Long, List<Long>>();
                    userCacheMap.values().stream()
                            .filter(it -> CollectionUtils.isNotEmpty(it.getItems()))
                            .forEach(it -> {
                                it.getItems().stream().forEach(item -> itemTsMap.put(item.getKey(), Collections.EMPTY_LIST));
                            });
                    if (CollectionUtils.isNotEmpty(itemTsMap)) {
                        OrmContext.getOrmManager()
                                .getCollection(FeedItemEntity.class)
                                .find(Filters.in("_id", personTsMap.keySet()))
                                .projection(Projections.include("_id", "newLinks"))
                                .forEach((Consumer<FeedItemEntity>) itemFeedEntity -> itemTsMap.put(itemFeedEntity.getId(), CollectionUtils.listJoinList(true
                                        , new Pair<>(AppConstant.STAR_FEED_PAGE_NEW_LINK_SIZE, itemFeedEntity.getNewLinks())
                                        , new Pair<>(AppConstant.STAR_FEED_PAGE_RECOMMEND_LINK_SIZE, itemFeedEntity.getRecommendLinks()))));
                    }

                    var result = new ArrayList<Pair<Long, List<Long>>>();
                    for (var user : userCacheMap.values()) {
                        var locations = new ArrayList<Long>();
                        if (CollectionUtils.isNotEmpty(user.getLocations())) {
                            user.getLocations().stream()
                                    .map(it -> it.getKey())
                                    .map(it -> locationTsMap.get(it))
                                    .forEach(it -> locations.addAll(it));
                        }
                        var persons = new ArrayList<Long>();
                        if (CollectionUtils.isNotEmpty(user.getPersons())) {
                            user.getPersons().stream()
                                    .map(it -> it.getKey())
                                    .map(it -> personTsMap.get(it))
                                    .forEach(it -> persons.addAll(it));
                        }
                        var items = new ArrayList<Long>();
                        if (CollectionUtils.isNotEmpty(user.getItems())) {
                            user.getItems().stream()
                                    .map(it -> it.getKey())
                                    .map(it -> itemTsMap.get(it))
                                    .forEach(it -> items.addAll(it));
                        }
                        var list = CollectionUtils.listJoinList(true
                                , new Pair<>(AppConstant.STAR_FEED_PAGE_LOCATION_LINK_SIZE, locations)
                                , new Pair<>(AppConstant.STAR_FEED_PAGE_PERSON_LINK_SIZE, persons)
                                , new Pair<>(AppConstant.STAR_FEED_PAGE_ITEM_LINK_SIZE, items))
                                .stream()
                                .limit(AppConstant.STAR_FEED_PAGE_TOTAL_SIZE)
                                .collect(Collectors.toList());
                        result.add(new Pair<>(user.getId(), list));
                    }
                    return result;
                } catch (Exception e) {
                    logger.error("用户star的推荐发生未知异常", e);
                    return Collections.EMPTY_LIST;
                }

            }
            , key -> Collections.EMPTY_LIST);


    /**
     * 首页推荐内容
     *
     * @param feed 首页推荐的种子，默认是-1，下次再请求的时候必须带上服务器返回的种子
     * @param page 页数，默认是第1页
     * @return 对象，feed + timeArrays(同搜索内容/api/search)
     */
    @GetMapping("/api/home/feed")
    @ResponseBody
    public BaseResponse homeFeed(HttpServletRequest request, @RequestParam("feed") long feed, @RequestParam("page") int page) {
        if (feed > AppConstant.HOME_FEED_SIZE || page <= 0) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, HomeFeedResponse.valueOf(feed, false, Collections.EMPTY_LIST));
        }
        if (feed < 0) {
            if (page != 1) {
                return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, HomeFeedResponse.valueOf(feed, false, Collections.EMPTY_LIST));
            } else {
                feed = RandomUtils.randomLong(1, AppConstant.HOME_FEED_SIZE + 1);
            }
        }

        var timeSliceList = feedPageToTs(feed, page);
        if (CollectionUtils.isNotEmpty(timeSliceList)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, HomeFeedResponse.valueOf(feed, false, timeSliceList));
        }

        var currentFeed = feed;
        for (var i = 1; i <= AppConstant.HOME_FEED_SIZE; i++) {
            currentFeed = currentFeed == AppConstant.HOME_FEED_SIZE ? 1 : (currentFeed + 1);
            timeSliceList = feedPageToTs(currentFeed, 1);
            if (CollectionUtils.isNotEmpty(timeSliceList)) {
                return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, HomeFeedResponse.valueOf(currentFeed, true, timeSliceList));
            }
        }

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, HomeFeedResponse.valueOf(feed, false, Collections.EMPTY_LIST));
    }

    private List<TimeSliceVO> feedPageToTs(long feed, int page) {
        var list = homeFeedCaches.get(feed);

        if (CollectionUtils.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }

        var homeFeedPage = Page.valueOf(page, AppConstant.FEED_PAGE_SIZE, list.size());
        var linkList = homeFeedPage.currentPageList(list);

        var timeSliceList = timeSliceService.existTimeSliceList(linkList);
        return timeSliceList;
    }


    /**
     * 用户订阅的内容
     * 返回最新的用户官族内容，可能和地点，人物，事物相关
     *
     * @param page 页数
     * @return 对象数组 (同搜索内容/api/search)
     */
    @GetMapping("/api/subscribe/feed")
    @ResponseBody
    public BaseResponse subscribeFeed(HttpServletRequest request, @RequestParam("page") int page) {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        if (page <= 0) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var list = subscribeFeedCaches.get(userId);

        if (CollectionUtils.isEmpty(list)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var itemFeedPage = Page.valueOf(page, AppConstant.FEED_PAGE_SIZE, list.size());
        var linkList = itemFeedPage.currentPageList(list);

        var timeSliceList = timeSliceService.existTimeSliceList(linkList);

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, timeSliceList);
    }


    @EventReceiver
    public void onPassReviewTimeSliceEvent(PassReviewTimeSliceEvent event) {
        var entity = event.getNewEntity();
        var recommend = event.isRecommend();
        var timeSliceId = entity.getId();
        NetContext.getConsumer().send(CreateTsAsk.valueOf(timeSliceId, recommend), timeSliceId);
    }

    @EventReceiver
    public void onPassEditTimeSliceEvent(PassEditTimeSliceEvent event) {
        var newEntity = event.getNewEntity();
        var recommend = event.isRecommend();
        var timeSliceId = newEntity.getId();

        NetContext.getConsumer().send(CreateTsAsk.valueOf(timeSliceId, recommend), timeSliceId);
    }

}
