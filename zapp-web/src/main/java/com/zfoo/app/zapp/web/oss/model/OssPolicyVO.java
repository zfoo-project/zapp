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

package com.zfoo.app.zapp.web.oss.model;

/**
 * oss签名返回
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-21 11:34
 */
public class OssPolicyVO {

    /**
     * oss生成的协议
     */
    private String policy;
    /**
     * 访问码accessKey
     */
    private String accessKeyId;
    /**
     * 签名
     */
    private String signature;
    /**
     * 文件夹，路径
     */
    private String dir;
    /**
     * 地址
     */
    private String host;
    /**
     * 过期时间
     */
    private String expire;

    public static OssPolicyVO valueOf(String policy, String accessKeyId, String signature, String dir, String host, String expire) {
        var ossPolicy = new OssPolicyVO();
        ossPolicy.policy = policy;
        ossPolicy.accessKeyId = accessKeyId;
        ossPolicy.signature = signature;
        ossPolicy.dir = dir;
        ossPolicy.host = host;
        ossPolicy.expire = expire;
        return ossPolicy;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }
}
