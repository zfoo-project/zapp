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

package com.zfoo.app.zapp.common.constant;

import com.zfoo.protocol.model.Quaternion;
import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.protocol.util.IOUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-06-07 12:03
 */
public abstract class OssPolicyConstant {

    public static final String AVATAR = "avatar";
    public static final String BACKGROUND = "background";
    public static final String IMAGE = "img";
    public static final String VIDEO = "video";
    public static final String VIDEO_POSTER = "video.poster";
    /**
     * 部分用作测试
     */
    public static final String REPORT = "report";
    public static final String WORD_BACKGROUND = "word.background";
    public static final String CHAT_IMAGE = "chat.img";
    public static final String CHAT_VIDEO = "chat.video";
    public static final String GROUP_AVATAR = "group.avatar";
    public static final String GROUP_BACKGROUND = "group.background";
    public static final String GROUP_IMAGE = "group.img";
    public static final String GROUP_VIDEO = "group.video";

    private static Map<String, String> urlMap = new HashMap<>();
    private static Map<String, String> dirMap = new HashMap<>();
    private static Map<String, String> bucketMap = new HashMap<>();

    public static void init() throws IOException {
        parsePolicies();
    }

    private static void parsePolicies() throws IOException {
        var path = StringUtils.format("policies-{}.json", ZappDeployEnum.getDeployEnum().name());
        var inputStream = ClassUtils.getFileFromClassPath(path);

        var bytes = IOUtils.toByteArray(inputStream);
        var str = StringUtils.bytesToString(bytes);
        var pairList = JsonUtils.string2Array(str, Quaternion.class);

        var queue = new LinkedList<>(List.of(pairList));
        while (!queue.isEmpty()) {
            var item = queue.poll();
            urlMap.put(item.getA().toString(), item.getB().toString());
            dirMap.put(item.getA().toString(), item.getC().toString());
            bucketMap.put(item.getA().toString(), item.getD().toString());
        }

        IOUtils.closeIO(inputStream);
    }


    public static String getUrl(String policy) {
        return urlMap.get(policy);
    }

    public static String getDir(String policy) {
        return dirMap.get(policy);
    }

    public static String getBucket(String policy) {
        return bucketMap.get(policy);
    }

}
