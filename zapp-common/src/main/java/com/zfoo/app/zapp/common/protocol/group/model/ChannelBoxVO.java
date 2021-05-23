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

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-22 11:43
 */
public class ChannelBoxVO implements IPacket {

    public static final transient short PROTOCOL_ID = 18011;

    private String name;
    private List<ChannelVO> channels;

    public static ChannelBoxVO valueOf(String name, List<ChannelVO> channels) {
        var vo = new ChannelBoxVO();
        vo.name = name;
        vo.channels = channels;
        return vo;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChannelVO> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelVO> channels) {
        this.channels = channels;
    }
}
