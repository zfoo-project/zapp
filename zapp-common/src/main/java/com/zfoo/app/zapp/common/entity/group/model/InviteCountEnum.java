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

package com.zfoo.app.zapp.common.entity.group.model;

import com.zfoo.protocol.util.AssertionUtils;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-03 20:25
 */
public enum InviteCountEnum {

    /**
     * 无限制邀请
     */
    COUNT_UNLIMITED(0, 0),

    /**
     * 可邀请一个人
     */
    COUNT_1(1, 1),

    COUNT_5(2, 5),

    COUNT_10(3, 10),

    COUNT_25(4, 25),

    COUNT_50(5, 50),

    COUNT_100(6, 100),


    ;

    private static Map<Integer, InviteCountEnum> typeMap = new HashMap<>();

    static {
        for (var countEnum : InviteCountEnum.values()) {
            var previousValue = typeMap.putIfAbsent(countEnum.type, countEnum);
            AssertionUtils.isNull(previousValue, "InviteCountEnum中不应该含有重复type的枚举类[{}]和[{}]", countEnum, previousValue);
        }
    }

    private int type;
    private int count;

    InviteCountEnum(int type, int count) {
        this.type = type;
        this.count = count;
    }

    @Nullable
    public static InviteCountEnum getInviteCountEnumByType(int type) {
        return typeMap.get(type);
    }

    public int getType() {
        return type;
    }

    public int getCount() {
        return count;
    }
}
