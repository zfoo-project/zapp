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

package com.zfoo.app.zapp.web.feed.controller;

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.constant.LocationConstant;
import com.zfoo.app.zapp.common.entity.feed.FeedLocationEntity;
import com.zfoo.app.zapp.common.protocol.cache.GetLocationCacheAnswer;
import com.zfoo.app.zapp.common.protocol.cache.GetLocationCacheAsk;
import com.zfoo.app.zapp.common.protocol.feed.location.CreateTsWithLocationAsk;
import com.zfoo.app.zapp.common.protocol.feed.location.DeleteTsWithLocationAsk;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.web.feed.model.LocationVO;
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
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 相关的地点相关接口
 * 相关的地点推荐，相关的地点信息，关注相关的地点
 *
 * @author jaysunxiao
 * @version 1.0
 * @module Location模块
 * @since 2020-03-04 14:26
 */
@Controller
public class LocationController {

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    private SimpleCache<Long, List<Long>> locationFeedCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_000
            , locations -> {
                var locationFeeds = OrmContext.getQuery().queryFieldIn("_id", locations, FeedLocationEntity.class);
                var result = new ArrayList<Pair<Long, List<Long>>>();
                for (var feed : locationFeeds) {
                    var list = CollectionUtils.listJoinList(true
                            , new Pair<>(AppConstant.COMMON_FEED_PAGE_TOP_LINK_SIZE, feed.getTopLoveLinks().stream().map(topTsLove -> topTsLove.getKey()).collect(Collectors.toList()))
                            , new Pair<>(AppConstant.COMMON_FEED_PAGE_TREND_LINK_SIZE, feed.getTrendLoveLinks().stream().map(trendLove -> trendLove.getKey()).collect(Collectors.toList()))
                            , new Pair<>(AppConstant.COMMON_FEED_PAGE_NEW_LINK_SIZE, feed.getNewLinks())
                            , new Pair<>(AppConstant.COMMON_FEED_PAGE_RECOMMEND_LINK_SIZE, feed.getRecommendLinks()));
                    result.add(new Pair<>(feed.getId(), list));
                }
                return result;
            }
            , key -> Collections.emptyList());

    @Autowired
    private TimeSliceService timeSliceService;

    /**
     * 相关的地点推荐
     *
     * @param location 相关的地点的id
     * @param page     页数，默认是0
     * @return 对象数组 (同搜索内容/api/search)
     */
    @GetMapping(value = "/api/location/feed")
    @ResponseBody
    public BaseResponse locationFeed(@RequestParam("location") long location, @RequestParam("page") int page) {
        if (!LocationConstant.checkLocations(List.of(location))) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var list = locationFeedCaches.get(location);

        if (CollectionUtils.isEmpty(list)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        if (page <= 0) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var locationFeedPage = Page.valueOf(page, AppConstant.FEED_PAGE_SIZE, list.size());
        var linkList = locationFeedPage.currentPageList(list);

        var timeSliceList = timeSliceService.existTimeSliceList(linkList);

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, timeSliceList);
    }

    /**
     * 相关的地点信息
     *
     * @param locations 相关的地点的id，用-连接，如1-2-3
     * @return 对象数组
     * @real_return {@link java.util.List<LocationVO>}
     */
    @GetMapping(value = "/api/location/info")
    @ResponseBody
    public BaseResponse locationInfo(@RequestParam("locations") String locations) throws Exception {
        var locationList = new ArrayList<>(HttpUtils.dashParamToSet(locations));
        if (CollectionUtils.isEmpty(locationList)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        if (!LocationConstant.checkLocations(locationList)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var firstPerson = locationList.stream().findFirst().get();
        var locationStringSet = locationList.stream().map(it -> LocationConstant.locationIdMap.get(it)).collect(Collectors.toSet());
        var answer = NetContext.getConsumer().syncAsk(GetLocationCacheAsk.valueOf(locationStringSet), GetLocationCacheAnswer.class, firstPerson).packet();
        var locationMap = answer.getLocationMap();


        var locationVos = locationList.stream()
                .map(it -> {
                    var word = LocationConstant.locationIdMap.get(it);

                    var locationTriple = locationMap.get(word);
                    var id = locationTriple == null ? 0L : locationTriple.getLeft();
                    var background = locationTriple == null ? StringUtils.EMPTY : locationTriple.getMiddle();
                    var description = locationTriple == null ? StringUtils.EMPTY : locationTriple.getRight();
                    return LocationVO.valueOf(id, word, background, description);
                })
                .collect(Collectors.toList());

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, locationVos);
    }

    @EventReceiver
    public void onPassReviewTimeSliceEvent(PassReviewTimeSliceEvent event) {
        var entity = event.getNewEntity();
        var locations = entity.getLocations();
        if (CollectionUtils.isEmpty(locations)) {
            return;
        }

        var tsId = entity.getId();
        var recommend = event.isRecommend();
        for (var locationId : locations) {
            NetContext.getConsumer().send(CreateTsWithLocationAsk.valueOf(locationId, tsId, recommend), locationId);
        }
    }

    @EventReceiver
    public void onPassEditTimeSliceEvent(PassEditTimeSliceEvent event) {
        var oldEntity = event.getOldEntity();
        var newEntity = event.getNewEntity();
        var recommend = event.isRecommend();

        var oldLocations = new HashSet<Long>();
        var newLocations = new HashSet<Long>();
        if (CollectionUtils.isNotEmpty(oldEntity.getLocations())) {
            oldLocations.addAll(oldEntity.getLocations());
        }
        if (CollectionUtils.isNotEmpty(newEntity.getLocations())) {
            newLocations.addAll(newEntity.getLocations());
        }

        var deleteLocations = new HashSet<>(oldLocations);
        deleteLocations.removeAll(newLocations);


        var tsId = newEntity.getId();
        if (CollectionUtils.isNotEmpty(deleteLocations)) {
            for (var locationId : deleteLocations) {
                NetContext.getConsumer().send(DeleteTsWithLocationAsk.valueOf(locationId, tsId), locationId);
            }
        }
        if (CollectionUtils.isNotEmpty(newLocations)) {
            for (var locationId : newLocations) {
                NetContext.getConsumer().send(CreateTsWithLocationAsk.valueOf(locationId, tsId, recommend), locationId);
            }
        }
    }

}
