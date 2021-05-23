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

package com.zfoo.app.zapp.common.protocol.cache.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.zfoo.net.packet.common.PairLS;
import com.zfoo.net.packet.common.PairString;
import com.zfoo.protocol.IPacket;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-13 16:04
 */
public class WordVO implements IPacket {

    public static final transient short PROTOCOL_ID = 3001;

    @JsonSerialize(using = ToStringSerializer.class)
    private long id;

    private String word;

    private String background;

    private List<String> paragraphs;

    private List<String> sections;

    private List<PairLS> links;

    private List<PairLS> categories;

    private List<PairString> externalLinks;

    public static WordVO valueOf(long id, String word) {
        var vo = new WordVO();
        vo.id = id;
        vo.word = word;
        return vo;
    }

    public static WordVO valueOf(long id, String word, String background, List<String> paragraphs, List<String> sections
            , List<PairLS> links, List<PairLS> categories, List<PairString> externalLinks) {
        var vo = new WordVO();
        vo.id = id;
        vo.word = word;
        vo.background = background;
        vo.paragraphs = paragraphs;
        vo.sections = sections;
        vo.links = links;
        vo.categories = categories;
        vo.externalLinks = externalLinks;
        return vo;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
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

    public List<PairLS> getLinks() {
        return links;
    }

    public void setLinks(List<PairLS> links) {
        this.links = links;
    }

    public List<PairLS> getCategories() {
        return categories;
    }

    public void setCategories(List<PairLS> categories) {
        this.categories = categories;
    }

    public List<PairString> getExternalLinks() {
        return externalLinks;
    }

    public void setExternalLinks(List<PairString> externalLinks) {
        this.externalLinks = externalLinks;
    }
}
