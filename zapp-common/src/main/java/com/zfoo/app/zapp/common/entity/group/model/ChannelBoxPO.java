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

package com.zfoo.app.zapp.common.entity.group.model;

import com.zfoo.app.zapp.common.protocol.group.model.ChannelBoxVO;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-22 14:40
 */
public class ChannelBoxPO {

    private String name;

    private List<ChannelPO> channels = new CopyOnWriteArrayList<>();

    public static ChannelBoxPO valueOf(String name, List<ChannelPO> channels) {
        var po = new ChannelBoxPO();
        po.name = name;
        po.channels = channels;
        return po;
    }

    public ChannelBoxVO toChannelBoxVO() {
        var vo = new ChannelBoxVO();
        vo.setName(name);
        vo.setChannels(channels.stream().map(it -> it.toChannelVO()).collect(Collectors.toList()));
        return vo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChannelPO> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelPO> channels) {
        this.channels = channels;
    }
}
