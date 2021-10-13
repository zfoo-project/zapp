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

package com.zfoo.app.zapp.cache.controller;

import com.zfoo.app.zapp.cache.service.CacheService;
import com.zfoo.app.zapp.common.protocol.cache.*;
import com.zfoo.app.zapp.common.protocol.cache.model.UserCache;
import com.zfoo.app.zapp.common.protocol.cache.refresh.RefreshCategoryCacheAsk;
import com.zfoo.app.zapp.common.protocol.cache.refresh.RefreshUserTsCacheAsk;
import com.zfoo.app.zapp.common.protocol.cache.refresh.RefreshWordCacheAsk;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.net.NetContext;
import com.zfoo.net.packet.model.GatewayPacketAttachment;
import com.zfoo.net.router.receiver.PacketReceiver;
import com.zfoo.net.session.model.Session;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.util.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-08 10:42
 */
@Component
public class CacheController {

    @Autowired
    private CacheService cacheService;

    private Map<Long, UserCache> getUserCaches(Set<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.EMPTY_MAP;
        }
        var userCacheMap = cacheService.userCaches
                .batchGet(userIds)
                .values()
                .stream()
                .collect(Collectors.toMap(key -> key.getId(), value -> value));
        return userCacheMap;
    }

    @PacketReceiver
    public void atGetUserCacheRequest(Session session, GetUserCacheRequest request, GatewayPacketAttachment gatewayAttachment) {
        NetContext.getRouter().send(session, GetUserCacheResponse.valueOf(getUserCaches(request.getUserIds())), gatewayAttachment);
    }


    @PacketReceiver
    public void atGetUserCacheAsk(Session session, GetUserCacheAsk ask) {
        var userIds = ask.getUserIds();
        NetContext.getRouter().send(session, GetUserCacheAnswer.valueOf(getUserCaches(userIds)));
    }

    @PacketReceiver
    public void atGetUserLatestCacheAsk(Session session, GetUserLatestCacheAsk ask) {
        var userIds = ask.getUserIds();
        userIds.forEach(it -> cacheService.userCaches.invalidate(it));
        NetContext.getRouter().send(session, GetUserLatestCacheAnswer.valueOf(getUserCaches(userIds)));
    }

    @PacketReceiver
    public void atGetUserTsCacheAsk(Session session, GetUserTsCacheAsk ask) {
        var userIds = ask.getUserIds();

        if (CollectionUtils.isEmpty(userIds)) {
            NetContext.getRouter().send(session, GetUserTsCacheAsk.valueOf(null));
            return;
        }
        var userTsMap = cacheService.userTimeSliceCaches
                .batchGet(userIds)
                .entrySet()
                .stream()
                .filter(it -> it.getValue() != Collections.EMPTY_LIST)
                .collect(Collectors.toMap(key -> key.getKey(), value -> value.getValue()));
        NetContext.getRouter().send(session, GetUserTsCacheAnswer.valueOf(userTsMap));
    }


    @PacketReceiver
    public void atRefreshUserTsCacheAsk(Session session, RefreshUserTsCacheAsk ask) {
        var userIds = ask.getUserIds();

        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }

        userIds.stream().forEach(it -> cacheService.userTimeSliceCaches.invalidate(it));
    }

    @PacketReceiver
    public void atRefreshWordCacheAsk(Session session, RefreshWordCacheAsk ask) {
        var words = ask.getWords();

        if (CollectionUtils.isEmpty(words)) {
            return;
        }

        words.stream().forEach(it -> cacheService.wordCaches.invalidate(it));
    }

    @PacketReceiver
    public void atRefreshCategoryCacheAsk(Session session, RefreshCategoryCacheAsk ask) {
        var categories = ask.getCategories();

        if (CollectionUtils.isEmpty(categories)) {
            return;
        }

        categories.stream().forEach(it -> cacheService.categoryCaches.invalidate(it));
    }

    private List<UserCache> searchUser(String query) {
        if (StringUtils.isBlank(query)) {
            return Collections.EMPTY_LIST;
        }

        var searchUserIds = new ArrayList<>(cacheService.userNameCaches.get(query));
        if (!CommonUtils.isUserIdInRange(searchUserIds)) {
            return Collections.EMPTY_LIST;
        }

        // 如果query是一个整数，有可能是玩家的id
        if (NumberUtils.isLong(query)) {
            searchUserIds.add(Long.parseLong(query));
        }

        var userCaches = cacheService.userCaches.batchGet(searchUserIds)
                .entrySet()
                .stream()
                .map(it -> it.getValue())
                .collect(Collectors.toList());

        return userCaches;
    }

    @PacketReceiver
    public void atSearchUserRequest(Session session, SearchUserRequest request, GatewayPacketAttachment gatewayAttachment) {
        var query = request.getQuery();
        if (StringUtils.isBlank(query)) {
            NetContext.getRouter().send(session, SearchUserResponse.valueOf(Collections.EMPTY_LIST), gatewayAttachment);
            return;
        }

        var userCaches = searchUser(query);
        NetContext.getRouter().send(session, SearchUserResponse.valueOf(userCaches), gatewayAttachment);
    }

    @PacketReceiver
    public void atSearchUserAsk(Session session, SearchUserAsk ask) {
        var query = ask.getQuery();
        if (StringUtils.isBlank(query)) {
            NetContext.getRouter().send(session, SearchUserAnswer.valueOf(Collections.EMPTY_LIST));
            return;
        }

        var userCaches = searchUser(query);
        NetContext.getRouter().send(session, SearchUserAnswer.valueOf(userCaches));
    }

    @PacketReceiver
    public void atWordFilterAsk(Session session, WordFilterAsk ask) {
        var word = ask.getWord();

        if (StringUtils.isBlank(word)) {
            NetContext.getRouter().send(session, WordFilterAnswer.valueOf(Collections.EMPTY_LIST));
            return;
        }

        // 一律转换成小写
        word = word.trim().toLowerCase();

        // 先过滤中文，再过滤英文
        var cnStr = new StringBuilder();
        var enStr = new StringBuilder();
        word.chars().forEach(it -> {
            var c = Character.valueOf((char) it);
            var s = String.valueOf(Character.valueOf((char) it));
            if (cacheService.CN_PATTERN.matcher(s).matches()) {
                cnStr.append(s);
            } else if (StringUtils.isStopChar(c)) {
                cnStr.append(s);
            } else {
                enStr.append(s);
            }
        });

        var result = new ArrayList<String>();
        var wordTree = cacheService.wordTree;
        result.addAll(wordTree.matchAll(word, -1, true, true));

        if (CollectionUtils.isNotEmpty(result)) {
            NetContext.getRouter().send(session, WordFilterAnswer.valueOf(result));
            return;
        }

        if (!StringUtils.isBlank(cnStr)) {
            result.addAll(wordTree.matchAll(cnStr.toString(), -1, true, true));
        }

        if (!StringUtils.isBlank(enStr)) {
            result.addAll(wordTree.matchAll(enStr.toString(), -1, true, true));
        }

        NetContext.getRouter().send(session, WordFilterAnswer.valueOf(result));
    }

    @PacketReceiver
    public void atGetLocationCacheAsk(Session session, GetLocationCacheAsk ask) {
        var locations = ask.getLocations();
        if (CollectionUtils.isEmpty(locations)) {
            NetContext.getRouter().send(session, GetPersonCacheAnswer.valueOf(Collections.EMPTY_MAP));
            return;
        }

        var locationMap = cacheService.locationCaches.batchGet(locations);
        NetContext.getRouter().send(session, GetLocationCacheAnswer.valueOf(locationMap));
    }

    @PacketReceiver
    public void atGetPersonCacheAsk(Session session, GetPersonCacheAsk ask) {
        var persons = ask.getPersons();
        if (CollectionUtils.isEmpty(persons)) {
            NetContext.getRouter().send(session, GetPersonCacheAnswer.valueOf(Collections.EMPTY_MAP));
            return;
        }

        var itemMap = cacheService.personCaches.batchGet(persons);
        NetContext.getRouter().send(session, GetPersonCacheAnswer.valueOf(itemMap));
    }

    @PacketReceiver
    public void atGetItemCacheAsk(Session session, GetItemCacheAsk ask) {
        var items = ask.getItems();
        if (CollectionUtils.isEmpty(items)) {
            NetContext.getRouter().send(session, GetItemCacheAnswer.valueOf(Collections.EMPTY_MAP));
            return;
        }

        var itemMap = cacheService.itemCaches.batchGet(items);
        NetContext.getRouter().send(session, GetItemCacheAnswer.valueOf(itemMap));
    }

    @PacketReceiver
    public void atGetWordCacheAsk(Session session, GetWordCacheAsk ask) {
        var words = ask.getWords();
        if (CollectionUtils.isEmpty(words)) {
            NetContext.getRouter().send(session, GetWordCacheAnswer.valueOf(Collections.EMPTY_MAP));
            return;
        }

        var wordMap = cacheService.wordCaches.batchGet(words);
        NetContext.getRouter().send(session, GetWordCacheAnswer.valueOf(wordMap));
    }

    @PacketReceiver
    public void atGetCategoryCacheAsk(Session session, GetCategoryCacheAsk ask) {
        var categories = ask.getCategories();
        if (CollectionUtils.isEmpty(categories)) {
            NetContext.getRouter().send(session, GetCategoryCacheAnswer.valueOf(Collections.EMPTY_MAP));
            return;
        }

        var categoryMap = cacheService.categoryCaches.batchGet(categories);
        NetContext.getRouter().send(session, GetCategoryCacheAnswer.valueOf(categoryMap));
    }

}
