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

package com.zfoo.app.zapp.user.login.controller;

import com.zfoo.app.zapp.common.entity.user.UserEntity;
import com.zfoo.app.zapp.common.entity.user.WeiBoEntity;
import com.zfoo.app.zapp.common.entity.user.model.GenderEnum;
import com.zfoo.app.zapp.common.protocol.user.weibo.*;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.user.group.service.IGroupService;
import com.zfoo.app.zapp.user.login.service.ILoginService;
import com.zfoo.net.NetContext;
import com.zfoo.net.dispatcher.model.anno.PacketReceiver;
import com.zfoo.net.packet.common.Error;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.session.model.Session;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-01-14 15:00
 */
@Component
public class WeiboLoginController {

    private static final Logger logger = LoggerFactory.getLogger(WeiboLoginController.class);

    @Autowired
    private ILoginService loginService;

    @Autowired
    private IGroupService groupService;

    @EntityCachesInjection
    private IEntityCaches<Long, UserEntity> entityCaches;

    @PacketReceiver
    public void atWeiBoSignInAsk(Session session, WeiBoSignInAsk ask) {
        logger.info("微博用户登录请求[{}]", JsonUtils.object2String(ask));

        var pair = loginService.weiBoSignIn(ask.getWeiBoUid());

        NetContext.getDispatcher().send(session, WeiBoSignInAnswer.valueOf(pair.getKey(), pair.getValue()));
    }

    @PacketReceiver
    public void atWeiBoBindAsk(Session session, WeiBoBindAsk ask) {
        logger.info("微博用户绑定请求[{}]", JsonUtils.object2String(ask));

        var weiBoUid = ask.getWeiBoUid();
        var userId = ask.getUserId();

        var weiBoEntity = OrmContext.getAccessor().load(weiBoUid, WeiBoEntity.class);
        if (weiBoEntity != null) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.USER_BIND_WEIBO_ERROR.getCode()));
            return;
        }

        var weiBoList = OrmContext.getQuery().queryFieldEqual("uid", userId, WeiBoEntity.class);
        if (CollectionUtils.isNotEmpty(weiBoList)) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.USER_ALREADY_BIND_WEIBO_ERROR.getCode()));
            return;
        }

        OrmContext.getAccessor().insert(WeiBoEntity.valueOf(weiBoUid, userId));
        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

    @PacketReceiver
    public void atWeiBoUnbindAsk(Session session, WeiBoUnbindAsk ask) {
        logger.info("微博用户解除绑定请求[{}]", JsonUtils.object2String(ask));

        var userId = ask.getUserId();

        var weiBoList = OrmContext.getQuery().queryFieldEqual("uid", userId, WeiBoEntity.class);
        if (CollectionUtils.isEmpty(weiBoList)) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.USER_BIND_WEIBO_EMPTY_ERROR.getCode()));
            return;
        }

        OrmContext.getAccessor().delete(weiBoList.get(0));
        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }


    @PacketReceiver
    public void atWeiBoUserInfoAsk(Session session, WeiBoUserInfoAsk ask) {
        logger.info("微博用户信息更新请求[{}]", JsonUtils.object2String(ask));

        var name = ask.getName();
        var avatar = ask.getProfileImageUrl();

        var userEntity = entityCaches.load(ask.getUid());
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        userEntity.setName(name);
        userEntity.setAvatar(avatar);

        if (ask.getGender().equals("m")) {
            userEntity.setGender(GenderEnum.MALE.getType());
        } else if (ask.getGender().equals("f")) {
            userEntity.setGender(GenderEnum.FEMALE.getType());
        } else {
            userEntity.setGender(GenderEnum.MALE_FEMALE.getType());
        }

        // 使用用户id的负数，创建一个属于自己的群组
        groupService.createGroup(userEntity, -userEntity.getId(), null, avatar);

        entityCaches.update(userEntity);

        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode(), CodeEnum.OK.getMessage()));
    }

}
