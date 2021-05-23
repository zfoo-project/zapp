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

package com.zfoo.app.zapp.web.time.controller;

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.constant.ReportConstant;
import com.zfoo.app.zapp.common.entity.time.TimeSliceEntity;
import com.zfoo.app.zapp.common.entity.time.TsEditEntity;
import com.zfoo.app.zapp.common.entity.time.TsReviewEntity;
import com.zfoo.app.zapp.common.entity.time.model.TimeLinkAlbumPO;
import com.zfoo.app.zapp.common.entity.user.model.AdminAuthEnum;
import com.zfoo.app.zapp.common.protocol.cache.GetUserTsCacheAnswer;
import com.zfoo.app.zapp.common.protocol.cache.GetUserTsCacheAsk;
import com.zfoo.app.zapp.common.protocol.cache.refresh.RefreshUserTsCacheAsk;
import com.zfoo.app.zapp.common.protocol.common.time.TimeKeyVO;
import com.zfoo.app.zapp.common.protocol.common.time.TimeLinkAlbumVO;
import com.zfoo.app.zapp.common.protocol.common.time.VideoLinkVO;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserAdminAuthAnswer;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserAdminAuthAsk;
import com.zfoo.app.zapp.common.protocol.user.time.UpdateTimeSliceAsk;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.web.time.model.*;
import com.zfoo.app.zapp.web.time.model.event.*;
import com.zfoo.app.zapp.web.time.model.vo.TimeSliceReviewVO;
import com.zfoo.app.zapp.web.time.service.ITimeSliceService;
import com.zfoo.app.zapp.web.util.HttpUtils;
import com.zfoo.app.zapp.web.word.service.IWordService;
import com.zfoo.event.manager.EventBus;
import com.zfoo.net.NetContext;
import com.zfoo.net.packet.common.Message;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-11-14 20:48
 */
@Controller
public class TsCreateController {

    private static final Logger logger = LoggerFactory.getLogger(TsCreateController.class);

    @Autowired
    private ITimeSliceService timeSliceService;

    @Autowired
    private IWordService wordService;

