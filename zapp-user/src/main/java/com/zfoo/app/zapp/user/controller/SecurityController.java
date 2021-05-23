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

package com.zfoo.app.zapp.user.controller;

import com.zfoo.app.zapp.common.entity.user.*;
import com.zfoo.app.zapp.common.protocol.user.info.*;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.net.NetContext;
import com.zfoo.net.dispatcher.model.anno.PacketReceiver;
import com.zfoo.net.packet.common.Error;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.session.model.Session;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.util.security.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-10-04 15:36
 */
@Component
public class SecurityController {

    private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);

    @EntityCachesInjection
    private IEntityCaches<Long, UserEntity> entityCaches;

    @PacketReceiver
    public void atAccountSecurityAsk(Session session, AccountSecurityAsk ask) {
        var userId = ask.getUserId();

        var phoneList = OrmContext.getQuery().queryFieldEqual("uid", userId, PhoneEntity.class);
        var phoneNumber = CollectionUtils.isEmpty(phoneList) ? 0 : phoneList.get(0).getId();

        var weiBoBind = false;
        var weiBoOptional = OrmContext.getQuery()
                .queryFieldEqual("uid", userId, WeiBoEntity.class)
                .stream()
                .findFirst();
        if (weiBoOptional.isPresent()) {
            weiBoBind = true;
        }

        var weiChatBind = false;
        var weiChatOptional = OrmContext.getQuery()
                .queryFieldEqual("uid", userId, WeChatEntity.class)
                .stream()
                .findFirst();
        if (weiChatOptional.isPresent()) {
            weiChatBind = true;
        }

        var passwordSet = false;
        var accountEntity = OrmContext.getAccessor().load(userId, AccountEntity.class);
        if (accountEntity != null) {
            passwordSet = true;
        }

        NetContext.getDispatcher().send(session, AccountSecurityAnswer.valueOf(phoneNumber, weiChatBind, weiBoBind, passwordSet));
    }

    @PacketReceiver
    public void atUpdatePasswordAsk(Session session, UpdatePasswordAsk ask) {
        var userId = ask.getUserId();
        var password = ask.getPassword();

        var accountEntity = OrmContext.getAccessor().load(userId, AccountEntity.class);
        if (accountEntity == null) {
            OrmContext.getAccessor().insert(AccountEntity.valueOf(userId, password));
        } else {
            var passwordMd5 = MD5Utils.strToMD5(password);
            accountEntity.setPassword(passwordMd5);
            OrmContext.getAccessor().update(accountEntity);
        }

        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK_QUIETLY.getCode()));
    }

    @PacketReceiver
    public void atUpdatePhoneAsk(Session session, UpdatePhoneAsk ask) {
        var userId = ask.getUserId();
        var phoneNumber = ask.getPhoneNumber();

        // 先查看手机号有没有绑定到别的账号
        if (OrmContext.getAccessor().load(phoneNumber, PhoneEntity.class) != null) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.USER_BIND_PHONE_ERROR.getCode()));
            return;
        }

        OrmContext.getQuery()
                .queryFieldEqual("uid", userId, PhoneEntity.class)
                .forEach(it -> OrmContext.getAccessor().delete(it));

        OrmContext.getAccessor().insert(PhoneEntity.valueOf(phoneNumber, userId));

        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK_QUIETLY.getCode()));
    }

    @PacketReceiver
    public void atUpdateAdminAuthAsk(Session session, UpdateAdminAuthAsk ask) {
        var userId = ask.getUserId();
        var adminAuth = ask.getAdminAuth();

        var entity = entityCaches.load(userId);
        if (entity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        entity.setAdminAuth(adminAuth);
        entityCaches.update(entity);
        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

    @PacketReceiver
    public void atGetUserAdminAuthAsk(Session session, GetUserAdminAuthAsk ask) {
        var userId = ask.getUserId();
        var entity = entityCaches.load(userId);
        if (entity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }

        NetContext.getDispatcher().send(session, GetUserAdminAuthAnswer.valueOf(userId, entity.getAdminAuth()));
    }
}
