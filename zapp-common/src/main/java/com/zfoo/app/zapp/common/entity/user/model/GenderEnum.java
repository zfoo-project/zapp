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

package com.zfoo.app.zapp.common.entity.user.model;

import com.zfoo.protocol.util.AssertionUtils;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-03 15:48
 */
public enum GenderEnum {

    /**
     * 女性
     */
    FEMALE((byte) 0),

    /**
     * 男性
     */
    MALE((byte) 1),

    /**
     * 双性
     */
    MALE_FEMALE((byte) 2),

    /**
     * 非二元性
     */
    NOT_BINARY((byte) 3),


    /**
     * 变性者
     */
    TRANS((byte) 4),
    ;

    private static Map<Byte, GenderEnum> typeMap = new HashMap<>();

    private byte type;

    GenderEnum(byte type) {
        this.type = type;
    }

    static {
        for (var genderEnum : GenderEnum.values()) {
            var previousValue = typeMap.putIfAbsent(genderEnum.type, genderEnum);
            AssertionUtils.isNull(previousValue, "GenderEnum中不应该含有重复type的枚举类[{}]和[{}]", genderEnum, previousValue);
        }
    }

    @Nullable
    public static GenderEnum getGenderEnumByType(byte type) {
        return typeMap.get(type);
    }

    public byte getType() {
        return type;
    }

}
