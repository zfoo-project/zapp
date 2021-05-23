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

package com.zfoo.app.zapp.common.protocol.group.model;

import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-05 12:35
 */
public class ChannelAuthVO implements IPacket {

    public static final transient short PROTOCOL_ID = 18012;

    /**
     * 对应于GroupAuthPO里的id
     */
    private long id;

    /**
     * 频道权限
     */
    private int channelAuth;

    public static ChannelAuthVO valueOf(long id, int channelAuth) {
        var vo = new ChannelAuthVO();
        vo.id = id;
        vo.channelAuth = channelAuth;
        return vo;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getChannelAuth() {
        return channelAuth;
    }

    public void setChannelAuth(int channelAuth) {
        this.channelAuth = channelAuth;
    }

}
