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

package com.zfoo.app.zapp.common.protocol.user;

import com.zfoo.app.zapp.common.protocol.cache.model.UserCache;
import com.zfoo.app.zapp.common.protocol.friend.model.ApplyFriendVO;
import com.zfoo.app.zapp.common.protocol.friend.model.FriendInfoVO;
import com.zfoo.app.zapp.common.protocol.group.model.GroupMemberSimpleVO;
import com.zfoo.app.zapp.common.protocol.group.model.GroupVO;
import com.zfoo.protocol.IPacket;

import java.util.List;
import java.util.Map;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-10-15 17:55
 */
public class WebsocketSignInResponse implements IPacket {

    public static final transient short PROTOCOL_ID = 1001;

    private long userId;

    private List<UserCache> friends;
    private List<UserCache> blacklist;
    private List<ApplyFriendVO> applyFriends;

    private Map<Long, FriendInfoVO> friendInfoMap;

    /**
     * 用户拥有的所有群组信息
     */
    private List<GroupVO> groups;
    /**
     * 用户所拥有的群组对应的权限信息
     */
    private List<GroupMemberSimpleVO> groupMemberSimpleVOs;

    public static WebsocketSignInResponse valueOf(long userId
            , List<UserCache> friends, List<UserCache> blacklist, List<ApplyFriendVO> applyFriends
            , Map<Long, FriendInfoVO> friendInfoMap, List<GroupVO> groups, List<GroupMemberSimpleVO> groupMemberSimpleVOs) {
        var response = new WebsocketSignInResponse();
        response.userId = userId;
        response.friends = friends;
        response.blacklist = blacklist;
        response.applyFriends = applyFriends;
        response.friendInfoMap = friendInfoMap;
        response.groups = groups;
        response.groupMemberSimpleVOs = groupMemberSimpleVOs;
        return response;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<UserCache> getFriends() {
        return friends;
    }

    public void setFriends(List<UserCache> friends) {
        this.friends = friends;
    }

    public List<ApplyFriendVO> getApplyFriends() {
        return applyFriends;
    }

    public void setApplyFriends(List<ApplyFriendVO> applyFriends) {
        this.applyFriends = applyFriends;
    }

    public List<GroupVO> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupVO> groups) {
        this.groups = groups;
    }

    public Map<Long, FriendInfoVO> getFriendInfoMap() {
        return friendInfoMap;
    }

    public void setFriendInfoMap(Map<Long, FriendInfoVO> friendInfoMap) {
        this.friendInfoMap = friendInfoMap;
    }

    public List<UserCache> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<UserCache> blacklist) {
        this.blacklist = blacklist;
    }

    public List<GroupMemberSimpleVO> getGroupMemberSimpleVOs() {
        return groupMemberSimpleVOs;
    }

    public void setGroupMemberSimpleVOs(List<GroupMemberSimpleVO> groupMemberSimpleVOs) {
        this.groupMemberSimpleVOs = groupMemberSimpleVOs;
    }
}
