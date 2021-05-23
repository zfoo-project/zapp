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
 * 权限枚举类，主要用于管理整个群组的权限
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-04 19:47
 */
public enum GroupAuthEnum {

    /**
     * 没有权限
     */
    NO_AUTH(0, Collections.EMPTY_SET),

    /**
     * 初级的基本权限
     */
    BASE_AUTH(3, EnumSet.of(OperationEnum.CREATE_INVITE_CODE, OperationEnum.DELETE_INVITE_CODE, OperationEnum.ACCESS_INVITE_CODE)),

    /**
     * 中级权限
     */
    MIDDLE_AUTH(6, EnumSet.of(OperationEnum.RENAME_GROUP_MEMBER, OperationEnum.CREATE_INVITE_CODE, OperationEnum.DELETE_INVITE_CODE, OperationEnum.ACCESS_INVITE_CODE)),

    /**
     * 高级权限
     */
    HIGH_AUTH(9, EnumSet.of(
            OperationEnum.KICK_GROUP_MEMBER, OperationEnum.BAN_GROUP_MEMBER,
            OperationEnum.RENAME_GROUP_MEMBER, OperationEnum.CREATE_INVITE_CODE, OperationEnum.DELETE_INVITE_CODE, OperationEnum.ACCESS_INVITE_CODE)),

    /**
     * 特级权限
     */
    SPECIAL_AUTH(12, EnumSet.of(
            OperationEnum.CREATE_GROUP_AUTH, OperationEnum.DELETE_GROUP, OperationEnum.DELETE_GROUP_AUTH, OperationEnum.CHANGE_GROUP_AUTH,
            OperationEnum.ADD_MEMBER_TO_GROUP_AUTH, OperationEnum.REMOVE_MEMBER_FROM_GROUP_AUTH,
            OperationEnum.CHANGE_CHANNEL_AUTH, OperationEnum.CREATE_CHANNEL, OperationEnum.DELETE_CHANNEL, OperationEnum.CHANGE_CHANNEL_NAME,
            OperationEnum.CREATE_CHANNEL_BOX, OperationEnum.DELETE_CHANNEL_BOX, OperationEnum.CHANGE_CHANNEL_BOX_NAME,
            OperationEnum.KICK_GROUP_MEMBER, OperationEnum.BAN_GROUP_MEMBER,
            OperationEnum.RENAME_GROUP_MEMBER, OperationEnum.CREATE_INVITE_CODE, OperationEnum.DELETE_INVITE_CODE, OperationEnum.ACCESS_INVITE_CODE)),

    /**
     * 管理员权限
     */
    ADMIN_AUTH(15, EnumSet.allOf(OperationEnum.class)),

    ;

    private static Map<Integer, GroupAuthEnum> typeMap = new HashMap<>();

    static {
        for (var authEnum : GroupAuthEnum.values()) {
            var previousValue = typeMap.putIfAbsent(authEnum.type, authEnum);
            AssertionUtils.isNull(previousValue, "AuthEnum中不应该含有重复type的枚举类[{}]和[{}]", authEnum, previousValue);
        }
    }

    private int type;

    private Set<OperationEnum> operations;

    GroupAuthEnum(int type, Set<OperationEnum> operations) {
        this.type = type;
        this.operations = operations;
    }

    @Nullable
    public static GroupAuthEnum getAuthEnumByType(int type) {
        return typeMap.get(type);
    }

    public boolean hasAuth(OperationEnum operation) {
        return this.operations.contains(operation);
    }

    public int getType() {
        return type;
    }


}
