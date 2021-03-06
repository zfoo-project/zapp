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
 * ?????????????????????
 * ????????????????????????????????????
 *
 * @author jaysunxiao
 * @version 1.0
 * @module Admin??????
 * @since 2020-05-31 15:54
 */
@Controller
public class AdminController {

    @Autowired
    private TimeSliceService timeSliceService;

    /**
     * ?????????????????????
     *
     * @param cm ??????????????????????????????
     * @return ??????????????????
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
     * ????????????????????????
     *
     * @param cm ????????????????????????
     * @return ??????????????????
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
     * ?????????????????????????????????????????????
     *
     * @param cm ???????????????????????????????????????
     * @return ??????????????????
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

        // ????????????????????????????????????
        NetContext.getConsumer().send(RefreshUserTsCacheAsk.valueOf(Set.of(userId)), userId);


        // ????????????????????????
        OrmContext.getAccessor().delete(timeSliceId, TsEditEntity.class);

        // ????????????????????????
        timeSliceService.timeSliceCaches.invalidate(timeSliceId);

        // ????????????????????????????????????
        var id = MongoIdUtils.getIncrementIdFromMongoDefault(TsReviewEntity.class);
        OrmContext.getAccessor().insert(TsReviewEntity.valueOf(id, timeSliceEntity.getUserId(), timeSliceEntity, null));

        return BaseResponse.valueOf(CodeEnum.OK);
    }
}
