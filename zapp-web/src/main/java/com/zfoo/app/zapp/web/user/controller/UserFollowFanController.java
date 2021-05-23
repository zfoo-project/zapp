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
import com.zfoo.app.zapp.common.entity.user.UserEntity;
import com.zfoo.app.zapp.common.protocol.cache.GetUserCacheAnswer;
import com.zfoo.app.zapp.common.protocol.cache.GetUserCacheAsk;
import com.zfoo.app.zapp.common.protocol.user.fan.FanCancelUserAsk;
import com.zfoo.app.zapp.common.protocol.user.fan.FanUserAsk;
import com.zfoo.app.zapp.common.protocol.user.fan.FollowUserAsk;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserProfileFanAnswer;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserProfileFanAsk;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserProfileFollowAnswer;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserProfileFollowAsk;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.web.user.model.FollowUserRequest;
import com.zfoo.app.zapp.web.util.HttpUtils;
import com.zfoo.net.NetContext;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.util.SimpleCache;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.model.query.Page;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.model.Pair;
import com.zfoo.scheduler.util.TimeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户的关注，粉丝
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-10-04 11:51
 */
@Controller
public class UserFollowFanController {

    private SimpleCache<Long, List<Long>> userFollowsCaches = SimpleCache.build(
            60 * TimeUtils.MILLIS_PER_SECOND, 30 * TimeUtils.MILLIS_PER_SECOND, 1_000
            , ids -> OrmContext.getQuery().queryFieldIn("_id", ids, UserEntity.class)
                    .stream()
                    .map(it -> new Pair<>(it.getId(), it.getFollows()))
                    .collect(Collectors.toList())
            , key -> Collections.EMPTY_LIST);

