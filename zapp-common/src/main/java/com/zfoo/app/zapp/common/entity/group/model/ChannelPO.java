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

import com.zfoo.app.zapp.common.protocol.group.model.ChannelVO;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-22 11:14
 */
public class ChannelPO {

    /**
     * 频道的id
     */
    private long id;

    private String name;

    private long refreshTime;

    private List<ChannelAuthPO> channelAuths = new CopyOnWriteArrayList<>();

    public static ChannelPO valueOf(long id, String name, List<ChannelAuthPO> channelAuths) {
        var po = new ChannelPO();
        po.id = id;
        po.name = name;
        po.channelAuths = channelAuths;
        return po;
    }

    public ChannelVO toChannelVO() {
        return ChannelVO.valueOf(id, name, refreshTime, channelAuths.stream().map(it -> it.toChannelAuthVO()).collect(Collectors.toList()));
    }

    @Nullable
    public ChannelAuthPO findChannelAuthPO(long id) {
        return channelAuths.stream().filter(it -> it.getId() == id).findFirst().orElse(null);
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

    public List<ChannelAuthPO> getChannelAuths() {
        return channelAuths;
    }

    public void setChannelAuths(List<ChannelAuthPO> channelAuths) {
        this.channelAuths = channelAuths;
    }
}
