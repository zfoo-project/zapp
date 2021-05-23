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

package com.zfoo.app.zapp.web.oss.controller;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.zfoo.app.zapp.common.entity.time.TimeSliceEntity;
import com.zfoo.app.zapp.common.entity.time.model.VideoLinkPO;
import com.zfoo.app.zapp.common.model.OssPolicyEnum;
import com.zfoo.app.zapp.common.model.time.TimeSliceVO;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.web.oss.service.IOssService;
import com.zfoo.app.zapp.web.time.model.event.*;
import com.zfoo.app.zapp.web.util.HttpUtils;
import com.zfoo.event.manager.EventBus;
import com.zfoo.event.model.anno.EventReceiver;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.exception.ExceptionUtils;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 阿里云OSS相关接口
 * 签名生成
 *
 * @author jaysunxiao
 * @version 1.0
 * @module OSS模块
 * @since 2019-11-07 20:40
 */
@Controller
public class OssController {

    private static final Logger logger = LoggerFactory.getLogger(OssController.class);

    @Value("${aliyun.filesystem.bucket}")
    private String bucket;

    @Value("${zfoo.domain}")
    private String zfooDomain;

    @Autowired
    private IOssService ossService;


    /**
     * 签名生成
     *
     * @param type 0头像上传，1用户的背景图片，
     *             2图片上传，3视频上传，
     *             10举报文件，
     *             20好友聊天图片，21好友聊天视频，
     *             30群组头像上传，31群组背景上传，35群组聊天图片，36群组聊天视频
     * @return 对象
     * @real_return {@link com.zfoo.app.zapp.web.oss.model.OssPolicyVO}
     */
    @GetMapping(value = "/api/aliyun/oss/policy")
    @ResponseBody
    @CrossOrigin(origins = "*", methods = RequestMethod.GET)
    public BaseResponse policy(HttpServletRequest request, @RequestParam("type") int type) {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var ossPolicyEnum = OssPolicyEnum.getOssPolicyEnumByType(type);
        if (ossPolicyEnum == null) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }

