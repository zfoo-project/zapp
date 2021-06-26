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

package com.zfoo.record.model.vo;

import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.protocol.util.AssertionUtils;
import com.zfoo.protocol.util.ReflectionUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.record.model.anno.RecordCache;
import com.zfoo.record.model.anno.RecordId;
import com.zfoo.record.model.config.CacheStrategy;
import com.zfoo.record.model.config.PersisterStrategy;
import com.zfoo.record.model.record.IRecord;
import com.zfoo.record.util.RecordUtils;
import org.springframework.data.elasticsearch.annotations.Document;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-06-02 11:40
 */
public class RecordDef {

    private Field idField;

    private Class<? extends IRecord<?>> clazz;

    private int cacheSize;

    private PersisterStrategy persisterStrategy;


    private RecordDef() {
    }

    public static RecordDef valueOf(Class<?> clazz, List<CacheStrategy> cacheStrategies, List<PersisterStrategy> persisterStrategies) {
        if (!IRecord.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(StringUtils.format("被[{}]注解标注的记录类[{}]必须继承[{}]"
                    , RecordCache.class.getName(), clazz.getName(), IRecord.class.getName()));
        }

        var recordDef = new RecordDef();
        recordDef.clazz = (Class<? extends IRecord<?>>) clazz;
        try {
            ReflectionUtils.makeAccessible(clazz.getDeclaredConstructor());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(StringUtils.format("记录类Record[{}]必须包含一个默认的构造器", clazz.getSimpleName()));
        }

        var cache = clazz.getAnnotation(RecordCache.class);
        AssertionUtils.notNull(cache);
        var cacheStrategyOptional = cacheStrategies.stream().filter(it -> it.getStrategy().equals(cache.cacheStrategy())).findFirst();
        if (cacheStrategyOptional.isEmpty()) {
            throw new RuntimeException(StringUtils.format("记录类Record[{}]没有找到缓存策略[{}]", clazz.getSimpleName(), cache.cacheStrategy()));
        }
        recordDef.cacheSize = cacheStrategyOptional.get().getMaxSize();

        var document = clazz.getAnnotation(Document.class);
        AssertionUtils.notNull(document);
        var indexName = RecordUtils.toIndexName(recordDef.getClazz());
        if (!indexName.equals(document.indexName())) {
            throw new RuntimeException(StringUtils.format("记录类Record[{}]的Document注解表示的索引名称[{}]和类名不匹配[{}]"
                    , clazz.getSimpleName(), document.indexName(), clazz));
        }

        var idFields = ReflectionUtils.getFieldsByAnnoInPOJOClass(clazz, RecordId.class);
        AssertionUtils.isTrue(ArrayUtils.isNotEmpty(idFields) && idFields.length == 1, "记录类Record[{}]必须只有且仅有一个RecordId注解", clazz.getSimpleName());
        recordDef.idField = ReflectionUtils.getFieldsByAnnoInPOJOClass(clazz, RecordId.class)[0];
        ReflectionUtils.makeAccessible(recordDef.idField);
        // idField必须用private修饰
        if (!Modifier.isPrivate(recordDef.idField.getModifiers())) {
            throw new RuntimeException(StringUtils.format("记录类Record[{}]的id必须是private私有的", clazz.getSimpleName()));
        }

        // record必须包含属性的get和set方法
        Arrays.stream(clazz.getDeclaredFields())
                .filter(it -> !Modifier.isTransient(it.getModifiers()))
                .forEach(it -> {
                    ReflectionUtils.fieldToGetMethod(clazz, it);
                    ReflectionUtils.fieldToSetMethod(clazz, it);
                });
        var persister = cache.persister();
        AssertionUtils.notNull(persister);
        var persisterStrategyOptional = persisterStrategies.stream().filter(it -> it.getStrategy().equals(persister)).findFirst();
        if (persisterStrategyOptional.isEmpty()) {
            throw new RuntimeException(StringUtils.format("记录类Record[{}]没有找到持久化策略[{}]", clazz.getSimpleName(), persister));
        }
        recordDef.persisterStrategy = persisterStrategyOptional.get();

        return recordDef;
    }


    public Field getIdField() {
        return idField;
    }

    public Class<? extends IRecord<?>> getClazz() {
        return clazz;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public PersisterStrategy getPersisterStrategy() {
        return persisterStrategy;
    }
}
