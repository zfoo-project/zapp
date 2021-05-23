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
import com.zfoo.app.zapp.common.constant.LocationConstant;
import com.zfoo.app.zapp.common.entity.user.model.GenderEnum;
import com.zfoo.app.zapp.common.entity.user.model.LanguageEnum;
import com.zfoo.app.zapp.common.entity.user.model.ThemeEnum;
import com.zfoo.app.zapp.common.model.OssPolicyEnum;
import com.zfoo.app.zapp.common.protocol.cache.*;
import com.zfoo.app.zapp.common.protocol.user.info.*;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.common.util.TokenUtils;
import com.zfoo.app.zapp.web.user.model.*;
import com.zfoo.app.zapp.web.util.HttpUtils;
import com.zfoo.app.zapp.web.word.service.WordService;
import com.zfoo.net.NetContext;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.packet.common.PairLS;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户相关接口
 * 用户获取自己的信息
 * 获取用户创建，编辑，创建被拒绝，编辑被拒绝的时间片
 * 批量获取用户的信息，关注用户，取关用户，保存用户背景，保存用户头像，保存用户基本信息，保存用户自定义状态
 * 保存自定义设置，用户安全信息请求，保存密码，保存手机号码请求，获取用户关注的用户列表，获取用户的粉丝列表
 *
 * @author jaysunxiao
 * @version 1.0
 * @module User模块
 * @since 2020-03-03 12:06
 */
@Controller
public class UserController {

    @Autowired
    private WordService wordService;

