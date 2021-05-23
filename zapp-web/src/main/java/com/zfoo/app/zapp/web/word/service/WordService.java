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

package com.zfoo.app.zapp.web.word.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mongodb.client.model.Filters;
import com.zfoo.app.zapp.common.entity.core.CategoryEntity;
import com.zfoo.app.zapp.common.entity.core.PersonEntity;
import com.zfoo.app.zapp.common.entity.core.WordEntity;
import com.zfoo.net.packet.common.PairLS;
import com.zfoo.net.util.SimpleCache;
import com.zfoo.orm.OrmContext;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-16 10:27
 */
@Component
public class WordService implements IWordService {

    /**
     * 词条相关
     */
    public SimpleCache<Long, String> wordCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_0000
            , items -> OrmContext.getQuery().queryFieldIn("_id", items, WordEntity.class)
                    .stream()
                    .map(it -> new Pair<>(it.getId(), it.getWord()))
                    .collect(Collectors.toList())
            , key -> StringUtils.EMPTY);
    public SimpleCache<Long, String> personCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_0000
            , persons -> OrmContext.getQuery().queryFieldIn("_id", persons, PersonEntity.class)
                    .stream()
                    .map(it -> new Pair<>(it.getId(), it.getWord()))
                    .collect(Collectors.toList())
            , key -> StringUtils.EMPTY);
    public SimpleCache<Long, String> categoryCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_0000
            , items -> OrmContext.getQuery().queryFieldIn("_id", items, CategoryEntity.class)
                    .stream()
                    .map(it -> new Pair<>(it.getId(), it.getName()))
                    .collect(Collectors.toList())
            , key -> StringUtils.EMPTY);
    /**
     * 提示相关
     */
    private Cache<String, List<PairLS>> personHintCaches = Caffeine.newBuilder()
            .expireAfterAccess(3 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS)
            .maximumSize(1_0000)
            .recordStats()
            .build();
    private Cache<String, List<PairLS>> wordHintCaches = Caffeine.newBuilder()
            .expireAfterAccess(3 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS)
            .maximumSize(1_0000)
            .recordStats()
            .build();
    private Cache<String, List<PairLS>> categoryHintCaches = Caffeine.newBuilder()
            .expireAfterAccess(3 * TimeUtils.MILLIS_PER_MINUTE, TimeUnit.MILLISECONDS)
            .maximumSize(1_0000)
            .recordStats()
            .build();

    @Override
    public List<PairLS> personHint(String name, int hintSize) {
        var hints = personHintCaches.getIfPresent(name);
        if (hints != null) {
            return hints;
        }

        var regex = StringUtils.format("^{}.*", name);

        var collection = OrmContext.getOrmManager().getCollection(PersonEntity.class);
        var result = new ArrayList<PairLS>();
        collection.find(Filters.regex("word", regex))
                .limit(hintSize)
                .map(it -> PairLS.valueOf(it.getId(), it.getWord()))
                .forEach((Consumer<PairLS>) pair -> result.add(pair));

        personHintCaches.put(name, result);
        return result;
    }

    @Override
    public List<PairLS> wordHint(String name, int hintSize) {
        var hints = wordHintCaches.getIfPresent(name);
        if (hints != null) {
            return hints;
        }

        var regex = StringUtils.format("^{}.*", name);

        var collection = OrmContext.getOrmManager().getCollection(WordEntity.class);
        var result = new ArrayList<PairLS>();
        collection.find(Filters.regex("word", regex))
                .limit(hintSize)
                .map(it -> PairLS.valueOf(it.getId(), it.getWord()))
                .forEach((Consumer<PairLS>) pair -> result.add(pair));

        wordHintCaches.put(name, result);
        return result;
    }

    @Override
    public List<PairLS> categoryHint(String name, int hintSize) {
        var hints = categoryHintCaches.getIfPresent(name);
        if (hints != null) {
            return hints;
        }

        var regex = StringUtils.format("^{}.*", name);

        var collection = OrmContext.getOrmManager().getCollection(CategoryEntity.class);
        var result = new ArrayList<PairLS>();
        collection.find(Filters.regex("name", regex))
                .limit(hintSize)
                .map(it -> PairLS.valueOf(it.getId(), it.getName()))
                .forEach((Consumer<PairLS>) pair -> result.add(pair));

        categoryHintCaches.put(name, result);
        return result;
    }

    @Override
    public List<Pair<Long, String>> existWordList(List<Long> words) {
        if (CollectionUtils.isEmpty(words)) {
            return Collections.EMPTY_LIST;
        }
        return wordCaches.batchGet(words)
                .entrySet()
                .stream()
                .filter(it -> !StringUtils.isBlank(it.getValue()))
                .map(it -> new Pair<>(it.getKey(), it.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Pair<Long, String>> existPersonList(List<Long> persons) {
        if (CollectionUtils.isEmpty(persons)) {
            return Collections.EMPTY_LIST;
        }
        return personCaches.batchGet(persons)
                .entrySet()
                .stream()
                .filter(it -> !StringUtils.isBlank(it.getValue()))
                .map(it -> new Pair<>(it.getKey(), it.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Pair<Long, String>> existCategoryList(List<Long> categories) {
        if (CollectionUtils.isEmpty(categories)) {
            return Collections.EMPTY_LIST;
        }
        return categoryCaches.batchGet(categories)
                .entrySet()
                .stream()
                .filter(it -> !StringUtils.isBlank(it.getValue()))
                .map(it -> new Pair<>(it.getKey(), it.getValue()))
                .collect(Collectors.toList());
    }

}
