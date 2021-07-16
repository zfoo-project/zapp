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

package com.zfoo.record.schema;

import com.zfoo.protocol.util.DomUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.record.RecordContext;
import com.zfoo.record.manager.RecordHub;
import com.zfoo.record.model.config.*;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;


/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-06-02 10:13
 */
public class RecordDefinitionParser implements BeanDefinitionParser {

    @Override
    public AbstractBeanDefinition parse(Element element, ParserContext parserContext) {
        Class<?> clazz;
        String name;
        BeanDefinitionBuilder builder;

        // 注册NetConfig
        parseRecordConfig(element, parserContext);

        // 注册RecordContext
        clazz = RecordContext.class;
        name = StringUtils.uncapitalize(clazz.getSimpleName());
        builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);
        parserContext.getRegistry().registerBeanDefinition(name, builder.getBeanDefinition());

        // 注册RecordHub
        clazz = RecordHub.class;
        name = StringUtils.uncapitalize(clazz.getSimpleName());
        builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);
        builder.addPropertyReference("recordConfig", RecordConfig.class.getCanonicalName());
        parserContext.getRegistry().registerBeanDefinition(name, builder.getBeanDefinition());

        return builder.getBeanDefinition();
    }


    private void parseRecordConfig(Element element, ParserContext parserContext) {
        var clazz = RecordConfig.class;
        var builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);

        resolvePlaceholder("id", "id", builder, element, parserContext);
        resolvePlaceholder("record-package", "recordPackage", builder, element, parserContext);

        parseHostConfig(DomUtils.getFirstChildElementByTagName(element, "host"), parserContext);
        builder.addPropertyReference("hostConfig", HostConfig.class.getCanonicalName());

        parseCaches(DomUtils.getFirstChildElementByTagName(element, "caches"), parserContext);
        builder.addPropertyReference("cachesConfig", CachesConfig.class.getCanonicalName());

        parsePersisters(DomUtils.getFirstChildElementByTagName(element, "persisters"), parserContext);
        builder.addPropertyReference("persistersConfig", PersistersConfig.class.getCanonicalName());

        parserContext.getRegistry().registerBeanDefinition(clazz.getCanonicalName(), builder.getBeanDefinition());
    }


    private void parseHostConfig(Element element, ParserContext parserContext) {
        // 解析host标签
        var clazz = HostConfig.class;
        var builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);

        resolvePlaceholder("cluster", "cluster", builder, element, parserContext);
        resolvePlaceholder("user", "user", builder, element, parserContext);
        resolvePlaceholder("password", "password", builder, element, parserContext);

        var addressMap = parseAddress(element, parserContext);
        builder.addPropertyValue("addressMap", addressMap);

        parserContext.getRegistry().registerBeanDefinition(clazz.getCanonicalName(), builder.getBeanDefinition());
    }


    private void parseCaches(Element element, ParserContext parserContext) {
        // 解析caches标签
        var clazz = CachesConfig.class;
        var builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);

        var cacheStrategies = parseCacheStrategies(element, parserContext);
        builder.addPropertyValue("cacheStrategies", cacheStrategies);
        parserContext.getRegistry().registerBeanDefinition(clazz.getCanonicalName(), builder.getBeanDefinition());
    }


    private void parsePersisters(Element element, ParserContext parserContext) {
        // 解析persisters标签
        var clazz = PersistersConfig.class;
        var builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);

        var persisterStrategies = parsePersisterStrategies(element, parserContext);
        builder.addPropertyValue("persisterStrategies", persisterStrategies);
        parserContext.getRegistry().registerBeanDefinition(clazz.getCanonicalName(), builder.getBeanDefinition());
    }


    private ManagedList<BeanDefinitionHolder> parseCacheStrategies(Element element, ParserContext parserContext) {
        var cacheStrategiesElementList = DomUtils.getChildElementsByTagName(element, "cache");
        var cacheStrategies = new ManagedList<BeanDefinitionHolder>();
        var environment = parserContext.getReaderContext().getEnvironment();
        for (var i = 0; i < cacheStrategiesElementList.size(); i++) {
            var addressElement = cacheStrategiesElementList.get(i);
            var clazz = CacheStrategy.class;
            var builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);

            builder.addConstructorArgValue(environment.resolvePlaceholders(addressElement.getAttribute("strategy")));
            builder.addConstructorArgValue(environment.resolvePlaceholders(addressElement.getAttribute("max-size")));

            cacheStrategies.add(new BeanDefinitionHolder(builder.getBeanDefinition(), StringUtils.format("{}.{}", clazz.getCanonicalName(), i)));
        }
        return cacheStrategies;
    }

    private ManagedList<BeanDefinitionHolder> parsePersisterStrategies(Element element, ParserContext parserContext) {
        var persisterStrategiesElementList = DomUtils.getChildElementsByTagName(element, "persister");
        var persisterStrategies = new ManagedList<BeanDefinitionHolder>();
        var environment = parserContext.getReaderContext().getEnvironment();
        for (var i = 0; i < persisterStrategiesElementList.size(); i++) {
            var addressElement = persisterStrategiesElementList.get(i);
            var clazz = PersisterStrategy.class;
            var builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);

            builder.addConstructorArgValue(environment.resolvePlaceholders(addressElement.getAttribute("strategy")));
            builder.addConstructorArgValue(environment.resolvePlaceholders(addressElement.getAttribute("type")));
            builder.addConstructorArgValue(environment.resolvePlaceholders(addressElement.getAttribute("config")));

            persisterStrategies.add(new BeanDefinitionHolder(builder.getBeanDefinition(), StringUtils.format("{}.{}", clazz.getCanonicalName(), i)));
        }
        return persisterStrategies;
    }

    private ManagedMap<String, String> parseAddress(Element element, ParserContext parserContext) {
        var addressElementList = DomUtils.getChildElementsByTagName(element, "address");
        var addressMap = new ManagedMap<String, String>();

        for (var addressElement : addressElementList) {
            var name = addressElement.getAttribute("name");
            var urlAttribute = addressElement.getAttribute("url");
            var url = parserContext.getReaderContext().getEnvironment().resolvePlaceholders(urlAttribute);
            addressMap.put(name, url);
        }
        return addressMap;
    }

    private void resolvePlaceholder(String attributeName, String fieldName, BeanDefinitionBuilder builder, Element element, ParserContext parserContext) {
        var attributeValue = element.getAttribute(attributeName);
        var environment = parserContext.getReaderContext().getEnvironment();
        var placeholder = environment.resolvePlaceholders(attributeValue);
        builder.addPropertyValue(fieldName, placeholder);
    }

}
