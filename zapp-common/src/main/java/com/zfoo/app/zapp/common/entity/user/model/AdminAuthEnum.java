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

import java.util.HashMap;
import java.util.Map;

/**
 * 权限枚举类，主要用于管理整个频道的权限
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-04 19:47
 */
public enum AdminAuthEnum {

    /**
     * 没有权限
     */
    NO_AUTH((byte) 0),

    /**
     * 初级的基本权限
     */
    BASE_AUTH((byte) 3),

    /**
     * 中级权限
     */
    MIDDLE_AUTH((byte) 6),

    /**
     * 高级权限
     */
    HIGH_AUTH((byte) 9),

    /**
     * 特级权限
     */
    SPECIAL_AUTH((byte) 12),

    /**
     * 管理员权限
     */
    ADMIN_AUTH((byte) 15),

    ;

    private static Map<Byte, AdminAuthEnum> typeMap = new HashMap<>();

    static {
        for (var authEnum : AdminAuthEnum.values()) {
            var previousValue = typeMap.putIfAbsent(authEnum.type, authEnum);
            AssertionUtils.isNull(previousValue, "AdminAuthEnum中不应该含有重复type的枚举类[{}]和[{}]", authEnum, previousValue);
        }
    }

    private byte type;

    AdminAuthEnum(byte type) {
        this.type = type;
    }

    public static AdminAuthEnum getAuthEnumByType(byte type) {
        var adminAuthEnum = typeMap.get(type);
        return adminAuthEnum == null ? NO_AUTH : adminAuthEnum;
    }

    public byte getType() {
        return type;
    }

    public boolean hasAuth(AdminAuthEnum authEnum) {
        return this.getType() >= authEnum.getType();
    }

}
