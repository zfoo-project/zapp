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

package com.zfoo.app.zapp.common.entity.friend;

import com.zfoo.orm.model.anno.EntityCache;
import com.zfoo.orm.model.anno.Id;
import com.zfoo.orm.model.anno.Index;
import com.zfoo.orm.model.anno.Persister;
import com.zfoo.orm.model.entity.IEntity;
import com.zfoo.util.security.IdUtils;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-14 11:41
 */
@EntityCache(cacheStrategy = "tenThousand", persister = @Persister("time30s"))
public class ApplicantEntity implements IEntity<String> {

    @Id
    private String id;

    private long vs;

    /**
     * 申请人用户的id
     */
    private long userId;

    /**
     * 目标用户id
     */
    @Index(ascending = true, unique = false)
    private long targetId;

    /**
     * 申请状态：0申请中，1被拒绝，2目标用户同意申请（同意申请过后就会删除该数据，理论上是看不到的）
     */
    private int status;

    private long timestamp;

    public static ApplicantEntity valueOf(long userId, long targetId) {
        var entity = new ApplicantEntity();
        entity.id = IdUtils.generateStringId(userId, targetId);
        entity.userId = userId;
        entity.targetId = targetId;
        entity.status = 0;
        return entity;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public long gvs() {
        return vs;
    }

    @Override
    public void svs(long vs) {
        this.vs = vs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getVs() {
        return vs;
    }

    public void setVs(long vs) {
        this.vs = vs;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
