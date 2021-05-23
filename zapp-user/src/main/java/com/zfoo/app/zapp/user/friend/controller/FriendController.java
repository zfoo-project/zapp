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

package com.zfoo.app.zapp.user.friend.controller;

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.entity.user.UserEntity;
import com.zfoo.app.zapp.common.protocol.user.friend.*;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.user.friend.service.IFriendService;
import com.zfoo.net.NetContext;
import com.zfoo.net.dispatcher.model.anno.PacketReceiver;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.session.model.Session;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-15 15:34
 */
@Component
public class FriendController {

    private static final Logger logger = LoggerFactory.getLogger(FriendController.class);

    @Autowired
    private IFriendService friendService;

    @EntityCachesInjection
    private IEntityCaches<Long, UserEntity> entityCaches;

    @PacketReceiver
    public void atApplyFriendLimitAsk(Session session, ApplyFriendLimitAsk ask) {
        var userId = ask.getUserId();

        var userEntity = entityCaches.load(userId);
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }

        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

    @PacketReceiver
    public void atUserAcceptFriendAsk(Session session, UserAcceptFriendAsk ask) {
        var userId = ask.getUserId();
        var friendId = ask.getFriendId();

        if (!CommonUtils.isUserIdInRange(List.of(userId, friendId)) || friendId == userId) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.PARAMETER_ERROR.getCode()));
            return;
        }

        var userEntity = entityCaches.load(userId);
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }

        var friends = userEntity.getFriends();
        if (friends.size() >= AppConstant.FRIEND_LIMIT) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.FRIEND_LIMIT.getCode()));
            return;
        }
        if (!friends.contains(friendId)) {
            friends.add(friendId);
            entityCaches.update(userEntity);
        }
        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

    @PacketReceiver
    public void atDeleteFriendAsk(Session session, DeleteFriendAsk ask) {
        var userId = ask.getUserId();
        var friendId = ask.getFriendId();

        if (!CommonUtils.isUserIdInRange(List.of(friendId)) || friendId == userId) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.PARAMETER_ERROR.getCode()));
            return;
        }

        var userEntity = entityCaches.load(userId);
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }

        var friends = userEntity.getFriends();
        friends.removeIf(it -> it == friendId);
        entityCaches.update(userEntity);
        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

    @PacketReceiver
    public void atBlacklistAsk(Session session, BlacklistAsk ask) {
        var userId = ask.getUserId();
        var targetId = ask.getTargetId();

        if (!CommonUtils.isUserIdInRange(List.of(targetId)) || targetId == userId) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.PARAMETER_ERROR.getCode()));
            return;
        }

        var userEntity = entityCaches.load(userId);
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }

        var blacklist = userEntity.getBlacklist();

        if (blacklist.size() >= AppConstant.BLACKLIST_LIMIT) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.BLACKLIST_LIMIT.getCode()));
            return;
        }

        if (blacklist.contains(targetId)) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.BLACKLIST_LIMIT.getCode()));
            return;
        }
        blacklist.add(targetId);
        entityCaches.update(userEntity);
        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

    @PacketReceiver
    public void atBlacklistCancelAsk(Session session, BlacklistCancelAsk ask) {
        var userId = ask.getUserId();
        var targetId = ask.getTargetId();

        if (!CommonUtils.isUserIdInRange(List.of(targetId)) || targetId == userId) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.PARAMETER_ERROR.getCode()));
            return;
        }

        var userEntity = entityCaches.load(userId);
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }

        var blacklist = userEntity.getBlacklist();
        blacklist.removeIf(it -> it == targetId);
        entityCaches.update(userEntity);
        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

}
