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

import com.zfoo.app.zapp.common.entity.core.WordEntity;
import com.zfoo.event.model.event.IEvent;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020 05.23 17:48
 */
public class EditWordEvent implements IEvent {

    private WordEntity wordEntity;

    public static EditWordEvent valueOf(WordEntity wordEntity) {
        var entity = new EditWordEvent();
        entity.wordEntity = wordEntity;
        return entity;
    }

    public WordEntity getWordEntity() {
        return wordEntity;
    }
}
