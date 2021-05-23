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
 * @since 2020-04-22 10:23
 */
public class ChannelTimeVO implements IPacket {

    public static final transient short PROTOCOL_ID = 18015;

    private long channelId;

    private boolean mute;

    private long refreshTime;

    public static ChannelTimeVO valueOf(long channelId, boolean mute, long refreshTime) {
        var po = new ChannelTimeVO();
        po.channelId = channelId;
        po.mute = mute;
        po.refreshTime = refreshTime;
        return po;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }


    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

}
