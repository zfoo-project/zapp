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

package com.zfoo.app.zapp.web.login.service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.zfoo.app.zapp.web.login.model.sms.SmsData;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.model.Triple;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.util.captcha.ArithmeticCaptcha;
import com.zfoo.util.captcha.GifCaptcha;
import com.zfoo.util.captcha.PngCaptcha;
import com.zfoo.util.captcha.model.CaptchaCharEnum;
import com.zfoo.util.math.RandomUtils;
import com.zfoo.util.security.IdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-25 18:31
 */
@Component
public class LoginService implements ILoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    /**
     * 图片的验证码缓存
     */
    public Cache<String, Integer> captchaCaches = Caffeine.newBuilder()
            .expireAfterWrite(2 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS)
            .maximumSize(1_0000)
            .build();

    /**
     * 手机验证码缓存
     */
    public Cache<Long, Integer> phoneCaches = Caffeine.newBuilder()
            .expireAfterWrite(3 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS)
            .maximumSize(1_0000)
            .build();

    /**
     * 微博微信绑定用户id缓存
     */
    public Cache<String, Long> bindCaches = Caffeine.newBuilder()
            .expireAfterWrite(1 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS)
            .maximumSize(1_0000)
            .build();

    /**
     * 微信用户的(openid，unionid，weChatAccessToken)缓存
     */
    public Cache<String, Triple<String, String, String>> weChatUnionCaches = Caffeine.newBuilder()
            .expireAfterWrite(1 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS)
            .maximumSize(5000)
            .build();

    /**
     * 微博用户的(unionid，weiBoAccessToken)缓存
     */
    public Cache<String, Pair<String, String>> weiBoUnionCaches = Caffeine.newBuilder()
            .expireAfterWrite(1 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS)
            .maximumSize(5000)
            .build();


    @Value("${aliyun.filesystem.accessKey}")
    private String accessKey;
    @Value("${aliyun.filesystem.secretKey}")
    private String secretKey;

    @Override
    public Pair<String, String> createCaptcha() {
        var uuid = IdUtils.getUUID();

        if (RandomUtils.randomBoolean() && RandomUtils.randomBoolean()) {
            // png类型
            var captcha = new PngCaptcha();
            captcha.setWidth(130);
            captcha.setHeight(48);
            captcha.setCaptchaChar(CaptchaCharEnum.ARAB_NUMBER);
            captcha.setLength(4);
            captcha.buildCaptcha();
            captchaCaches.put(uuid, Integer.parseInt(captcha.captcha()));
            return new Pair<>(uuid, captcha.toBase64());
        }

        if (RandomUtils.randomBoolean() && RandomUtils.randomBoolean()) {
            // gif类型
            var captcha = new GifCaptcha();
            captcha.setWidth(130);
            captcha.setHeight(48);
            captcha.setCaptchaChar(CaptchaCharEnum.ARAB_NUMBER);
            captcha.setLength(4);
            captcha.buildCaptcha();
            captchaCaches.put(uuid, Integer.parseInt(captcha.captcha()));
            return new Pair<>(uuid, captcha.toBase64());
        }

        while (true) {
            // 算术类型
            var captcha = new ArithmeticCaptcha();
            captcha.setWidth(130);
            captcha.setHeight(48);

            // 几位数运算，默认是两位
            captcha.setLength(3);

            captcha.buildCaptcha();

            // 获取运算的公式：3+2=?
            captcha.getArithmeticString();
            // 获取运算的结果：5
            var result = Integer.parseInt(captcha.captcha());
            if (result < 0) {
                continue;
            }
            captchaCaches.put(uuid, result);
            return new Pair<>(uuid, captcha.toBase64());
        }
    }

    @Override
    public Pair<Long, Integer> createPhoneVerifyCode(long phoneNumber) {
        var randomNum = RandomUtils.randomInt(10_0000, 100_0000);

        var profile = DefaultProfile.getProfile("cn-hangzhou", accessKey, secretKey);
        var client = new DefaultAcsClient(profile);
        var request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", String.valueOf(phoneNumber));
        request.putQueryParameter("SignName", "浊浮zfoo");
        request.putQueryParameter("TemplateCode", "SMS_190794423");
        request.putQueryParameter("TemplateParam", StringUtils.format("{ code: {}}", randomNum));

        try {
            var response = client.getCommonResponse(request);
            var sms = JsonUtils.string2Object(response.getData(), SmsData.class);
            if (sms.getMessage().toLowerCase().equals("ok")) {
                logger.info("给用户[{}]发送了一条验证码[{}]", phoneNumber, randomNum);
                phoneCaches.put(phoneNumber, randomNum);
                return new Pair<>(phoneNumber, randomNum);
            } else {
                logger.error("用户[{}]短信验证码发送错误[{}]", response.getData());
                return null;
            }
        } catch (Exception e) {
            logger.error("用户[{}]短信验证码生成错误", e);
        }

        return null;
    }

    @Override
    public Pair<String, Long> createBind(long userId) {
        var uuid = IdUtils.getUUID();
        bindCaches.put(uuid, userId);
        return new Pair<>(uuid, userId);
    }

    @Override
    public Pair<String, Triple<String, String, String>> createWeChatUnion(String openid, String unionid, String weChatAccessToken) {
        var uuid = IdUtils.getUUID();
        var triple = new Triple<>(openid, unionid, weChatAccessToken);
        weChatUnionCaches.put(uuid, triple);
        return new Pair<>(uuid, triple);
    }

    @Override
    public Pair<String, Pair<String, String>> createWeiBoUnion(String unionid, String weiBoAccessToken) {
        var uuid = IdUtils.getUUID();
        var pair = new Pair<>(unionid, weiBoAccessToken);
        weiBoUnionCaches.put(uuid, pair);
        return new Pair<>(uuid, pair);
    }

}