    private SimpleCache<Long, List<Long>> userFansCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_000
            , ids -> OrmContext.getQuery().queryFieldIn("_id", ids, UserEntity.class)
                    .stream()
                    .map(it -> new Pair<>(it.getId(), it.getFans()))
                    .collect(Collectors.toList())
            , key -> Collections.EMPTY_LIST);

    /**
     * 获取用户关注的用户列表
     *
     * @param id   用户id
     * @param page 页数
     * @return 对象数组，UserCache
     */
    @GetMapping(value = "/api/user/follow/list")
    @ResponseBody
    public BaseResponse userFollowList(@RequestParam("id") long id, @RequestParam("page") int page) throws Exception {
        if (!CommonUtils.isUserIdInRange(List.of(id))) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        if (page <= 0) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var follows = userFollowsCaches.get(id);
        var followPage = Page.valueOf(page, AppConstant.USER_LIST_PAGE_SIZE, follows.size());
        var userList = followPage.currentPageList(follows);

        if (CollectionUtils.isEmpty(userList)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var answer = NetContext.getConsumer().syncAsk(GetUserCacheAsk.valueOf(new HashSet<>(userList)), GetUserCacheAnswer.class, userList.get(0)).packet();
        var userInfoList = CollectionUtils.isNotEmpty(answer.getUserCacheMap())
                ? answer.getUserCacheMap().values().stream().collect(Collectors.toList())
                : Collections.EMPTY_LIST;

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, userInfoList);
    }

    /**
     * 获取用户的粉丝列表
     *
     * @param id   用户的id
     * @param page 页数
     * @return 对象数组，UserCache
     */
    @GetMapping(value = "/api/user/fan/list")
    @ResponseBody
    public BaseResponse userFanList(@RequestParam("id") long id, @RequestParam("page") int page) throws Exception {
        if (!CommonUtils.isUserIdInRange(List.of(id))) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        if (page <= 0) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var fans = userFansCaches.get(id);
        var fanPage = Page.valueOf(page, AppConstant.USER_LIST_PAGE_SIZE, fans.size());
        var userList = fanPage.currentPageList(fans);

        if (CollectionUtils.isEmpty(userList)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var answer = NetContext.getConsumer().syncAsk(GetUserCacheAsk.valueOf(new HashSet<>(userList)), GetUserCacheAnswer.class, userList.get(0)).packet();
        var userInfoList = CollectionUtils.isNotEmpty(answer.getUserCacheMap())
                ? answer.getUserCacheMap().values().stream().collect(Collectors.toList())
                : Collections.EMPTY_LIST;

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, userInfoList);
    }

    /**
     * 获取当前用户的粉丝列表
     *
     * @param page 页数
     * @return 对象数组，UserCache
     */
    @GetMapping(value = "/api/user/profile/fan")
    @ResponseBody
    public BaseResponse profileFan(HttpServletRequest request, @RequestParam("page") int page) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        if (page <= 0) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var getUserProfileFanAnswer = NetContext.getConsumer().syncAsk(GetUserProfileFanAsk.valueOf(userId), GetUserProfileFanAnswer.class, userId).packet();
        var fans = getUserProfileFanAnswer.getFans();
        var fanPage = Page.valueOf(page, AppConstant.USER_LIST_PAGE_SIZE, fans.size());
        var userList = fanPage.currentPageList(fans);

        if (CollectionUtils.isEmpty(userList)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var answer = NetContext.getConsumer().syncAsk(GetUserCacheAsk.valueOf(new HashSet<>(userList)), GetUserCacheAnswer.class, userList.get(0)).packet();
        var userInfoList = CollectionUtils.isNotEmpty(answer.getUserCacheMap())
                ? answer.getUserCacheMap().values().stream().collect(Collectors.toList())
                : Collections.EMPTY_LIST;

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, userInfoList);
    }

    /**
     * 获取当前用户的关注
     *
     * @param page 页数
     * @return 对象数组，UserCache
     */
    @GetMapping(value = "/api/user/profile/follow")
    @ResponseBody
    public BaseResponse profileFollow(HttpServletRequest request, @RequestParam("page") int page) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        if (page <= 0) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var getUserProfileFanAnswer = NetContext.getConsumer().syncAsk(GetUserProfileFollowAsk.valueOf(userId), GetUserProfileFollowAnswer.class, userId).packet();
        var follows = getUserProfileFanAnswer.getFollows();
        var followPage = Page.valueOf(page, AppConstant.USER_LIST_PAGE_SIZE, follows.size());
        var userList = followPage.currentPageList(follows);

        if (CollectionUtils.isEmpty(userList)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var answer = NetContext.getConsumer().syncAsk(GetUserCacheAsk.valueOf(new HashSet<>(userList)), GetUserCacheAnswer.class, userList.get(0)).packet();
        var userInfoList = CollectionUtils.isNotEmpty(answer.getUserCacheMap())
                ? answer.getUserCacheMap().values().stream().collect(Collectors.toList())
                : Collections.EMPTY_LIST;

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, userInfoList);
    }

    /**
     * 关注用户
     *
     * @param cm 关注用户请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/follow/user", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse follow(HttpServletRequest request, @RequestBody FollowUserRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        var followUserId = cm.getFollowUserId();

        if (!CommonUtils.isUserIdInRange(List.of(userId, followUserId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        if (userId == followUserId) {
            return BaseResponse.valueOf(CodeEnum.STAR_MYSELF_ERROR);
        }

        NetContext.getConsumer().syncAsk(FollowUserAsk.valueOf(userId, followUserId), Message.class, userId).packet();
        NetContext.getConsumer().syncAsk(FanUserAsk.valueOf(followUserId, userId), Message.class, followUserId).packet();
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }

    /**
     * 取关用户
     *
     * @param cm 取关用户请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/unfollow/user")
    @ResponseBody
    public BaseResponse unfollow(HttpServletRequest request, @RequestBody FollowUserRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        var starUserId = cm.getFollowUserId();

        if (!CommonUtils.isUserIdInRange(List.of(userId, starUserId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        NetContext.getConsumer().syncAsk(FollowUserAsk.valueOf(userId, starUserId), Message.class, userId).packet();
        NetContext.getConsumer().syncAsk(FanCancelUserAsk.valueOf(starUserId, userId), Message.class, starUserId).packet();
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }

}
