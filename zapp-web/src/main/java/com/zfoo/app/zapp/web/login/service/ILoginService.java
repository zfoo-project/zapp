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

package com.zfoo.app.zapp.web.login.service;

import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.model.Triple;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-25 18:31
 */
public interface ILoginService {

    Pair<String, String> createCaptcha();

    Pair<Long, Integer> createPhoneVerifyCode(long phoneNumber);

    Pair<String, Long> createBind(long userId);

    Pair<String, Triple<String, String, String>> createWeChatUnion(String openid, String unionid, String weChatAccessToken);

    Pair<String, Pair<String, String>> createWeiBoUnion(String unionid, String weiBoAccessToken);

}
