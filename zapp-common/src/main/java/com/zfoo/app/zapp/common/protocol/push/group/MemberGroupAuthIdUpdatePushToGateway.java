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

import java.util.Set;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-27 15:41
 */
public class MemberGroupAuthIdUpdatePushToGateway implements IPacket {

    public static final transient short PROTOCOL_ID = 30221;

    private Set<Long> sidSet;

    private MemberGroupAuthIdUpdateNotice notice;

    public static MemberGroupAuthIdUpdatePushToGateway valueOf(Set<Long> sidSet, MemberGroupAuthIdUpdateNotice notice) {
        var push = new MemberGroupAuthIdUpdatePushToGateway();
        push.sidSet = sidSet;
        push.notice = notice;
        return push;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public Set<Long> getSidSet() {
        return sidSet;
    }

    public void setSidSet(Set<Long> sidSet) {
        this.sidSet = sidSet;
    }

    public MemberGroupAuthIdUpdateNotice getNotice() {
        return notice;
    }

    public void setNotice(MemberGroupAuthIdUpdateNotice notice) {
        this.notice = notice;
    }
}
