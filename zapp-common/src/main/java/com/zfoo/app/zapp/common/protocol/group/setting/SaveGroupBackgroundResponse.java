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

package com.zfoo.app.zapp.common.protocol.group.setting;

import com.zfoo.net.core.gateway.IGatewayLoadBalancer;
import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-23 14:20
 */
public class SaveGroupBackgroundResponse implements IPacket, IGatewayLoadBalancer {

    public static final transient short PROTOCOL_ID = 18203;

    private long groupId;

    private String background;

    public static SaveGroupBackgroundResponse valueOf(long groupId, String background) {
        var response = new SaveGroupBackgroundResponse();
        response.groupId = groupId;
        response.background = background;
        return response;
    }

    @Override
    public Object loadBalancerConsistentHashObject() {
        return groupId;
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

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }
}
