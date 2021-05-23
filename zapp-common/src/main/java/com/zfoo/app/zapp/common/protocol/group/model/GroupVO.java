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
public class GroupVO implements IPacket {

    public static final transient short PROTOCOL_ID = 18000;

    private long id;

    private long adminId;

    private List<ChannelBoxVO> channelBoxes;

    private String name;

    private String avatar;

    private String background;

    private long createTime;

    private List<GroupAuthVO> groupAuths;

    public static GroupVO valueOf(long id, long adminId, List<ChannelBoxVO> channelBoxes, String name, String avatar, String background
            , long createTime, List<GroupAuthVO> groupAuths) {
        var vo = new GroupVO();
        vo.id = id;
        vo.adminId = adminId;
        vo.channelBoxes = channelBoxes;
        vo.name = name;
        vo.avatar = avatar;
        vo.background = background;
        vo.createTime = createTime;
        vo.groupAuths = groupAuths;
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

    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    public List<ChannelBoxVO> getChannelBoxes() {
        return channelBoxes;
    }

    public void setChannelBoxes(List<ChannelBoxVO> channelBoxes) {
        this.channelBoxes = channelBoxes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public List<GroupAuthVO> getGroupAuths() {
        return groupAuths;
    }

    public void setGroupAuths(List<GroupAuthVO> groupAuths) {
        this.groupAuths = groupAuths;
    }
}
