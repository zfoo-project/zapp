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

import com.zfoo.app.zapp.common.entity.user.UserEntity;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserProfileStarAnswer;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserProfileStarAsk;
import com.zfoo.app.zapp.common.protocol.user.time.StarTimeSliceAsk;
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
 * @since 2020-10-04 16:45
 */
@Component
public class StarController {

    private static final Logger logger = LoggerFactory.getLogger(StarController.class);

    @EntityCachesInjection
    private IEntityCaches<Long, UserEntity> entityCaches;

    @PacketReceiver
    public void atGetUserProfileStarAsk(Session session, GetUserProfileStarAsk ask) {
        var userId = ask.getUserId();
        var userEntity = entityCaches.load(userId);
        if (userEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        var stars = userEntity.getStars();
        NetContext.getRouter().send(session, GetUserProfileStarAnswer.valueOf(stars));
    }

    @PacketReceiver
    public void atStarTimeSliceAsk(Session session, StarTimeSliceAsk ask) {
        var userId = ask.getUserId();
        var tsId = ask.getTsId();
        var userEntity = entityCaches.load(userId);
        if (userEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }

        var stars = userEntity.getStars();
        if (stars.contains(tsId)) {
            stars.removeIf(it -> it == tsId);
        } else {
            stars.add(tsId);
        }

        entityCaches.update(userEntity);
        NetContext.getRouter().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

}
