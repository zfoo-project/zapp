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

package com.zfoo.app.zapp.common.entity.user;

import com.zfoo.orm.model.anno.EntityCache;
import com.zfoo.orm.model.anno.Id;
import com.zfoo.orm.model.anno.Index;
import com.zfoo.orm.model.anno.Persister;
import com.zfoo.orm.model.entity.IEntity;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-01-14 14:31
 */
@EntityCache(cacheStrategy = "tenThousand", persister = @Persister("time30s"))
public class WeiBoEntity implements IEntity<String> {

    /**
     * 对应微博的唯一uid
     */
    @Id
    private String id;

    /**
     * 对应UserEntity的id
     */
    @Index(ascending = true, unique = true)
    private long uid;

    public static WeiBoEntity valueOf(String id, long uid) {
        var entity = new WeiBoEntity();
        entity.id = id;
        entity.uid = uid;
        return entity;
    }

    @Override
    public String id() {
        return id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }
}
