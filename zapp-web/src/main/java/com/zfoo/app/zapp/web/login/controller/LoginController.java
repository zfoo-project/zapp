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

package com.zfoo.app.zapp.web.login.controller;

import com.zfoo.app.zapp.common.protocol.user.SignInAnswer;
import com.zfoo.app.zapp.common.protocol.user.SignInByAccountAsk;
import com.zfoo.app.zapp.common.protocol.user.SignInByPhoneAsk;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.web.login.model.SignInByAccountRequest;
import com.zfoo.app.zapp.web.login.model.SignInByPhoneRequest;
import com.zfoo.app.zapp.web.login.model.SignInResponse;
import com.zfoo.app.zapp.web.login.service.LoginService;
import com.zfoo.net.NetContext;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.util.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 登录相关接口
 * 账号登录，手机号码登录
 *
 * @author jaysunxiao
 * @version 1.0
 * @module Login模块
 * @since 2019-11-07 20:40
 */
@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;

    /**
     * 账号登录
     * 通过账号，密码，验证码登录
     *
     * @param cm 登录请求
     * @return 对象
     * @real_return {@link com.zfoo.app.zapp.web.login.model.SignInResponse}
     */
    @PostMapping(value = "/api/account/signIn", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse accountSignIn(@RequestBody SignInByAccountRequest cm) throws Exception {
        var account = cm.getAccount();
        var password = cm.getPassword();
        var verifyKey = cm.getVerifyKey();
        var verifyCode = cm.getVerifyCode();

        if (StringUtils.isBlank(account) || StringUtils.isBlank(password) || StringUtils.isBlank(verifyKey) || StringUtils.isBlank(verifyCode)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_EMPTY);
        }

        // 验证码校验
        var verifyCodeCache = loginService.captchaCaches.getIfPresent(verifyKey);
        if (verifyCodeCache == null) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_VERIFY_CODE_NOT_EXIST);
        }

        if (!NumberUtils.isInteger(verifyCode)) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_VERIFY_CODE_ERROR);
        }

        if (Integer.parseInt(verifyCode) != verifyCodeCache) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_VERIFY_CODE_CALCULATE_ERROR);
        }

        // 用户登录请求
        var signInAnswer = NetContext.getConsumer().syncAsk(SignInByAccountAsk.valueOf(account, password), SignInAnswer.class, account).packet();
        var token = signInAnswer.getToken();
        if (StringUtils.isBlank(token)) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FAIL);
        }
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, SignInResponse.valueOf(signInAnswer.getToken()));
    }

    /**
     * 手机号码登录
     * 通过手机号码，验证码登录
     *
     * @param cm 登录请求
     * @return 对象
     * @real_return {@link com.zfoo.app.zapp.web.login.model.SignInResponse}
     */
    @PostMapping(value = "/api/phone/signIn", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse phoneSignIn(@RequestBody SignInByPhoneRequest cm) throws Exception {
        var phoneNumberStr = cm.getPhoneNumber();
        var phoneCode = cm.getPhoneCode();

        if (StringUtils.isBlank(phoneNumberStr) || StringUtils.isBlank(phoneCode)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_EMPTY);
        }

        if (!NumberUtils.isLong(phoneNumberStr)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_NOT_MATCH);
        }

        var phoneNumber = Long.parseLong(phoneNumberStr);

        // 验证码校验
        var verifyCodeCache = loginService.phoneCaches.getIfPresent(phoneNumber);
        if (verifyCodeCache == null) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_PHONE_CODE_NOT_EXIST);
        }

        if (!NumberUtils.isInteger(phoneCode)) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_PHONE_CODE_ERROR);
        }

        if (Integer.parseInt(phoneCode) != verifyCodeCache) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_PHONE_CODE_ERROR);
        }

        // 用户登录请求
        var signInAnswer = NetContext.getConsumer().syncAsk(SignInByPhoneAsk.valueOf(phoneNumber), SignInAnswer.class, phoneNumberStr).packet();
        var token = signInAnswer.getToken();
        if (StringUtils.isBlank(token)) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FAIL);
        }
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, SignInResponse.valueOf(signInAnswer.getToken()));
    }


}
