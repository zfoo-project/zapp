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

package com.zfoo.app.zapp.web.util;

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.util.TokenUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.util.math.NumberUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-01-15 18:44
 */
public abstract class HttpUtils {

    public static final String HTTP_PREFIX = "http://";
    public static final String HTTPS_PREFIX = "https://";

    /**
     * 搜索引擎UA：
     * 百度
     * 谷歌
     * 搜狗
     * 360
     * 神马
     * 今日头条
     * 雅虎
     * 必应
     */
    private static final List<String> SEARCH_ENGINE_UA_LIST = List.of("baidu", "google", "sogou"
            , "360spider", "yisou", "bytespider", "yahoo", "bingbot");

    /**
     * @param request
     * @return 如果获取不到用户，返回-1
     */

    public static long getUid(HttpServletRequest request) {
        // 读取cookie
        var cookies = request.getCookies();

        if (CollectionUtils.isEmpty(cookies)) {
            return -1;
        }

        // 遍历数组
        for (var cookie : cookies) {
            if (cookie.getName().equals(AppConstant.ZFOO_COOKIE_TOKEN)) {
                return TokenUtils.get(cookie.getValue()).getLeft();
            }
        }

        return -1;
    }

    /**
     * 将形如1-2-3-1的参数转换为，1，2，3不重复的集合
     *
     * @param dashParam 斜杠参数
     * @return 参数集合
     */
    public static Set<Long> dashParamToSet(String dashParam) {
        if (StringUtils.isBlank(dashParam)) {
            return Collections.EMPTY_SET;
        }
        var set = Arrays.stream(dashParam.split(StringUtils.DASH))
                .filter(it -> NumberUtils.isNumeric(it))
                .map(it -> Long.parseLong(it))
                .collect(Collectors.toSet());
        return set;
    }

    /**
     * 主要检查url是否已http开头，如果不已http开头，则认为是非法的url
     */
    public static boolean isHttpUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        return url.startsWith("http://") || url.startsWith("https://");
    }

    /**
     * 检查文件的url是否合法
     */
    public static boolean isHttpUrls(List<String> urlLinks) {
        if (CollectionUtils.isEmpty(urlLinks)) {
            return true;
        }
        for (var urlLink : urlLinks) {
            if (!isHttpUrl(urlLink)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSpiderRequest(HttpServletRequest request) {
        var userAgent = request.getHeader("user-agent");
        if (StringUtils.isBlank(userAgent)) {
            return false;
        }

        var lowerCaseUserAgent = userAgent.toLowerCase();
        if (lowerCaseUserAgent.contains("spider") || lowerCaseUserAgent.contains("bot")) {
            if (SEARCH_ENGINE_UA_LIST.stream().anyMatch(it -> lowerCaseUserAgent.contains(it))) {
                return true;
            }
        }
        return false;
    }

}
