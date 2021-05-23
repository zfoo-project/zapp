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
import com.zfoo.app.zapp.common.entity.user.WeChatEntity;
import com.zfoo.app.zapp.common.entity.user.model.GenderEnum;
import com.zfoo.app.zapp.common.protocol.user.wechat.*;
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
public class WeChatLoginController {

    private static final Logger logger = LoggerFactory.getLogger(WeChatLoginController.class);

    @Autowired
    private ILoginService loginService;

    @Autowired
    private IGroupService groupService;

    @EntityCachesInjection
    private IEntityCaches<Long, UserEntity> entityCaches;


    @PacketReceiver
    public void atWeChatSignInAsk(Session session, WeChatSignInAsk ask) {
        logger.info("微信用户登录请求[{}]", JsonUtils.object2String(ask));

        var pair = loginService.weChatSignIn(ask.getUnionid());

        NetContext.getDispatcher().send(session, WeChatSignInAnswer.valueOf(pair.getKey(), pair.getValue()));
    }

    @PacketReceiver
    public void atWeChatBindAsk(Session session, WeChatBindAsk ask) {
        logger.info("微信用户绑定请求[{}]", JsonUtils.object2String(ask));

        var unionid = ask.getUnionid();
        var userId = ask.getUserId();

        var weChatEntity = OrmContext.getAccessor().load(unionid, WeChatEntity.class);
        if (weChatEntity != null) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.USER_BIND_WECHAT_ERROR.getCode()));
            return;
        }

        var weChatList = OrmContext.getQuery().queryFieldEqual("uid", userId, WeChatEntity.class);
        if (CollectionUtils.isNotEmpty(weChatList)) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.USER_ALREADY_BIND_WECHAT_ERROR.getCode()));
            return;
        }

        OrmContext.getAccessor().insert(WeChatEntity.valueOf(unionid, userId));
        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

    @PacketReceiver
    public void atWeChatUnbindAsk(Session session, WeChatUnbindAsk ask) {
        logger.info("微信用户解除绑定请求[{}]", JsonUtils.object2String(ask));

        var userId = ask.getUserId();

        var weChatList = OrmContext.getQuery().queryFieldEqual("uid", userId, WeChatEntity.class);
        if (CollectionUtils.isEmpty(weChatList)) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.USER_BIND_WECHAT_EMPTY_ERROR.getCode()));
            return;
        }

        OrmContext.getAccessor().delete(weChatList.get(0));
        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

    @PacketReceiver
    public void atWeChatUserInfoAsk(Session session, WeChatUserInfoAsk ask) {
        logger.info("微信用户信息更新请求[{}]", JsonUtils.object2String(ask));
        var name = ask.getNickname();
        var avatar = ask.getHeadImgUrl();

        var gender = ask.getSex() == 1 ? GenderEnum.MALE_FEMALE.getType() : GenderEnum.FEMALE.getType();

        var userEntity = entityCaches.load(ask.getUid());
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        userEntity.setName(name);
        userEntity.setAvatar(avatar);
        userEntity.setGender(gender);

        // 使用用户id的负数，创建一个属于自己的群组
        groupService.createGroup(userEntity, -userEntity.getId(), null, avatar);

        entityCaches.update(userEntity);

        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode(), CodeEnum.OK.getMessage()));
    }

}
