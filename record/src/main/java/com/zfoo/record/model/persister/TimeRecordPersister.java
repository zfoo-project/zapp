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

package com.zfoo.record.model.persister;

import com.zfoo.event.manager.EventBus;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.collection.ConcurrentArrayList;
import com.zfoo.protocol.collection.ConcurrentHashSet;
import com.zfoo.protocol.exception.ExceptionUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.record.RecordContext;
import com.zfoo.record.model.constant.RecordConstant;
import com.zfoo.record.model.record.IRecord;
import com.zfoo.record.model.vo.RecordDef;
import com.zfoo.record.util.RecordUtils;
import com.zfoo.scheduler.SchedulerContext;
import io.netty.util.concurrent.FastThreadLocal;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-07-20 12:13
 */
public class TimeRecordPersister extends AbstractRecordPersister {

    private static final Logger logger = LoggerFactory.getLogger(TimeRecordPersister.class);

    /**
     * elastic search中的索引名称
     */
    private String indexName;

    private int cacheSize;

    /**
     * 执行频率
     */
    private long rate;

    private Set<ConcurrentArrayList<IRecord<?>>> recordsSet;

    private FastThreadLocal<ConcurrentArrayList<IRecord<?>>> recordsThreadLocal;

    public TimeRecordPersister(RecordDef recordDef) {
        super(recordDef);
        this.indexName = RecordUtils.toIndexName(recordDef.getClazz());
        this.cacheSize = recordDef.getCacheSize();
        this.rate = Long.parseLong(recordDef.getPersisterStrategy().getConfig());
        this.recordsSet = new ConcurrentHashSet<>();
        this.recordsThreadLocal = new FastThreadLocal<ConcurrentArrayList<IRecord<?>>>() {
            @Override
            protected ConcurrentArrayList<IRecord<?>> initialValue() {
                var list = new ConcurrentArrayList<IRecord<?>>(cacheSize + 32);
                recordsSet.add(list);
                return list;
            }
        };

        if (this.rate <= 0) {
            throw new RuntimeException(StringUtils.format("记录类[{}]刷新频率[{}]不能小于0", indexName, rate));
        }
    }

    @Override
    public void start() {
        SchedulerContext.getSchedulerManager().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!RecordContext.isStop()) {
                    EventBus.asyncExecute().execute(new Runnable() {
                        @Override
                        public void run() {
                            persistAll();
                        }
                    });
                }
            }
        }, rate, TimeUnit.MILLISECONDS);
    }

    /**
     * 之所以没有用无诸塞队列，是因为这边每条线程都维护了一个自己的队列
     * <p>
     * ConcurrentArrayList中的ReentrantLock本质还是CAS操作，操作自己的队列并没有锁的竞争
     * <p>
     * clear的时候还会批量复制会极大的减少批量操作的时间
     */
    @Override
    public void submitRecord(IRecord<?> record) {
        var records = recordsThreadLocal.get();
        records.add(record);
        if (records.size() >= cacheSize) {
            persistRecords(records.clearAndReturn());
        }
    }

    @Override
    public void persistRecords(List<IRecord<?>> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        var bulkRequest = new BulkRequest();

        try {
            records.stream()
                    .map(it -> JsonUtils.object2String(it))
                    .forEach(it -> {
                        var indexRequest = new IndexRequest(indexName);
                        indexRequest.source(it, XContentType.JSON);
                        bulkRequest.add(indexRequest);
                    });
        } catch (Exception e) {
            logger.error("记录类[{}] elastic search记录提交异常", indexName, e);
        }

        if (bulkRequest.numberOfActions() <= 0) {
            return;
        }

        var count = new AtomicInteger(0);

        doPersist(count, bulkRequest);
    }

    @Override
    public void persistAll() {
        for (var records : recordsSet) {
            try {
                persistRecords(records.clearAndReturn());
            } catch (Throwable t) {
                logger.error(ExceptionUtils.getMessage(t));
            }
        }
    }

    private void doPersist(AtomicInteger count, BulkRequest bulkRequest) {
        var restHighLevelClient = RecordContext.getRecordHub().getRestHighLevelClient();

        restHighLevelClient.bulkAsync(bulkRequest, RequestOptions.DEFAULT, new ActionListener<BulkResponse>() {
            @Override
            public void onResponse(BulkResponse response) {
                if (response.hasFailures()) {
                    logger.error("记录类[{}] elastic search记录提交失败response[{}]", indexName, response.buildFailureMessage());
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (count.getAndIncrement() < RecordConstant.RETRY_TIMES) {
                    doPersist(count, bulkRequest);
                    logger.error("记录类[{}] elastic search发送失败，重新发送[{}]", indexName, count.get());
                    return;
                }
                logger.error("记录类[{}] elastic search重试超出了最大限制[{}]，停止发送", indexName, RecordConstant.RETRY_TIMES, e);
            }
        });
    }


}
