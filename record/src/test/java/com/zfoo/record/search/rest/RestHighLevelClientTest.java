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

import com.zfoo.protocol.util.JsonUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-07-16 19:38
 */
@Ignore
public class RestHighLevelClientTest {

    private static final RestHighLevelClient restHighLevelClient;
    private static final String index = "blog";


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

        restHighLevelClient = new RestHighLevelClient(builder);
    }

    private static final Blog blog = Blog.valueOf(Integer.MAX_VALUE, "Kotlin从入门到精通", "kotlin是一个结合了Java和scala的编程语言", "2020-07-15 16:00:00");


    @Test
    public void insertTest() throws IOException {
        IndexRequest indexRequest = new IndexRequest(index);
        indexRequest.id("1");
        String source = JsonUtils.object2String(blog);
        indexRequest.source(source, XContentType.JSON);

        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }


    @Test
    public void batchInsertTest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();

        List<IndexRequest> indexRequestList = new ArrayList<>();

        IndexRequest indexRequest = new IndexRequest(index);
        String source = JsonUtils.object2String(blog);
        indexRequest.source(source, XContentType.JSON);

        indexRequestList.add(indexRequest);
        indexRequestList.add(indexRequest);
        indexRequestList.add(indexRequest);
        indexRequestList.add(indexRequest);

        for (IndexRequest request : indexRequestList) {
            bulkRequest.add(request);
        }
        BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        System.out.println(response.buildFailureMessage());
    }

    @Test
    public void deleteTest() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(index);
        deleteRequest.id("1");
        DeleteResponse response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(response);
    }


    @Test
    public void updateTest() throws IOException {
        UpdateRequest request = new UpdateRequest(index, "1");

        blog.setTitle(blog.getTitle() + "update");
        String source = JsonUtils.object2String(blog);
        request.doc(source, XContentType.JSON);
        request.version(request.version() + 1);

        restHighLevelClient.update(request, RequestOptions.DEFAULT);
    }

    /*
      get /_search
      {
          "from": "0",
          "size": "10",
          "_source": [
              "id",
              "title",
              "content"
          ],
          "query": {
              "bool": {
                  "must": {
                      "range": {
                          "id": {
                              "gte": "1",
                              "lte": "2"
                          }
                      }
                  }
              }
          }
      }
     */
    @Test
    public void queryTest() throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(0);
        sourceBuilder.size(10);
        sourceBuilder.fetchSource(new String[]{"id", "title", "content"}, new String[]{});


        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("content", "语言");
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("id");
        rangeQueryBuilder.gte("1");
        rangeQueryBuilder.lte("3");


        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        boolBuilder.must(matchQueryBuilder);
        boolBuilder.must(rangeQueryBuilder);


        sourceBuilder.query(boolBuilder);
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(sourceBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(response);
    }

}
