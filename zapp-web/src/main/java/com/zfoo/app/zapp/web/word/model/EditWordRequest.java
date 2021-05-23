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

package com.zfoo.app.zapp.web.word.model;

import com.zfoo.net.packet.common.PairString;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-13 16:28
 */
public class EditWordRequest {

    private long id;

    private String word;

    private boolean person;

    private String background;

    private List<String> paragraphs;

    private List<String> sections;

    private List<Long> links;

    private List<Long> categories;

    private List<PairString> externalLinks;


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

    public boolean isPerson() {
        return person;
    }

    public void setPerson(boolean person) {
        this.person = person;
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

    public List<Long> getLinks() {
        return links;
    }

    public void setLinks(List<Long> links) {
        this.links = links;
    }

    public List<Long> getCategories() {
        return categories;
    }

    public void setCategories(List<Long> categories) {
        this.categories = categories;
    }

    public List<PairString> getExternalLinks() {
        return externalLinks;
    }

    public void setExternalLinks(List<PairString> externalLinks) {
        this.externalLinks = externalLinks;
    }
}
