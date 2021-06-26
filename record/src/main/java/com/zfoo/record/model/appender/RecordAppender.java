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

package com.zfoo.record.model.appender;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.zfoo.protocol.collection.ArrayUtils;
import com.zfoo.record.manager.RecordHub;
import com.zfoo.record.model.record.IRecord;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-06-05 11:10
 */
public class RecordAppender extends AsyncAppender {

    @Override
    protected void append(ILoggingEvent loggingEvent) {
        super.append(loggingEvent);
        // 将对象写入到es
        var recordHub = RecordHub.getInstance();
        if (recordHub == null) {
            return;
        }
        var argumentArray = loggingEvent.getArgumentArray();
        if (ArrayUtils.isEmpty(argumentArray)) {
            return;
        }
        for (var argument : argumentArray) {
            if (argument instanceof IRecord) {
                RecordHub.getInstance().submitRecord((IRecord<?>) argument);
            }
        }
    }

}
