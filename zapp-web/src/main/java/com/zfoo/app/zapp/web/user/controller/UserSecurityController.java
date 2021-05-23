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

package com.zfoo.app.zapp.web.user.controller;

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.protocol.user.info.UpdatePasswordAsk;
import com.zfoo.app.zapp.common.protocol.user.info.UpdatePhoneAsk;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.web.login.service.LoginService;
import com.zfoo.app.zapp.web.user.model.SavePasswordRequest;
import com.zfoo.app.zapp.web.user.model.SavePhoneNumberRequest;
import com.zfoo.app.zapp.web.util.HttpUtils;
import com.zfoo.net.NetContext;
import com.zfoo.net.packet.common.Message;
import com.zfoo.protocol.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-10-04 11:35
 */
@Controller
public class UserSecurityController {

    @Autowired
    private LoginService loginService;

    /**
     * 保存密码
     *
     * @param cm 保存密码请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/user/savePassword", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse savePassword(HttpServletRequest request, @RequestBody SavePasswordRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var phoneNumber = cm.getPhoneNumber();
        var phoneCode = cm.getPhoneCode();
        var newPassword = cm.getNewPassword();

        var verifyCodeCache = loginService.phoneCaches.getIfPresent(phoneNumber);
        if (verifyCodeCache == null) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_PHONE_CODE_NOT_EXIST);
        }

        if (phoneCode != verifyCodeCache) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_PHONE_CODE_ERROR);
        }

        // 验证密码的格式是否正确
        if (StringUtils.isBlank(newPassword)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }
        if (newPassword.length() < AppConstant.PASSWORD_MIN_LENGTH) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }
        if (newPassword.length() > AppConstant.PASSWORD_MAX_LENGTH) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_TWO);
        }
        if (newPassword.chars().noneMatch(it -> Character.isDigit(it))) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_THREE);
        }
        if (newPassword.chars().noneMatch(it -> StringUtils.isEnglishChar((char) it))) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_FOUR);
        }
        var ask = UpdatePasswordAsk.valueOf(userId, newPassword);
        var answer = NetContext.getConsumer().syncAsk(ask, Message.class, userId).packet();
        return BaseResponse.valueOf(answer.getCode());
    }

    /**
     * 保存手机号码请求
     *
     * @param cm 保存手机号码请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/user/savePhone", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse savePhoneNumber(HttpServletRequest request, @RequestBody SavePhoneNumberRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var phoneNumber = cm.getPhoneNumber();
        var phoneCode = cm.getPhoneCode();

        var verifyCodeCache = loginService.phoneCaches.getIfPresent(phoneNumber);
        if (verifyCodeCache == null) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_PHONE_CODE_NOT_EXIST);
        }

        if (phoneCode != verifyCodeCache) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_PHONE_CODE_ERROR);
        }

        var ask = UpdatePhoneAsk.valueOf(userId, phoneNumber);
        var answer = NetContext.getConsumer().syncAsk(ask, Message.class, userId).packet();
        return BaseResponse.valueOf(answer.getCode());
    }


}
