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
 * @since 2020-09-30 13:46
 */
public enum ImageQualityEnum {

    /**
     * 原图
     */
    ORIGIN((byte) 0, ""),

    /**
     * 高质量
     */
    HIGH((byte) 2, "!high"),

    /**
     * 中质量
     */
    MIDDLE((byte) 4, "!middle"),

    /**
     * 低质量
     */
    LOW((byte) 6, "!low"),

    ;

    private static Map<Byte, ImageQualityEnum> typeMap = new HashMap<>();

    static {
        for (var imageQualityEnum : ImageQualityEnum.values()) {
            var previousValue = typeMap.putIfAbsent(imageQualityEnum.type, imageQualityEnum);
            AssertionUtils.isNull(previousValue, "ImageQualityEnum中不应该含有重复type的枚举类[{}]和[{}]", imageQualityEnum, previousValue);
        }
    }

    private byte type;

    private String suffix;

    ImageQualityEnum(byte type, String suffix) {
        this.type = type;
        this.suffix = suffix;
    }

    @Nullable
    public static ImageQualityEnum getImageQualityEnumByType(int type) {
        return typeMap.get((byte) type);
    }

    public String getSuffix() {
        return suffix;
    }
}
