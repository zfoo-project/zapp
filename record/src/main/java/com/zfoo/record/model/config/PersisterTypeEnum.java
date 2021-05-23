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

package com.zfoo.record.model.config;

import com.zfoo.protocol.util.StringUtils;
import com.zfoo.record.model.persister.CronRecordPersister;
import com.zfoo.record.model.persister.IRecordPersister;
import com.zfoo.record.model.persister.TimeRecordPersister;
import com.zfoo.record.model.vo.RecordDef;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2017-12-02 13:12
 */
public enum PersisterTypeEnum {

    CRON {
        @Override
        public IRecordPersister createPersister(RecordDef entityDef) {
            return new CronRecordPersister(entityDef);
        }
    },

    TIME {
        @Override
        public IRecordPersister createPersister(RecordDef entityDef) {
            return new TimeRecordPersister(entityDef);
        }
    },
    ;


    public static PersisterTypeEnum getPersisterType(String persisterType) {
        for (var persister : values()) {
            if (persister.name().equalsIgnoreCase(persisterType)) {
                return persister;
            }
        }
        throw new IllegalArgumentException(StringUtils.format("无效的持久化类型[persisterType:{}]", persisterType));
    }

    public abstract IRecordPersister createPersister(RecordDef entityDef);

}
