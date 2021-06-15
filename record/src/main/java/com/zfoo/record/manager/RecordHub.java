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

package com.zfoo.record.manager;

import com.zfoo.protocol.util.AssertionUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.record.model.anno.RecordCache;
import com.zfoo.record.model.config.RecordConfig;
import com.zfoo.record.model.persister.IRecordPersister;
import com.zfoo.record.model.record.IRecord;
import com.zfoo.record.model.vo.RecordDef;
import com.zfoo.util.net.HostAndPort;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-06-02 14:06
 */
public class RecordHub implements IRecordHub {

    private static final Logger logger = LoggerFactory.getLogger(RecordHub.class);

    private static RecordHub instance = null;

    private Map<Class<? extends IRecord<?>>, IRecordPersister> recordPersisterMap = new HashMap<>();

    private RecordConfig recordConfig;

    private RestHighLevelClient restHighLevelClient;
    private ElasticsearchRestTemplate restTemplate;


    public static RecordHub getInstance() {
        return instance;
    }

    public RecordConfig getRecordConfig() {
        return recordConfig;
    }

    public void setRecordConfig(RecordConfig recordConfig) {
        this.recordConfig = recordConfig;
    }

    @Override
    public void init() {
        instance = this;

        var recordDefMap = scanRecord();

        // 初始化persister
        for (var entry : recordDefMap.entrySet()) {
            var recordClazz = entry.getKey();
            var recordDef = entry.getValue();
            var persister = recordDef.getPersisterStrategy().getType().createPersister(recordDef);
            recordPersisterMap.put(recordClazz, persister);
            persister.start();
        }

        // 初始化elastic search的客户端
        var hostConfig = recordConfig.getHostConfig();
        var hostList = HostAndPort.toHostAndPortList(hostConfig.getAddressMap().values());
        var addressArray = hostList
                .stream()
                .map(it -> new HttpHost(it.getHost(), it.getPort()))
                .collect(Collectors.toList())
                .toArray(new HttpHost[hostList.size()]);

        var builder = RestClient.builder(addressArray);

        // 设置每个请求需要发送的默认headers，这样就不用在每个请求中指定它们。
        Header[] defaultHeaders = new Header[]{new BasicHeader("header", "value")};
        builder.setDefaultHeaders(defaultHeaders);

        // 设置数据库账号密码
        if (!StringUtils.isBlank(hostConfig.getUser()) && !StringUtils.isBlank(hostConfig.getPassword())) {
            var credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(hostConfig.getUser(), hostConfig.getPassword()));

            builder.setHttpClientConfigCallback(httpClientBuilder -> {
                httpClientBuilder.disableAuthCaching();
                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            });
        }

        builder.setFailureListener(new RestClient.FailureListener() {
            @Override
            public void onFailure(Node node) {
                super.onFailure(node);
                // 设置一个监听程序，每次节点发生故障时都会收到通知，这样就可以采取相应的措施。
                // Used internally when sniffing on failure is enabled.(这句话没搞懂啥意思)
            }
        });

        builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                //设置允许修改默认请求配置的回调
                // （例如，请求超时，身份验证或org.apache.http.client.config.RequestConfig.Builder允许设置的任何内容）
                return requestConfigBuilder.setSocketTimeout(25_000);
            }
        });

        restHighLevelClient = new RestHighLevelClient(builder);
        restTemplate = new ElasticsearchRestTemplate(restHighLevelClient);

        // Elastic Search即使
        try {
            for (var recordClazz : recordPersisterMap.keySet()) {
                var operation = restTemplate.indexOps(recordClazz);
                if (!operation.exists()) {
                    operation.create();
                    var document = operation.createMapping();
                    operation.putMapping(document);
                }
            }
        } catch (Exception e) {
            logger.warn("elastic search[{}] client初始化异常，可能导致无法写入日志记录，如果不需要es日志记录则可以忽略本条警告", hostList);
        }
    }


    private Map<Class<? extends IRecord<?>>, RecordDef> scanRecord() {
        var cacheDefMap = new HashMap<Class<? extends IRecord<?>>, RecordDef>();
        var entityPackage = recordConfig.getRecordPackage();
        var cacheMap = recordConfig.getCachesConfig().getCacheStrategies();
        var persisterMap = recordConfig.getPersistersConfig().getPersisterStrategies();

        var locationSet = getRecordLocation(entityPackage);
        for (var location : locationSet) {
            Class<?> entityClazz;
            try {
                entityClazz = Class.forName(location);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(StringUtils.format("无法获取记录[{}]", location));
            }
            var cacheDef = RecordDef.valueOf(entityClazz, cacheMap, persisterMap);
            var previousCacheDef = cacheDefMap.putIfAbsent((Class<? extends IRecord<?>>) entityClazz, cacheDef);
            AssertionUtils.isNull(previousCacheDef, "记录类不能包含重复的[class:{}]", entityClazz.getSimpleName());
        }
        return cacheDefMap;
    }

    private Set<String> getRecordLocation(String scanLocation) {
        var prefixPattern = "classpath*:";
        var suffixPattern = "**/*.class";

        var resourcePatternResolver = new PathMatchingResourcePatternResolver();
        var metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

        try {
            String packageSearchPath = prefixPattern + scanLocation.replace(StringUtils.PERIOD, StringUtils.SLASH) + StringUtils.SLASH + suffixPattern;
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            Set<String> result = new HashSet<>();
            String name = RecordCache.class.getName();
            for (int i = 0; i < resources.length; i++) {
                Resource resource = resources[i];
                if (resource.isReadable()) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    AnnotationMetadata annoMeta = metadataReader.getAnnotationMetadata();
                    if (annoMeta.hasAnnotation(name)) {
                        ClassMetadata clazzMeta = metadataReader.getClassMetadata();
                        result.add(clazzMeta.getClassName());
                    }
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("无法读取记录类信息:" + e);
        }
    }

    @Override
    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    @Override
    public Map<Class<? extends IRecord<?>>, IRecordPersister> getPersisterMap() {
        return recordPersisterMap;
    }

    @Override
    public void submitRecord(IRecord<?> record) {
        if (restHighLevelClient == null) {
            return;
        }

        var persister = recordPersisterMap.get(record.getClass());
        if (persister == null) {
            throw new RuntimeException(StringUtils.format("记录类[{}]没有定义，考虑没有加注解或者没有被扫描到", record.getClass().getSimpleName()));
        }
        persister.submitRecord(record);
    }

}
