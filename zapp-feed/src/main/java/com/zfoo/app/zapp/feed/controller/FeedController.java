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

package com.zfoo.app.zapp.feed.controller;

import com.zfoo.app.zapp.common.protocol.feed.home.CreateTsAsk;
import com.zfoo.app.zapp.common.protocol.feed.home.LoveTsAsk;
import com.zfoo.app.zapp.common.protocol.feed.item.CreateTsWithItemAsk;
import com.zfoo.app.zapp.common.protocol.feed.item.LoveTsWithItemAsk;
import com.zfoo.app.zapp.common.protocol.feed.location.CreateTsWithLocationAsk;
import com.zfoo.app.zapp.common.protocol.feed.location.LoveTsWithLocationAsk;
import com.zfoo.app.zapp.common.protocol.feed.person.CreateTsWithPersonAsk;
import com.zfoo.app.zapp.common.protocol.feed.person.LoveTsWithPersonAsk;
import com.zfoo.app.zapp.feed.service.IFeedService;
import com.zfoo.net.dispatcher.model.anno.PacketReceiver;
import com.zfoo.net.session.model.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-16 11:12
 */
@Component
public class FeedController {

    @Autowired
    private IFeedService feedService;

    @PacketReceiver
    public void atCreateTsAsk(Session session, CreateTsAsk ask) {
        var tsId = ask.getTsId();
        var recommend = ask.isRecommend();
        feedService.createTs(tsId, recommend);
    }

    @PacketReceiver
    public void atLoveTsAsk(Session session, LoveTsAsk ask) {
        var tsId = ask.getTsId();
        var love = ask.getLove();
        var score = ask.getScore();
        feedService.loveTs(tsId, love, score);
    }

    @PacketReceiver
    public void atCreateTsWithLocationAsk(Session session, CreateTsWithLocationAsk ask) {
        var locationId = ask.getLocationId();
        var tsId = ask.getTsId();
        var recommend = ask.isRecommend();
        feedService.createTsWithLocation(locationId, tsId, recommend);
    }

    @PacketReceiver
    public void atCreateTsWithItemAsk(Session session, CreateTsWithItemAsk ask) {
        var itemId = ask.getItemId();
        var tsId = ask.getTsId();
        var recommend = ask.isRecommend();
        feedService.createTsWithItem(itemId, tsId, recommend);
    }

    @PacketReceiver
    public void atCreateTsWithPersonAsk(Session session, CreateTsWithPersonAsk ask) {
        var personId = ask.getPersonId();
        var tsId = ask.getTsId();
        var recommend = ask.isRecommend();
        feedService.createTsWithPerson(personId, tsId, recommend);
    }

    @PacketReceiver
    public void atLoveTsWithLocationAsk(Session session, LoveTsWithLocationAsk ask) {
        var locationId = ask.getLocationId();
        var tsId = ask.getTsId();
        var love = ask.getLove();
        var score = ask.getScore();
        feedService.loveTsWithLocation(locationId, tsId, love, score);
    }

    @PacketReceiver
    public void atLoveTsWithItemAsk(Session session, LoveTsWithItemAsk ask) {
        var itemId = ask.getItemId();
        var tsId = ask.getTsId();
        var love = ask.getLove();
        var score = ask.getScore();
        feedService.loveTsWithItem(itemId, tsId, love, score);
    }

    @PacketReceiver
    public void atLoveTsWithPersonAsk(Session session, LoveTsWithPersonAsk ask) {
        var personId = ask.getPersonId();
        var tsId = ask.getTsId();
        var love = ask.getLove();
        var score = ask.getScore();
        feedService.loveTsWithPerson(personId, tsId, love, score);
    }

}
