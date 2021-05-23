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
import com.zfoo.app.zapp.common.protocol.user.fan.SubscribeItemAsk;
import com.zfoo.app.zapp.common.protocol.user.fan.SubscribeLocationAsk;
import com.zfoo.app.zapp.common.protocol.user.fan.SubscribePersonAsk;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.net.NetContext;
import com.zfoo.net.dispatcher.model.anno.PacketReceiver;
import com.zfoo.net.packet.common.Error;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.session.model.Session;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-10-04 15:37
 */
@Component
public class SubscribeController {

    private static final Logger logger = LoggerFactory.getLogger(SubscribeController.class);

    @EntityCachesInjection
    private IEntityCaches<Long, UserEntity> entityCaches;

    @PacketReceiver
    public void atSubscribeItemAsk(Session session, SubscribeItemAsk ask) {
        var userEntity = entityCaches.load(ask.getUserId());
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        var itemId = ask.getItemId();
        var items = userEntity.getItems();
        if (items.contains(itemId)) {
            items.removeIf(it -> it == itemId);
        } else {
            items.add(itemId);
        }
        entityCaches.update(userEntity);
        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

    @PacketReceiver
    public void atSubscribePersonAsk(Session session, SubscribePersonAsk ask) {
        var userEntity = entityCaches.load(ask.getUserId());
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }

        var personId = ask.getPersonId();
        var persons = userEntity.getPersons();
        if (persons.contains(personId)) {
            persons.removeIf(it -> it == personId);
        } else {
            persons.add(personId);
        }
        entityCaches.update(userEntity);
        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

    @PacketReceiver
    public void atSubscribeLocationAsk(Session session, SubscribeLocationAsk ask) {
        var userEntity = entityCaches.load(ask.getUserId());
        if (userEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(ask, CodeEnum.USER_NOT_EXIST.getCode()));
            return;
        }
        var locationId = ask.getLocationId();
        var locations = userEntity.getLocations();
        if (locations.contains(locationId)) {
            locations.removeIf(it -> it == locationId);
        } else {
            locations.add(locationId);
        }
        entityCaches.update(userEntity);
        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }
}
