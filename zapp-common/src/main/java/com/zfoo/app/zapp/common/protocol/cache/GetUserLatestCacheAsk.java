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

import com.zfoo.protocol.IPacket;

import java.util.Set;

/**
 * 获取用户的最新缓存数据，会使缓存失效，重新加载缓存
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-25 18:15
 */
public class GetUserLatestCacheAsk implements IPacket {

    public static final transient short PROTOCOL_ID = 3027;

    private Set<Long> userIds;

    public static GetUserLatestCacheAsk valueOf(Set<Long> userIds) {
        var ask = new GetUserLatestCacheAsk();
        ask.userIds = userIds;
        return ask;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }


    public Set<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Long> userIds) {
        this.userIds = userIds;
    }
}
