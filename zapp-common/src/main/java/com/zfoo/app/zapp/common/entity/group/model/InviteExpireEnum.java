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
import com.zfoo.scheduler.util.TimeUtils;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-03 20:25
 */
public enum InviteExpireEnum {

    /**
     * 无到期时间邀请
     */
    EXPIRE_UNLIMITED(0, 0),

    /**
     * 办小时
     */
    EXPIRE_HALF_HOUR(1, 30 * TimeUtils.MILLIS_PER_HOUR),

    /**
     * 1小时
     */
    EXPIRE_ONE_HOUR(2, 1 * TimeUtils.MILLIS_PER_HOUR),

    /**
     * 6小时
     */
    EXPIRE_SIX_HOUR(3, 6 * TimeUtils.MILLIS_PER_HOUR),

    /**
     * 12小时
     */
    EXPIRE_TWELVE_HOUR(4, 12 * TimeUtils.MILLIS_PER_HOUR),

    /**
     * 1天
     */
    EXPIRE_ONE_DAY(5, 1 * TimeUtils.MILLIS_PER_DAY),


    ;

    private static Map<Integer, InviteExpireEnum> typeMap = new HashMap<>();

    static {
        for (var expireEnum : InviteExpireEnum.values()) {
            var previousValue = typeMap.putIfAbsent(expireEnum.type, expireEnum);
            AssertionUtils.isNull(previousValue, "InviteExpireEnum中不应该含有重复type的枚举类[{}]和[{}]", expireEnum, previousValue);
        }
    }

    private int type;
    private long expire;

    InviteExpireEnum(int type, long expire) {
        this.type = type;
        this.expire = expire;
    }

    @Nullable
    public static InviteExpireEnum getInviteExpireEnumByType(int type) {
        return typeMap.get(type);
    }

    public int getType() {
        return type;
    }

    public long getExpire() {
        return expire;
    }
}
