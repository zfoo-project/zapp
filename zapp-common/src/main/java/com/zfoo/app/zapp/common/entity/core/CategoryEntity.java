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

package com.zfoo.app.zapp.common.entity.core;

import com.zfoo.orm.model.anno.EntityCache;
import com.zfoo.orm.model.anno.Id;
import com.zfoo.orm.model.anno.Index;
import com.zfoo.orm.model.anno.Persister;
import com.zfoo.orm.model.entity.IEntity;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-02 11:16
 */
@EntityCache(cacheStrategy = "tenThousand", persister = @Persister("time30s"))
public class CategoryEntity implements IEntity<Long> {

    @Id
    private long id;

    @Index(ascending = true, unique = true)
    private String name;

    private String background;

    /**
     * 父类别
     */
    private Set<Long> parent = new CopyOnWriteArraySet<>();
    /**
     * 子类别
     */
    private Set<Long> children = new CopyOnWriteArraySet<>();
    /**
     * 和这个类别相关的相关词条
     */
    private Set<Long> words = new CopyOnWriteArraySet<>();


    public static CategoryEntity valueOf(long id, String name) {
        var entity = new CategoryEntity();
        entity.id = id;
        entity.name = name;
        return entity;
    }

    @Override
    public Long id() {
        return id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public Set<Long> getParent() {
        return parent;
    }

    public void setParent(Set<Long> parent) {
        this.parent = parent;
    }

    public Set<Long> getChildren() {
        return children;
    }

    public void setChildren(Set<Long> children) {
        this.children = children;
    }

    public Set<Long> getWords() {
        return words;
    }

    public void setWords(Set<Long> words) {
        this.words = words;
    }
}
