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

package com.zfoo.app.zapp.cache.service;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.zfoo.app.zapp.cache.model.resource.FilterResource;
import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.constant.LocationConstant;
import com.zfoo.app.zapp.common.entity.core.CategoryEntity;
import com.zfoo.app.zapp.common.entity.core.WordEntity;
import com.zfoo.app.zapp.common.entity.user.UserEntity;
import com.zfoo.app.zapp.common.protocol.cache.model.CategoryVO;
import com.zfoo.app.zapp.common.protocol.cache.model.UserCache;
import com.zfoo.app.zapp.common.protocol.cache.model.WordVO;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.event.model.event.AppStartEvent;
import com.zfoo.net.packet.common.PairLS;
import com.zfoo.net.packet.common.TripleLSS;
import com.zfoo.net.packet.common.TripleString;
import com.zfoo.net.util.SimpleCache;
import com.zfoo.orm.OrmContext;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.storage.model.anno.ResInjection;
import com.zfoo.storage.model.vo.Storage;
import com.zfoo.util.math.NumberUtils;
import com.zfoo.util.math.dfa.WordTree;
import org.bson.Document;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ????????????
 * ???????????????????????????AppStartEvent??????
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-08 10:59
 */
@Component
public class CacheService implements ICacheService, ApplicationListener<AppStartEvent> {
    /**
     * ?????????
     */
    public WordTree wordTree;
    /**
     * ????????????
     */
    public Pattern CN_PATTERN = Pattern.compile("[\u4E00-\u9FA5]");
    /**
     * ?????????excel?????????
     */
    @ResInjection
    private Storage<Integer, FilterResource> filterResources;

    /**
     * ????????????????????????(???????????????Spring???????????????????????????????????????????????????)
     *
     * @throws IOException
     */
    @PostConstruct
    public void init() throws IOException {
        LocationConstant.init();
    }

