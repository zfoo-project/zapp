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

package com.zfoo.app.zapp.common.protocol.group.channel;

import com.zfoo.app.zapp.common.protocol.group.model.GroupVO;
import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-23 15:33
 */
public class SaveChannelResponse implements IPacket {

    public static final transient short PROTOCOL_ID = 18309;

    private GroupVO groupVO;

    public static SaveChannelResponse valueOf(GroupVO groupVO) {
        var response = new SaveChannelResponse();
        response.groupVO = groupVO;
        return response;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public GroupVO getGroupVO() {
        return groupVO;
    }

    public void setGroupVO(GroupVO groupVO) {
        this.groupVO = groupVO;
    }
}
