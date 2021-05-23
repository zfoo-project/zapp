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

import com.zfoo.app.zapp.web.oss.service.IOssService;
import com.zfoo.protocol.exception.ExceptionUtils;
import com.zfoo.util.ThreadUtils;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

import java.util.concurrent.Executor;

/**
 * kill -2 和 kill -15 才能触发这个回调方法
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-03-10 19:43
 */
@Configuration
public class GracefulShutdownConfig implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(GracefulShutdownConfig.class);

    private volatile Connector connector;

    @Autowired
    private IOssService ossService;

    @Bean
    public Connector redirectConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        //监听http的端口号
        connector.setPort(80);
        connector.setSecure(false);
        //监听到http的端口号后转向到的https的端口号
        connector.setRedirectPort(443);
        return connector;
    }

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint constraint = new SecurityConstraint();
                constraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                constraint.addCollection(collection);
                context.addConstraint(constraint);
            }
        };
        tomcat.addConnectorCustomizers(this);
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        logger.info("Deploy graceful shutdown config successfully!");
        return tomcat;
    }

    @Override
    public void customize(Connector connector) {
        this.connector = connector;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        // 关闭Tomcat线程池
        this.shutdownTomcat();

        // 关闭Oss
        ossService.ossClient().shutdown();
    }

    private void shutdownTomcat() {
        // 关闭Tomcat线程池
        try {
            this.connector.pause();
            Executor executor = this.connector.getProtocolHandler().getExecutor();
            if (executor instanceof ThreadPoolExecutor) {
                var threadPoolExecutor = (ThreadPoolExecutor) executor;
                ThreadUtils.shutdown(threadPoolExecutor);
            }
        } catch (Throwable e) {
            logger.error("Tomcat thread pool failed shutdown: " + ExceptionUtils.getMessage(e));
            return;
        }
        logger.info("Tomcat shutdown gracefully.");
    }


}
