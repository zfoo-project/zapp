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

import java.util.*;

/**
 * 权限枚举类，主要用于管理整个频道的权限
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-04 19:47
 */
public enum ChannelAuthEnum {

    /**
     * 没有权限
     */
    NO_AUTH(0, Collections.EMPTY_SET),

    /**
     * 初级的基本权限
     */
    BASE_AUTH(3, EnumSet.of(OperationEnum.CHANNEL_ACCESS_MESSAGE)),

    /**
     * 中级权限
     */
    MIDDLE_AUTH(6, EnumSet.of(OperationEnum.CHANNEL_SEND_NORMAL_MESSAGE, OperationEnum.CHANNEL_ACCESS_MESSAGE)),

    /**
     * 高级权限
     */
    HIGH_AUTH(9, EnumSet.of(OperationEnum.CHANNEL_SEND_MESSAGE_WITH_ATTACHMENT, OperationEnum.CHANNEL_SEND_NORMAL_MESSAGE, OperationEnum.CHANNEL_ACCESS_MESSAGE)),

    /**
     * 特级权限
     */
    SPECIAL_AUTH(12, EnumSet.of(OperationEnum.CHANNEL_DELETE_MESSAGE, OperationEnum.CHANNEL_EDIT_MESSAGE, OperationEnum.CHANNEL_PIN_MESSAGE
            , OperationEnum.CHANNEL_SEND_MESSAGE_WITH_ATTACHMENT, OperationEnum.CHANNEL_SEND_NORMAL_MESSAGE, OperationEnum.CHANNEL_ACCESS_MESSAGE)),


    /**
     * 管理员权限
     */
    ADMIN_AUTH(15, EnumSet.allOf(OperationEnum.class)),

    ;

    private static Map<Integer, ChannelAuthEnum> typeMap = new HashMap<>();

    static {
        for (var authEnum : ChannelAuthEnum.values()) {
            var previousValue = typeMap.putIfAbsent(authEnum.type, authEnum);
            AssertionUtils.isNull(previousValue, "AuthEnum中不应该含有重复type的枚举类[{}]和[{}]", authEnum, previousValue);
        }
    }

    private int type;

    private Set<OperationEnum> operations;

    ChannelAuthEnum(int type, Set<OperationEnum> operations) {
        this.type = type;
        this.operations = operations;
    }

    @Nullable
    public static ChannelAuthEnum getAuthEnumByType(int type) {
        return typeMap.get(type);
    }

    public boolean hasAuth(OperationEnum operation) {
        return this.operations.contains(operation);
    }

    public int getType() {
        return type;
    }

}
