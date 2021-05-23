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

package com.zfoo.app.zapp.web.admin.controller;

import com.zfoo.app.zapp.common.entity.time.TimeSliceEntity;
import com.zfoo.app.zapp.common.entity.time.TsEditEntity;
import com.zfoo.app.zapp.common.entity.time.TsReviewEntity;
import com.zfoo.app.zapp.common.entity.user.model.AdminAuthEnum;
import com.zfoo.app.zapp.common.protocol.cache.refresh.RefreshUserTsCacheAsk;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserAdminAuthAnswer;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserAdminAuthAsk;
import com.zfoo.app.zapp.common.protocol.user.info.UpdateAdminAuthAsk;
import com.zfoo.app.zapp.common.protocol.user.time.DeleteTimeSliceAsk;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.web.admin.model.AdminTimeDeleteRequest;
import com.zfoo.app.zapp.web.admin.model.AdminTimeReviewRequest;
import com.zfoo.app.zapp.web.admin.model.AdminUserAuthRequest;
import com.zfoo.app.zapp.web.time.service.TimeSliceService;
import com.zfoo.app.zapp.web.util.HttpUtils;
import com.zfoo.net.NetContext;
import com.zfoo.net.packet.common.Message;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.util.MongoIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * 管理员相关接口
 * 搜索用户，修改用户的权限
 *
 * @author jaysunxiao
 * @version 1.0
 * @module Admin模块
 * @since 2020-05-31 15:54
 */
@Controller
public class AdminController {

    @Autowired
    private TimeSliceService timeSliceService;

    /**
     * 修改用户的权限
     *
     * @param cm 修改用户的权限的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/admin/user/auth", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse userAuth(HttpServletRequest request, @RequestBody AdminUserAuthRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var targetId = cm.getTargetId();
        var adminAuth = AdminAuthEnum.getAuthEnumByType(cm.getAdminAuth());

        if (!CommonUtils.isUserIdInRange(List.of(userId, targetId))) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }

        var answer = NetContext.getConsumer().syncAsk(GetUserAdminAuthAsk.valueOf(userId), GetUserAdminAuthAnswer.class, userId).packet();
        if (!AdminAuthEnum.getAuthEnumByType(answer.getAdminAuth()).hasAuth(AdminAuthEnum.ADMIN_AUTH)) {
            return BaseResponse.valueOf(CodeEnum.FAIL);
        }

        var message = NetContext.getConsumer().syncAsk(UpdateAdminAuthAsk.valueOf(targetId, adminAuth.getType()), Message.class, targetId).packet();
        return BaseResponse.valueOf(message.getCode());
    }

    /**
     * 管理员删除时间片
     *
     * @param cm 删除时间片的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/admin/time/delete", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse timeDelete(HttpServletRequest request, @RequestBody AdminTimeDeleteRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var timeSliceId = cm.getTimeSliceId();

        var answer = NetContext.getConsumer().syncAsk(GetUserAdminAuthAsk.valueOf(userId), GetUserAdminAuthAnswer.class, userId).packet();
        if (!AdminAuthEnum.getAuthEnumByType(answer.getAdminAuth()).hasAuth(AdminAuthEnum.ADMIN_AUTH)) {
            return BaseResponse.valueOf(CodeEnum.FAIL);
        }

        timeSliceService.deleteTimeSlice(timeSliceId);
        return BaseResponse.valueOf(CodeEnum.OK);
    }


    /**
     * 管理员强制让时间片进入审核状态
     *
     * @param cm 让时间片进入审核状态的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/admin/time/review", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse timeReview(HttpServletRequest request, @RequestBody AdminTimeReviewRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var timeSliceId = cm.getTimeSliceId();

        var answer = NetContext.getConsumer().syncAsk(GetUserAdminAuthAsk.valueOf(userId), GetUserAdminAuthAnswer.class, userId).packet();
        if (!AdminAuthEnum.getAuthEnumByType(answer.getAdminAuth()).hasAuth(AdminAuthEnum.ADMIN_AUTH)) {
            return BaseResponse.valueOf(CodeEnum.FAIL);
        }

        var timeSliceEntity = OrmContext.getAccessor().load(timeSliceId, TimeSliceEntity.class);

        NetContext.getConsumer().send(DeleteTimeSliceAsk.valueOf(timeSliceId), timeSliceId);

        // 然后刷新用户的时间片缓存
        NetContext.getConsumer().send(RefreshUserTsCacheAsk.valueOf(Set.of(userId)), userId);


        // 删除编辑的时间片
        OrmContext.getAccessor().delete(timeSliceId, TsEditEntity.class);

        // 删除时间片的缓存
        timeSliceService.timeSliceCaches.invalidate(timeSliceId);

        // 使时间片重新进入审核状态
        var id = MongoIdUtils.getIncrementIdFromMongoDefault(TsReviewEntity.class);
        OrmContext.getAccessor().insert(TsReviewEntity.valueOf(id, timeSliceEntity.getUserId(), timeSliceEntity, null));

        return BaseResponse.valueOf(CodeEnum.OK);
    }
}
