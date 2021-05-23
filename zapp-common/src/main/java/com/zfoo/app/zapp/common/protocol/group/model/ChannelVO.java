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
public class ChannelVO implements IPacket {

    public static final transient short PROTOCOL_ID = 18010;

    private long id;
    private String name;
    private long refreshTime;
    private List<ChannelAuthVO> channelAuths;

    public static ChannelVO valueOf(long id, String name, long refreshTime, List<ChannelAuthVO> channelAuths) {
        var vo = new ChannelVO();
        vo.id = id;
        vo.name = name;
        vo.refreshTime = refreshTime;
        vo.channelAuths = channelAuths;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public List<ChannelAuthVO> getChannelAuths() {
        return channelAuths;
    }

    public void setChannelAuths(List<ChannelAuthVO> channelAuths) {
        this.channelAuths = channelAuths;
    }
}
