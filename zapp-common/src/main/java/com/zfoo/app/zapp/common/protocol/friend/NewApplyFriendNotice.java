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

package com.zfoo.app.zapp.common.protocol.friend;

import com.zfoo.app.zapp.common.protocol.friend.model.ApplyFriendVO;
import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-18 10:46
 */
public class NewApplyFriendNotice implements IPacket {

    public static final transient short PROTOCOL_ID = 16001;

    private ApplyFriendVO applyFriendVO;

    public static NewApplyFriendNotice valueOf(ApplyFriendVO applyFriendVO) {
        var notification = new NewApplyFriendNotice();
        notification.applyFriendVO = applyFriendVO;
        return notification;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }


    public ApplyFriendVO getApplyFriendVO() {
        return applyFriendVO;
    }

    public void setApplyFriendVO(ApplyFriendVO applyFriendVO) {
        this.applyFriendVO = applyFriendVO;
    }
}
