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

package com.zfoo.app.zapp.common.protocol.cache;

import com.zfoo.app.zapp.common.protocol.cache.model.UserCache;
import com.zfoo.protocol.IPacket;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-04 21:44
 */
public class SearchUserResponse implements IPacket {

    public static final transient short PROTOCOL_ID = 3031;

    private List<UserCache> userCaches;

    public static SearchUserResponse valueOf(List<UserCache> userCaches) {
        var response = new SearchUserResponse();
        response.userCaches = userCaches;
        return response;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public List<UserCache> getUserCaches() {
        return userCaches;
    }

    public void setUserCaches(List<UserCache> userCaches) {
        this.userCaches = userCaches;
    }
}
