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

package com.zfoo.app.zapp.common.protocol.push.friend;

import com.zfoo.app.zapp.common.protocol.friend.EditFriendMessageNotice;
import com.zfoo.protocol.IPacket;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-13 18:41
 */
public class EditFriendMessagePush implements IPacket {

    public static final transient short PROTOCOL_ID = 30109;

    private List<Long> targetIdList;

    private EditFriendMessageNotice notice;

    public static EditFriendMessagePush valueOf(List<Long> targetIdList, EditFriendMessageNotice notice) {
        var push = new EditFriendMessagePush();
        push.targetIdList = targetIdList;
        push.notice = notice;
        return push;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public List<Long> getTargetIdList() {
        return targetIdList;
    }

    public void setTargetIdList(List<Long> targetIdList) {
        this.targetIdList = targetIdList;
    }

    public EditFriendMessageNotice getNotice() {
        return notice;
    }

    public void setNotice(EditFriendMessageNotice notice) {
        this.notice = notice;
    }
}
