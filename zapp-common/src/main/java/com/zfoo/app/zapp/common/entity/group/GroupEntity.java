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

package com.zfoo.app.zapp.common.entity.group;

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.entity.group.model.*;
import com.zfoo.app.zapp.common.protocol.group.model.GroupVO;
import com.zfoo.orm.model.anno.EntityCache;
import com.zfoo.orm.model.anno.Id;
import com.zfoo.orm.model.anno.Persister;
import com.zfoo.orm.model.entity.IEntity;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-21 18:11
 */
@EntityCache(cacheStrategy = "tenThousand", persister = @Persister("time30s"))
public class GroupEntity implements IEntity<Long> {

    @Id
    private long id;

    private long vs;

    /**
     * 管理员的id，一般是创建者的id
     */
    private long adminId;

    private List<ChannelBoxPO> channelBoxes = new CopyOnWriteArrayList<>();

    private String name;

    private String avatar;

    private String background;

    /**
     * 群组的创建时间
     */
    private long createTime;

    /**
     * 群组里有哪些加入的用户，包含一个群里全部的用户，且这些人的拥有groupAuths的默认权限
     */
    private Set<Long> people = new CopyOnWriteArraySet<>();

    /**
     * 用户的身分组，包含又特定身份和权限的用户
     */
    private List<GroupAuthPO> groupAuths = new CopyOnWriteArrayList<>();

    /**
     * 邀请码
     */
    private List<InviteCodePO> inviteCodes = new CopyOnWriteArrayList<>();


    public GroupVO toGroupVO() {
        return GroupVO.valueOf(id, adminId, channelBoxes.stream().map(it -> it.toChannelBoxVO()).collect(Collectors.toList()), name
                , avatar, background, createTime, groupAuths.stream().map(it -> it.toGroupAuthVO()).collect(Collectors.toList()));
    }

    public long maxGroupAuthId() {
        return groupAuths.stream().map(it -> it.getId()).max((a, b) -> Long.compare(a, b)).orElse(AppConstant.GROUP_AUTH_DEFAULT_ID);
    }

    /**
     * 返回用户的身份信息
     */
    public List<Long> toGroupAuthIds(long memberId) {
        // 因为登录的时候忽略了people，所以这边就不不需要判断了
        // if (!people.contains(memberId)) {
        //     return Collections.EMPTY_LIST;
        // }

        var list = new ArrayList<Long>();
        list.add(groupAuths.get(0).getId());
        for (var groupAuthPO : groupAuths) {
            if (groupAuthPO.getMembers().contains(memberId)) {
                list.add(groupAuthPO.getId());
            }
        }
        return list;
    }

    /**
     * 获取一个组员的最大群组权限
     */
    public GroupAuthEnum memberOfMaxGroupAuth(long memberId) {
        // 如果这个人是创建者，则拥有管理员权限
        if (adminId == memberId) {
            return GroupAuthEnum.ADMIN_AUTH;
        }
        // 如果群组中不包括这个人，则没有任何的权限
        if (!people.contains(memberId)) {
            return GroupAuthEnum.NO_AUTH;
        }
        var auth = GroupAuthEnum.getAuthEnumByType(groupAuths.get(0).getGroupAuth());
        for (var groupAuthPO : groupAuths) {
            if (groupAuthPO.getMembers().contains(memberId) && groupAuthPO.getGroupAuth() > auth.getType()) {
                auth = GroupAuthEnum.getAuthEnumByType(groupAuthPO.getGroupAuth());
            }
        }
        return auth;
    }

    /**
     * 获取一个组员的最大群组权限
     */
    public ChannelAuthEnum memberOfMaxChannelAuth(long channelId, long memberId) {
        // 如果这个人是创建者，则拥有管理员权限
        if (adminId == memberId) {
            return ChannelAuthEnum.ADMIN_AUTH;
        }
        // 如果群组中不包括这个人，则没有任何的权限
        if (!people.contains(memberId)) {
            return ChannelAuthEnum.NO_AUTH;
        }
        var channelPO = findChannel(channelId);
        if (channelPO == null) {
            return ChannelAuthEnum.NO_AUTH;
        }

        var auth = ChannelAuthEnum.getAuthEnumByType(channelPO.getChannelAuths().get(0).getChannelAuth());
        for (var channelAuthPO : channelPO.getChannelAuths()) {
            var groupAuthPO = findGroupAuthPO(channelAuthPO.getId());
            if (groupAuthPO == null) {
                continue;
            }
            if (groupAuthPO.getMembers().contains(memberId) && channelAuthPO.getChannelAuth() > auth.getType()) {
                auth = ChannelAuthEnum.getAuthEnumByType(channelAuthPO.getChannelAuth());
            }
        }
        return auth;
    }

    @Nullable
    public GroupAuthPO findGroupAuthPO(long groupAuthId) {
        return groupAuths.stream().filter(it -> it.getId() == groupAuthId).findFirst().orElse(null);
    }

    @Nullable
    public ChannelPO findChannel(long channelId) {
        for (var channelBox : channelBoxes) {
            for (var channel : channelBox.getChannels()) {
                if (channel.getId() == channelId) {
                    return channel;
                }
            }
        }
        return null;
    }

    @Nullable
    public ChannelBoxPO findChannelBox(String channelBoxName) {
        return channelBoxes.stream().filter(it -> it.getName().equals(channelBoxName)).findFirst().orElse(null);
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public long gvs() {
        return vs;
    }

    @Override
    public void svs(long vs) {
        this.vs = vs;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVs() {
        return vs;
    }

    public void setVs(long vs) {
        this.vs = vs;
    }

    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    public List<ChannelBoxPO> getChannelBoxes() {
        return channelBoxes;
    }

    public void setChannelBoxes(List<ChannelBoxPO> channelBoxes) {
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

    public Set<Long> getPeople() {
        return people;
    }

    public void setPeople(Set<Long> people) {
        this.people = people;
    }

    public List<GroupAuthPO> getGroupAuths() {
        return groupAuths;
    }

    public void setGroupAuths(List<GroupAuthPO> groupAuths) {
        this.groupAuths = groupAuths;
    }

    public List<InviteCodePO> getInviteCodes() {
        return inviteCodes;
    }

    public void setInviteCodes(List<InviteCodePO> inviteCodes) {
        this.inviteCodes = inviteCodes;
    }
}
