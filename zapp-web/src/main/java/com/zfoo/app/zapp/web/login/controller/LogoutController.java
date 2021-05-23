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

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.web.login.model.*;
import com.zfoo.app.zapp.web.login.service.LoginService;
import com.zfoo.app.zapp.web.util.HttpUtils;
import com.zfoo.event.model.anno.EventReceiver;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.model.anno.Scheduler;
import com.zfoo.util.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 登出相关模块
 * 获取图片验证码，绑定请求，获取图片验证码，手机验证码验证，登出
 *
 * @author jaysunxiao
 * @version 1.0
 * @module Logout模块
 * @since 2020-05-25 18:29
 */
@Controller
public class LogoutController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;


    /**
     * 获取图片验证码
     * linux环境下需要安装下面的软件：yum install fontconfig
     *
     * @return 对象
     * @real_return {@link com.zfoo.app.zapp.web.login.model.CaptchaResponse}
     */
    @GetMapping(value = "/api/verify/captcha")
    @ResponseBody
    public BaseResponse captchaVerify() {
        var pair = loginService.createCaptcha();
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, CaptchaResponse.valueOf(pair.getKey(), pair.getValue()));
    }

    /**
     * 绑定请求
     * 绑定微博，微信的时候，获取一个验证码，然后在绑定的时候使用
     *
     * @return 对象
     * @real_return {@link com.zfoo.app.zapp.web.login.model.BindResponse}
     */
    @GetMapping(value = "/api/verify/bind")
    @ResponseBody
    public BaseResponse bindVerify(HttpServletRequest request) {
        var userId = HttpUtils.getUid(request);
        if (userId <= 0) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }
        var pair = loginService.createBind(userId);
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, BindResponse.valueOf(pair.getKey()));
    }

    /**
     * 获取图片验证码
     * 通过手机号获取一个验证码，直接发送到手机上，这个调用有频率限制，一般一分钟只能调用一次
     *
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/verify/phone", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse phoneVerify(@RequestBody GetVerifyCodeByPhoneRequest cm) {
        var phoneNumberStr = cm.getPhoneNumber();
        var verifyKey = cm.getVerifyKey();
        var verifyCode = cm.getVerifyCode();

        // 验证手机号码
        if (StringUtils.isBlank(phoneNumberStr) || phoneNumberStr.length() != AppConstant.PHONE_NUMBER_LENGTH || !NumberUtils.isLong(phoneNumberStr)) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_PHONE_NUMBER_ERROR);
        }

        var phoneNumber = Long.parseLong(phoneNumberStr);
        if (String.valueOf(phoneNumber).length() != AppConstant.PHONE_NUMBER_LENGTH) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_PHONE_NUMBER_ERROR);
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

        var pair = loginService.createPhoneVerifyCode(phoneNumber);
        if (pair == null) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_VERIFY_CODE_CALCULATE_ERROR);
        }
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }

    /**
     * 手机验证码验证
     * 用在修改密码的时候，当密码验证成功，进行下一步设置密码
     *
     * @param cm 手机验证码验证请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/check/phoneCode", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse checkPhoneCode(@RequestBody CheckPhoneCodeRequest cm) {
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

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }


    /**
     * 登出
     *
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/logout")
    @ResponseBody
    public BaseResponse logout(HttpServletRequest request) {
        return BaseResponse.valueOf(CodeEnum.OK);
    }


    @EventReceiver
    public void onLoginSuccessEvent(LoginSuccessEvent event) {
        logger.info("LoginController收到事件[{}]，在这边做一些业务处理", JsonUtils.object2String(event));
    }


    @Scheduler(cron = "0/20 * * * * ?")
    public void cronHelloScheduler() {
        System.out.println("这是一个cron表达式的定时任务，每隔20秒执行一次");
    }

}
