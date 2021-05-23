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

import com.zfoo.net.packet.common.PairString;
import com.zfoo.orm.model.anno.EntityCache;
import com.zfoo.orm.model.anno.Id;
import com.zfoo.orm.model.anno.Index;
import com.zfoo.orm.model.anno.Persister;
import com.zfoo.orm.model.entity.IEntity;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-02 11:16
 */
@EntityCache(cacheStrategy = "tenThousand", persister = @Persister("time30s"))
public class WordEntity implements IEntity<Long> {

    @Id
    private long id;

    /**
     * 词条名称
     */
    @Index(ascending = true, unique = true)
    private String word;

    /**
     * 词条的背景图像
     */
    private String background;

    /**
     * 词条相关内容，各个不同段落
     */
    private List<String> paragraphs = new CopyOnWriteArrayList<>();


    /**
     * 内容分为哪几个部分
     */
    private List<String> sections = new CopyOnWriteArrayList<>();
    ;

    /**
     * 词条对应的内部连接，一般是连接其它词条，因为一个词条还可能和另外一个词条关联
     */
    private Set<Long> links = new CopyOnWriteArraySet<>();

    /**
     * 词条对应的类别，对应CateEntity
     */
    private Set<Long> categories = new CopyOnWriteArraySet<>();

    /**
     * 外部链接
     */
    private List<PairString> externalLinks = new CopyOnWriteArrayList<>();

    public static WordEntity valueOf(long id, String word) {
        var entity = new WordEntity();
        entity.id = id;
        entity.word = word;
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

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public List<String> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<String> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public List<String> getSections() {
        return sections;
    }

    public void setSections(List<String> sections) {
        this.sections = sections;
    }

    public Set<Long> getLinks() {
        return links;
    }

    public void setLinks(Set<Long> links) {
        this.links = links;
    }

    public Set<Long> getCategories() {
        return categories;
    }

    public void setCategories(Set<Long> categories) {
        this.categories = categories;
    }

    public List<PairString> getExternalLinks() {
        return externalLinks;
    }

    public void setExternalLinks(List<PairString> externalLinks) {
        this.externalLinks = externalLinks;
    }
}
