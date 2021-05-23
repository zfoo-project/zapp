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

package com.zfoo.app.zapp.web.login;

import com.zfoo.event.manager.EventBus;
import com.zfoo.protocol.util.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-01-14 13:55
 */
@Ignore
public class WeiboTest {

    private static final String weChatAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid={}&secret={}&code={}&grant_type=authorization_code";
    private static final String weChatAppid = "xxxxxxxxxxxxxxxxxxxx";
    private static final String weChatSecret = "xxxxxxxxxxxxxxxxxxxxxxxxx";

    @Test
    public void accessTokeTest() throws IOException, InterruptedException {
        var code = "061oJJyy1cQoAc0xX2wy1jyTyy1oJJyg";

        var client = HttpClient.newBuilder()
                .executor(EventBus.asyncExecute())
                .build();

        var request = HttpRequest
                .newBuilder(URI.create(StringUtils.format(weChatAccessTokenUrl, weChatAppid, weChatSecret, code)))
                .GET()
                .build();

        var responseBodyHandler = HttpResponse.BodyHandlers.ofString();

        var weChatAccessTokenResponse = client.send(request, responseBodyHandler);
        System.out.println(weChatAccessTokenResponse.body());
    }


}
