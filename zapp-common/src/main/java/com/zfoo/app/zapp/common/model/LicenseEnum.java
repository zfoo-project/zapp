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

package com.zfoo.app.zapp.common.model;

import com.zfoo.protocol.util.AssertionUtils;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-30 13:46
 */
public enum LicenseEnum {

    /**
     * CC0
     */
    CC0((byte) 0),
    /**
     * BY
     */
    CC1((byte) 1),
    /**
     * BY-SA
     */
    CC2((byte) 2),


    /**
     * BY-NC
     */
    CC6((byte) 6),
    /**
     * BY-NC-SA
     */
    CC7((byte) 7),
    /**
     * BY-ND
     */
    CC8((byte) 8),
    /**
     * BY-NC-ND
     */
    CC9((byte) 9),

    /**
     * ZFOO协议
     */
    ZFOO((byte) 100),

    ;

    private static Map<Byte, LicenseEnum> typeMap = new HashMap<>();

    static {
        for (var licenseEnum : LicenseEnum.values()) {
            var previousValue = typeMap.putIfAbsent(licenseEnum.type, licenseEnum);
            AssertionUtils.isNull(previousValue, "LicenseEnum中不应该含有重复type的枚举类[{}]和[{}]", licenseEnum, previousValue);
        }
    }

    private byte type;

    LicenseEnum(byte type) {
        this.type = type;
    }

    @Nullable
    public static LicenseEnum getLicenseEnumByType(int type) {
        return typeMap.get((byte) type);
    }

}
