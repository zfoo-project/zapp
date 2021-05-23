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

package com.zfoo.app.zapp.user.login.service;


import com.zfoo.protocol.model.Pair;
import org.springframework.lang.Nullable;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-10-28 15:29
 */
public interface ILoginService {


    /**
     * @return token
     */
    @Nullable
    String signInByAccount(String account, String password);

    /**
     * @return token + newUser
     */
    Pair<String, Boolean> signInByPhone(long phoneNumber);

    /**
     * 微信登录
     */
    Pair<String, Boolean> weChatSignIn(String unionid);

    /**
     * 微博登录
     */
    Pair<String, Boolean> weiBoSignIn(String weiBoUid);

}
