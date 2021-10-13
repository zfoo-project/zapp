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

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.entity.user.UserEntity;
import com.zfoo.app.zapp.common.protocol.user.fan.FanCancelUserAsk;
import com.zfoo.app.zapp.common.protocol.user.fan.FanUserAsk;
import com.zfoo.app.zapp.common.protocol.user.fan.FollowUserAsk;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserProfileFanAnswer;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserProfileFanAsk;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserProfileFollowAnswer;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserProfileFollowAsk;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.net.NetContext;
import com.zfoo.net.packet.common.Error;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.router.receiver.PacketReceiver;
import com.zfoo.net.session.model.Session;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-10-04 15:33
 */
@Component
public class FollowFanController {

    private static final Logger logger = LoggerFactory.getLogger(FollowFanController.class);

    @EntityCachesInjection
    private IEntityCaches<Long, UserEntity> entityCaches;

    @PacketReceiver
    public void atGetUserProfileFollowAsk(Session session, GetUserProfileFollowAsk ask) {
        var userId = ask.getUserId();
        var userEntity = entityCaches.load(userId);
        if (userEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        var follows = userEntity.getFollows();
        NetContext.getRouter().send(session, GetUserProfileFollowAnswer.valueOf(follows));
    }

    @PacketReceiver
    public void atGetUserProfileFanAsk(Session session, GetUserProfileFanAsk ask) {
        var userId = ask.getUserId();
        var userEntity = entityCaches.load(userId);
        if (userEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        var fans = userEntity.getFans();
        NetContext.getRouter().send(session, GetUserProfileFanAnswer.valueOf(fans));
    }

    @PacketReceiver
    public void atFanCancelUserAsk(Session session, FanCancelUserAsk ask) {
        var userEntity = entityCaches.load(ask.getUserId());
        if (userEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        var fanCancelUserId = ask.getFanUserId();
        var fans = userEntity.getFans();
        if (fans.contains(fanCancelUserId)) {
            fans.removeIf(it -> it == fanCancelUserId);
        }
        userEntity.setFanNum(Math.max(userEntity.getFanNum() - 1, 0));
        entityCaches.update(userEntity);
        NetContext.getRouter().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

    @PacketReceiver
    public void atFanUserAsk(Session session, FanUserAsk ask) {
        var userEntity = entityCaches.load(ask.getUserId());
        if (userEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        var fanUserId = ask.getFanUserId();
        var fans = userEntity.getFans();
        if (!fans.contains(fanUserId)) {
            fans.add(fanUserId);
            if (fans.size() > AppConstant.USER_FAN_MAX_SIZE) {
                fans.remove(0);
            }
        }
        userEntity.setFanNum(userEntity.getFanNum() + 1);
        entityCaches.update(userEntity);
        NetContext.getRouter().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }


    @PacketReceiver
    public void atFollowUserAsk(Session session, FollowUserAsk ask) {
        var userEntity = entityCaches.load(ask.getUserId());
        if (userEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        var followUserId = ask.getFollowUserId();
        var follows = userEntity.getFollows();
        if (follows.contains(followUserId)) {
            follows.removeIf(it -> it == followUserId);
        } else {
            follows.add(followUserId);
        }
        entityCaches.update(userEntity);
        NetContext.getRouter().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }
}