    /**
     * 搜索用户
     *
     * @param query 搜索的内容
     * @return 对象数组
     * @real_return {@link java.util.List<com.zfoo.app.zapp.common.protocol.cache.model.UserCache>}
     */
    @GetMapping(value = "/api/user/search")
    @ResponseBody
    public BaseResponse searchUser(HttpServletRequest request, @RequestParam("query") String query) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        if (StringUtils.isBlank(query)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_EMPTY);
        }

        var answer = NetContext.getConsumer().syncAsk(SearchUserAsk.valueOf(query), SearchUserAnswer.class, query).packet();
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, answer.getUserCaches());
    }

    /**
     * 用户获取自己的信息
     * 一般是用户登录的时候获取自己的信息
     *
     * @param token 令牌
     * @return 用户的基本信息
     * @real_return {@link com.zfoo.app.zapp.web.user.model.GetUserProfileResponse}
     */
    @GetMapping(value = "/api/user/profile")
    @ResponseBody
    public BaseResponse getUserProfile(@RequestParam("token") String token) throws Exception {
        if (StringUtils.isBlank(token)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_EMPTY);
        }

        var userId = TokenUtils.get(token).getLeft();
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var answer = NetContext.getConsumer().syncAsk(GetUserProfileAsk.valueOf(userId), GetUserProfileAnswer.class, userId).packet();

        var locations = LocationConstant.converter(answer.getLocations());
        var items = wordService.wordCaches
                .batchGet(answer.getItems())
                .entrySet()
                .stream()
                .map(it -> PairLS.valueOf(it.getKey(), it.getValue()))
                .collect(Collectors.toList());
        var persons = wordService.personCaches
                .batchGet(answer.getPersons())
                .entrySet()
                .stream()
                .map(it -> PairLS.valueOf(it.getKey(), it.getValue()))
                .collect(Collectors.toList());

        var response = GetUserProfileResponse.valueOf(answer.getId(), answer.getAdminAuth(), answer.getName(), answer.getCoin(), answer.getAvatar()
                , answer.getBackground(), answer.getGender(), answer.getSignature(), answer.getCustomTime(), answer.getCustom()
                , answer.getFollowNum(), answer.getFanNum(), answer.getStarNum(), answer.getFree(), answer.getNormal()
                , answer.getFollows(), answer.getStars()
                , locations, persons, items, answer.getSetting());
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, response);
    }


    /**
     * 批量获取用户的信息
     *
     * @param ids 时间片id列表，用-连接，如1-2-3
     * @return 对象数组，UserCache
     */
    @GetMapping(value = "/api/user/info")
    @ResponseBody
    public BaseResponse getUserInfo(HttpServletRequest request, @RequestParam("ids") String ids) throws Exception {
        var userIdList = new ArrayList<>(HttpUtils.dashParamToSet(ids));
        if (CollectionUtils.isEmpty(userIdList)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        if (!CommonUtils.isUserIdInRange(userIdList)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        var answer = NetContext.getConsumer().syncAsk(GetUserCacheAsk.valueOf(new HashSet<>(userIdList)), GetUserCacheAnswer.class, userIdList.get(0)).packet();
        var userInfoList = CollectionUtils.isNotEmpty(answer.getUserCacheMap())
                ? answer.getUserCacheMap().values().stream().collect(Collectors.toList())
                : Collections.EMPTY_LIST;

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, userInfoList);
    }


    /**
     * 保存用户背景
     *
     * @param cm 保存用户背景请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/user/saveBackground", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse saveBackground(HttpServletRequest request, @RequestBody SaveBackgroundRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var background = cm.getBackground();
        if (StringUtils.isBlank(background)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_EMPTY);
        }

        if (!OssPolicyEnum.BACKGROUND.toUrlAndDir(userId).equals(background)) {
            return BaseResponse.valueOf(CodeEnum.USER_BACKGROUND_PATH_ERROR);
        }

        var ask = UpdateBackgroundAsk.valueOf(userId, background);
        var answer = NetContext.getConsumer().syncAsk(ask, Message.class, userId).packet();
        return BaseResponse.valueOf(answer.getCode());
    }


    /**
     * 保存用户头像
     *
     * @param cm 保存用户头像请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/user/saveAvatar", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse saveAvatar(HttpServletRequest request, @RequestBody SaveAvatarRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var avatar = cm.getAvatar();
        if (StringUtils.isBlank(avatar)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_EMPTY);
        }

        if (!OssPolicyEnum.AVATAR.toUrlAndDir(userId).equals(avatar)) {
            return BaseResponse.valueOf(CodeEnum.USER_AVATAR_PATH_ERROR);
        }

        var ask = UpdateAvatarAsk.valueOf(userId, avatar);
        var answer = NetContext.getConsumer().syncAsk(ask, Message.class, userId).packet();
        return BaseResponse.valueOf(answer.getCode());
    }


    /**
     * 保存用户基本信息
     *
     * @param cm 保存用户基本信息请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/user/saveBaseInfo", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse saveBaseInfo(HttpServletRequest request, @RequestBody SaveBaseInfoRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var name = cm.getName();
        var gender = cm.getGender();
        var signature = cm.getSignature();

        if (StringUtils.isBlank(name)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_EMPTY);
        }

        // 用户名称长度检查
        if (name.length() < AppConstant.USER_NAME_MIN_LENGTH || name.length() > AppConstant.USER_NAME_MAX_LENGTH) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        for (var i = 0; i < name.length(); i++) {
            var c = name.charAt(i);
            if (StringUtils.isStopChar(c)) {
                continue;
            }
            if (AppConstant.CN_PATTERN.matcher(String.valueOf(c)).matches()) {
                continue;
            }
            if (StringUtils.isEnglishChar(c)) {
                continue;
            }
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }

        if (!StringUtils.isBlank(signature) && signature.length() > AppConstant.USER_SIGNATURE_MAX_LENGTH) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_TWO);
        }

        if (GenderEnum.getGenderEnumByType(gender) == null) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_THREE);
        }

        var worldFilterAnswer = NetContext.getConsumer().syncAsk(WordFilterAsk.valueOf(name + signature), WordFilterAnswer.class, userId).packet();
        // 敏感字符检测
        if (CollectionUtils.isNotEmpty(worldFilterAnswer.getFilterWords())) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_WORD_FILTER_ERROR);
        }

        var ask = UpdateBaseInfoAsk.valueOf(userId, gender, name, signature);
        var answer = NetContext.getConsumer().syncAsk(ask, Message.class, userId).packet();
        return BaseResponse.valueOf(answer.getCode());
    }

    /**
     * 保存用户自定义状态
     *
     * @param cm 保存用户自定义状态请求
     * @return 对象
     * @real_return {@link com.zfoo.app.zapp.web.user.model.SaveCustomStatusResponse}
     */
    @PostMapping(value = "/api/user/saveCustomStatus", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse saveCustomStatus(HttpServletRequest request, @RequestBody SaveCustomStatusRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var customTime = cm.getCustomTime();
        var custom = cm.getCustom();


        if (StringUtils.isBlank(custom)) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_EMPTY);
        }

        if (customTime <= 0 || customTime > AppConstant.USER_CUSTOM_STATUS_MAX_TIME) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        var worldFilterAnswer = NetContext.getConsumer().syncAsk(WordFilterAsk.valueOf(custom), WordFilterAnswer.class, userId).packet();
        // 敏感字符检测
        if (CollectionUtils.isNotEmpty(worldFilterAnswer.getFilterWords())) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_WORD_FILTER_ERROR);
        }

        var ask = UpdateCustomStatusAsk.valueOf(userId, customTime + TimeUtils.now(), custom);
        var answer = NetContext.getConsumer().syncAsk(ask, Message.class, userId).packet();
        return BaseResponse.valueOf(CodeEnum.OK, SaveCustomStatusResponse.valueOf(ask.getCustomTime()));
    }

    /**
     * 保存自定义设置
     *
     * @param cm 保存自定义设置请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/user/saveSetting", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse saveSetting(HttpServletRequest request, @RequestBody SaveSettingRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var theme = cm.getTheme();
        var language = cm.getLanguage();

        if (ThemeEnum.getThemeEnumByType(theme) == null) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        if (LanguageEnum.getLanguageEnumByType(language) == null) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        var ask = UpdateSettingAsk.valueOf(userId, theme, language);
        var answer = NetContext.getConsumer().syncAsk(ask, Message.class, userId).packet();
        return BaseResponse.valueOf(answer.getCode());
    }

    /**
     * 用户安全信息请求
     * 一般用在安全中心，查看密码设置状态，微信绑定状态，微博绑定状态，手机号码绑定状态
     *
     * @return 对象
     * @real_return {@link com.zfoo.app.zapp.web.user.model.AccountSecurityResponse}
     */
    @GetMapping(value = "/api/user/accountSecurity")
    @ResponseBody
    public BaseResponse saveSetting(HttpServletRequest request) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        var ask = AccountSecurityAsk.valueOf(userId);
        var answer = NetContext.getConsumer().syncAsk(ask, AccountSecurityAnswer.class, userId).packet();

        var phoneNumber = answer.getPhoneNumber();
        var phoneNumberStr = StringUtils.EMPTY;
        if (phoneNumber > 0) {
            phoneNumberStr = String.valueOf(phoneNumber);
            phoneNumberStr = StringUtils.format("{}xxxx{}", phoneNumberStr.substring(0, 3), phoneNumberStr.substring(7, 11));
        }
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, AccountSecurityResponse.valueOf(phoneNumberStr, answer.isWeChatBind(), answer.isWeiBoBind(), answer.isPasswordSet()));
    }

}
