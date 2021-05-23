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

package com.zfoo.app.zapp.common.protocol.group.member.model;

import com.zfoo.app.zapp.common.protocol.cache.model.UserCache;
import com.zfoo.protocol.IPacket;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-05 12:35
 */
public class GroupMemberVO implements IPacket {

    public static final transient short PROTOCOL_ID = 18401;

    /**
     * 用户在群里的身份
     */
    private List<Long> groupAuthIds;
    /**
     * 用户的信息
     */
    private UserCache userCache;

    public static GroupMemberVO valueOf(List<Long> groupAuthIds, UserCache userCache) {
        var vo = new GroupMemberVO();
        vo.groupAuthIds = groupAuthIds;
        vo.userCache = userCache;
        return vo;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public List<Long> getGroupAuthIds() {
        return groupAuthIds;
    }

    public void setGroupAuthIds(List<Long> groupAuthIds) {
        this.groupAuthIds = groupAuthIds;
    }

    public UserCache getUserCache() {
        return userCache;
    }

    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }
}
