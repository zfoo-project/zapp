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

package com.zfoo.app.zapp.web.util;

import com.zfoo.protocol.util.AssertionUtils;
import com.zfoo.protocol.util.StringUtils;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-22 15:45
 */
public abstract class SliceUtils {

    private static String UP_CACE_STR = "abcdefghijklmnopqrstuvwxyz";

    /**
     * 将1、2、3、映射为在Excel中的递增序列：A、B、C、…、Z、AA、AB、AC、…、AZ、BA、BB、BC、…、BZ、CA、…、ZZ、AAA、AAB…。
     */
    public static int fromNumberSystem26(String s) {
        if (StringUtils.isBlank(s) || !s.matches("[a-z]*")) {
            throw new RuntimeException(StringUtils.format("字符串{}必须为A-Z的字符串", s));
        }
        int n = 0;
        for (int i = s.length() - 1, j = 1; i >= 0; i--, j *= 26) {
            char c = s.charAt(i);
            n += (UP_CACE_STR.indexOf(c) + 1) * j;
        }
        return n;
    }

    public static String toNumberSystem26(int n) {
        AssertionUtils.ge1(n);
        var s = new StringBuilder();
        while (n > 0) {
            int m = n % 26;
            if (m == 0) {
                m = 26;
            }
            s.insert(0, UP_CACE_STR.charAt(m - 1));
            n = (n - m) / 26;
        }
        return s.toString();
    }

}
