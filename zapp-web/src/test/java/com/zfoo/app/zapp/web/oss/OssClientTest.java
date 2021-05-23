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

package com.zfoo.app.zapp.web.oss;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClientBuilder;
import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-21 10:04
 */
@Ignore
public class OssClientTest {

    public static final String accessKeyId = "xxxxxxxxxxxxxxxxx";
    public static final String accessKeySecret = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    public static final String endpoint = "http://oss-cn-shanghai.aliyuncs.com";
    public static final String bucketName = "zfoo-report";

    @Test
    public void uploadTest() throws IOException {
        var fileName = "app.xml";
        var fileInputStream = ClassUtils.getFileFromClassPath(fileName);

        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        var objectName = StringUtils.format("test/{}", fileName);

        // 创建OSSClient实例。
        var ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 上传内容到指定的存储空间（bucketName）并保存为指定的文件名称（objectName）。
        ossClient.putObject(bucketName, objectName, fileInputStream);

        // 关闭OSSClient。
        ossClient.shutdown();

    }

    @Test
    public void signVisitTest() {
        var ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        var date = new Date(TimeUtils.currentTimeMillis() + 100_000);
        var url = ossClient.generatePresignedUrl(bucketName, "test/temp/img/20200910/9_FwEWW6VFEqRf0frNmgMNUKcjFE7MZxAg_14105917_1", date, HttpMethod.GET);
        System.out.println(url);
        ossClient.shutdown();
    }

}
