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

package com.zfoo.app.zapp.user.login.controller;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.entity.friend.ApplicantEntity;
import com.zfoo.app.zapp.common.entity.friend.FriendEntity;
import com.zfoo.app.zapp.common.entity.group.GroupEntity;
import com.zfoo.app.zapp.common.entity.user.UserEntity;
import com.zfoo.app.zapp.common.entity.user.model.GroupTimePO;
import com.zfoo.app.zapp.common.protocol.cache.GetUserCacheAnswer;
import com.zfoo.app.zapp.common.protocol.cache.GetUserCacheAsk;
import com.zfoo.app.zapp.common.protocol.friend.model.ApplyFriendVO;
import com.zfoo.app.zapp.common.protocol.friend.model.FriendInfoVO;
import com.zfoo.app.zapp.common.protocol.group.model.GroupMemberSimpleVO;
import com.zfoo.app.zapp.common.protocol.user.*;
import com.zfoo.app.zapp.common.util.TokenUtils;
import com.zfoo.app.zapp.user.login.service.ILoginService;
import com.zfoo.net.NetContext;
import com.zfoo.net.core.gateway.model.AuthUidToGatewayCheck;
import com.zfoo.net.core.gateway.model.AuthUidToGatewayConfirm;
import com.zfoo.net.packet.model.GatewayPacketAttachment;
import com.zfoo.net.router.receiver.PacketReceiver;
import com.zfoo.net.session.model.Session;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.util.security.IdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-10-16 13:54
 */