    /**
     * 用户创建时间片
     *
     * @param cm 用户创建时间片的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/time/creation", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse creation(HttpServletRequest request, @RequestBody CreateTimeSliceRequest cm) throws ParseException {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var tsReviewList = new ArrayList<TsReviewEntity>();
        for (var vo : cm.getCreateList()) {
            var baseResponse = timeSliceService.checkCreateTimeSliceVO(vo);
            if (baseResponse != null) {
                return baseResponse;
            }

            var type = vo.getType();
            var start = vo.getStart();
            var end = vo.getEnd();
            var locations = vo.getLocations();
            var persons = vo.getPersons();
            var items = timeSliceService.toItems(vo.getItems(), vo.getNotExistItems());
            var content = StringUtils.trim(vo.getContent());
            var images = CollectionUtils.isNotEmpty(vo.getImages())
                    ? vo.getImages().stream().map(it -> it.trim()).collect(Collectors.toList())
                    : Collections.EMPTY_LIST;
            var video = vo.getVideo();
            if (video != null) {
                video.trim();
            }
            var key = vo.getKey();
            if (key != null) {
                key.trim();
            }
            var albums = vo.getAlbums();
            if (CollectionUtils.isNotEmpty(albums)) {
                albums.forEach(it -> it.trim());
            }
            var startDateTime = TimeUtils.stringToDate(start);
            var endDateTime = TimeUtils.stringToDate(end);

            // 插入到数据库
            var id = MongoIdUtils.getIncrementIdFromMongoDefault(TsReviewEntity.class);
            var timeSliceEntity = TimeSliceEntity.valueOf(0L, userId, TimeUtils.now(), type, startDateTime.getTime(), endDateTime.getTime()
                    , locations, persons, items, content, images
                    , video, key, albums);

            var timeSliceReviewEntity = TsReviewEntity.valueOf(id, userId, timeSliceEntity, null);
            tsReviewList.add(timeSliceReviewEntity);
        }

        var reviewLinks = tsReviewList.stream().map(it -> it.getId()).collect(Collectors.toList());
        tsReviewList.forEach(it -> it.setReviewLinks(reviewLinks));
        OrmContext.getAccessor().batchInsert(tsReviewList);
        return BaseResponse.valueOf(CodeEnum.OK);
    }

    /**
     * 重新提交时间片
     * 一般用于提交时间片没有过审，失败后再重新提交
     *
     * @param cm 重新提交时间片的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/time/recommit", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse recommit(HttpServletRequest request, @RequestBody RecommitTimeSliceRequest cm) throws ParseException {
        logger.info(JsonUtils.object2String(cm));
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var reviewId = cm.getReviewId();
        var vo = cm.getVo();

        var baseResponse = timeSliceService.checkCreateTimeSliceVO(vo);
        if (baseResponse != null) {
            return baseResponse;
        }

        var type = vo.getType();
        var start = vo.getStart();
        var end = vo.getEnd();
        var locations = vo.getLocations();
        var persons = vo.getPersons();
        var items = timeSliceService.toItems(vo.getItems(), vo.getNotExistItems());
        var content = vo.getContent();
        var images = vo.getImages();
        var video = vo.getVideo();
        var key = vo.getKey();
        var albums = vo.getAlbums();

        var startDateTime = TimeUtils.stringToDate(start);
        var endDateTime = TimeUtils.stringToDate(end);

        var newTsEntity = TimeSliceEntity.valueOf(0L, userId, TimeUtils.now(), type, startDateTime.getTime(), endDateTime.getTime()
                , locations, persons, items, content, images
                , video, key, albums);

        var tsReviewEntity = OrmContext.getAccessor().load(reviewId, TsReviewEntity.class);
        if (tsReviewEntity == null) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }
        var oldTsEntity = tsReviewEntity.getTimeSlice();

        tsReviewEntity.setTimeSlice(newTsEntity);
        tsReviewEntity.clearOption();
        OrmContext.getAccessor().update(tsReviewEntity);

        EventBus.syncSubmit(RecommitTimeSliceEvent.valueOf(oldTsEntity, newTsEntity));
        return BaseResponse.valueOf(CodeEnum.OK);
    }


    /**
     * 编辑时间片
     *
     * @param cm 编辑时间片的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/time/edit", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse editTs(HttpServletRequest request, @RequestBody EditTimeSliceRequest cm) throws ParseException {
        logger.info(JsonUtils.object2String(cm));
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var editId = cm.getEditId();
        var vo = cm.getVo();

        var baseResponse = timeSliceService.checkCreateTimeSliceVO(vo);
        if (baseResponse != null) {
            return baseResponse;
        }
        var tsList = timeSliceService.existTimeSliceList(List.of(editId));
        if (CollectionUtils.isEmpty(tsList)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }
        if (tsList.get(0).getUserInfo().getId() != userId) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }

        var type = vo.getType();
        var start = vo.getStart();
        var end = vo.getEnd();
        var locations = vo.getLocations();
        var persons = vo.getPersons();
        var items = timeSliceService.toItems(vo.getItems(), vo.getNotExistItems());
        var content = vo.getContent();
        var images = vo.getImages();
        var video = vo.getVideo();
        var key = vo.getKey();
        var albums = vo.getAlbums();

        var startDateTime = TimeUtils.stringToDate(start);
        var endDateTime = TimeUtils.stringToDate(end);
        var timeSliceEntity = TimeSliceEntity.valueOf(editId, userId, TimeUtils.now(), type, startDateTime.getTime(), endDateTime.getTime()
                , locations, persons, items, content, images
                , video, key, albums);

        var oldEditEntity = OrmContext.getAccessor().load(editId, TsEditEntity.class);
        var currentEntity = OrmContext.getAccessor().load(editId, TimeSliceEntity.class);

        var newEditEntity = TsEditEntity.valueOf(editId, userId, timeSliceEntity);

        if (oldEditEntity == null) {
            OrmContext.getAccessor().insert(newEditEntity);
        } else {
            OrmContext.getAccessor().update(newEditEntity);
            newEditEntity.clearOption();
        }

        EventBus.syncSubmit(EditTimeSliceEvent.valueOf(
                oldEditEntity == null ? null : oldEditEntity.getTimeSlice(), newEditEntity.getTimeSlice(), currentEntity));
        return BaseResponse.valueOf(CodeEnum.OK);
    }

    /**
     * 删除时间片
     * 玩家可以删除自己创建的时间片
     *
     * @param timeLinks 时间片id列表，用-连接，如1-2-3
     * @return 一个空的对象
     */
    @DeleteMapping(value = "/api/time/delete")
    @ResponseBody
    public BaseResponse deleteTs(HttpServletRequest request, @RequestParam("timeLinks") String timeLinks) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }
        var linkList = new ArrayList<>(HttpUtils.dashParamToSet(timeLinks));
        if (CollectionUtils.isEmpty(linkList)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_NOT_MATCH);
        }

        if (!CommonUtils.isTsIdInRange(linkList)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        var notExistTimeLinks = timeSliceService.notExistTimeLinks(linkList);

        if (notExistTimeLinks.size() != 0) {
            return BaseResponse.valueOf(CodeEnum.TIME_SLICE_NOT_EXIST);
        }

        var answer = NetContext.getConsumer().syncAsk(GetUserTsCacheAsk.valueOf(Set.of(userId)), GetUserTsCacheAnswer.class, userId).packet();
        var userTsMap = answer.getUserTsMap();
        if (CollectionUtils.isEmpty(userTsMap)) {
            return BaseResponse.valueOf(CodeEnum.FAIL);
        }
        var tsList = userTsMap.get(userId);
        if (CollectionUtils.isEmpty(tsList)) {
            return BaseResponse.valueOf(CodeEnum.FAIL_ONE);
        }
        for (var tsId : linkList) {
            if (tsList.stream().allMatch(it -> it.longValue() != tsId.longValue())) {
                return BaseResponse.valueOf(CodeEnum.FAIL_TWO);
            }
        }

        linkList.forEach(it -> timeSliceService.deleteTimeSlice(it));
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }

    /**
     * 审核通过时间片
     * 管理员审核通过的时间片
     *
     * @param cm 审核通过时间片的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/time/review/pass", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse passReview(HttpServletRequest request, @RequestBody PassTimeSliceReviewRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }
        var answer = NetContext.getConsumer().syncAsk(GetUserAdminAuthAsk.valueOf(userId), GetUserAdminAuthAnswer.class, userId).packet();
        if (!AdminAuthEnum.getAuthEnumByType(answer.getAdminAuth()).hasAuth(AdminAuthEnum.BASE_AUTH)) {
            return BaseResponse.valueOf(CodeEnum.FAIL);
        }

        var timeSliceReviewId = cm.getTsReviewId();
        var recommend = cm.isRecommend();


        var tsReviewEntity = OrmContext.getAccessor().load(timeSliceReviewId, TsReviewEntity.class);
        if (tsReviewEntity == null) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        var reviewList = OrmContext.getQuery().queryFieldIn("_id", tsReviewEntity.getReviewLinks(), TsReviewEntity.class);
        if (CollectionUtils.isEmpty(reviewList)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }

        var tsList = new ArrayList<TimeSliceEntity>();
        for (var review : reviewList) {
            var tsEntity = review.getTimeSlice();
            var id = tsEntity.getId() == 0L
                    ? MongoIdUtils.getIncrementIdFromMongoDefault(TimeSliceEntity.class)
                    : tsEntity.getId();
            tsEntity.setId(id);
            tsList.add(tsEntity);
        }

        if (tsList.size() > 1) {
            var tsLinks = tsList.stream().map(it -> it.getId()).collect(Collectors.toList());
            var linkAlbumPO = TimeLinkAlbumPO.valueOf(StringUtils.EMPTY, tsLinks);
            tsList.forEach(it -> {
                var albums = it.getAlbums();
                if (CollectionUtils.isEmpty(albums)) {
                    it.setAlbums(List.of(linkAlbumPO));
                } else {
                    albums.add(linkAlbumPO);
                }
            });
        }
        OrmContext.getAccessor().batchDelete(reviewList);
        OrmContext.getAccessor().batchInsert(tsList);

        // 刷新用户的时间片缓存
        NetContext.getConsumer().send(RefreshUserTsCacheAsk.valueOf(Set.of(userId)), userId);
        // 抛出一个异步处理事件
        tsList.forEach(it -> EventBus.asyncSubmit(PassReviewTimeSliceEvent.valueOf(it, recommend)));
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, PassTimeSliceReviewResponse.valueOf(tsReviewEntity.getReviewLinks()));
    }

    /**
     * 审核通过编辑的时间片
     *
     * @param cm 审核通过编辑的时间片的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/time/edit/pass", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse passEdit(HttpServletRequest request, @RequestBody PassTimeSliceEditRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }
        var answer = NetContext.getConsumer().syncAsk(GetUserAdminAuthAsk.valueOf(userId), GetUserAdminAuthAnswer.class, userId).packet();
        if (!AdminAuthEnum.getAuthEnumByType(answer.getAdminAuth()).hasAuth(AdminAuthEnum.BASE_AUTH)) {
            return BaseResponse.valueOf(CodeEnum.FAIL);
        }

        var tsEditId = cm.getTsEditId();
        var recommend = cm.isRecommend();


        var tsEditEntity = OrmContext.getAccessor().load(tsEditId, TsEditEntity.class);
        if (tsEditEntity == null) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        var oldTsEntity = OrmContext.getAccessor().load(tsEditId, TimeSliceEntity.class);
        if (oldTsEntity == null) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }

        var newEntity = tsEditEntity.getTimeSlice();

        var ask = UpdateTimeSliceAsk.valueOf(tsEditId, newEntity.getType(), newEntity.getStart(), newEntity.getEnd()
                , newEntity.getLocations(), newEntity.getPersons(), newEntity.getItems(), newEntity.getContent(), newEntity.getImages()
                , VideoLinkVO.valueOf(newEntity.getVideo()), TimeKeyVO.valueOf(newEntity.getKey()), TimeLinkAlbumVO.posToVos(newEntity.getAlbums()));
        NetContext.getConsumer().syncAsk(ask, Message.class, tsEditId);

        OrmContext.getAccessor().delete(tsEditId, TsEditEntity.class);

        // 抛出一个异步处理事件
        EventBus.asyncSubmit(PassEditTimeSliceEvent.valueOf(oldTsEntity, newEntity, recommend));
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }

    /**
     * 审核拒绝创建的时间片
     *
     * @param cm 审核拒绝创建的时间片的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/time/review/reject", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse rejectReview(HttpServletRequest request, @RequestBody RejectTimeSliceReviewRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var tsReviewId = cm.getTsReviewId();
        var optionType = cm.getOptionType();
        var selectedItems = cm.getSelectedItems();
        var content = cm.getContent();

        if (!ReportConstant.reportMap.containsKey(optionType)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        for (var item : selectedItems) {
            if (!ReportConstant.reportMap.containsKey(item)) {
                return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
            }
        }

        var answer = NetContext.getConsumer().syncAsk(GetUserAdminAuthAsk.valueOf(userId), GetUserAdminAuthAnswer.class, userId).packet();
        if (!AdminAuthEnum.getAuthEnumByType(answer.getAdminAuth()).hasAuth(AdminAuthEnum.BASE_AUTH)) {
            return BaseResponse.valueOf(CodeEnum.FAIL);
        }

        var tsReviewEntity = OrmContext.getAccessor().load(tsReviewId, TsReviewEntity.class);
        if (tsReviewEntity == null) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        tsReviewEntity.setOptionType(optionType);
        tsReviewEntity.setSelectedItems(selectedItems);
        tsReviewEntity.setContent(content);
        OrmContext.getAccessor().update(tsReviewEntity);

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }

    /**
     * 审核拒绝编辑的时间片
     *
     * @param cm 审核拒绝编辑的时间片的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/time/edit/reject", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse rejectEdit(HttpServletRequest request, @RequestBody RejectTimeSliceEditRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var tsEditId = cm.getTsEditId();
        var optionType = cm.getOptionType();
        var selectedItems = cm.getSelectedItems();
        var content = cm.getContent();

        if (!ReportConstant.reportMap.containsKey(optionType)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        for (var item : selectedItems) {
            if (!ReportConstant.reportMap.containsKey(item)) {
                return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
            }
        }

        var answer = NetContext.getConsumer().syncAsk(GetUserAdminAuthAsk.valueOf(userId), GetUserAdminAuthAnswer.class, userId).packet();
        if (!AdminAuthEnum.getAuthEnumByType(answer.getAdminAuth()).hasAuth(AdminAuthEnum.BASE_AUTH)) {
            return BaseResponse.valueOf(CodeEnum.FAIL);
        }

        var tsEditEntity = OrmContext.getAccessor().load(tsEditId, TsEditEntity.class);
        if (tsEditEntity == null) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }
        tsEditEntity.setOptionType(optionType);
        tsEditEntity.setSelectedItems(selectedItems);
        tsEditEntity.setContent(content);

        OrmContext.getAccessor().update(tsEditEntity);
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }


    /**
     * 删除我的审核中的时间片
     *
     * @param reviewIds 时间片id列表，用-连接，如1-2-3
     * @return 一个空的对象
     */
    @DeleteMapping(value = "/api/time/review/delete")
    @ResponseBody
    public BaseResponse deleteTsReview(HttpServletRequest request, @RequestParam("reviewIds") String reviewIds) {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }
        var reviewSet = HttpUtils.dashParamToSet(reviewIds);
        if (CollectionUtils.isEmpty(reviewSet)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_NOT_MATCH);
        }
        var list = OrmContext.getQuery().queryFieldIn("_id", new ArrayList<>(reviewSet), TsReviewEntity.class);
        if (list.size() != reviewSet.size()) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }
        if (list.stream().anyMatch(it -> it.getUserId() != userId)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }

        OrmContext.getAccessor().batchDelete(new ArrayList<>(reviewSet), TsReviewEntity.class);

        var timeSlices = list.stream().map(it -> it.getTimeSlice()).collect(Collectors.toList());
        EventBus.syncSubmit(DeleteReviewTimeSliceEvent.value(timeSlices));

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }

    /**
     * 删除我的被拒绝的时间片
     *
     * @param rejectIds 时间片id列表，用-连接，如1-2-3
     * @return 一个空的对象
     */
    @DeleteMapping(value = "/api/time/review/reject/delete")
    @ResponseBody
    public BaseResponse deleteTsReviewReject(HttpServletRequest request, @RequestParam("reviewIds") String rejectIds) {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }
        var reviewRejectSet = HttpUtils.dashParamToSet(rejectIds);
        if (CollectionUtils.isEmpty(reviewRejectSet)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_NOT_MATCH);
        }
        var list = OrmContext.getQuery().queryFieldIn("_id", new ArrayList<>(reviewRejectSet), TsReviewEntity.class);
        if (list.size() != reviewRejectSet.size()) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }
        if (list.stream().anyMatch(it -> it.getUserId() != userId)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }

        OrmContext.getAccessor().batchDelete(new ArrayList<>(reviewRejectSet), TsReviewEntity.class);

        var timeSlices = list.stream().map(it -> it.getTimeSlice()).collect(Collectors.toList());
        EventBus.syncSubmit(DeleteReviewTimeSliceEvent.value(timeSlices));

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }

    /**
     * 删除我的编辑的时间片
     *
     * @param editIds 时间片id列表，用-连接，如1-2-3
     * @return 一个空的对象
     */
    @DeleteMapping(value = "/api/time/edit/delete")
    @ResponseBody
    public BaseResponse deleteTsEdit(HttpServletRequest request, @RequestParam("editIds") String editIds) {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }
        var editSet = HttpUtils.dashParamToSet(editIds);
        if (CollectionUtils.isEmpty(editSet)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_NOT_MATCH);
        }
        var list = OrmContext.getQuery().queryFieldIn("_id", new ArrayList<>(editSet), TsEditEntity.class);
        if (list.size() != editSet.size()) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }
        if (list.stream().anyMatch(it -> it.getUserId() != userId)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }

        OrmContext.getAccessor().batchDelete(new ArrayList<>(editSet), TsEditEntity.class);

        var tsIds = list.stream().map(it -> it.getTimeSlice().getId()).collect(Collectors.toList());
        var timeSlices = OrmContext.getQuery().queryFieldIn("_id", tsIds, TimeSliceEntity.class);
        var editTimeSlices = list.stream().map(it -> it.getTimeSlice()).collect(Collectors.toList());
        EventBus.syncSubmit(DeleteEditTimeSliceEvent.value(editTimeSlices, timeSlices));

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }

    /**
     * 删除我的编辑被拒绝的时间片
     *
     * @param editIds 时间片id列表，用-连接，如1-2-3
     * @return 一个空的对象
     */
    @DeleteMapping(value = "/api/time/edit/reject/delete")
    @ResponseBody
    public BaseResponse deleteTsEditReject(HttpServletRequest request, @RequestParam("editIds") String editIds) {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }
        var editRejectSet = HttpUtils.dashParamToSet(editIds);
        if (CollectionUtils.isEmpty(editRejectSet)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_NOT_MATCH);
        }
        var list = OrmContext.getQuery().queryFieldIn("_id", new ArrayList<>(editRejectSet), TsEditEntity.class);
        if (list.size() != editRejectSet.size()) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }
        if (list.stream().anyMatch(it -> it.getUserId() != userId)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }

        OrmContext.getAccessor().batchDelete(new ArrayList<>(editRejectSet), TsEditEntity.class);

        var tsIds = list.stream().map(it -> it.getTimeSlice().getId()).collect(Collectors.toList());
        var timeSlices = OrmContext.getQuery().queryFieldIn("_id", tsIds, TimeSliceEntity.class);
        var editTimeSlices = list.stream().map(it -> it.getTimeSlice()).collect(Collectors.toList());
        EventBus.syncSubmit(DeleteEditTimeSliceEvent.value(editTimeSlices, timeSlices));

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }


    /**
     * 审核列表
     *
     * @param page 页数，默认是0
     * @return reviews (同搜索内容/api/search) + edits (同搜索内容/api/search)
     */
    @GetMapping("/api/time/review/ts")
    @ResponseBody
    public BaseResponse reviewTs(HttpServletRequest request, @RequestParam("page") int page) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var answer = NetContext.getConsumer().syncAsk(GetUserAdminAuthAsk.valueOf(userId), GetUserAdminAuthAnswer.class, userId).packet();
        if (!AdminAuthEnum.getAuthEnumByType(answer.getAdminAuth()).hasAuth(AdminAuthEnum.BASE_AUTH)) {
            return BaseResponse.valueOf(CodeEnum.FAIL);
        }

        if (page <= 0) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var reviewList = new ArrayList<TimeSliceReviewVO>();
        OrmContext.getQuery().pageQuery(page, AppConstant.TS_REVIEW_PER_PAGE, TsReviewEntity.class)
                .getValue()
                .stream()
                .filter(it -> !ReportConstant.reportMap.containsKey(it.getOptionType()))
                .forEach(it -> {
                    var timeSliceVO = timeSliceService.converter(List.of(it.getTimeSlice())).stream().findFirst().get();
                    reviewList.add(TimeSliceReviewVO.valueOf(it.getId(), it.getTimeSlice().getUserId(), timeSliceVO));
                });


        // edit相关
        var editList = new ArrayList<TimeSliceReviewVO>();
        OrmContext.getQuery().pageQuery(page, AppConstant.TS_REVIEW_PER_PAGE, TsEditEntity.class)
                .getValue()
                .stream()
                .filter(it -> !ReportConstant.reportMap.containsKey(it.getOptionType()))
                .forEach(it -> {
                    var timeSliceVO = timeSliceService.converter(List.of(it.getTimeSlice())).stream().findFirst().get();
                    reviewList.add(TimeSliceReviewVO.valueOf(it.getId(), it.getTimeSlice().getUserId(), timeSliceVO));
                });

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, ReviewTimeSliceResponse.valueOf(reviewList, editList));
    }


}
