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

package com.zfoo.app.zapp.user.time.controller;

import com.zfoo.app.zapp.common.entity.time.TimeSliceEntity;
import com.zfoo.app.zapp.common.protocol.common.time.TimeLinkAlbumVO;
import com.zfoo.app.zapp.common.protocol.user.time.DeleteTimeSliceAsk;
import com.zfoo.app.zapp.common.protocol.user.time.LoveTimeSliceAsk;
import com.zfoo.app.zapp.common.protocol.user.time.UpdateTimeSliceAsk;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.user.time.servece.ITsOperationService;
import com.zfoo.net.NetContext;
import com.zfoo.net.dispatcher.model.anno.PacketReceiver;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.session.model.Session;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-16 11:21
 */
@Component
public class TsOperationController {

    @Autowired
    private ITsOperationService tsOperationService;

    @EntityCachesInjection
    private IEntityCaches<Long, TimeSliceEntity> tsCaches;

    @PacketReceiver
    public void atLoveTimeSliceAsk(Session session, LoveTimeSliceAsk ask) {
        var tsId = ask.getId();
        var count = ask.getCount();
        tsOperationService.loveTimeSlice(tsId, count);
    }

    @PacketReceiver
    public void atDeleteTimeSliceAsk(Session session, DeleteTimeSliceAsk ask) {
        var tsId = ask.getId();
        tsOperationService.deleteTimeSlice(tsId);
    }

    @PacketReceiver
    public void atUpdateTimeSliceAsk(Session session, UpdateTimeSliceAsk ask) {
        var tsId = ask.getId();

        var entity = tsCaches.load(tsId);
        if (entity.id() == 0L) {
            NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
            return;
        }
        var type = ask.getType();
        var start = ask.getStart();
        var end = ask.getEnd();
        var locations = ask.getLocations();
        var persons = ask.getPersons();
        var items = ask.getItems();
        var content = ask.getContent();
        var images = ask.getImages();
        var video = ask.getVideo();
        var key = ask.getKey();
        var albums = ask.getAlbums();

        entity.setType(type);
        entity.setStart(start);
        entity.setEnd(end);
        entity.setLocations(locations);
        entity.setPersons(persons);
        entity.setItems(items);
        entity.setContent(content);
        entity.setImages(images);
        if (video == null) {
            entity.setVideo(null);
        } else {
            entity.setVideo(video.toVideoLinkPO());
        }

        if (key == null) {
            entity.setKey(null);
        } else {
            entity.setKey(key.toTimeKeyPO());
        }

        entity.setAlbums(TimeLinkAlbumVO.vosToPos(albums));
        tsCaches.update(entity);
        NetContext.getDispatcher().send(session, Message.valueOf(ask, CodeEnum.OK.getCode()));
    }

}
