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

package com.zfoo.app.zapp.web.controller;

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.entity.group.ChannelEntity;
import com.zfoo.app.zapp.common.entity.group.GroupEntity;
import com.zfoo.app.zapp.common.entity.group.model.*;
import com.zfoo.app.zapp.common.entity.user.AccountEntity;
import com.zfoo.app.zapp.common.entity.user.MailEntity;
import com.zfoo.app.zapp.common.entity.user.PhoneEntity;
import com.zfoo.app.zapp.common.entity.user.UserEntity;
import com.zfoo.app.zapp.common.entity.user.model.AdminAuthEnum;
import com.zfoo.app.zapp.common.entity.user.model.GroupTimePO;
import com.zfoo.app.zapp.common.entity.user.model.ThemeEnum;
import com.zfoo.app.zapp.common.protocol.user.SignInAnswer;
import com.zfoo.app.zapp.common.protocol.user.SignInByAccountAsk;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.web.login.model.SignInResponse;
import com.zfoo.net.NetContext;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.util.math.NumberUtils;
import com.zfoo.util.security.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-07 20:40
 */
@Controller
public class LoginControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(LoginControllerTest.class);

    // localhost:8090/signIn
    @RequestMapping(value = "/api/signInTest", method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse signIn(@RequestParam("account") String account) throws Exception {
        if (StringUtils.isBlank(account)) {
            return BaseResponse.valueOf(CodeEnum.FAIL);
        }

        var userId = 0L;

        // 测试环境，如果用户不存在，则直接创建用户
        if (userId <= 0) {
            var mailEntity = OrmContext.getAccessor().load(account, MailEntity.class);
            if (mailEntity != null) {
                userId = mailEntity.getUid();
            }
        }

        if (userId <= 0) {
            if (NumberUtils.isLong(account)) {
                var phoneNumber = Long.parseLong(account);
                var phoneEntity = OrmContext.getAccessor().load(phoneNumber, PhoneEntity.class);
                if (phoneEntity != null) {
                    userId = phoneEntity.getUid();
                }
            }
        }

        // **********************************************************************************************
        // 测试环境暂时不用校验密码，测试环境创建默认的用户，将account插入到AccountEntity中
        if (userId <= 0) {
            var userEntity = createNewUser(account);
            userEntity.setName(account);

            // 使用用户id的负数，创建一个属于自己的群组
            createGroup(userEntity, -userEntity.getId(), null, null);

            OrmContext.getAccessor().insert(MailEntity.valueOf(account, userEntity.getId()));
            var passwordMd5 = MD5Utils.strToMD5(StringUtils.ARAB_NUMBER);
            OrmContext.getAccessor().insert(AccountEntity.valueOf(userEntity.getId(), passwordMd5));
            OrmContext.getAccessor().insert(userEntity);

            userId = userEntity.getId();
        }

        // 用户登录请求
        var signInAnswer = NetContext.getConsumer().syncAsk(SignInByAccountAsk.valueOf(account, StringUtils.ARAB_NUMBER), SignInAnswer.class, account).packet();
        var token = signInAnswer.getToken();
        if (StringUtils.isBlank(token)) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FAIL);
        }
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, SignInResponse.valueOf(signInAnswer.getToken()));
    }

    private UserEntity createNewUser(String account) {
        var id = MongoIdUtils.getIncrementIdFromMongoDefault(UserEntity.class);

        var userEntity = UserEntity.valueOf(id, account);
        userEntity.getSetting().setTheme(ThemeEnum.LIGHT.getType());
        userEntity.setAdminAuth(AdminAuthEnum.ADMIN_AUTH.getType());

        return userEntity;
    }

    private GroupEntity createGroup(UserEntity userEntity, long groupId, @Nullable String groupName, @Nullable String avatar) {
        var userId = userEntity.getId();

        groupName = StringUtils.isBlank(groupName) ? userEntity.getName() + AppConstant.GROUP_DEFAULT_GROUP_NAME_SUFFIX : groupName;


        // 创建一个用户的公共群群，群组的id为用户id的负数
        var groupEntity = new GroupEntity();
        groupEntity.setId(groupId);
        groupEntity.setName(groupName);
        groupEntity.setAvatar(avatar);
        groupEntity.setAdminId(userId);
        groupEntity.getPeople().add(userId);
        groupEntity.setCreateTime(TimeUtils.now());

        // 默认身份
        var defaultGroupAuth = GroupAuthPO.defaultGroupAuthPO();
        groupEntity.setGroupAuths(new ArrayList<>(List.of(defaultGroupAuth)));

        // 创建一个公共频道
        var commonChannelId = MongoIdUtils.getIncrementIdFromMongoDefault(ChannelEntity.class);
        var commonChannelEntity = new ChannelEntity();
        commonChannelEntity.setId(commonChannelId);
        var commonChannelPO = ChannelPO.valueOf(commonChannelId, AppConstant.GROUP_COMMON_CHANNEL_CN_NAME
                , new ArrayList<>(List.of(ChannelAuthPO.valueOf(defaultGroupAuth.getId(), ChannelAuthEnum.MIDDLE_AUTH.getType()))));
        var commonChannelBoxPO = ChannelBoxPO.valueOf(AppConstant.GROUP_MAIN_CHANNEL_BOX, new ArrayList<>(List.of(commonChannelPO)));


        // 创建一个默认的频道
        var defaultChannelId = MongoIdUtils.getIncrementIdFromMongoDefault(ChannelEntity.class);
        var defaultChannelEntity = new ChannelEntity();
        defaultChannelEntity.setId(defaultChannelId);
        var defaultChannelPO = ChannelPO.valueOf(defaultChannelId, AppConstant.GROUP_DEFAULT_CHANNEL_CN_NAME
                , new ArrayList<>(List.of(ChannelAuthPO.valueOf(defaultGroupAuth.getId(), ChannelAuthEnum.BASE_AUTH.getType()))));
        var defaultChannelBoxPO = ChannelBoxPO.valueOf(AppConstant.GROUP_DEFAULT_PARENT_CHANNEL_CN_NAME, new ArrayList<>(List.of(defaultChannelPO)));


        // 每一个group都有一个主要的分组，放在列表的最前方
        groupEntity.getChannelBoxes().add(commonChannelBoxPO);
        groupEntity.getChannelBoxes().add(defaultChannelBoxPO);

        OrmContext.getAccessor().insert(commonChannelEntity);
        OrmContext.getAccessor().insert(defaultChannelEntity);
        OrmContext.getAccessor().insert(groupEntity);

        userEntity.getGroups().add(GroupTimePO.valueOf(groupId));

        userEntity.getGroups().add(GroupTimePO.valueOf(AppConstant.ZFOO_GROUP_ID));
        return groupEntity;
    }

}
