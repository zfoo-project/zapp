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

package com.zfoo.app.zapp.group.controller;

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.entity.group.ChannelEntity;
import com.zfoo.app.zapp.common.entity.group.GroupEntity;
import com.zfoo.app.zapp.common.entity.group.model.*;
import com.zfoo.app.zapp.common.protocol.cache.WordFilterAnswer;
import com.zfoo.app.zapp.common.protocol.cache.WordFilterAsk;
import com.zfoo.app.zapp.common.protocol.group.DeleteGroupNotice;
import com.zfoo.app.zapp.common.protocol.group.GroupUpdateNotice;
import com.zfoo.app.zapp.common.protocol.group.MemberGroupAuthIdUpdateNotice;
import com.zfoo.app.zapp.common.protocol.group.auth.*;
import com.zfoo.app.zapp.common.protocol.push.group.DeleteGroupPush;
import com.zfoo.app.zapp.common.protocol.push.group.GroupUpdatePush;
import com.zfoo.app.zapp.common.protocol.push.group.MemberGroupAuthIdUpdatePush;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.net.NetContext;
import com.zfoo.net.packet.common.Error;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.router.attachment.GatewayAttachment;
import com.zfoo.net.router.receiver.PacketReceiver;
import com.zfoo.net.session.model.Session;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-05 17:33
 */
@Component
public class AuthController {

    @EntityCachesInjection
    private IEntityCaches<Long, GroupEntity> groupEntityCaches;

    @EntityCachesInjection
    private IEntityCaches<Long, ChannelEntity> channelEntityCaches;