    /**
     * ???????????????????????????AppStartEvent??????
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(AppStartEvent event) {
        var tree = new WordTree();
        var words = filterResources.getAll().stream()
                .map(it -> it.getFilter().trim().toLowerCase())
                .collect(Collectors.toList());
        tree.addWords(words);
        wordTree = tree;
    }

    public SimpleCache<Long, List<Long>> userTimeSliceCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_0000
            , userIds -> {
                var map = new HashMap<Long, Set<Pair<Long, Long>>>();
                var collection = OrmContext.getOrmManager().getCollection("timeSlice");
                collection.find(Filters.in("userId", userIds))
                        .projection(Projections.include("_id", "userId", "create"))
                        .forEach(document -> {
                            var tsId = document.getLong("_id");
                            var userId = document.getLong("userId");
                            var create = document.getLong("create");
                            var tsSet = map.computeIfAbsent(userId, it -> new HashSet<>());
                            tsSet.add(new Pair<Long, Long>(tsId, create));
                        });
                return map.entrySet()
                        .stream()
                        .map(it -> new Pair<>(it.getKey(), it.getValue().stream().sorted((a, b) -> Long.compare(b.getValue(), a.getValue())).map(pair -> pair.getKey()).collect(Collectors.toList())))
                        .collect(Collectors.toList());
            }
            , key -> Collections.emptyList());
    /**
     * ???????????????cache penetration [pen??'tre????n] n.?????????????????????
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????id??????-1???????????????id?????????????????????????????????
     * ??????????????????????????????????????????????????????????????????????????????
     * ???????????????1.????????????????????????id??????????????????id<=0??????????????????2.???key-null??????????????????????????????????????????????????????????????????????????????
     * <p>
     * ???????????????cache breakdown??????
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ???????????????????????????????????????????????????
     * <p>
     * ???????????????cache avalanche??????
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????down????????????????????????????????????
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ???????????????1.??????????????????????????????????????????2.????????????????????????????????????
     */
    // ??????expireAfterAccess?????????????????????????????????
    // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    public SimpleCache<Long, CategoryVO> categoryCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_0000
            , categories -> {
                var categoryList = OrmContext.getQuery(CategoryEntity.class).in("_id", categories).queryAll();

                var categorySet = new HashSet<Long>();
                categoryList.stream().filter(it -> CollectionUtils.isNotEmpty(it.getParent()))
                        .map(it -> it.getParent())
                        .forEach(it -> categorySet.addAll(it));
                categoryList.stream().filter(it -> CollectionUtils.isNotEmpty(it.getChildren()))
                        .map(it -> it.getChildren())
                        .forEach(it -> categorySet.addAll(it));

                var categoryMap = OrmContext.getQuery(CategoryEntity.class).in("_id", new ArrayList<>(categorySet)).queryAll()
                        .stream()
                        .collect(Collectors.toMap(key -> key.getId(), value -> value.getName()));

                var wordSet = new HashSet<Long>();
                categoryList.stream().filter(it -> CollectionUtils.isNotEmpty(it.getWords()))
                        .map(it -> it.getWords())
                        .forEach(it -> wordSet.addAll(it));
                var wordMap = OrmContext.getQuery(WordEntity.class).in("_id", new ArrayList<>(wordSet)).queryAll()
                        .stream()
                        .collect(Collectors.toMap(key -> key.getId(), value -> value.getWord()));

                var list = new ArrayList<Pair<Long, CategoryVO>>();
                for (var category : categoryList) {
                    var parentCategories = CollectionUtils.isEmpty(category.getParent())
                            ? Collections.EMPTY_LIST
                            : category.getParent().stream().filter(it -> categoryMap.containsKey(it)).map(it -> PairLS.valueOf(it, categoryMap.get(it))).collect(Collectors.toList());
                    var childrenCategories = CollectionUtils.isEmpty(category.getChildren())
                            ? Collections.EMPTY_LIST
                            : category.getChildren().stream().filter(it -> categoryMap.containsKey(it)).map(it -> PairLS.valueOf(it, categoryMap.get(it))).collect(Collectors.toList());

                    var words = CollectionUtils.isEmpty(category.getWords())
                            ? Collections.EMPTY_LIST
                            : category.getWords().stream().filter(it -> wordMap.containsKey(it)).map(it -> PairLS.valueOf(it, wordMap.get(it))).collect(Collectors.toList());

                    var categoryVO = CategoryVO.valueOf(category.getId(), category.getName(), category.getBackground(), parentCategories, childrenCategories, words);
                    var pair = new Pair<>(category.getId(), categoryVO);
                    list.add(pair);
                }
                return list;
            }
            , key -> CategoryVO.valueOf(key, AppConstant.CATEGORY_UNKNOWN_NAME));

    public SimpleCache<Long, WordVO> wordCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_0000
            , words -> {
                var wordList = OrmContext.getQuery(WordEntity.class).in("_id", words).queryAll();
                var allLinks = new HashSet<Long>();
                wordList.stream().filter(it -> CollectionUtils.isNotEmpty(it.getLinks()))
                        .map(it -> it.getLinks())
                        .forEach(it -> allLinks.addAll(it));
                var linkMap = OrmContext.getQuery(WordEntity.class).in("_id", new ArrayList<>(allLinks)).queryAll()
                        .stream()
                        .collect(Collectors.toMap(key -> key.getId(), value -> value.getWord()));

                var allCategories = new HashSet<Long>();
                wordList.stream().filter(it -> CollectionUtils.isNotEmpty(it.getCategories()))
                        .map(it -> it.getCategories())
                        .forEach(it -> allCategories.addAll(it));
                var categoryMap = OrmContext.getQuery(CategoryEntity.class).in("_id", new ArrayList<>(allCategories)).queryAll()
                        .stream()
                        .collect(Collectors.toMap(key -> key.getId(), value -> value.getName()));

                var list = new ArrayList<Pair<Long, WordVO>>();
                for (var word : wordList) {
                    var links = CollectionUtils.isEmpty(word.getLinks())
                            ? Collections.EMPTY_LIST
                            : word.getLinks().stream().filter(it -> linkMap.containsKey(it)).map(it -> PairLS.valueOf(it, linkMap.get(it))).collect(Collectors.toList());
                    var categories = CollectionUtils.isEmpty(word.getCategories())
                            ? Collections.EMPTY_LIST
                            : word.getCategories().stream().filter(it -> categoryMap.containsKey(it)).map(it -> PairLS.valueOf(it, categoryMap.get(it))).collect(Collectors.toList());
                    var wordVO = WordVO.valueOf(word.getId(), word.getWord(), word.getBackground(), word.getParagraphs(), word.getSections(), links, categories, word.getExternalLinks());
                    var pair = new Pair<>(word.getId(), wordVO);
                    list.add(pair);
                }
                return list;
            }
            , key -> WordVO.valueOf(key, AppConstant.WORD_UNKNOWN_NAME));

    public SimpleCache<String, TripleLSS> locationCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_0000
            , locations -> OrmContext.getQuery(WordEntity.class).in("word", locations).queryAll()
                    .stream()
                    .map(it -> {
                        var id = it.getId();
                        var word = it.getWord();
                        var background = it.getBackground();

                        var descriptionBuilder = new StringBuilder();
                        var paragraphs = it.getParagraphs();
                        if (CollectionUtils.isNotEmpty(paragraphs)) {
                            for (var paragraph : paragraphs) {
                                if (descriptionBuilder.length() >= AppConstant.TS_PERSON_DESCRIPTION_LIMIT) {
                                    break;
                                }
                                descriptionBuilder.append(paragraph);
                            }
                        }
                        return new Pair<>(word, TripleLSS.valueOf(id, background, descriptionBuilder.toString()));
                    })
                    .collect(Collectors.toList())
            , key -> TripleLSS.valueOf(0L, StringUtils.EMPTY, StringUtils.EMPTY));

    public SimpleCache<Long, TripleString> personCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_0000
            , persons -> OrmContext.getQuery(WordEntity.class).in("_id", persons).queryAll()
                    .stream()
                    .map(it -> {
                        var id = it.getId();
                        var word = it.getWord();
                        var background = it.getBackground();
                        var descriptionBuilder = new StringBuilder();
                        var paragraphs = it.getParagraphs();
                        if (CollectionUtils.isNotEmpty(paragraphs)) {
                            for (var paragraph : paragraphs) {
                                if (descriptionBuilder.length() >= AppConstant.TS_PERSON_DESCRIPTION_LIMIT) {
                                    break;
                                }
                                descriptionBuilder.append(paragraph);
                            }
                        }
                        return new Pair<>(id, TripleString.valueOf(word, background, descriptionBuilder.toString()));
                    })
                    .collect(Collectors.toList())
            , key -> TripleString.valueOf(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY));


    public SimpleCache<Long, TripleString> itemCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_0000
            , items -> OrmContext.getQuery(WordEntity.class).in("_id", items).queryAll()
                    .stream()
                    .map(it -> {
                        var id = it.getId();
                        var word = it.getWord();
                        var background = it.getBackground();
                        var descriptionBuilder = new StringBuilder();
                        var paragraphs = it.getParagraphs();
                        if (CollectionUtils.isNotEmpty(paragraphs)) {
                            for (var paragraph : paragraphs) {
                                if (descriptionBuilder.length() >= AppConstant.TS_ITEM_DESCRIPTION_LIMIT) {
                                    break;
                                }
                                descriptionBuilder.append(paragraph);
                            }
                        }
                        return new Pair<>(id, TripleString.valueOf(word, background, descriptionBuilder.toString()));
                    })
                    .collect(Collectors.toList())
            , key -> TripleString.valueOf(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY));


    public SimpleCache<Long, UserCache> userCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_0000
            , userIds -> {
                var userList = OrmContext.getQuery(UserEntity.class).in("_id", userIds).queryAll();

                var userCacheList = new ArrayList<Pair<Long, UserCache>>();
                for (var user : userList) {
                    var id = user.getId();
                    var name = user.getName();
                    var coin = user.getCoin();
                    var avatar = user.getAvatar();
                    var background = user.getBackground();
                    var gender = user.getGender();
                    var signature = user.getSignature();
                    var customTime = user.getCustomTime();
                    var custom = user.getCustom();
                    var followNum = user.getFollows().size();
                    var fanNum = user.getFanNum();
                    var starNum = user.getStars().size();
                    var free = user.getFree();
                    var normal = user.getNormal();
                    var locations = LocationConstant.converter(user.getLocations());
                    var persons = new ArrayList<PairLS>();
                    var items = new ArrayList<PairLS>();
                    if (CollectionUtils.isNotEmpty(user.getPersons())) {
                        personCaches.batchGet(user.getPersons()).forEach((key, value) -> persons.add(PairLS.valueOf(key, value.getLeft())));
                    }
                    if (CollectionUtils.isNotEmpty(user.getItems())) {
                        itemCaches.batchGet(user.getPersons()).forEach((key, value) -> items.add(PairLS.valueOf(key, value.getLeft())));
                    }
                    var userCache = UserCache.valueOf(id, name, coin, avatar, background, gender, signature
                            , customTime, custom
                            , followNum, fanNum, starNum, free, normal
                            , locations, persons, items);
                    userCacheList.add(new Pair<>(userCache.getId(), userCache));
                }

                return userCacheList;
            }
            , key -> UserCache.valueOf(key, AppConstant.USER_UNKNOWN_NAME));

    /**
     * ????????????
     */
    public SimpleCache<String, List<Long>> userNameCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_0000
            , names -> {
                var collection = OrmContext.getOrmManager().getCollection("user");
                var map = new HashMap<String, List<Long>>();
                collection.find(Filters.in("name", names))
                        .projection(Projections.include("_id", "name"))
                        .forEach(new Consumer<Document>() {
                            @Override
                            public void accept(Document document) {
                                var userId = document.getLong("_id");
                                var name = document.getString("name");
                                var list = map.computeIfAbsent(name, it -> new ArrayList<>());
                                list.add(userId);
                            }
                        });
                return map.entrySet().stream().map(it -> new Pair<>(it.getKey(), it.getValue())).collect(Collectors.toList());
            }
            , key -> Collections.emptyList());

    /**
     * ????????????
     */
    public SimpleCache<String, List<Long>> hotSearchCaches = SimpleCache.build(
            30 * TimeUtils.MILLIS_PER_MINUTE, 15 * TimeUtils.MILLIS_PER_MINUTE, 1_0000
            , names -> {
                var collection = OrmContext.getOrmManager().getCollection("user");
                var map = new HashMap<String, List<Long>>();
                collection.find(Filters.in("name", names))
                        .projection(Projections.include("_id", "name"))
                        .forEach(new Consumer<Document>() {
                            @Override
                            public void accept(Document document) {
                                var userId = document.getLong("_id");
                                var name = document.getString("name");
                                var list = map.computeIfAbsent(name, it -> new ArrayList<>());
                                list.add(userId);
                            }
                        });
                return map.entrySet().stream().map(it -> new Pair<>(it.getKey(), it.getValue())).collect(Collectors.toList());
            }
            , key -> Collections.emptyList());


    public List<UserCache> searchUser(String query) {
        if (StringUtils.isBlank(query)) {
            return Collections.EMPTY_LIST;
        }

        var searchUserIds = new ArrayList<>(userNameCaches.get(query));
        if (!CommonUtils.isUserIdInRange(searchUserIds)) {
            return Collections.EMPTY_LIST;
        }

        // ??????query???????????????????????????????????????id
        if (NumberUtils.isLong(query)) {
            searchUserIds.add(Long.parseLong(query));
        }

        var userCaches = this.userCaches.batchGet(searchUserIds)
                .entrySet()
                .stream()
                .map(it -> it.getValue())
                .collect(Collectors.toList());

        return userCaches;
    }

    public Map<Long, UserCache> getUserCaches(Set<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.EMPTY_MAP;
        }
        var userCacheMap = this.userCaches
                .batchGet(userIds)
                .values()
                .stream()
                .collect(Collectors.toMap(key -> key.getId(), value -> value));
        return userCacheMap;
    }

}
