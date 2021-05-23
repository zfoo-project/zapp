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

package com.zfoo.app.zapp.common.protocol.group;

import com.zfoo.protocol.IPacket;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-23 14:20
 */
public class MemberGroupAuthIdUpdateNotice implements IPacket {

    public static final transient short PROTOCOL_ID = 19002;

    private long groupId;

    private long memberId;

    private List<Long> groupAuthIds;

    public static MemberGroupAuthIdUpdateNotice valueOf(long groupId, long memberId, List<Long> groupAuthIds) {
        var notice = new MemberGroupAuthIdUpdateNotice();
        notice.groupId = groupId;
        notice.memberId = memberId;
        notice.groupAuthIds = groupAuthIds;
        return notice;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public List<Long> getGroupAuthIds() {
        return groupAuthIds;
    }

    public void setGroupAuthIds(List<Long> groupAuthIds) {
        this.groupAuthIds = groupAuthIds;
    }
}