    @PacketReceiver
    public void atCreateGroupAuthRequest(Session session, CreateGroupAuthRequest cm, GatewayAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var name = cm.getName();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (StringUtils.isBlank(name)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_EMPTY.getCode()), gatewayAttachment);
            return;
        }

        if (name.equals(AppConstant.GROUP_AUTH_DEFAULT_NAME)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.CREATE_GROUP_AUTH)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        NetContext.getConsumer()
                .asyncAsk(WordFilterAsk.valueOf(name), WordFilterAnswer.class, userId)
                .whenComplete(new Consumer<>() {
                    @Override
                    public void accept(WordFilterAnswer answer) {
                        // 敏感字符检测
                        if (CollectionUtils.isNotEmpty(answer.getFilterWords())) {
                            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_WORD_FILTER_ERROR.getCode()), gatewayAttachment);
                            return;
                        }
                        groupEntity.getGroupAuths().add(GroupAuthPO.valueOf(groupEntity.maxGroupAuthId() + 1, name, GroupAuthEnum.BASE_AUTH.getType(), StringUtils.EMPTY, new HashSet<>()));
                        groupEntityCaches.update(groupEntity);

                        NetContext.getRouter().send(session, Message.valueOf(cm, CodeEnum.OK.getCode()), gatewayAttachment);
                        NetContext.getConsumer().send(GroupUpdatePush.valueOf(groupEntity.getPeople(), GroupUpdateNotice.valueOf(groupEntity.toGroupVO())), groupId);
                    }
                });
    }

    @PacketReceiver
    public void atDeleteGroupAuthRequest(Session session, DeleteGroupAuthRequest cm, GatewayAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var groupAuthId = cm.getGroupAuthId();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (groupAuthId == AppConstant.GROUP_AUTH_DEFAULT_ID) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.DELETE_GROUP_AUTH)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        groupEntity.getGroupAuths().removeIf(it -> it.getId() == groupAuthId);
        for (var channelBox : groupEntity.getChannelBoxes()) {
            for (var channel : channelBox.getChannels()) {
                channel.getChannelAuths().removeIf(it -> it.getId() == groupAuthId);
            }
        }
        groupEntityCaches.update(groupEntity);

        NetContext.getRouter().send(session, Message.valueOf(cm, CodeEnum.OK.getCode()), gatewayAttachment);
        NetContext.getConsumer().send(GroupUpdatePush.valueOf(groupEntity.getPeople(), GroupUpdateNotice.valueOf(groupEntity.toGroupVO())), groupId);
    }

    @PacketReceiver
    public void atSaveGroupAuthRequest(Session session, SaveGroupAuthRequest cm, GatewayAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var groupAuthVOs = cm.getGroupAuths();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (CollectionUtils.isEmpty(groupAuthVOs)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_EMPTY.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.CHANGE_GROUP_AUTH)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        for (var groupAuth : groupAuthVOs) {
            var groupAuthId = groupAuth.getId();
            var name = groupAuth.getName();
            var groupAuthEnum = GroupAuthEnum.getAuthEnumByType(groupAuth.getGroupAuth());
            var color = groupAuth.getColor();
            if (groupEntity.findGroupAuthPO(groupAuthId) == null) {
                NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_ONE.getCode()), gatewayAttachment);
                return;
            }
            if (StringUtils.isBlank(name)) {
                NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_EMPTY.getCode()), gatewayAttachment);
                return;
            }
            if (groupAuth.getId() != AppConstant.GROUP_AUTH_DEFAULT_ID && name.equals(AppConstant.GROUP_AUTH_DEFAULT_NAME)) {
                NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_NAME_CAN_NOT_DEFAULT.getCode()), gatewayAttachment);
                return;
            }
            if (groupAuthEnum == null) {
                NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_NOT_MATCH.getCode()), gatewayAttachment);
                return;
            }
            if (!StringUtils.isBlank(color) && color.length() != AppConstant.GROUP_AUTH_COLOR_LENGTH) {
                NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_TWO.getCode()), gatewayAttachment);
                return;
            }

            // 默认身份不能删除，不能改名，不能改颜色，仅可改权限
            if (groupAuthId == AppConstant.GROUP_AUTH_DEFAULT_ID) {
                if (!AppConstant.GROUP_AUTH_DEFAULT_NAME.equals(name) || !StringUtils.isBlank(color)) {
                    NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_THREE.getCode()), gatewayAttachment);
                    return;
                }
            }
        }

        NetContext.getConsumer()
                .asyncAsk(WordFilterAsk.valueOf(groupAuthVOs.stream().map(it -> it.getName()).collect(Collectors.joining())), WordFilterAnswer.class, userId)
                .whenComplete(new Consumer<>() {
                    @Override
                    public void accept(WordFilterAnswer answer) {
                        // 敏感字符检测
                        if (CollectionUtils.isNotEmpty(answer.getFilterWords())) {
                            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_WORD_FILTER_ERROR.getCode()), gatewayAttachment);
                            return;
                        }
                        for (var groupAuthVO : groupAuthVOs) {
                            var groupAuthId = groupAuthVO.getId();
                            var name = groupAuthVO.getName();
                            var groupAuthEnum = GroupAuthEnum.getAuthEnumByType(groupAuthVO.getGroupAuth());
                            var color = groupAuthVO.getColor();

                            var groupAuthPO = groupEntity.findGroupAuthPO(groupAuthId);
                            groupAuthPO.setName(name);
                            groupAuthPO.setGroupAuth(groupAuthEnum.getType());
                            groupAuthPO.setColor(color);
                        }

                        groupEntityCaches.update(groupEntity);

                        NetContext.getRouter().send(session, SaveGroupAuthResponse.valueOf(), gatewayAttachment);
                        NetContext.getConsumer().send(GroupUpdatePush.valueOf(groupEntity.getPeople(), GroupUpdateNotice.valueOf(groupEntity.toGroupVO())), groupId);
                    }
                });
    }


    @PacketReceiver
    public void atSaveChannelAuthRequest(Session session, SaveChannelAuthRequest cm, GatewayAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var channelId = cm.getChannelId();
        var channelAuths = cm.getChannelAuths();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.CHANGE_CHANNEL_AUTH)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var channelPO = groupEntity.findChannel(channelId);
        if (channelPO == null) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_ONE.getCode()), gatewayAttachment);
            return;
        }

        for (var channel : channelAuths) {
            var groupAuthPO = groupEntity.findGroupAuthPO(channel.getId());
            if (groupAuthPO == null) {
                NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
                return;
            }
            if (ChannelAuthEnum.getAuthEnumByType(channel.getChannelAuth()) == null) {
                NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_NOT_MATCH.getCode()), gatewayAttachment);
                return;
            }
        }

        for (var channelAuth : channelAuths) {
            var channelAuthPO = channelPO.findChannelAuthPO(channelAuth.getId());
            if (channelAuthPO == null) {
                channelPO.getChannelAuths().add(ChannelAuthPO.valueOf(channelAuth.getId(), channelAuth.getChannelAuth()));
                continue;
            }
            channelAuthPO.setChannelAuth(channelAuth.getChannelAuth());
        }

        groupEntityCaches.update(groupEntity);
        NetContext.getRouter().send(session, SaveChannelAuthResponse.valueOf(), gatewayAttachment);
        NetContext.getConsumer().send(GroupUpdatePush.valueOf(groupEntity.getPeople(), GroupUpdateNotice.valueOf(groupEntity.toGroupVO())), groupId);
    }

    @PacketReceiver
    public void atAddMemberToGroupAuthRequest(Session session, AddMemberToGroupAuthRequest cm, GatewayAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var groupAuthId = cm.getGroupAuthId();
        var memberId = cm.getMemberId();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (!CommonUtils.isUserIdInRange(List.of(memberId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }

        if (groupAuthId == AppConstant.GROUP_AUTH_DEFAULT_ID) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_ONE.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.ADD_MEMBER_TO_GROUP_AUTH)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        if (!groupEntity.getPeople().contains(memberId)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_HAVE_MEMBER.getCode()), gatewayAttachment);
            return;
        }

        var groupAuthPO = groupEntity.findGroupAuthPO(groupAuthId);
        if (groupAuthPO == null) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_THREE.getCode()), gatewayAttachment);
            return;
        }

        groupAuthPO.getMembers().add(memberId);
        groupEntityCaches.update(groupEntity);

        NetContext.getRouter().send(session, AddMemberToGroupAuthResponse.valueOf(groupId, groupAuthId, memberId), gatewayAttachment);

        NetContext.getConsumer().send(GroupUpdatePush.valueOf(Set.of(memberId), GroupUpdateNotice.valueOf(groupEntity.toGroupVO())), groupId);
        NetContext.getConsumer().send(MemberGroupAuthIdUpdatePush.valueOf(memberId
                , MemberGroupAuthIdUpdateNotice.valueOf(groupId, memberId, groupEntity.toGroupAuthIds(memberId))), groupId);
    }

    @PacketReceiver
    public void atRemoveMemberFromGroupAuthRequest(Session session, RemoveMemberFromGroupAuthRequest cm, GatewayAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var groupAuthId = cm.getGroupAuthId();
        var memberId = cm.getMemberId();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (!CommonUtils.isUserIdInRange(List.of(memberId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }

        if (groupAuthId == AppConstant.GROUP_AUTH_DEFAULT_ID) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_ONE.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.REMOVE_MEMBER_FROM_GROUP_AUTH)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        if (!groupEntity.getPeople().contains(memberId)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_HAVE_MEMBER.getCode()), gatewayAttachment);
            return;
        }

        var groupAuthPO = groupEntity.findGroupAuthPO(groupAuthId);
        if (groupAuthPO == null) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_TWO.getCode()), gatewayAttachment);
            return;
        }

        groupAuthPO.getMembers().remove(memberId);
        groupEntityCaches.update(groupEntity);

        NetContext.getRouter().send(session, RemoveMemberFromGroupAuthResponse.valueOf(groupId, groupAuthId, memberId), gatewayAttachment);
        NetContext.getConsumer().send(GroupUpdatePush.valueOf(Set.of(memberId), GroupUpdateNotice.valueOf(groupEntity.toGroupVO())), groupId);
        NetContext.getConsumer().send(MemberGroupAuthIdUpdatePush.valueOf(memberId
                , MemberGroupAuthIdUpdateNotice.valueOf(groupId, memberId, groupEntity.toGroupAuthIds(memberId))), groupId);
    }

    @PacketReceiver
    public void atDeleteGroupRequest(Session session, DeleteGroupRequest cm, GatewayAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (groupId == -userId) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_DELETE_SELF_GROUP_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.DELETE_GROUP)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        groupEntityCaches.invalidate(groupId);
        // 删除主群组
        OrmContext.getAccessor().delete(groupId, GroupEntity.class);
        // 删除群组中的channel
        for (var channelBox : groupEntity.getChannelBoxes()) {
            for (var channel : channelBox.getChannels()) {
                channelEntityCaches.invalidate(channel.getId());
                OrmContext.getAccessor().delete(channel.getId(), ChannelEntity.class);
            }
        }

        NetContext.getRouter().send(session, Message.valueOf(cm, CodeEnum.OK.getCode()), gatewayAttachment);

        NetContext.getConsumer().send(DeleteGroupPush.valueOf(groupEntity.getPeople(), DeleteGroupNotice.valueOf(groupId, groupEntity.getName())), groupId);
    }

    @PacketReceiver
    public void atChangeGroupAdminRequest(Session session, ChangeGroupAdminRequest cm, GatewayAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var adminId = cm.getAdminId();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        // 用户公共的群组不可以转移所有权
        if (groupId == -groupEntity.getAdminId()) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_CHANGE_ADMIN_SELF_GROUP_ERROR.getCode()), gatewayAttachment);
            return;
        }

        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.CREATE_GROUP_AUTH)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        if (!groupEntity.getPeople().contains(adminId)) {
            NetContext.getRouter().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_HAVE_MEMBER.getCode()), gatewayAttachment);
            return;
        }

        groupEntity.setAdminId(adminId);
        groupEntityCaches.update(groupEntity);

        NetContext.getRouter().send(session, ChangeGroupAdminResponse.valueOf(groupId, adminId), gatewayAttachment);
        NetContext.getConsumer().send(GroupUpdatePush.valueOf(groupEntity.getPeople(), GroupUpdateNotice.valueOf(groupEntity.toGroupVO())), groupId);
    }

}
