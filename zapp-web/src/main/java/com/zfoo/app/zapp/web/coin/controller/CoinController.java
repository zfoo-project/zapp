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

package com.zfoo.app.zapp.web.coin.controller;

import com.aliyun.oss.HttpMethod;
import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.model.ImageQualityEnum;
import com.zfoo.app.zapp.common.model.LicenseEnum;
import com.zfoo.app.zapp.common.model.OssPolicyEnum;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.web.coin.model.DownloadTimeSliceRequest;
import com.zfoo.app.zapp.web.coin.model.DownloadTimeSliceResponse;
import com.zfoo.app.zapp.web.coin.service.ICoinService;
import com.zfoo.app.zapp.web.oss.service.IOssService;
import com.zfoo.app.zapp.web.time.model.LoveTimeSliceRequest;
import com.zfoo.app.zapp.web.time.service.ITimeSliceService;
import com.zfoo.app.zapp.web.util.HttpUtils;
import com.zfoo.net.packet.common.PairLong;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-23 18:57
 */
@Controller
public class CoinController {

    @Autowired
    private ITimeSliceService timeSliceService;

    @Autowired
    private ICoinService coinService;

    @Autowired
    private IOssService ossService;

    /**
     * 时间片点赞
     * 用户给时间片点赞
     *
     * @param cm 时间片点赞的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/time/love", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse love(HttpServletRequest request, @RequestBody LoveTimeSliceRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }
        var timeSliceId = cm.getId();
        var timeSliceList = timeSliceService.existTimeSliceList(List.of(timeSliceId));
        if (timeSliceList.size() == 0) {
            return BaseResponse.valueOf(CodeEnum.TIME_SLICE_NOT_EXIST);
        }
        var timeSliceVO = timeSliceList.get(0);
        var triple = coinService.loveConsume(userId, AppConstant.LOVE_COIN_CONSUME, timeSliceVO);
        if (triple.getLeft() != null) {
            return BaseResponse.valueOf(triple.getLeft());
        }

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, PairLong.valueOf(triple.getMiddle(), triple.getRight()));
    }


    /**
     * 时间片资源下载
     *
     * @param cm 时间片点赞的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/time/download", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse download(HttpServletRequest request, @RequestBody DownloadTimeSliceRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }
        var timeSliceId = cm.getId();
        var imageQuality = ImageQualityEnum.getImageQualityEnumByType(cm.getImageQuality());
        var selectedImages = cm.getSelectedImages();

        var timeSliceList = timeSliceService.existTimeSliceList(List.of(timeSliceId));
        if (timeSliceList.size() == 0) {
            return BaseResponse.valueOf(CodeEnum.TIME_SLICE_NOT_EXIST);
        }

        var timeSliceVO = timeSliceList.get(0);
        // ZFOO协议的时间片无法提供下载服务
        if (LicenseEnum.ZFOO == LicenseEnum.getLicenseEnumByType(timeSliceVO.getType())) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        // 计算下载的资源消耗了多少的z币
        var coinConsume = 0;
        var images = timeSliceVO.getImages();
        var video = timeSliceVO.getVideo();

        if (CollectionUtils.isNotEmpty(images)) {
            if (imageQuality == null) {
                return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
            }
            if (CollectionUtils.isEmpty(cm.getSelectedImages()) || selectedImages.stream().anyMatch(it -> it > images.size() - 1)) {
                return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_TWO);
            }
            coinConsume = coinConsume + selectedImages.size() * AppConstant.DOWNLOAD_IMAGE_COIN_CONSUME;
        } else {
            if (video != null && !StringUtils.isBlank(video.getUrl())) {
                coinConsume = coinConsume + AppConstant.DOWNLOAD_VIDEO_COIN_CONSUME;
            }
        }

        if (coinConsume <= 0) {
            return BaseResponse.valueOf(CodeEnum.TIME_SLICE_RESOURCE_EMPTY_ERROR);
        }

        // 只有下载别人的资源需要花费z币
        var love = 0L;
        var coin = 0L;
        if (timeSliceVO.getUserInfo().getId() != userId) {
            var triple = coinService.loveConsume(userId, coinConsume, timeSliceVO);
            if (triple.getLeft() != null) {
                return BaseResponse.valueOf(triple.getLeft());
            }
            love = triple.getMiddle();
            coin = triple.getRight();
        }

        var downloadUrls = new ArrayList<String>();
        // 因为图片开启了原图保护，所以如果是图片的话需要签名返回
        if (CollectionUtils.isNotEmpty(images)) {
            var selectedImageList = selectedImages.stream()
                    .map(it -> images.get(it) + imageQuality.getSuffix())
                    .collect(Collectors.toList());

            if (imageQuality == ImageQualityEnum.ORIGIN) {
                var ossClient = ossService.ossClient();
                var date = new Date(TimeUtils.now() + 2 * TimeUtils.MILLIS_PER_MINUTE);
                var imageList = selectedImageList.stream()
                        .map(it -> StringUtils.substringAfterLast(it, ".com/"))
                        .map(it -> ossClient.generatePresignedUrl(OssPolicyEnum.IMAGE.getBucket(), it, date, HttpMethod.GET))
                        .map(it -> it.toString())
                        .map(it -> StringUtils.substringAfterLast(it, ".com/"))
                        .map(it -> StringUtils.format("{}/{}", OssPolicyEnum.IMAGE.getUrl(), it))
                        .collect(Collectors.toList());
                downloadUrls.addAll(imageList);
            } else {
                downloadUrls.addAll(selectedImageList);
            }
        }
        if (video != null && !StringUtils.isBlank(video.getUrl())) {
            downloadUrls.add(video.getPoster());
            downloadUrls.add(video.getUrl());
        }

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, DownloadTimeSliceResponse.valueOf(timeSliceId, love, coin, downloadUrls));
    }

}
