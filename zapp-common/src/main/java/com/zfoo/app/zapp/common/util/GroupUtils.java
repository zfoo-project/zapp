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

package com.zfoo.app.zapp.common.util;

import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.util.math.RandomUtils;
import com.zfoo.util.security.AesUtils;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-03 20:55
 */
public abstract class GroupUtils {

    public static String set(long groupId) {
        return AesUtils.getEncryptString(groupId + StringUtils.VERTICAL_BAR + RandomUtils.randomString(12));
    }

    public static Pair<Long, String> get(String inviteCode) {
        var source = AesUtils.getDecryptString(inviteCode);
        var splits = source.split(StringUtils.VERTICAL_BAR_REGEX);
        var groupId = Long.parseLong(splits[0]);
        var randomStr = splits[1];
        return new Pair<>(groupId, randomStr);
    }

}
