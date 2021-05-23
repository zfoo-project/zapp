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

package com.zfoo.app.zapp.common.entity.common;

import com.zfoo.protocol.util.AssertionUtils;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-09 13:48
 */
public enum MessageEnum {
    /**
     * 文本
     */
    TEXT((byte) 0),
    /**
     * 语音
     */
    AUDIO((byte) 1),
    /**
     * 图片
     */
    IMAGE((byte) 2),
    /**
     * 视频
     */
    VIDEO((byte) 3),
    /**
     * 除了图片和视频的其它文件
     */
    FILE((byte) 4),

    /**
     * 自带表情
     */
    EMOTION((byte) 7),

    /**
     * 系统自带表情
     */
    GIF((byte) 8),

    /**
     * 位置
     */
    LOCATION((byte) 10),
    /**
     * 玩家名片
     */
    USER_PROFILE((byte) 20),
    ;

    private static Map<Byte, MessageEnum> typeMap = new HashMap<>();

    static {
        for (var messageEnum : MessageEnum.values()) {
            var previousValue = typeMap.putIfAbsent(messageEnum.type, messageEnum);
            AssertionUtils.isNull(previousValue, "[{}]中不应该含有重复的枚举类[{}]和[{}]", messageEnum.getClass().getSimpleName(), messageEnum, previousValue);
        }
    }

    private byte type;

    MessageEnum(byte type) {
        this.type = type;
    }

    @Nullable
    public static MessageEnum getMessageEnumByType(byte type) {
        return typeMap.get(type);
    }

    public byte getType() {
        return type;
    }

}
