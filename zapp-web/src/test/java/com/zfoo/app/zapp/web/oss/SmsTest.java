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

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.zfoo.app.zapp.web.login.model.sms.SmsData;
import com.zfoo.protocol.util.JsonUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-26 11:30
 */
@Ignore
public class SmsTest {

    @Test
    public void sendMessageTest() {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", OssClientTest.accessKeyId, OssClientTest.accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", "85300503750");
        request.putQueryParameter("SignName", "浊浮zfoo");
        request.putQueryParameter("TemplateCode", "SMS_190794423");
        request.putQueryParameter("TemplateParam", "{ code: 111111 }");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
            var sms = JsonUtils.string2Object(response.getData(), SmsData.class);
            System.out.println(sms.getMessage());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

}
