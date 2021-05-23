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

import com.zfoo.net.core.gateway.IGatewayLoadBalancer;
import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-23 14:20
 */
public class CreateInviteGroupCodeRequest implements IPacket, IGatewayLoadBalancer {

    public static final transient short PROTOCOL_ID = 18410;

    private long groupId;

    /**
     * 邀请码的过期时间
     */
    private int expireType;

    /**
     * 邀请码可以邀请多少个人
     */
    private int countType;

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

    public int getExpireType() {
        return expireType;
    }

    public void setExpireType(int expireType) {
        this.expireType = expireType;
    }

    public int getCountType() {
        return countType;
    }

    public void setCountType(int countType) {
        this.countType = countType;
    }
}
