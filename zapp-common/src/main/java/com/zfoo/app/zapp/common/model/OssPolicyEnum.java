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

import com.zfoo.app.zapp.common.constant.OssPolicyConstant;
import com.zfoo.protocol.util.AssertionUtils;
import com.zfoo.protocol.util.IOUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.util.math.RandomUtils;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-02 14:27
 */
public enum OssPolicyEnum {


    /**
     * 头像上传，https://img.zfoo.com会映射到https://zfoo-union.oss-cn-shanghai.aliyuncs.com，所以无需指定bucket
     */
    AVATAR(0, 20 * TimeUtils.MILLIS_PER_SECOND, 20 * IOUtils.BYTES_PER_MB
            , OssPolicyConstant.AVATAR
            , obj -> List.of(obj.toString())),

    /**
     * 用户的背景图片
     */
    BACKGROUND(1, 20 * TimeUtils.MILLIS_PER_SECOND, 20 * IOUtils.BYTES_PER_MB
            , OssPolicyConstant.BACKGROUND
            , obj -> List.of(obj.toString())),

    /**
     * 图片上传
     */
    IMAGE(2, 180 * TimeUtils.MILLIS_PER_SECOND, 20 * IOUtils.BYTES_PER_MB
            , OssPolicyConstant.IMAGE
            , obj -> List.of(TimeUtils.simpleDateString().substring(0, 8), obj.toString(), RandomUtils.randomString(32))),

    /**
     * 视频上传
     */
    VIDEO(3, 600 * TimeUtils.MILLIS_PER_SECOND, 200 * IOUtils.BYTES_PER_MB
            , OssPolicyConstant.VIDEO
            , obj -> List.of(TimeUtils.simpleDateString().substring(0, 8), obj.toString(), RandomUtils.randomString(32))),

    /**
     * 视频封面上传
     */
    VIDEO_POSTER(4, 20 * TimeUtils.MILLIS_PER_SECOND, 20 * IOUtils.BYTES_PER_MB
            , OssPolicyConstant.VIDEO_POSTER
            , obj -> List.of(TimeUtils.simpleDateString().substring(0, 8), obj.toString(), RandomUtils.randomString(32))),


    /**
     * 举报文件，举报文件可能很大，过期时间设置长一些，部分用作测试
     */
    REPORT(10, 100 * TimeUtils.MILLIS_PER_SECOND, 200 * IOUtils.BYTES_PER_MB
            , OssPolicyConstant.REPORT
            , obj -> List.of(TimeUtils.simpleDateString().substring(0, 8), obj.toString(), RandomUtils.randomString(32), RandomUtils.randomString(32))),


    /**
     * 词条和类别背景图片上传
     */
    WORD_BACKGROUND(15, 20 * TimeUtils.MILLIS_PER_SECOND, 20 * IOUtils.BYTES_PER_MB
            , OssPolicyConstant.WORD_BACKGROUND
            , obj -> List.of(TimeUtils.simpleDateString().substring(0, 8), obj.toString(), RandomUtils.randomString(32))),

    /**
     * 好友聊天图片
     */
    CHAT_IMAGE(20, 20 * TimeUtils.MILLIS_PER_SECOND, 20 * IOUtils.BYTES_PER_MB
            , OssPolicyConstant.CHAT_IMAGE
            , obj -> List.of(TimeUtils.simpleDateString().substring(0, 8), obj.toString(), RandomUtils.randomString(32), RandomUtils.randomString(32))),

    /**
     * 好友聊天视频
     */
    CHAT_VIDEO(21, 20 * TimeUtils.MILLIS_PER_SECOND, 200 * IOUtils.BYTES_PER_MB
            , OssPolicyConstant.CHAT_VIDEO
            , obj -> List.of(TimeUtils.simpleDateString().substring(0, 8), obj.toString(), RandomUtils.randomString(32), RandomUtils.randomString(32))),

    /**
     * 群组头像上传
     */
    GROUP_AVATAR(30, 20 * TimeUtils.MILLIS_PER_SECOND, 20 * IOUtils.BYTES_PER_MB
            , OssPolicyConstant.GROUP_AVATAR
            , obj -> List.of(obj.toString(), String.valueOf(TimeUtils.now()))),

    /**
     * 群组背景上传
     */
    GROUP_BACKGROUND(31, 20 * TimeUtils.MILLIS_PER_SECOND, 20 * IOUtils.BYTES_PER_MB
            , OssPolicyConstant.GROUP_BACKGROUND
            , obj -> List.of(obj.toString(), String.valueOf(TimeUtils.now()))),

    /**
     * 群组聊天图片
     */
    GROUP_IMAGE(35, 20 * TimeUtils.MILLIS_PER_SECOND, 20 * IOUtils.BYTES_PER_MB
            , OssPolicyConstant.GROUP_IMAGE
            , obj -> List.of(TimeUtils.simpleDateString().substring(0, 8), obj.toString(), RandomUtils.randomString(32), RandomUtils.randomString(32))),

    /**
     * 群组聊天视频
     */
    GROUP_VIDEO(36, 20 * TimeUtils.MILLIS_PER_SECOND, 200 * IOUtils.BYTES_PER_MB
            , OssPolicyConstant.GROUP_VIDEO
            , obj -> List.of(TimeUtils.simpleDateString().substring(0, 8), obj.toString(), RandomUtils.randomString(32), RandomUtils.randomString(32))),

    ;

    /**
     * 上传的类别
     */
    private int type;
    /**
     * policy过期时间
     */
    private long expireTime;
    /**
     * 文件大小限制
     */
    private long maxFileSize;
    /**
     * key映射到url和文件路径
     */
    private String key;
    /**
     * 生成dir参数的函数
     */
    private Function<Object, List<String>> dirFunction;

    public long getExpireTime() {
        return expireTime;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    OssPolicyEnum(int type, long expireTime, long maxFileSize, String key, Function<Object, List<String>> dirFunction) {
        this.type = type;
        this.expireTime = expireTime;
        this.maxFileSize = maxFileSize;
        this.key = key;
        this.dirFunction = dirFunction;
    }

    public String getKey() {
        return key;
    }

    public String getUrl() {
        return OssPolicyConstant.getUrl(this.key);
    }

    public String getDir() {
        return OssPolicyConstant.getDir(this.key);
    }

    public String getBucket() {
        return OssPolicyConstant.getBucket(this.key);
    }

    public String toDir(Object obj) {
        return StringUtils.format(getDir(), this.dirFunction.apply(obj).toArray());
    }

    public String toUrlAndDir(Object obj) {
        return getUrl() + StringUtils.SLASH + toDir(obj);
    }


    private static Map<Integer, OssPolicyEnum> typeMap = new HashMap<>();

    static {
        for (var policyEnum : OssPolicyEnum.values()) {
            var previousValue = typeMap.putIfAbsent(policyEnum.type, policyEnum);
            AssertionUtils.isNull(previousValue, "OssPolicyEnum中不应该含有重复type的枚举类[{}]和[{}]", policyEnum, previousValue);
        }
    }

    @Nullable
    public static OssPolicyEnum getOssPolicyEnumByType(int type) {
        var ossPolicyEnum = typeMap.get(type);
        return ossPolicyEnum;
    }

}
