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

package com.zfoo.app.zapp.common.protocol.group.model;

import com.zfoo.protocol.IPacket;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-04 19:59
 */
public class GroupMemberSimpleVO implements IPacket {

    public static final transient short PROTOCOL_ID = 18013;

    private long groupId;

    private long memberId;

    private GroupTimeVO groupTime;

    private List<Long> groupAuthIds;

    public static GroupMemberSimpleVO valueOf(long groupId, long memberId, GroupTimeVO groupTime, List<Long> groupAuthIds) {
        var vo = new GroupMemberSimpleVO();
        vo.groupId = groupId;
        vo.memberId = memberId;
        vo.groupTime = groupTime;
        vo.groupAuthIds = groupAuthIds;
        return vo;
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

    public GroupTimeVO getGroupTime() {
        return groupTime;
    }

    public void setGroupTime(GroupTimeVO groupTime) {
        this.groupTime = groupTime;
    }

    public List<Long> getGroupAuthIds() {
        return groupAuthIds;
    }

    public void setGroupAuthIds(List<Long> groupAuthIds) {
        this.groupAuthIds = groupAuthIds;
    }
}
