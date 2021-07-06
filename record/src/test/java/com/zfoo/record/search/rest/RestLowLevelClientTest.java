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

package com.zfoo.record.search.rest;

import com.zfoo.protocol.util.IOUtils;
import com.zfoo.util.ThreadUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.*;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-07-16 18:03
 */
@Ignore
public class RestLowLevelClientTest {

    private static final RestClient restClient;

    static {
        RestClientBuilder builder = RestClient.builder(
                new HttpHost("localhost", 9200, "http")
                // new HttpHost("localhost", 9201, "http")
        );
        Header[] defaultHeaders = new Header[]{new BasicHeader("header", "value")};
        //设置每个请求需要发送的默认headers，这样就不用在每个请求中指定它们。
        builder.setDefaultHeaders(defaultHeaders);
        builder.setFailureListener(new RestClient.FailureListener() {
            @Override
            public void onFailure(Node node) {
                super.onFailure(node);
                //设置一个监听程序，每次节点发生故障时都会收到通知，这样就可以采取相应的措施。
                //Used internally when sniffing on failure is enabled.(这句话没搞懂啥意思)
            }
        });
        builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                //设置允许修改默认请求配置的回调
                // （例如，请求超时，身份验证或org.apache.http.client.config.RequestConfig.Builder允许设置的任何内容）
                return requestConfigBuilder.setSocketTimeout(10000);
            }
        });

        restClient = builder.build();
    }

    @Test
    public void syncTest() throws IOException {

        //方式1：只提供谓词和终节点，这两个参数是必需要的参数
        Request request0 = new Request("GET", "/");
        Response response0 = restClient.performRequest(request0);
        System.out.println(response0);
        System.out.println(new String(IOUtils.toByteArray(response0.getEntity().getContent())));

        //方式2：提供谓词和终节点以及可选查询字符串参数和org.apache.http.HttpEntity对象中包含的请求主体来发送请求
        String queryJsonString = "{\n" +
                "    \"query\": {\n" +
                "        \"multi_match\": {\n" +
                "            \"query\": \"语言\",\n" +
                "            \"fields\": [\n" +
                "                \"title\",\n" +
                "                \"content\"\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "}";
        //为HttpEntity指定ContentType非常重要，因为它将用于设置Content-Type请求头，以便Elasticsearch可以正确解析内容。
        HttpEntity entity = new NStringEntity(queryJsonString, ContentType.APPLICATION_JSON);
        Request request1 = new Request("GET", "/_search");
        request1.setEntity(entity);
        Response response1 = restClient.performRequest(request1);
        System.out.println(response1);
        System.out.println(new String(IOUtils.toByteArray(response1.getEntity().getContent())));

        restClient.close();
    }

    @Test
    public void asyncTest() {
        //方式1： 提供谓词，终节点和响应监听器来发送异步请求，一旦请求完成，就会通知响应监听器，这三个参数是必需要的参数
        ResponseListener responseListener = new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                // 定义请求成功执行时需要做的事情
                System.out.println(response);
                try {
                    System.out.println(new String(IOUtils.toByteArray(response.getEntity().getContent())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                // 定义请求失败时需要做的事情，即每当发生连接错误或返回错误状态码时做的操作。
            }
        };
        restClient.performRequestAsync(new Request("GET", "/"), responseListener);

        ThreadUtils.sleep(Integer.MAX_VALUE);
    }


}
