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

package com.zfoo.app.zapp.common.protocol.user.group;

import com.zfoo.app.zapp.common.protocol.group.model.GroupVO;
import com.zfoo.protocol.IPacket;

/**
 * 创建群组
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-22 13:20
 */
public class CreateGroupResponse implements IPacket {

    public static final transient short PROTOCOL_ID = 1301;

    private GroupVO group;

    public static CreateGroupResponse valueOf(GroupVO group) {
        var response = new CreateGroupResponse();
        response.group = group;
        return response;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public GroupVO getGroup() {
        return group;
    }

    public void setGroup(GroupVO group) {
        this.group = group;
    }
}