@Component
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private ILoginService loginService;

    @EntityCachesInjection
    private IEntityCaches<Long, UserEntity> entityCaches;

    @PacketReceiver
    public void atWebsocketSignInRequest(Session session, WebsocketSignInRequest cm, GatewayPacketAttachment gatewayAttachment) {
        logger.info("用户登录请求[{}]", JsonUtils.object2String(cm));

        var userId = TokenUtils.get(cm.getToken()).getLeft();
        var userEntity = entityCaches.load(userId);
        // 授权给网关uid
        NetContext.getRouter().send(session, AuthUidToGatewayCheck.valueOf(userEntity.id()), gatewayAttachment);
    }

    @PacketReceiver
    public void atAuthUidToGatewayConfirm(Session session, AuthUidToGatewayConfirm confirm, GatewayPacketAttachment gatewayAttachment) {
        var userId = confirm.getUid();
        var applyFriends = new ArrayList<ApplyFriendVO>();
        var applyCollection = OrmContext.getOrmManager().getCollection(ApplicantEntity.class);
        applyCollection.find(Filters.eq("targetId", userId))
                .sort(Sorts.descending("timestamp"))
                .limit(AppConstant.FRIEND_APPLY_LIMIT)
                .forEach(new Consumer<ApplicantEntity>() {
                    @Override
                    public void accept(ApplicantEntity entity) {
                        applyFriends.add(ApplyFriendVO.valueOf(entity.getUserId(), entity.getStatus(), entity.getTimestamp()));
                    }
                });

        var userEntity = entityCaches.load(userId);

        // 请求所有的好友和申请人的基本信息
        var userIdSet = new HashSet<Long>();
        userIdSet.addAll(userEntity.getFriends());
        userIdSet.addAll(userEntity.getBlacklist());
        applyFriends.stream().forEach(it -> userIdSet.add(it.getFriendId()));
        userIdSet.add(userId);


        NetContext.getConsumer()
                .asyncAsk(GetUserCacheAsk.valueOf(new HashSet<>(userIdSet)), GetUserCacheAnswer.class, userId)
                .whenComplete(userCacheAnswer -> {
                    var userCacheMap = userCacheAnswer.getUserCacheMap();

                    // 好友相关
                    var friends = userEntity.getFriends().stream().map(it -> userCacheMap.get(it)).filter(it -> it != null).collect(Collectors.toList());
                    var blacklist = userEntity.getBlacklist().stream().map(it -> userCacheMap.get(it)).filter(it -> it != null).collect(Collectors.toList());
                    applyFriends.stream().forEach(it -> it.setFriendCache(userCacheMap.get(it.getFriendId())));


                    var friendInfoMap = new HashMap<Long, FriendInfoVO>();
                    var ids = friends.stream().map(it -> IdUtils.generateStringId(userId, it.getId())).collect(Collectors.toList());
                    OrmContext.getOrmManager().getCollection(FriendEntity.class)
                            .find(Filters.in("_id", ids))
                            .projection(Projections.exclude("inc", "messages"))
                            .forEach((Consumer<FriendEntity>) friendEntity -> {
                                var friendInfo = FriendInfoVO.valueOf(friendEntity.friendId(userId), friendEntity.getRefreshTime(), friendEntity.readTimeOfUserId(userId), friendEntity.tag(userId));
                                friendInfoMap.put(friendEntity.friendId(userId), friendInfo);
                            });


                    // 群组相关
                    // 所有的用户都必须加入zfoo官方的群组，而且不能退出
                    if (userEntity.getGroups().stream().noneMatch(it -> it.getGroupId() == AppConstant.ZFOO_GROUP_ID)) {
                        userEntity.getGroups().add(GroupTimePO.valueOf(AppConstant.ZFOO_GROUP_ID));
                    }

                    var groupTimeMap = userEntity.getGroups().stream().collect(Collectors.toMap(key -> key.getGroupId(), value -> value));
                    var groupIds = userEntity.getGroups().stream().map(it -> it.getGroupId()).collect(Collectors.toList());
                    var groupList = new ArrayList<GroupEntity>();
                    OrmContext.getOrmManager().getCollection(GroupEntity.class)
                            .find(Filters.in("_id", groupIds))
                            .projection(Projections.exclude("people"))
                            .forEach((Consumer<GroupEntity>) groupEntity -> groupList.add(groupEntity));

                    // 检查群组，并且删除不存在的群组，因为群组可能会被删除
                    var currentGroups = new HashSet<>(userEntity.getGroups().stream().map(it -> it.getGroupId()).collect(Collectors.toSet()));
                    var existGroups = groupList.stream().map(it -> it.getId()).collect(Collectors.toSet());
                    currentGroups.removeAll(existGroups);
                    if (CollectionUtils.isNotEmpty(currentGroups)) {
                        currentGroups.forEach(it -> userEntity.getGroups().removeIf(group -> group.getGroupId() == it));
                    }

                    // 检查频道，并且删除不存在的频道， 因为频道可能会被删除
                    for (var groupTime : userEntity.getGroups()) {
                        var currentChannels = new HashSet<Long>();
                        for (var channelTime : groupTime.getChannelTimes()) {
                            currentChannels.add(channelTime.getChannelId());
                        }
                        var existChannels = new HashSet<Long>();
                        var group = groupList.stream().filter(it -> it.getId() == groupTime.getGroupId()).findFirst().get();
                        for (var channelBox : group.getChannelBoxes()) {
                            for (var channel : channelBox.getChannels()) {
                                existChannels.add(channel.getId());
                            }
                        }
                        currentChannels.removeAll(existChannels);
                        if (CollectionUtils.isNotEmpty(currentChannels)) {
                            currentChannels.forEach(channelId -> groupTime.getChannelTimes().removeIf(channelTimePO -> channelTimePO.getChannelId() == channelId));
                        }
                    }

                    entityCaches.update(userEntity);

                    // 保持原有的群组顺序
                    var groupVOList = userEntity.getGroups()
                            .stream()
                            .map(it -> {
                                var groupEntity = groupList.stream().filter(entity -> entity.getId() == it.getGroupId()).findFirst().get();
                                return groupEntity.toGroupVO();
                            })
                            .collect(Collectors.toList());
                    var memberGroupAuthIdVOs = groupList.stream().map(it -> GroupMemberSimpleVO.valueOf(it.getId(), userId, groupTimeMap.get(it.getId()).toGroupTimeVO(), it.toGroupAuthIds(userId))).collect(Collectors.toList());

                    // 返回登录信息
                    NetContext.getRouter().send(session, WebsocketSignInResponse.valueOf(userId
                            , friends, blacklist, applyFriends, friendInfoMap
                            , groupVOList, memberGroupAuthIdVOs), gatewayAttachment);
                });
    }

    @PacketReceiver
    public void atSignInByAccountAsk(Session session, SignInByAccountAsk ask) {
        var token = loginService.signInByAccount(ask.getAccount(), ask.getPassword());
        NetContext.getRouter().send(session, SignInAnswer.valueOf(token, false));
    }

    @PacketReceiver
    public void atSignInByPhoneAsk(Session session, SignInByPhoneAsk ask) {
        var pair = loginService.signInByPhone(ask.getPhoneNumber());
        NetContext.getRouter().send(session, SignInAnswer.valueOf(pair.getKey(), pair.getValue()));
    }

}
