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

package com.zfoo.app.zapp.common.entity.user.model;

import com.zfoo.app.zapp.common.protocol.group.model.GroupTimeVO;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-22 10:23
 */
public class GroupTimePO {

    private long groupId;

    private boolean mute;

    private List<ChannelTimePO> channelTimes = new CopyOnWriteArrayList<>();

    public static GroupTimePO valueOf(long groupId) {
        var po = new GroupTimePO();
        po.groupId = groupId;
        return po;
    }

    public GroupTimeVO toGroupTimeVO() {
        return GroupTimeVO.valueOf(groupId, mute, channelTimes.stream().map(it -> it.toChannelTimeVO()).collect(Collectors.toList()));
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public List<ChannelTimePO> getChannelTimes() {
        return channelTimes;
    }

    public void setChannelTimes(List<ChannelTimePO> channelTimes) {
        this.channelTimes = channelTimes;
    }

}
