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

package com.zfoo.record.util;

import com.zfoo.protocol.util.StringUtils;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-06-12 13:45
 */
public abstract class RecordUtils {

    public static String toIndexName(Class<?> clazz) {
        return StringUtils.substringBeforeLast(StringUtils.uncapitalize(clazz.getSimpleName()), "Record").toLowerCase();
    }

}
