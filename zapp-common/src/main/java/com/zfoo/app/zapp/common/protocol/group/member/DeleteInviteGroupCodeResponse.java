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

import com.zfoo.app.zapp.common.protocol.group.member.model.InviteCodeVO;
import com.zfoo.protocol.IPacket;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-23 14:20
 */
public class DeleteInviteGroupCodeResponse implements IPacket {

    public static final transient short PROTOCOL_ID = 18421;

    private List<InviteCodeVO> inviteCodes;

    public static DeleteInviteGroupCodeResponse valueOf(List<InviteCodeVO> inviteCodes) {
        var response = new DeleteInviteGroupCodeResponse();
        response.inviteCodes = inviteCodes;
        return response;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public List<InviteCodeVO> getInviteCodes() {
        return inviteCodes;
    }

    public void setInviteCodes(List<InviteCodeVO> inviteCodes) {
        this.inviteCodes = inviteCodes;
    }
}
