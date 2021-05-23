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

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-13 16:28
 */
public class EditCategoryRequest {

    private long id;

    private String name;

    private String background;

    private List<Long> parent;

    private List<Long> children;

    private List<Long> words;

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

    public List<Long> getParent() {
        return parent;
    }

    public void setParent(List<Long> parent) {
        this.parent = parent;
    }

    public List<Long> getChildren() {
        return children;
    }

    public void setChildren(List<Long> children) {
        this.children = children;
    }

    public List<Long> getWords() {
        return words;
    }

    public void setWords(List<Long> words) {
        this.words = words;
    }
}
