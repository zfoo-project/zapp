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

package com.zfoo.record;

import com.zfoo.record.manager.RecordHub;
import com.zfoo.record.record.CurrencyRecord;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.util.ThreadUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-06-02 14:17
 */
@Ignore
public class ApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationTest.class);

    public static final String CONFIG_LOCATION = "application.xml";

    private static final int EXECUTORS_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    private static final ExecutorService[] executors;
    private static final int INSERT_NUM = 10000_0000;
    private static final int BATCH_INSERT_NUM = 100_0000;
    private static final AtomicInteger atomic = new AtomicInteger(0);

    static {
        executors = new ExecutorService[EXECUTORS_SIZE];

        for (int i = 0; i < executors.length; i++) {
            executors[i] = Executors.newSingleThreadExecutor();
        }
    }

    @Test
    public void speedTest() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(CONFIG_LOCATION);

        var startTime = TimeUtils.currentTimeMillis();

        for (var executor : executors) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    for (var i = 0; i < INSERT_NUM; i++) {
                        RecordHub.getInstance().submitRecord(CurrencyRecord.valueOf(1, 1, (short) 1, 1));
                        atomic.incrementAndGet();
                        if (i % BATCH_INSERT_NUM == 0) {
                            ThreadUtils.sleep(120_000);
                        }
                    }
                }
            });
        }

        while (true) {
            if (atomic.get() >= EXECUTORS_SIZE * INSERT_NUM) {
                break;
            }
        }

        var endTime = TimeUtils.currentTimeMillis();
        System.out.println(endTime - startTime);
        System.out.println("成功");
        ThreadUtils.sleep(Long.MAX_VALUE);
    }

    @Test
    public void bulkTest() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(CONFIG_LOCATION);
        RecordHub.getInstance().submitRecord(CurrencyRecord.valueOf(1, 1, (short) 1, 1));
        ThreadUtils.sleep(Long.MAX_VALUE);
    }

    @Test
    public void logTest() {
        logger.info("日志测试[{}]", CurrencyRecord.valueOf(1, 1, (short) 1, 1));
        ThreadUtils.sleep(Long.MAX_VALUE);
    }

}
