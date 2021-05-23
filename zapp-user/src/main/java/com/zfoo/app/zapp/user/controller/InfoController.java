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
import com.zfoo.app.zapp.common.model.LicenseEnum;
import com.zfoo.app.zapp.common.protocol.user.info.*;
import com.zfoo.app.zapp.common.protocol.user.time.LoveStatisticsAsk;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.user.login.model.LoginEvent;
import com.zfoo.event.manager.EventBus;
import com.zfoo.net.NetContext;
import com.zfoo.net.dispatcher.model.anno.PacketReceiver;
import com.zfoo.net.packet.common.Error;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.session.model.Session;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import com.zfoo.protocol.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-21 12:40
 */
@Component
public class InfoController {

    private static final Logger logger = LoggerFactory.getLogger(InfoController.class);

    @EntityCachesInjection
    private IEntityCaches<Long, UserEntity> entityCaches;

    @PacketReceiver
    public void atUpdateBaseInfoAsk(Session session, UpdateBaseInfoAsk ask) {
        var userEntity = entityCaches.load(ask.getUserId());
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        userEntity.setGender(ask.getGender());
        userEntity.setName(ask.getName());
        userEntity.setSignature(ask.getSignature());
        entityCaches.update(userEntity);
        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

    @PacketReceiver
    public void atUpdateAvatarAsk(Session session, UpdateAvatarAsk ask) {
        var userEntity = entityCaches.load(ask.getUserId());
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        userEntity.setAvatar(ask.getAvatar());
        entityCaches.update(userEntity);
        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

    @PacketReceiver
    public void atUpdateBackgroundAsk(Session session, UpdateBackgroundAsk ask) {
        var userEntity = entityCaches.load(ask.getUserId());
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        userEntity.setBackground(ask.getBackground());
        entityCaches.update(userEntity);

        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

    @PacketReceiver
    public void atUpdateCustomStatusAsk(Session session, UpdateCustomStatusAsk ask) {
        var userEntity = entityCaches.load(ask.getUserId());
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        userEntity.setCustomTime(ask.getCustomTime());
        userEntity.setCustom(ask.getCustom());
        entityCaches.update(userEntity);
        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

    @PacketReceiver
    public void atUpdateSettingAsk(Session session, UpdateSettingAsk ask) {
        var userEntity = entityCaches.load(ask.getUserId());
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        var settingPO = userEntity.getSetting();
        settingPO.setLanguage(ask.getLanguage());
        settingPO.setTheme(ask.getTheme());
        entityCaches.update(userEntity);
        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

    @PacketReceiver
    public void atLoveStatisticsAsk(Session session, LoveStatisticsAsk ask) {
        var userEntity = entityCaches.load(ask.getUserId());
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        var license = LicenseEnum.getLicenseEnumByType(ask.getType());
        var count = ask.getCount();

        switch (license) {
            case CC0:
            case CC1:
            case CC2:
                userEntity.setFree(userEntity.getFree() + count);
                break;
            case CC6:
            case CC7:
            case CC8:
            case CC9:
            case ZFOO:
                userEntity.setNormal(userEntity.getNormal() + count);
                break;
            default:
                logger.error("未知的枚举类型[{}]", JsonUtils.object2String(ask));
        }

        entityCaches.update(userEntity);
    }


    @PacketReceiver
    public void atGetUserProfileAsk(Session session, GetUserProfileAsk ask) {
        var userId = ask.getUserId();
        var entity = entityCaches.load(userId);
        if (entity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }

        EventBus.syncSubmit(LoginEvent.valueOf(entity));

        var answer = GetUserProfileAnswer.valueOf(entity.getId(), entity.getAdminAuth(), entity.getName(), entity.getCoin()
                , entity.getAvatar(), entity.getBackground(), entity.getGender()
                , entity.getSignature(), entity.getCustomTime(), entity.getCustom()
                , entity.getFollows().size(), entity.getFanNum(), entity.getStars().size(), entity.getFree(), entity.getNormal()
                , entity.getFollows(), entity.getStars()
                , entity.getLocations(), entity.getPersons(), entity.getItems(), entity.getSetting().toSettingVO());

        NetContext.getDispatcher().send(session, answer);
    }

}
