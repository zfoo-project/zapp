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
import com.zfoo.app.zapp.common.protocol.user.SignInAnswer;
import com.zfoo.app.zapp.common.protocol.user.SignInByPhoneAsk;
import com.zfoo.app.zapp.common.protocol.user.weibo.WeiBoBindAsk;
import com.zfoo.app.zapp.common.protocol.user.weibo.WeiBoSignInAnswer;
import com.zfoo.app.zapp.common.protocol.user.weibo.WeiBoSignInAsk;
import com.zfoo.app.zapp.common.protocol.user.weibo.WeiBoUnbindAsk;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.common.util.TokenUtils;
import com.zfoo.app.zapp.web.login.model.SignInResponse;
import com.zfoo.app.zapp.web.login.model.weibo.WeiBoRegisterRequest;
import com.zfoo.app.zapp.web.login.model.weibo.WeiboAccessTokenResponse;
import com.zfoo.app.zapp.web.login.model.weibo.WeiboUserInfoResponse;
import com.zfoo.app.zapp.web.login.service.LoginService;
import com.zfoo.app.zapp.web.util.HttpUtils;
import com.zfoo.event.manager.EventBus;
import com.zfoo.net.NetContext;
import com.zfoo.net.packet.common.Message;
import com.zfoo.protocol.exception.ExceptionUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.util.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * 微博相关接口
 * 微博登录，微博绑定，微博解绑
 *
 * @author jaysunxiao
 * @version 1.0
 * @module WeChatService模块
 * @since 2019-11-07 20:40
 */
@Controller
public class WeiBoController {

    private static final Logger logger = LoggerFactory.getLogger(WeiBoController.class);

    @Value("${weiBo.accessToken.url}")
    private String weiBoAccessTokenUrl;

    @Value("${weiBo.userInfo.url}")
    private String weiBoUserInfoUrl;

    @Value("${weiBo.appid}")
    private String weiBoAppid;

    @Value("${weiBo.secret}")
    private String weiBoSecret;

    @Autowired
    private LoginService loginService;

    /**
     * 微博登录
     * 电脑，手机都可以使用这个微博登录接口
     *
     * @param code 微信平台的返回code码
     * @return 地址重定向
     */
    @RequestMapping(value = "/api/weiBo/signIn")
    public void weiBoSignIn(@RequestParam("code") String code, HttpServletResponse response) throws Exception {
        logger.info("微博用户登录请求[code:{}]", code);

        if (StringUtils.isBlank(code)) {
            return;
        }

        var client = HttpClient.newBuilder()
                .executor(EventBus.asyncExecute())
                .build();

        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();

        var accessTokenRequest = HttpRequest
                .newBuilder(URI.create(StringUtils.format(weiBoAccessTokenUrl, weiBoAppid, weiBoSecret, code)))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();


        var weiBoAccessTokenResponse = JsonUtils.string2Object(client.send(accessTokenRequest, responseBodyHandler).body(), WeiboAccessTokenResponse.class);
        var weiBoUid = weiBoAccessTokenResponse.getUid();
        var weiBoAccessToken = weiBoAccessTokenResponse.getAccessToken();

        var weiBoSignInAnswer = NetContext.getConsumer().syncAsk(WeiBoSignInAsk.valueOf(weiBoUid), WeiBoSignInAnswer.class, weiBoUid).packet();


        // 如果是新用户再绑定手机号码
        if (weiBoSignInAnswer.isNewUser()) {
            var pair = loginService.createWeiBoUnion(weiBoUid, weiBoAccessToken);
            response.sendRedirect(StringUtils.format("/register?type=weiBo&verifyKey={}", pair.getKey()));
        } else {
            var token = weiBoSignInAnswer.getToken();

            var cookie = new Cookie(AppConstant.ZFOO_COOKIE_TOKEN, token);
            cookie.setPath("/");
            cookie.setMaxAge(AppConstant.ZFOO_COOKIE_TOKEN_EXPIRE_TIME);
            response.addCookie(cookie);

            response.sendRedirect("/");
        }
    }

    @PostMapping(value = "/api/weiBo/register", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse register(@RequestBody WeiBoRegisterRequest cm) throws Exception {
        var verifyKey = cm.getVerifyKey();
        var phoneNumberStr = cm.getPhoneNumber();
        var phoneCode = cm.getPhoneCode();

        logger.info("微博用户注册请求[verifyKey:{}][phoneNumber:{}][phoneCode:{}]", verifyKey, phoneNumberStr, phoneCode);

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

        // verifyKey校验
        var pair = loginService.weiBoUnionCaches.getIfPresent(verifyKey);
        if (pair == null) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_REGISTER_CODE_NOT_EXIST);
        }

