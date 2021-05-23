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

import com.zfoo.protocol.exception.ExceptionUtils;
import com.zfoo.protocol.util.IOUtils;
import com.zfoo.protocol.util.ReflectionUtils;
import com.zfoo.record.manager.IRecordHub;
import com.zfoo.record.manager.RecordHub;
import com.zfoo.scheduler.SchedulerContext;
import com.zfoo.scheduler.util.TimeUtils;
import com.zfoo.util.ThreadUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-06-02 10:41
 */
public class RecordContext implements ApplicationListener<ApplicationContextEvent>, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RecordContext.class);

    private static RecordContext instance;

    private ApplicationContext applicationContext;

    private IRecordHub recordHub;

    private boolean stop = false;

    public static IRecordHub getRecordHub() {
        return instance.recordHub;
    }

    public static boolean isStop() {
        return instance.stop;
    }

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            if (instance != null) {
                return;
            }
            RecordContext.instance = this;
            instance.applicationContext = event.getApplicationContext();
            instance.recordHub = applicationContext.getBean(IRecordHub.class);

            instance.recordHub.init();

        } else if (event instanceof ContextClosedEvent) {
            shutdownBefore();
            shutdownAfter();
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }

    public synchronized static void shutdownBefore() {
        SchedulerContext.shutdown();
    }

    public static synchronized void shutdownAfter() {

        instance.stop = true;

        var manager = instance.recordHub;
        if (manager == null) {
            return;
        }

        try {
            instance.recordHub.getPersisterMap()
                    .values()
                    .stream()
                    .forEach(it -> it.persistAll());
            // 等待3秒，以防止缓存种有没有插入的record
            ThreadUtils.sleep(3 * TimeUtils.MILLIS_PER_SECOND);
            var field = RecordHub.class.getDeclaredField("restHighLevelClient");
            ReflectionUtils.makeAccessible(field);
            var restHighLevelClient = (RestHighLevelClient) ReflectionUtils.getField(field, manager);
            IOUtils.closeIO(restHighLevelClient);
        } catch (Exception e) {
            logger.error("Record failed shutdown: " + ExceptionUtils.getMessage(e));
            return;
        }

        logger.info("Record shutdown gracefully.");
    }
}
