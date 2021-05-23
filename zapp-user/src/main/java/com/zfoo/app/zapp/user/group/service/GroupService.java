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

package com.zfoo.app.zapp.user.group.service;

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.entity.group.ChannelEntity;
import com.zfoo.app.zapp.common.entity.group.GroupEntity;
import com.zfoo.app.zapp.common.entity.group.model.*;
import com.zfoo.app.zapp.common.entity.user.UserEntity;
import com.zfoo.app.zapp.common.entity.user.model.GroupTimePO;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-22 14:14
 */
@Component
public class GroupService implements IGroupService {


    @Override
    public GroupEntity createGroup(UserEntity userEntity, long groupId, @Nullable String groupName, @Nullable String avatar) {
        var userId = userEntity.getId();

        groupName = StringUtils.isBlank(groupName) ? userEntity.getName() + AppConstant.GROUP_DEFAULT_GROUP_NAME_SUFFIX : groupName;


        // 创建一个用户的公共群群，群组的id为用户id的负数
        var groupEntity = new GroupEntity();
        groupEntity.setId(groupId);
        groupEntity.setName(groupName);
        groupEntity.setAvatar(avatar);
        groupEntity.setAdminId(userId);
        groupEntity.getPeople().add(userId);
        groupEntity.setCreateTime(TimeUtils.now());

        // 默认身份
        var defaultGroupAuth = GroupAuthPO.defaultGroupAuthPO();
        groupEntity.setGroupAuths(new ArrayList<>(List.of(defaultGroupAuth)));

        // 创建一个公共频道
        var commonChannelId = MongoIdUtils.getIncrementIdFromMongoDefault(ChannelEntity.class);
        var commonChannelEntity = new ChannelEntity();
        commonChannelEntity.setId(commonChannelId);
        var commonChannelPO = ChannelPO.valueOf(commonChannelId, AppConstant.GROUP_COMMON_CHANNEL_CN_NAME
                , new ArrayList<>(List.of(ChannelAuthPO.valueOf(defaultGroupAuth.getId(), ChannelAuthEnum.MIDDLE_AUTH.getType()))));
        var commonChannelBoxPO = ChannelBoxPO.valueOf(AppConstant.GROUP_MAIN_CHANNEL_BOX, new ArrayList<>(List.of(commonChannelPO)));


        // 创建一个默认的频道
        var defaultChannelId = MongoIdUtils.getIncrementIdFromMongoDefault(ChannelEntity.class);
        var defaultChannelEntity = new ChannelEntity();
        defaultChannelEntity.setId(defaultChannelId);
        var defaultChannelPO = ChannelPO.valueOf(defaultChannelId, AppConstant.GROUP_DEFAULT_CHANNEL_CN_NAME
                , new ArrayList<>(List.of(ChannelAuthPO.valueOf(defaultGroupAuth.getId(), ChannelAuthEnum.BASE_AUTH.getType()))));
        var defaultChannelBoxPO = ChannelBoxPO.valueOf(AppConstant.GROUP_DEFAULT_PARENT_CHANNEL_CN_NAME, new ArrayList<>(List.of(defaultChannelPO)));


        // 每一个group都有一个主要的分组，放在列表的最前方
        groupEntity.getChannelBoxes().add(commonChannelBoxPO);
        groupEntity.getChannelBoxes().add(defaultChannelBoxPO);

        OrmContext.getAccessor().insert(commonChannelEntity);
        OrmContext.getAccessor().insert(defaultChannelEntity);
        OrmContext.getAccessor().insert(groupEntity);

        userEntity.getGroups().add(GroupTimePO.valueOf(groupId));

        // 新用户默认都添加官方账号
        userEntity.getGroups().add(GroupTimePO.valueOf(AppConstant.ZFOO_GROUP_ID));

        return groupEntity;
    }

    @Override
    public void perfectChannelTime(UserEntity userEntity, long groupId, List<Long> allChannelIds) {
        var groups = userEntity.getGroups();
        var groupTimeOptional = groups.stream().filter(it -> it.getGroupId() == groupId).findFirst();
        if (groupTimeOptional.isEmpty()) {
            return;
        }

        var channelIdSet = new HashSet<Long>(allChannelIds);
        var groupTime = groupTimeOptional.get();
        groupTime.getChannelTimes().removeIf(it -> !channelIdSet.contains(it.getChannelId()));
    }

}