        // 用户登录请求
        var signInAnswer = NetContext.getConsumer().syncAsk(SignInByPhoneAsk.valueOf(phoneNumber), SignInAnswer.class, phoneNumberStr).packet();
        var token = signInAnswer.getToken();
        if (StringUtils.isBlank(token)) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FAIL);
        }

        var userId = TokenUtils.get(token).getLeft();
        var weiBoUid = pair.getKey();
        var weiBoAccessToken = pair.getValue();

        // 如果是新用户再请求微博基本的用户信息
        if (signInAnswer.isNewUser()) {
            try {
                var client = HttpClient.newBuilder()
                        .executor(EventBus.asyncExecute())
                        .build();

                var responseBodyHandler = HttpResponse.BodyHandlers.ofString();

                var userInfoRequest = HttpRequest
                        .newBuilder(URI.create(StringUtils.format(weiBoUserInfoUrl, weiBoAccessToken, weiBoUid)))
                        .GET()
                        .build();

                var weiBoUserInfoResponse = JsonUtils.string2Object(client.send(userInfoRequest, responseBodyHandler).body(), WeiboUserInfoResponse.class);
                // 将微博的http图片替换成https
                var avatarUrl = weiBoUserInfoResponse.getProfileImageUrl();
                if (!StringUtils.isBlank(avatarUrl) && avatarUrl.startsWith(HttpUtils.HTTP_PREFIX)) {
                    weiBoUserInfoResponse.setProfileImageUrl(avatarUrl.replaceFirst(HttpUtils.HTTP_PREFIX, HttpUtils.HTTPS_PREFIX));
                }

                NetContext.getConsumer().syncAsk(weiBoUserInfoResponse.toWeiboUserInfoAsk(userId), Message.class, userId);
            } catch (Exception e) {
                logger.error(ExceptionUtils.getMessage(e));
            }
        }

        var message = NetContext.getConsumer().syncAsk(WeiBoBindAsk.valueOf(weiBoUid, userId), Message.class, weiBoUid).packet();
        if (!message.success()) {
            return BaseResponse.valueOf(message.getCode());
        }

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, SignInResponse.valueOf(signInAnswer.getToken()));
    }

    /**
     * 微博绑定
     * 通过扫描二维码将账号和微博绑定
     *
     * @param code  微博平台的返回code码
     * @param state 状态
     * @return 地址重定向
     */
    @RequestMapping(value = "/api/weiBo/bind")
    public void weiBoBind(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletResponse response) throws Exception {
        logger.info("微博用户绑定请求[code:{}][state:{}]", code, state);

        if (StringUtils.isBlank(code)) {
            return;
        }

        var client = HttpClient.newBuilder()
                .executor(EventBus.asyncExecute())
                .build();

        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();

        var accessTokenRequest = HttpRequest
                .newBuilder(URI.create(StringUtils.format(weiBoAccessTokenUrl, weiBoAppid, weiBoSecret, code)))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();


        var weiBoAccessTokenResponse = JsonUtils.string2Object(client.send(accessTokenRequest, responseBodyHandler).body(), WeiboAccessTokenResponse.class);
        var weiBoUid = weiBoAccessTokenResponse.getUid();

        var userId = loginService.bindCaches.getIfPresent(state);
        if (userId == null) {
            response.sendRedirect("/bind/error/weibo");
            return;
        }

        var message = NetContext.getConsumer().syncAsk(WeiBoBindAsk.valueOf(weiBoUid, userId), Message.class, weiBoUid).packet();
        if (message.success()) {
            response.sendRedirect("/user/security");
        } else {
            response.sendRedirect("/bind/error/weibo");
        }
    }

    /**
     * 微博解绑
     * 将账号和微博解绑
     *
     * @return 一个空的对象
     */
    @DeleteMapping(value = "/api/weiBo/unbind")
    @ResponseBody
    public BaseResponse weiBoUnbind(HttpServletRequest request) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var answer = NetContext.getConsumer().syncAsk(WeiBoUnbindAsk.valueOf(userId), Message.class, userId).packet();
        return BaseResponse.valueOf(answer.getCode());
    }

}
