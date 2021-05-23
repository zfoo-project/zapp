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

package com.zfoo.app.zapp.common.protocol.push.group;

import com.zfoo.app.zapp.common.protocol.group.MemberGroupAuthIdUpdateNotice;
import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-27 11:41
 */
public class MemberGroupAuthIdUpdatePush implements IPacket {

    public static final transient short PROTOCOL_ID = 30220;

    private long memberId;

    private MemberGroupAuthIdUpdateNotice notice;

    public static MemberGroupAuthIdUpdatePush valueOf(long memberId, MemberGroupAuthIdUpdateNotice notice) {
        var push = new MemberGroupAuthIdUpdatePush();
        push.memberId = memberId;
        push.notice = notice;
        return push;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public MemberGroupAuthIdUpdateNotice getNotice() {
        return notice;
    }

    public void setNotice(MemberGroupAuthIdUpdateNotice notice) {
        this.notice = notice;
    }
}
