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
import com.zfoo.app.zapp.common.entity.feed.FeedItemEntity;
import com.zfoo.app.zapp.common.protocol.cache.GetItemCacheAnswer;
import com.zfoo.app.zapp.common.protocol.cache.GetItemCacheAsk;
import com.zfoo.app.zapp.common.protocol.feed.item.CreateTsWithItemAsk;
import com.zfoo.app.zapp.common.protocol.feed.item.DeleteTsWithItemAsk;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.web.feed.model.ItemVO;
import com.zfoo.app.zapp.web.time.model.event.PassEditTimeSliceEvent;
import com.zfoo.app.zapp.web.time.model.event.PassReviewTimeSliceEvent;
import com.zfoo.app.zapp.web.time.service.ITimeSliceService;
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
 * 相关的物相关接口
 * 相关的物推荐，相关的物信息，关注相关的物
 *
 * @author jaysunxiao
 * @version 1.0
 * @module Item模块
 * @since 2020-03-04 14:26
 */
@Controller
public class ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    private SimpleCache<Long, List<Long>> itemFeedCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_000
            , items -> {
                var itemFeeds = OrmContext.getQuery().queryFieldIn("_id", items, FeedItemEntity.class);
                var result = new ArrayList<Pair<Long, List<Long>>>();
                for (var feed : itemFeeds) {
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
    private ITimeSliceService timeSliceService;

    /**
     * 相关的物推荐
     *
     * @param item 相关的物的id
     * @param page 页数，默认是0
     * @return 对象数组 (同搜索内容/api/search)
     */
    @GetMapping(value = "/api/item/feed")
    @ResponseBody
    public BaseResponse itemFeed(@RequestParam("item") long item, @RequestParam("page") int page) {
        var list = itemFeedCaches.get(item);

        if (CollectionUtils.isEmpty(list)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        if (!CommonUtils.isWordIdInRange(List.of(item))) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        if (page <= 0) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var itemFeedPage = Page.valueOf(page, AppConstant.FEED_PAGE_SIZE, list.size());
        var linkList = itemFeedPage.currentPageList(list);

        var timeSliceList = timeSliceService.existTimeSliceList(linkList);

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, timeSliceList);
    }

    /**
     * 相关的物信息
     *
     * @param items 相关的物的id，用-连接，如1-2-3
     * @return 对象数组
     * @real_return {@link java.util.List<ItemVO>}
     */
    @GetMapping(value = "/api/item/info")
    @ResponseBody
    public BaseResponse itemInfo(@RequestParam("items") String items) throws Exception {
        var itemList = new ArrayList<>(HttpUtils.dashParamToSet(items));
        if (CollectionUtils.isEmpty(itemList)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        if (!CommonUtils.isWordIdInRange(itemList)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var firstItem = itemList.stream().findFirst().get();
        var answer = NetContext.getConsumer().syncAsk(GetItemCacheAsk.valueOf(new HashSet<>(itemList)), GetItemCacheAnswer.class, firstItem).packet();

        var itemVos = answer.getItemMap()
                .entrySet()
                .stream()
                .map(it -> ItemVO.valueOf(it.getKey(), it.getValue().getLeft(), it.getValue().getMiddle(), it.getValue().getRight()))
                .collect(Collectors.toList());

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, itemVos);
    }

    @EventReceiver
    public void onPassReviewTimeSliceEvent(PassReviewTimeSliceEvent event) {
        var entity = event.getNewEntity();
        var items = entity.getItems();
        if (CollectionUtils.isEmpty(items)) {
            return;
        }

        var timeSliceId = entity.getId();
        var recommend = event.isRecommend();
        for (var itemId : items) {
            NetContext.getConsumer().send(CreateTsWithItemAsk.valueOf(itemId, timeSliceId, recommend), itemId);
        }
    }

    @EventReceiver
    public void onPassEditTimeSliceEvent(PassEditTimeSliceEvent event) {
        var oldEntity = event.getOldEntity();
        var newEntity = event.getNewEntity();
        var recommend = event.isRecommend();

        var oldItems = new HashSet<Long>();
        var newItems = new HashSet<Long>();
        if (CollectionUtils.isNotEmpty(oldEntity.getItems())) {
            oldItems.addAll(oldEntity.getItems());
        }
        if (CollectionUtils.isNotEmpty(newEntity.getItems())) {
            newItems.addAll(newEntity.getItems());
        }

        var deleteItems = new HashSet<>(oldItems);
        deleteItems.removeAll(newItems);


        var tsId = newEntity.getId();
        if (CollectionUtils.isNotEmpty(deleteItems)) {
            for (var itemId : deleteItems) {
                NetContext.getConsumer().send(DeleteTsWithItemAsk.valueOf(itemId, tsId), itemId);
            }
        }
        if (CollectionUtils.isNotEmpty(newItems)) {
            for (var itemId : newItems) {
                NetContext.getConsumer().send(CreateTsWithItemAsk.valueOf(itemId, tsId, recommend), itemId);
            }
        }
    }

}
