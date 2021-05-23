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

package com.zfoo.app.zapp.common.protocol.user.weibo;

import com.zfoo.protocol.IPacket;


/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-10-15 17:55
 */
public class WeiBoSignInAnswer implements IPacket {

    public static final transient short PROTOCOL_ID = 1081;

    private String token;

    private boolean newUser;

    public static WeiBoSignInAnswer valueOf(String token, boolean newUser) {
        var answer = new WeiBoSignInAnswer();
        answer.token = token;
        answer.newUser = newUser;
        return answer;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }
}
