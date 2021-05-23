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
import com.zfoo.app.zapp.common.entity.admin.ReportEntity;
import com.zfoo.app.zapp.common.model.OssPolicyEnum;
import com.zfoo.app.zapp.common.model.time.TimeSliceVO;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.web.time.model.ReportTimeSliceRequest;
import com.zfoo.app.zapp.web.time.model.SingleTimeSliceResponse;
import com.zfoo.app.zapp.web.time.service.ITimeSliceService;
import com.zfoo.app.zapp.web.util.HttpUtils;
import com.zfoo.app.zapp.web.word.service.IWordService;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * 时间片相关接口
 * 获取用户的时间片，批量时间片信息查询，单个时间片信息查询，时间片点赞，举报时间片，用户创建时间片，重新提交时间片，时间片竞争
 * 编辑时间片，删除时间片，审核通过时间片，审核通过编辑的时间片，审核拒绝创建的时间片，审核拒绝编辑的时间片，删除我的审核中的时间片
 * 删除我的被拒绝的时间片，删除我的编辑的时间片，删除我的编辑被拒绝的时间片，审核列表，用户创建时间片的相关的人的提示
 * 用户创建时间片的相关的物的提示，用户创建时间片的时间片链接是否合法
 *
 * @author jaysunxiao
 * @version 1.0
 * @module TimeSlice模块
 * @since 2020-01-28 13:55
 */
@Controller
public class TimeSliceController {

    private static final Logger logger = LoggerFactory.getLogger(TimeSliceController.class);

    @Autowired
    private ITimeSliceService timeSliceService;

    @Autowired
    private IWordService wordService;


    /**
     * 批量时间片信息查询
     * 通过时间片id获取时间片的具体信息
     *
     * @param timeLinks 时间片id列表，用-连接，如1-2-3
     * @return 对象数组 (同搜索内容/api/search)
     */
    @GetMapping(value = "/api/time/info")
    @ResponseBody
    public BaseResponse timeInfo(@RequestParam("timeLinks") String timeLinks) {
        var linkList = new ArrayList<>(HttpUtils.dashParamToSet(timeLinks));

        if (CollectionUtils.isEmpty(linkList)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        if (!CommonUtils.isTsIdInRange(linkList)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        var timeSliceList = timeSliceService.existTimeSliceList(linkList);

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, timeSliceList);
    }

    /**
     * 单个时间片信息查询
     * 通过时间片id获取时间片的具体信息
     *
     * @param timeSliceId 时间片的id
     * @param dimension   维度
     * @return 对象，now + rootTimeSlice + dimensionTimeSliceList，分别为当前时间，根时间片，相关的时间片数组
     */
    @GetMapping(value = "/api/singleTimeSlice/info")
    @ResponseBody
    public BaseResponse singleTimeSlice(@RequestParam("id") long timeSliceId, @RequestParam("dimension") int dimension) {
        if (!CommonUtils.isTsIdInRange(List.of(timeSliceId))) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_URL_ERROR);
        }

        if (dimension > AppConstant.DIMENSION_LIMIT) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        var timeSliceList = timeSliceService.existTimeSliceList(List.of(timeSliceId));

        if (CollectionUtils.isEmpty(timeSliceList)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, null);
        }

        var rootTimeSlice = timeSliceList.get(0);
        if (CollectionUtils.isEmpty(rootTimeSlice.getAlbums())) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, SingleTimeSliceResponse.valueOf(rootTimeSlice, Collections.EMPTY_LIST));
        }

        var links = new ArrayList<Long>();
        rootTimeSlice.getAlbums().stream().forEach(it -> links.addAll(it.getLinks()));


        var set = new HashSet<TimeSliceVO>();
        for (var i = 0; i < dimension; i++) {
            var tsList = timeSliceService.existTimeSliceList(links);
            set.addAll(tsList);
            links.clear();
            for (var ts : tsList) {
                if (CollectionUtils.isNotEmpty(ts.getAlbums())) {
                    ts.getAlbums().stream().forEach(it -> links.addAll(it.getLinks()));
                }
            }
        }

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, SingleTimeSliceResponse.valueOf(rootTimeSlice, new ArrayList<>(set)));
    }

    /**
     * 举报时间片
     *
     * @param cm 举报时间片的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/time/report", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse report(HttpServletRequest request, @RequestBody ReportTimeSliceRequest cm) {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var timeSliceId = cm.getTimeSliceId();
        var optionType = cm.getOptionType();
        var selectedItems = cm.getSelectedItems();
        var content = cm.getContent();
        var fileLinks = cm.getFileLinks();

        if (!CommonUtils.isTsIdInRange(List.of(timeSliceId))) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        if (!ReportConstant.reportMap.containsKey(optionType)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }

        for (var item : selectedItems) {
            if (!ReportConstant.reportMap.containsKey(item)) {
                return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_TWO);
            }
        }

        if (CollectionUtils.isNotEmpty(fileLinks)) {
            if (!HttpUtils.isHttpUrls(fileLinks)) {
                return BaseResponse.valueOf(CodeEnum.PARAMETER_URL_ERROR);
            }
            if (fileLinks.stream().anyMatch(it -> !it.startsWith(OssPolicyEnum.REPORT.getUrl()))) {
                return BaseResponse.valueOf(CodeEnum.PARAMETER_URL_ERROR);
            }
        }


        var result = timeSliceService.notExistTimeLinks(List.of(timeSliceId));
        if (result.size() != 0) {
            return BaseResponse.valueOf(CodeEnum.TIME_SLICE_NOT_EXIST);
        }


        var id = MongoIdUtils.getIncrementIdFromMongoDefault(ReportEntity.class);
        var entity = ReportEntity.valueOf(id, timeSliceId, optionType, selectedItems, content, fileLinks);
        OrmContext.getAccessor().insert(entity);
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }

    /**
     * 用户创建时间片的时间片链接是否合法
     *
     * @param timeLinks 时间片id列表，用-连接，如1-2-3
     * @return 一个空的对象
     */
    @GetMapping(value = "/api/time/creation/checkTimeLinks")
    @ResponseBody
    public BaseResponse checkTimeLinks(@RequestParam("timeLinks") String timeLinks) {
        var linkList = new ArrayList<>(HttpUtils.dashParamToSet(timeLinks));
        if (CollectionUtils.isEmpty(linkList)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_EMPTY);
        }

        if (!CommonUtils.isTsIdInRange(linkList)) {
            return BaseResponse.valueOf(CodeEnum.TIME_SLICE_NOT_EXIST);
        }

        var notExistTimeLinks = timeSliceService.notExistTimeLinks(linkList);

        if (notExistTimeLinks.size() != 0) {
            return BaseResponse.valueOf(CodeEnum.TIME_SLICE_NOT_EXIST);
        }

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }


}
