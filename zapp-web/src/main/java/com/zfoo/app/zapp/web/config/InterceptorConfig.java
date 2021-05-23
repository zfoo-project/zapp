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

package com.zfoo.app.zapp.web.config;

import com.zfoo.app.zapp.web.util.HttpUtils;
import com.zfoo.monitor.util.OSUtils;
import com.zfoo.net.util.SimpleCache;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 如果需要SEO，就要开启这个配置
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-07-29 20:42
 */
// @Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(InterceptorConfig.class);

    // seo需要排除路径
    private static final List<String> excludePaths = new ArrayList<>();

    // 服务器页面渲染缓存
    private static final SimpleCache<String, String> htmlPageCaches = SimpleCache.build(
            24 * TimeUtils.MILLIS_PER_DAY, 12 * TimeUtils.MILLIS_PER_DAY, 1_000
            , urls -> {
                var list = new ArrayList<Pair<String, String>>();
                for (var url : urls) {
                    try {
                        var command = StringUtils.format("node /usr/local/spider/spider.js {}", url);
                        var html = OSUtils.execCommand(command);

                        if (StringUtils.isBlank(html)) {
                            logger.error("执行node命令[command:{}]发生返回了空的结果", command);
                            continue;
                        }
                        if (html.startsWith("zfoo_error")) {
                            logger.error("执行node命令[command:{}]发生内部错误[error:{}]", command, html);
                            continue;
                        }

                        list.add(new Pair<>(url, html));
                    } catch (Exception e) {
                        logger.error("执行node命令未知错误", e);
                    }
                }

                return list;
            }
            , key -> StringUtils.EMPTY);

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // api路径
        excludePaths.add(StringUtils.format("/api"));
        // 静态资源路径
        excludePaths.addAll(rootStaticPaths());
        // 错误页面
        excludePaths.add("/404");
        excludePaths.add("/500");
        excludePaths.add("/error");

        logger.info("Deploy interceptor successfully and excludePaths[{}]", excludePaths);
        registry.addInterceptor(new SeoInterceptor());
    }

    private List<String> rootStaticPaths() {
        try {
            var resourceList = new ArrayList<Resource>();
            var resourcePatternResolver = new PathMatchingResourcePatternResolver();

            // 匹配根目录的文件夹
            var resources = resourcePatternResolver.getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "static/*/");
            resourceList.addAll(Arrays.asList(resources));

            // 匹配根目录的文件
            resources = resourcePatternResolver.getResources(ResourceUtils.CLASSPATH_URL_PREFIX + "static/*");
            resourceList.addAll(Arrays.asList(resources));

            var staticPaths = resourceList.stream()
                    .map(it -> {
                        if (it.isReadable()) {
                            return StringUtils.format("/{}", it.getFilename());
                        } else {
                            var name = it.getFilename();
                            if (StringUtils.isBlank(name)) {
                                if (it instanceof ClassPathResource) {
                                    name = ((ClassPathResource) it).getPath();
                                } else {
                                    throw new RuntimeException(StringUtils.format("静态资源未知路径[{}]", it.getDescription()));
                                }
                            }
                            if (name.endsWith(StringUtils.SLASH)) {
                                name = StringUtils.substringBeforeLast(name, StringUtils.SLASH);
                            }
                            if (name.startsWith("static") || name.startsWith("/static")) {
                                name = StringUtils.substringAfterFirst(name, "static/");
                            }
                            return StringUtils.format("/{}", name);
                        }
                    })
                    .collect(Collectors.toList());
            return staticPaths;
        } catch (Exception e) {
            throw new RuntimeException("静态资源读取未知异常", e);
        }
    }

    private static class SeoInterceptor implements HandlerInterceptor {

        /**
         * 方法在请求处理之前被调用。该方法在 Interceptor 类中最先执行，用来进行一些前置初始化操作或是对当前请求做预处理，也可以进行一些判断来决定请求是否要继续进行下去。
         * 该方法的返回至是 Boolean 类型，当它返回 false 时，表示请求结束，后续的 Interceptor 和 Controller 都不会再执行；
         * 当它返回为 true 时会继续调用下一个 Interceptor 的 preHandle 方法，如果已经是最后一个 Interceptor 的时候就会调用当前请求的 Controller 方法。
         */
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            var uri = request.getRequestURI();
            if (excludePaths.stream().anyMatch(it -> uri.startsWith(it))) {
                return true;
            }

            if (HttpUtils.isSpiderRequest(request)) {
                var url = request.getRequestURL().toString();
                logger.info("excludePaths bool[{}]", excludePaths.stream().anyMatch(it -> uri.startsWith(it)));
                logger.info("spider[{}][ua:{}][url:{}][uri:{}]", request, request.getHeader("user-agent"), url, uri);

                var html = htmlPageCaches.get(url);

                response.setCharacterEncoding(StringUtils.DEFAULT_CHARSET_NAME);
                response.setHeader("Content-Type", "text/html;charset=UTF-8");
                response.getWriter().write(html);
                return false;
            }

            return true;
        }

        /**
         * 方法在当前请求处理完成之后，也就是 Controller 方法调用之后执行，但是它会在  DispatcherServlet  进行视图返回渲染之前被调用，
         * 所以我们可以在这个方法中对 Controller 处理之后的 ModelAndView 对象进行操作。
         */
        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        }

        /**
         * 方法需要在当前对应的 Interceptor 类的 preHandle 方法返回值为 true 时才会执行。顾名思义，该方法将在整个请求结束之后，
         * 也就是在 DispatcherServlet 渲染了对应的视图之后执行。此方法主要用来进行资源清理。
         */
        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        }
    }

}
