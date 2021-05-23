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

package com.zfoo.app.zapp.web.seo.controller;

import com.zfoo.app.zapp.web.seo.model.BaiDuSeoResponse;
import com.zfoo.app.zapp.web.time.model.event.PassReviewTimeSliceEvent;
import com.zfoo.app.zapp.web.word.model.EditCategoryEvent;
import com.zfoo.app.zapp.web.word.model.EditWordEvent;
import com.zfoo.event.manager.EventBus;
import com.zfoo.event.model.anno.EventReceiver;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-07-27 10:41
 */
@Controller
public class SeoController {

    private static final Logger logger = LoggerFactory.getLogger(SeoController.class);

    @Value("${baiDu.auto.seo.api}")
    private String baiDuAutoSeoApi;

    @Value("${zfoo.time.url}")
    private String zfooTimeUrl;

    @Value("${zfoo.word.url}")
    private String zfooWordUrl;

    @Value("${zfoo.category.url}")
    private String zfooCategoryUrl;

    @EventReceiver
    public void onPassReviewTimeSliceEvent(PassReviewTimeSliceEvent event) {
        var entity = event.getNewEntity();
        var param = StringUtils.format(zfooTimeUrl, entity.getId());
        doBaiduSeo(param);
    }

    @EventReceiver
    public void onEditCategoryEvent(EditCategoryEvent event) {
        var entity = event.getCategoryEntity();
        var param = StringUtils.format(zfooCategoryUrl, entity.getId());
        doBaiduSeo(param);
    }

    @EventReceiver
    public void onEditWordEvent(EditWordEvent event) {
        var entity = event.getWordEntity();
        var param = StringUtils.format(zfooWordUrl, entity.getId());
        doBaiduSeo(param);
    }

    private void doBaiduSeo(String param) {
        EventBus.asyncExecute().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    var client = HttpClient.newBuilder().build();
                    var responseBodyHandler = HttpResponse.BodyHandlers.ofString();
                    var seoRequest = HttpRequest.newBuilder(URI.create(baiDuAutoSeoApi))
                            .header("Content-Type", "text/plain")
                            .POST(HttpRequest.BodyPublishers.ofString(param))
                            .build();
                    var baiDuSeoResponse = JsonUtils.string2Object(client.send(seoRequest, responseBodyHandler).body(), BaiDuSeoResponse.class);

                    if (!StringUtils.isBlank(baiDuSeoResponse.getMessage())) {
                        logger.error("推送失败[error:{}][message:{}]", baiDuSeoResponse.getError(), baiDuSeoResponse.getMessage());
                        return;
                    }

                    if (CollectionUtils.isNotEmpty(baiDuSeoResponse.getNotSameSite()) || CollectionUtils.isNotEmpty(baiDuSeoResponse.getNotValid())) {
                        logger.error("推送url不合法[success:{}][remain:{}][not_same_site:{}][not_valid:{}]"
                                , baiDuSeoResponse.getSuccess(), baiDuSeoResponse.getRemain(), baiDuSeoResponse.getNotSameSite(), baiDuSeoResponse.getNotValid());
                        return;
                    }

                    logger.info("百度seo推送[url:{}][success:{}][remain:{}]成功", param, baiDuSeoResponse.getSuccess(), baiDuSeoResponse.getRemain());
                } catch (Exception e) {
                    logger.error("seo未知异常", e);
                }
            }
        });
    }

}
