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

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-02 11:16
 */
@EntityCache(cacheStrategy = "tenThousand", persister = @Persister("time30s"))
public class PersonEntity implements IEntity<Long> {

    /**
     * 主要是人物的id，对应WordEntity里的id
     */
    @Id
    private long id;

    @Index(ascending = true, unique = true)
    private String word;

    public static PersonEntity valueOf(long id, String word) {
        var entity = new PersonEntity();
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

}
