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

package com.zfoo.record.record;

import com.zfoo.record.model.anno.RecordCache;
import com.zfoo.record.model.anno.RecordId;
import com.zfoo.record.model.record.IRecord;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-06-02 14:19
 */
@RecordCache(cacheStrategy = "tenThousand", persister = "time10s")
@Document(indexName = "currency", refreshInterval = "30s")
public class CurrencyRecord implements IRecord<Long> {

    @RecordId
    @Field(type = FieldType.Long)
    private long uid;

    @Field(type = FieldType.Integer)
    private int type;

    @Field(type = FieldType.Short)
    private short value;

    @Field(type = FieldType.Long)
    private long num;

    public static CurrencyRecord valueOf(long id, int type, short value, long num) {
        var record = new CurrencyRecord();
        record.uid = id;
        record.type = type;
        record.value = value;
        record.num = num;
        return record;
    }

    @Override
    public Long id() {
        return uid;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "CurrencyRecord:" + "uid=" + uid + " type=" + type + " value=" + value + " num=" + num;
    }
}
