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

package com.zfoo.app.zapp.user.login.service;

import com.zfoo.app.zapp.common.entity.user.*;
import com.zfoo.app.zapp.common.entity.user.model.AdminAuthEnum;
import com.zfoo.app.zapp.common.entity.user.model.ThemeEnum;
import com.zfoo.app.zapp.common.util.TokenUtils;
import com.zfoo.app.zapp.user.group.service.IGroupService;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.util.math.NumberUtils;
import com.zfoo.util.security.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-10-28 15:29
 */
@Component
public class LoginService implements ILoginService {

    private static final String DEFAULT_NAME = "default";

    @EntityCachesInjection
    private IEntityCaches<Long, UserEntity> entityCaches;

    @Autowired
    private IGroupService groupService;

    private UserEntity createNewUser() {
        var id = MongoIdUtils.getIncrementIdFromMongoDefault(UserEntity.class);

        var userEntity = UserEntity.valueOf(id, DEFAULT_NAME);
        userEntity.getSetting().setTheme(ThemeEnum.LIGHT.getType());
        if (userEntity.getId() <= 1) {
            userEntity.setAdminAuth(AdminAuthEnum.ADMIN_AUTH.getType());
        }
        return userEntity;
    }


    @Override
    public String signInByAccount(String account, String password) {
        var userId = 0L;

        // 先查邮件账号是否存在
        if (userId <= 0) {
            var mailEntity = OrmContext.getAccessor().load(account, MailEntity.class);
            if (mailEntity != null) {
                userId = mailEntity.getUid();
            }
        }

        // 再查电话号码是否存在
        if (userId <= 0) {
            if (NumberUtils.isLong(account)) {
                var phoneNumber = Long.parseLong(account);
                var phoneEntity = OrmContext.getAccessor().load(phoneNumber, PhoneEntity.class);
                if (phoneEntity != null) {
                    userId = phoneEntity.getUid();
                }
            }
        }

        if (userId <= 0) {
            return null;
        }

        var accountEntity = OrmContext.getAccessor().load(userId, AccountEntity.class);

        // 验证密码是否正确
        var passwordMd5 = MD5Utils.strToMD5(password);
        if (!passwordMd5.equals(accountEntity.getPassword())) {
            return null;
        }
        var token = TokenUtils.set(userId);
        return token;
    }

    @Override
    public Pair<String, Boolean> signInByPhone(long phoneNumber) {
        // 先查数据库是否存在
        var phoneEntity = OrmContext.getAccessor().load(phoneNumber, PhoneEntity.class);

        // 手机号登录会自动注册用户
        if (phoneEntity == null) {
            var userEntity = createNewUser();
            userEntity.setName(String.valueOf(phoneNumber).substring(0, 8));

            // 使用用户id的负数，创建一个属于自己的群组
            groupService.createGroup(userEntity, -userEntity.getId(), null, null);

            phoneEntity = PhoneEntity.valueOf(phoneNumber, userEntity.getId());
            OrmContext.getAccessor().insert(phoneEntity);
            OrmContext.getAccessor().insert(userEntity);
            return new Pair<>(TokenUtils.set(phoneEntity.getUid()), Boolean.TRUE);
        }

        return new Pair<>(TokenUtils.set(phoneEntity.getUid()), Boolean.FALSE);
    }

    @Override
    public Pair<String, Boolean> weChatSignIn(String unionid) {
        var weChatEntity = OrmContext.getAccessor().load(unionid, WeChatEntity.class);

        if (weChatEntity == null) {
            return new Pair<>(StringUtils.EMPTY, Boolean.TRUE);
        }

        return new Pair<>(TokenUtils.set(weChatEntity.getUid()), Boolean.FALSE);
    }

    @Override
    public Pair<String, Boolean> weiBoSignIn(String weiBoUid) {
        var weiBoEntity = OrmContext.getAccessor().load(weiBoUid, WeiBoEntity.class);

        if (weiBoEntity == null) {
            return new Pair<>(StringUtils.EMPTY, Boolean.TRUE);
        }

        return new Pair<>(TokenUtils.set(weiBoEntity.getUid()), Boolean.FALSE);
    }

}
