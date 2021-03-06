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
import com.zfoo.app.zapp.common.entity.time.TsEditEntity;
import com.zfoo.app.zapp.common.entity.time.TsReviewEntity;
import com.zfoo.app.zapp.common.protocol.cache.GetUserTsCacheAnswer;
import com.zfoo.app.zapp.common.protocol.cache.GetUserTsCacheAsk;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserProfileStarAnswer;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserProfileStarAsk;
import com.zfoo.app.zapp.common.protocol.user.time.StarTimeSliceAsk;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.web.time.model.StarTimeSliceRequest;
import com.zfoo.app.zapp.web.time.model.vo.TimeSliceRejectVO;
import com.zfoo.app.zapp.web.time.model.vo.TimeSliceReviewVO;
import com.zfoo.app.zapp.web.time.service.TimeSliceService;
import com.zfoo.app.zapp.web.user.model.GetUserCreationResponse;
import com.zfoo.app.zapp.web.util.HttpUtils;
import com.zfoo.net.NetContext;
import com.zfoo.net.packet.common.Message;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.model.query.Page;
import com.zfoo.protocol.collection.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-10-04 11:57
 */
@Controller
public class TsUserController {

    @Autowired
    private TimeSliceService timeSliceService;

    /**
     * ????????????????????????
     * ???????????????id??????????????????????????????????????????????????????????????????????????????
     *
     * @param id   ??????id
     * @param page ??????????????????0
     * @return ???????????? (???????????????/api/search)
     */
    @GetMapping(value = "/api/time/user")
    @ResponseBody
    public BaseResponse userTs(@RequestParam("id") long id, @RequestParam("page") int page) throws Exception {
        if (!CommonUtils.isUserIdInRange(List.of(id))) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        if (page <= 0) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var answer = NetContext.getConsumer().syncAsk(GetUserTsCacheAsk.valueOf(Set.of(id)), GetUserTsCacheAnswer.class, id).packet();
        var userTsMap = answer.getUserTsMap();
        if (CollectionUtils.isEmpty(userTsMap)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }
        var tsList = userTsMap.get(id);
        if (CollectionUtils.isEmpty(tsList)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var tsPage = Page.valueOf(page, AppConstant.USER_TS_PAGE_SIZE, tsList.size());
        var linkList = tsPage.currentPageList(tsList);

        var timeSliceList = timeSliceService.existTimeSliceList(linkList);
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, timeSliceList);
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     *
     * @return ?????????tsReviews + tsEdits + tsReviewRejects + tsEditRejects??????????????????????????????????????????????????????
     */
    @GetMapping("/api/user/creation")
    @ResponseBody
    public BaseResponse getUserCreation(HttpServletRequest request) {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var tsReviewList = OrmContext.getQuery(TsReviewEntity.class).eq("userId", userId).queryAll();
        var tsEditList = OrmContext.getQuery(TsEditEntity.class).eq("userId", userId).queryAll();

        var tsReviews = new ArrayList<TimeSliceReviewVO>();
        var tsReviewRejects = new ArrayList<TimeSliceRejectVO>();
        for (var tsReview : tsReviewList) {
            if (ReportConstant.reportMap.containsKey(tsReview.getOptionType())) {
                timeSliceService.converter(List.of(tsReview.getTimeSlice())).stream()
                        .findFirst()
                        .ifPresent(it -> tsReviewRejects.add(TimeSliceRejectVO.valueOf(tsReview.getId(), tsReview.getUserId()
                                , tsReview.getOptionType(), tsReview.getSelectedItems(), tsReview.getContent(), it)));
            } else {
                timeSliceService.converter(List.of(tsReview.getTimeSlice())).stream()
                        .findFirst()
                        .ifPresent(it -> tsReviews.add(TimeSliceReviewVO.valueOf(tsReview.getId(), tsReview.getUserId(), it)));
            }
        }


        var tsEdits = new ArrayList<TimeSliceReviewVO>();
        var tsEditRejects = new ArrayList<TimeSliceRejectVO>();
        for (var tsEdit : tsEditList) {
            if (ReportConstant.reportMap.containsKey(tsEdit.getOptionType())) {
                timeSliceService.converter(List.of(tsEdit.getTimeSlice())).stream()
                        .findFirst()
                        .ifPresent(it -> tsReviewRejects.add(TimeSliceRejectVO.valueOf(tsEdit.getId(), tsEdit.getUserId(), tsEdit.getOptionType(), tsEdit.getSelectedItems(), tsEdit.getContent(), it)));

            } else {
                timeSliceService.converter(List.of(tsEdit.getTimeSlice())).stream()
                        .findFirst()
                        .ifPresent(it -> tsEdits.add(TimeSliceReviewVO.valueOf(tsEdit.getId(), tsEdit.getUserId(), it)));
            }
        }
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, GetUserCreationResponse.valueOf(tsReviews, tsEdits, tsReviewRejects, tsEditRejects));
    }


    /**
     * ????????????????????????
     *
     * @param cm ??????????????????
     * @return ??????????????????
     */
    @PostMapping(value = "/api/time/star", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse starTimeSlice(HttpServletRequest request, @RequestBody StarTimeSliceRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        var tsId = cm.getTsId();
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        if (!CommonUtils.isTsIdInRange(List.of(tsId))) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        var message = NetContext.getConsumer().syncAsk(StarTimeSliceAsk.valueOf(userId, tsId), Message.class, userId).packet();
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }

    /**
     * ????????????????????????
     *
     * @param page ??????
     * @return ???????????? (???????????????/api/search)
     */
    @GetMapping(value = "/api/user/star/list")
    @ResponseBody
    public BaseResponse userStarList(HttpServletRequest request, @RequestParam("page") int page) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        if (page <= 0) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var answer = NetContext.getConsumer().syncAsk(GetUserProfileStarAsk.valueOf(userId), GetUserProfileStarAnswer.class, userId).packet();
        var stars = answer.getStars();
        var starPage = Page.valueOf(page, AppConstant.USER_LIST_PAGE_SIZE, stars.size());
        var starList = starPage.currentPageList(stars);

        if (CollectionUtils.isEmpty(starList)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var tsList = timeSliceService.timeSliceCaches.batchGet(starList).values();
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, tsList);
    }

}
