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
import com.zfoo.app.zapp.common.entity.feed.FeedPersonEntity;
import com.zfoo.app.zapp.common.protocol.cache.GetPersonCacheAnswer;
import com.zfoo.app.zapp.common.protocol.cache.GetPersonCacheAsk;
import com.zfoo.app.zapp.common.protocol.feed.person.CreateTsWithPersonAsk;
import com.zfoo.app.zapp.common.protocol.feed.person.DeleteTsWithPersonAsk;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.web.feed.model.PersonVO;
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
 * 相关的人相关接口
 * 相关的人推荐，相关的人信息，关注相关的人
 *
 * @author jaysunxiao
 * @version 1.0
 * @module Person模块
 * @since 2020-03-04 14:26
 */
@Controller
public class PersonController {

    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    private SimpleCache<Long, List<Long>> personFeedCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_000
            , items -> {
                var personFeeds = OrmContext.getQuery().queryFieldIn("_id", items, FeedPersonEntity.class);
                var result = new ArrayList<Pair<Long, List<Long>>>();
                for (var feed : personFeeds) {
                    var list = CollectionUtils.listJoinList(true
                            , new Pair<>(AppConstant.COMMON_FEED_PAGE_TOP_LINK_SIZE, feed.getTopLoveLinks().stream().map(topTsLove -> topTsLove.getKey()).collect(Collectors.toList()))
                            , new Pair<>(AppConstant.COMMON_FEED_PAGE_TREND_LINK_SIZE, feed.getTrendLoveLinks().stream().map(trendLove -> trendLove.getKey()).collect(Collectors.toList()))
                            , new Pair<>(AppConstant.COMMON_FEED_PAGE_NEW_LINK_SIZE, feed.getNewLinks())
                            , new Pair<>(AppConstant.COMMON_FEED_PAGE_RECOMMEND_LINK_SIZE, feed.getRecommendLinks()));
                    result.add(new Pair<>(feed.getId(), list));
                }
                return result;
            }
            , key -> Collections.EMPTY_LIST);

    @Autowired
    private TimeSliceService timeSliceService;

    /**
     * 相关的人推荐
     *
     * @param person 相关的人的id
     * @param page   页数，默认是0
     * @return 对象数组 (同搜索内容/api/search)
     */
    @GetMapping(value = "/api/person/feed")
    @ResponseBody
    public BaseResponse personFeed(@RequestParam("person") long person, @RequestParam("page") int page) {
        if (!CommonUtils.isWordIdInRange(List.of(person))) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var list = personFeedCaches.get(person);

        if (CollectionUtils.isEmpty(list)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }
        if (page <= 0) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var personFeedPage = Page.valueOf(page, AppConstant.FEED_PAGE_SIZE, list.size());
        var linkList = personFeedPage.currentPageList(list);

        var timeSliceList = timeSliceService.existTimeSliceList(linkList);

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, timeSliceList);
    }

    /**
     * 相关的人信息
     *
     * @param persons 相关的人的id，用-连接，如1-2-3
     * @return 对象数组
     * @real_return {@link java.util.List< PersonVO >}
     */
    @GetMapping(value = "/api/person/info")
    @ResponseBody
    public BaseResponse personInfo(@RequestParam("persons") String persons) throws Exception {
        var personList = new ArrayList<>(HttpUtils.dashParamToSet(persons));
        if (CollectionUtils.isEmpty(personList)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        if (!CommonUtils.isWordIdInRange(personList)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var firstPerson = personList.stream().findFirst().get();
        var answer = NetContext.getConsumer().syncAsk(GetPersonCacheAsk.valueOf(new HashSet<>(personList)), GetPersonCacheAnswer.class, firstPerson).packet();
        var personVos = answer.getPersonMap()
                .entrySet()
                .stream()
                .map(it -> PersonVO.valueOf(it.getKey(), it.getValue().getLeft(), it.getValue().getMiddle(), it.getValue().getRight()))
                .collect(Collectors.toList());
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, personVos);
    }

    @EventReceiver
    public void onPassReviewTimeSliceEvent(PassReviewTimeSliceEvent event) {
        var entity = event.getNewEntity();
        var persons = entity.getPersons();
        if (CollectionUtils.isEmpty(persons)) {
            return;
        }

        var timeSliceId = entity.getId();
        var recommend = event.isRecommend();
        for (var personId : persons) {
            NetContext.getConsumer().send(CreateTsWithPersonAsk.valueOf(personId, timeSliceId, recommend), personId);
        }
    }

    @EventReceiver
    public void onPassEditTimeSliceEvent(PassEditTimeSliceEvent event) {
        var oldEntity = event.getOldEntity();
        var newEntity = event.getNewEntity();
        var recommend = event.isRecommend();

        var oldPersons = new HashSet<Long>();
        var newPersons = new HashSet<Long>();
        if (CollectionUtils.isNotEmpty(oldEntity.getPersons())) {
            oldPersons.addAll(oldEntity.getPersons());
        }
        if (CollectionUtils.isNotEmpty(newEntity.getPersons())) {
            newPersons.addAll(newEntity.getPersons());
        }

        var deletePersons = new HashSet<>(oldPersons);
        deletePersons.removeAll(newPersons);


        var tsId = newEntity.getId();
        if (CollectionUtils.isNotEmpty(deletePersons)) {
            for (var personId : deletePersons) {
                NetContext.getConsumer().send(DeleteTsWithPersonAsk.valueOf(personId, tsId), personId);
            }
        }
        if (CollectionUtils.isNotEmpty(newPersons)) {
            for (var personId : newPersons) {
                NetContext.getConsumer().send(CreateTsWithPersonAsk.valueOf(personId, tsId, recommend), personId);
            }
        }
    }

}
