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

import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-12-11 10:05
 */
public class FriendInfoVO implements IPacket {

    public static final transient short PROTOCOL_ID = 15001;

    private long friendId;
    private long refreshTime;
    private long readTime;
    private String tag;

    public static FriendInfoVO valueOf(long friendId, long refreshTime, long readTime, String tag) {
        var vo = new FriendInfoVO();
        vo.friendId = friendId;
        vo.refreshTime = refreshTime;
        vo.readTime = readTime;
        vo.tag = tag;
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

    public long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public long getReadTime() {
        return readTime;
    }

    public void setReadTime(long readTime) {
        this.readTime = readTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