        try {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, ossService.policy(userId, ossPolicyEnum));
        } catch (Exception e) {
            logger.error(ExceptionUtils.getMessage(e));
            return BaseResponse.valueOf(CodeEnum.FAIL);
        }
    }


    /**
     * 指定key和objectName生成唯一访问固定object的令牌
     */
    public String policyObject() {
        var ossClient = ossService.ossClient();
        var request = new GeneratePresignedUrlRequest(bucket, "test/img/20201030/1_AeYa5WI63qYTMaPqCDeOYvONtOAqFEDd_1", HttpMethod.PUT);
        // 设置URL过期时间为1小时。
        var expiration = new Date(TimeUtils.now() + 3600 * 1000);
        request.setExpiration(expiration);
        // 设置ContentType。
        request.setContentType("text/plain");

        // 生成签名URL。
        var signedUrl = ossClient.generatePresignedUrl(request);

        System.out.println(signedUrl);
        return signedUrl.toString();
    }

    @EventReceiver
    public void onDeleteTimeSliceEvent(DeleteTimeSliceEvent event) {
        var timeSlices = event.getTimeSlices();
        if (CollectionUtils.isEmpty(timeSlices)) {
            return;
        }

        var policyAndUrls = new ArrayList<Pair<OssPolicyEnum, String>>();
        for (var ts : timeSlices) {
            policyAndUrls.addAll(toPolicyAndUrls(ts));
        }

        deleteOssObject(policyAndUrls);
    }


    @EventReceiver
    public void onDeleteEditTimeSliceEvent(DeleteEditTimeSliceEvent event) {
        var editTimeSlices = event.getEditTimeSlices();
        var timeSlices = event.getTimeSlices();

        if (CollectionUtils.isEmpty(editTimeSlices)) {
            return;
        }

        var policyAndUrls = new HashSet<Pair<OssPolicyEnum, String>>();
        for (var ts : editTimeSlices) {
            policyAndUrls.addAll(toPolicyAndUrls(ts));
        }

        for (var ts : timeSlices) {
            policyAndUrls.removeAll(toPolicyAndUrls(ts));
        }

        deleteOssObject(new ArrayList<>(policyAndUrls));
    }


    @EventReceiver
    public void onDeleteReviewTimeSliceEvent(DeleteReviewTimeSliceEvent event) {
        var timeSlices = event.getTimeSlices();

        if (CollectionUtils.isEmpty(timeSlices)) {
            return;
        }

        var policyAndUrls = new ArrayList<Pair<OssPolicyEnum, String>>();
        for (var ts : timeSlices) {
            policyAndUrls.addAll(toPolicyAndUrls(ts));
        }

        deleteOssObject(new ArrayList<>(policyAndUrls));
    }

    @EventReceiver
    public void onPassEditTimeSliceEvent(PassEditTimeSliceEvent event) {
        var oldTs = event.getOldEntity();
        var newTs = event.getNewEntity();

        var policyAndUrls = new HashSet<Pair<OssPolicyEnum, String>>();
        policyAndUrls.addAll(toPolicyAndUrls(oldTs));
        policyAndUrls.removeAll(toPolicyAndUrls(newTs));

        deleteOssObject(new ArrayList<>(policyAndUrls));
    }

    @EventReceiver
    public void onEditTimeSliceEvent(EditTimeSliceEvent event) {
        var oldTs = event.getOldEntity();
        var newTs = event.getNewEntity();
        var currentTs = event.getCurrentEntity();

        var policyAndUrls = new HashSet<Pair<OssPolicyEnum, String>>();
        policyAndUrls.addAll(toPolicyAndUrls(oldTs));
        policyAndUrls.removeAll(toPolicyAndUrls(newTs));
        policyAndUrls.removeAll(toPolicyAndUrls(currentTs));

        deleteOssObject(new ArrayList<>(policyAndUrls));
    }

    @EventReceiver
    public void onRecommitTimeSliceEvent(RecommitTimeSliceEvent event) {
        var oldTs = event.getOldEntity();
        var newTs = event.getNewEntity();

        var policyAndUrls = new HashSet<Pair<OssPolicyEnum, String>>();
        policyAndUrls.addAll(toPolicyAndUrls(oldTs));
        policyAndUrls.removeAll(toPolicyAndUrls(newTs));

        deleteOssObject(new ArrayList<>(policyAndUrls));
    }

    private List<Pair<OssPolicyEnum, String>> toPolicyAndUrls(TimeSliceEntity ts) {
        if (ts == null) {
            return Collections.EMPTY_LIST;
        }
        return toPolicyAndUrls(ts.getImages(), ts.getVideo());
    }

    private List<Pair<OssPolicyEnum, String>> toPolicyAndUrls(TimeSliceVO ts) {
        if (ts == null) {
            return Collections.EMPTY_LIST;
        }
        return toPolicyAndUrls(ts.getImages(), ts.getVideo());
    }

    private List<Pair<OssPolicyEnum, String>> toPolicyAndUrls(List<String> images, VideoLinkPO video) {
        var policyAndUrls = new ArrayList<Pair<OssPolicyEnum, String>>();

        if (CollectionUtils.isNotEmpty(images)) {
            images.stream().filter(it -> !StringUtils.isBlank(it)).forEach(it -> policyAndUrls.add(new Pair<>(OssPolicyEnum.IMAGE, it)));
        }

        if (video != null) {
            if (!StringUtils.isBlank(video.getPoster())) {
                policyAndUrls.add(new Pair<>(OssPolicyEnum.VIDEO, video.getPoster()));
            }
            if (!StringUtils.isBlank(video.getUrl())) {
                policyAndUrls.add(new Pair<>(OssPolicyEnum.VIDEO, video.getUrl()));
            }
        }

        return policyAndUrls;
    }

    private void deleteOssObject(List<Pair<OssPolicyEnum, String>> policyAndUrls) {
        if (CollectionUtils.isEmpty(policyAndUrls)) {
            return;
        }

        EventBus.asyncExecute().execute(new Runnable() {
            @Override
            public void run() {
                var ossClient = ossService.ossClient();

                // 删除文件。如需删除文件夹，请将ObjectName设置为对应的文件夹名称。如果文件夹非空，则需要将文件夹下的所有object删除后才能删除该文件夹。
                policyAndUrls.stream()
                        .forEach(it -> {
                            var ossObject = StringUtils.substringAfterFirst(it.getValue(), zfooDomain + StringUtils.SLASH);
                            ossClient.deleteObjects(new DeleteObjectsRequest(it.getKey().getBucket()).withKeys(List.of(ossObject)));
                        });
            }
        });
    }

}
