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

package com.zfoo.app.zapp.common.protocol.group.member;

import com.zfoo.app.zapp.common.protocol.group.member.model.GroupMemberVO;
import com.zfoo.protocol.IPacket;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-08 14:20
 */
public class GroupMemberInfoResponse implements IPacket {

    public static final transient short PROTOCOL_ID = 18433;

    private long groupId;
    private List<GroupMemberVO> members;

    public static GroupMemberInfoResponse valueOf(long groupId, List<GroupMemberVO> members) {
        var response = new GroupMemberInfoResponse();
        response.groupId = groupId;
        response.members = members;
        return response;
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

    public List<GroupMemberVO> getMembers() {
        return members;
    }

    public void setMembers(List<GroupMemberVO> members) {
        this.members = members;
    }
}
