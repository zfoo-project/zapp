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

package com.zfoo.app.zapp.common.protocol.friend.model;

import com.zfoo.app.zapp.common.protocol.cache.model.UserCache;
import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-12-11 10:05
 */
public class ApplyFriendVO implements IPacket {

    public static final transient short PROTOCOL_ID = 15000;

    private long friendId;
    private int status;
    private long timestamp;

    private UserCache friendCache;

    public static ApplyFriendVO valueOf(long friendId, int status, long timestamp) {
        var vo = new ApplyFriendVO();
        vo.friendId = friendId;
        vo.status = status;
        vo.timestamp = timestamp;
        return vo;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }


    public long getFriendId() {
        return friendId;
    }

    public void setFriendId(long friendId) {
        this.friendId = friendId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public UserCache getFriendCache() {
        return friendCache;
    }

    public void setFriendCache(UserCache friendCache) {
        this.friendCache = friendCache;
    }
}
