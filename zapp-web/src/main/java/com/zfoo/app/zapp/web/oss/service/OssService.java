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

package com.zfoo.app.zapp.web.oss.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.zfoo.app.zapp.common.model.OssPolicyEnum;
import com.zfoo.app.zapp.web.oss.model.OssPolicyVO;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;

@Component
public class OssService implements IOssService {

    @Value("${aliyun.filesystem.accessKey}")
    private String accessKey;
    @Value("${aliyun.filesystem.secretKey}")
    private String secretKey;
    @Value("${aliyun.filesystem.endpoint}")
    private String endpoint;

    private OSS ossClient;

    @PostConstruct
    public void initOss() {
        this.ossClient = new OSSClientBuilder().build(endpoint, accessKey, secretKey);
    }

    @Override
    public OSS ossClient() {
        return this.ossClient;
    }

    @Override
    public OssPolicyVO policy(long uid, OssPolicyEnum ossPolicyEnum) {
        long expireEndTime = TimeUtils.now() + ossPolicyEnum.getExpireTime();
        var expiration = new Date(expireEndTime);
        var policyConditions = new PolicyConditions();

        // 使用uid做前缀，防止互相覆盖文件
        var dir = ossPolicyEnum.toDir(uid);
        policyConditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, ossPolicyEnum.getMaxFileSize());
        policyConditions.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

        var postPolicy = ossClient.generatePostPolicy(expiration, policyConditions);
        var binaryData = StringUtils.bytes(postPolicy);
        var encodedPolicy = Base64.getEncoder().encodeToString(binaryData);
        var postSignature = ossClient.calculatePostSignature(postPolicy);

        var ossPolicy = OssPolicyVO.valueOf(encodedPolicy, accessKey, postSignature, dir, ossPolicyEnum.getUrl(), String.valueOf(expireEndTime / 1000));
        return ossPolicy;
    }

}
