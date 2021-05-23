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
import com.zfoo.app.zapp.common.entity.group.GroupEntity;
import com.zfoo.app.zapp.common.entity.group.model.*;
import com.zfoo.app.zapp.common.protocol.cache.GetUserCacheAnswer;
import com.zfoo.app.zapp.common.protocol.cache.GetUserCacheAsk;
import com.zfoo.app.zapp.common.protocol.group.GroupUpdateNotice;
import com.zfoo.app.zapp.common.protocol.group.KickMemberNotice;
import com.zfoo.app.zapp.common.protocol.group.MemberGroupAuthIdUpdateNotice;
import com.zfoo.app.zapp.common.protocol.group.member.*;
import com.zfoo.app.zapp.common.protocol.group.member.model.GroupMemberVO;
import com.zfoo.app.zapp.common.protocol.push.group.GroupUpdatePush;
import com.zfoo.app.zapp.common.protocol.push.group.KickMemberPush;
import com.zfoo.app.zapp.common.protocol.push.group.MemberGroupAuthIdUpdatePush;
import com.zfoo.app.zapp.common.protocol.user.group.JoinGroupAsk;
import com.zfoo.app.zapp.common.protocol.user.group.LeaveGroupAsk;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.common.util.GroupUtils;
import com.zfoo.app.zapp.group.service.IGroupService;
import com.zfoo.net.NetContext;
import com.zfoo.net.dispatcher.model.anno.PacketReceiver;
import com.zfoo.net.packet.common.Error;
import com.zfoo.net.packet.common.Message;
import com.zfoo.net.packet.model.GatewayPacketAttachment;
import com.zfoo.net.session.model.Session;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import com.zfoo.orm.model.query.Page;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-03 17:54
 */
@Component
public class MemberController {

    @EntityCachesInjection
    private IEntityCaches<Long, GroupEntity> groupEntityCaches;

    @Autowired
    private IGroupService groupService;

    @PacketReceiver
    public void atCreateInviteGroupCodeRequest(Session session, CreateInviteGroupCodeRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var expireEnum = InviteExpireEnum.getInviteExpireEnumByType(cm.getExpireType());
        var countEnum = InviteCountEnum.getInviteCountEnumByType(cm.getCountType());

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (expireEnum == null || countEnum == null) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_ONE.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.CREATE_INVITE_CODE)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var inviteCode = GroupUtils.set(groupId);
        var expireTime = expireEnum == InviteExpireEnum.EXPIRE_UNLIMITED ? Long.MAX_VALUE : TimeUtils.now() + expireEnum.getExpire();
        groupEntity.getInviteCodes().add(InviteCodePO.valueOf(inviteCode, expireEnum.getType(), expireTime, countEnum.getType(), 0));
        groupEntityCaches.update(groupEntity);

        NetContext.getDispatcher().send(session
                , CreateInviteGroupCodeResponse.valueOf(groupEntity.getInviteCodes().stream().map(it -> it.toInviteCodeVO()).collect(Collectors.toList()))
                , gatewayAttachment);
    }

    @PacketReceiver
    public void atJoinGroupRequest(Session session, JoinGroupRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();

        var inviteCode = cm.getInviteCode();
        var pair = GroupUtils.get(inviteCode);

        var groupId = pair.getKey();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        // 移除过期的邀请码
        // groupEntity.getInviteCodes().removeIf(it -> TimeUtils.now() > it.getExpireTime());

        var inviteCodeOptional = groupEntity.getInviteCodes().stream().filter(it -> it.getCode().equals(inviteCode)).findFirst();

        if (inviteCodeOptional.isEmpty()) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_INVITE_CODE_EXPIRE.getCode()), gatewayAttachment);
            return;
        }

        var inviteCodePO = inviteCodeOptional.get();

        // 验证邀请码时间，即使过期也不能删除邀请码
        if (TimeUtils.now() > inviteCodePO.getExpireTime()) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_INVITE_CODE_EXPIRE.getCode()), gatewayAttachment);
            return;
        }

        // 验证邀请码人数上限
        var countEnum = InviteCountEnum.getInviteCountEnumByType(inviteCodePO.getCountType());
        if (countEnum != InviteCountEnum.COUNT_UNLIMITED && inviteCodePO.getCount() >= countEnum.getCount()) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_INVITE_CODE_EXPIRE.getCode()), gatewayAttachment);
            return;
        }

        // 验证是否已经加入群组
        if (groupEntity.getPeople().contains(userId)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_JOIN_ALREADY.getCode()), gatewayAttachment);
            return;
        }


        NetContext.getConsumer()
                .asyncAsk(JoinGroupAsk.valueOf(userId, groupId), Message.class, userId)
                .whenComplete(message -> {
                    if (!message.success()) {
                        NetContext.getDispatcher().send(session, Error.valueOf(cm, message.getCode(), message.getMessage()), gatewayAttachment);
                        return;
                    }

                    inviteCodePO.addCount();
                    groupEntity.getPeople().add(userId);
                    groupEntityCaches.update(groupEntity);

                    NetContext.getDispatcher().send(session, JoinGroupResponse.valueOf(), gatewayAttachment);
                    NetContext.getConsumer().send(GroupUpdatePush.valueOf(Set.of(userId), GroupUpdateNotice.valueOf(groupEntity.toGroupVO())), groupId);
                    NetContext.getConsumer().send(MemberGroupAuthIdUpdatePush.valueOf(userId
                            , MemberGroupAuthIdUpdateNotice.valueOf(groupId, userId, groupEntity.toGroupAuthIds(userId))), groupId);
                });
    }

    @PacketReceiver
    public void atJoinGroupByUserIdRequest(Session session, JoinGroupByUserIdRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = -cm.getUserId();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);

        if (groupEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }

        // 只有用户公共的群组可以加入
        if (groupId != -groupEntity.getAdminId()) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_ONE.getCode()), gatewayAttachment);
            return;
        }

        // 如果已经加入群组则直接返回
        if (groupEntity.getPeople().contains(userId)) {
            return;
        }

        if (groupEntity.getAdminId() < 0) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR_TWO.getCode()), gatewayAttachment);
            return;
        }

        NetContext.getConsumer()
                .asyncAsk(JoinGroupAsk.valueOf(userId, groupId), Message.class, userId)
                .whenComplete(message -> {
                    if (!message.success()) {
                        NetContext.getDispatcher().send(session, Error.valueOf(cm, message.getCode(), message.getMessage()), gatewayAttachment);
                        return;
                    }

                    groupEntity.getPeople().add(userId);
                    groupEntityCaches.update(groupEntity);

                    NetContext.getDispatcher().send(session, JoinGroupResponse.valueOf(), gatewayAttachment);
                    NetContext.getConsumer().send(GroupUpdatePush.valueOf(Set.of(userId), GroupUpdateNotice.valueOf(groupEntity.toGroupVO())), groupId);
                    NetContext.getConsumer().send(MemberGroupAuthIdUpdatePush.valueOf(userId
                            , MemberGroupAuthIdUpdateNotice.valueOf(groupId, userId, groupEntity.toGroupAuthIds(userId))), groupId);
                });
    }

    @PacketReceiver
    public void atDeleteInviteGroupCodeRequest(Session session, DeleteInviteGroupCodeRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();

        var inviteCode = cm.getInviteCode();
        var pair = GroupUtils.get(inviteCode);

        var groupId = pair.getKey();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.DELETE_INVITE_CODE)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        // 移除过期的邀请码
        groupEntity.getInviteCodes().removeIf(it -> it.getCode().equals(inviteCode));

        NetContext.getDispatcher().send(session
                , DeleteInviteGroupCodeResponse.valueOf(groupEntity.getInviteCodes().stream().map(it -> it.toInviteCodeVO()).collect(Collectors.toList()))
                , gatewayAttachment);
    }

    @PacketReceiver
    public void atLeaveGroupRequest(Session session, LeaveGroupRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        // 不能退出官方群组
        if (groupId == AppConstant.ZFOO_GROUP_ID) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_LEAVE_ZFOO_ERROR.getCode()), gatewayAttachment);
            return;
        }

        // 不能退出自己的公共群组
        if (groupId == -userId) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_LEAVE_SELF_GROUP_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (groupEntity.memberOfMaxGroupAuth(userId) == GroupAuthEnum.ADMIN_AUTH) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_ADMIN_CAN_NOT_LEAVE_GROUP.getCode()), gatewayAttachment);
            return;
        }


        NetContext.getConsumer()
                .asyncAsk(LeaveGroupAsk.valueOf(userId, groupId), Message.class, userId)
                .whenComplete(message -> {
                    if (!message.success()) {
                        NetContext.getDispatcher().send(session, Error.valueOf(cm, message.getCode(), message.getMessage()), gatewayAttachment);
                        return;
                    }

                    groupService.removeMemberFromGroup(groupEntity, userId);
                    groupEntityCaches.update(groupEntity);

                    NetContext.getDispatcher().send(session, LeaveGroupResponse.valueOf(groupId, groupEntity.getName()), gatewayAttachment);
                });
    }

    @PacketReceiver
    public void atKickMemberRequest(Session session, KickMemberRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var memberId = cm.getMemberId();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.ACCESS_INVITE_CODE)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }

        if (groupEntity.memberOfMaxGroupAuth(memberId) == GroupAuthEnum.ADMIN_AUTH) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_ADMIN_CAN_NOT_LEAVE_GROUP.getCode()), gatewayAttachment);
            return;
        }

        if (!groupEntity.getPeople().contains(memberId)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_HAVE_MEMBER.getCode()), gatewayAttachment);
            return;
        }


        NetContext.getConsumer()
                .asyncAsk(LeaveGroupAsk.valueOf(memberId, groupId), Message.class, memberId)
                .whenComplete(message -> {
                    if (!message.success()) {
                        NetContext.getDispatcher().send(session, Error.valueOf(cm, message.getCode(), message.getMessage()), gatewayAttachment);
                        return;
                    }

                    groupService.removeMemberFromGroup(groupEntity, memberId);
                    groupEntityCaches.update(groupEntity);

                    NetContext.getDispatcher().send(session, KickMemberResponse.valueOf(groupId, memberId), gatewayAttachment);
                    NetContext.getConsumer().send(KickMemberPush.valueOf(memberId, KickMemberNotice.valueOf(groupId, memberId)), groupId);
                });
    }


    @PacketReceiver
    public void atAllInviteGroupCodeRequest(Session session, AllInviteGroupCodeRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.memberOfMaxGroupAuth(userId).hasAuth(OperationEnum.ACCESS_INVITE_CODE)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_AUTH_ERROR.getCode()), gatewayAttachment);
            return;
        }


        NetContext.getDispatcher().send(session
                , AllInviteGroupCodeResponse.valueOf(groupEntity.getInviteCodes().stream().map(it -> it.toInviteCodeVO()).collect(Collectors.toList()))
                , gatewayAttachment);
    }


    @PacketReceiver
    public void atGroupMemberListRequest(Session session, GroupMemberListRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var page = cm.getPage();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.getPeople().contains(userId)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_JOIN_ERROR.getCode()), gatewayAttachment);
            return;
        }

        var sortedGroupAuthPOs = groupEntity.getGroupAuths()
                .stream()
                .filter(it -> it.getId() != AppConstant.GROUP_AUTH_DEFAULT_ID)
                .sorted((a, b) -> Integer.compare(b.getGroupAuth(), a.getGroupAuth()))
                .collect(Collectors.toList());
        // 用来记录已经被选择的用户
        var set = new HashSet<Long>();
        // 展示的用户
        var members = new ArrayList<Long>();
        for (var groupAuthPO : sortedGroupAuthPOs) {
            for (var memberId : groupAuthPO.getMembers()) {
                if (!set.contains(memberId)) {
                    set.add(memberId);
                    members.add(memberId);
                }
            }
        }
        for (var memberId : groupEntity.getPeople()) {
            if (!set.contains(memberId)) {
                set.add(memberId);
                members.add(memberId);
            }
        }

        var memberPage = Page.valueOf(page, AppConstant.USER_LIST_PAGE_SIZE, members.size());
        var memberList = memberPage.currentPageList(members);

        if (CollectionUtils.isEmpty(memberList)) {
            NetContext.getDispatcher().send(session, GroupMemberListResponse.valueOf(groupId, page, Collections.EMPTY_LIST), gatewayAttachment);
            return;
        }

        NetContext.getConsumer()
                .asyncAsk(GetUserCacheAsk.valueOf(new HashSet<>(memberList)), GetUserCacheAnswer.class, groupId)
                .whenComplete(answer -> {
                    var userCacheMap = answer.getUserCacheMap();

                    var memberVOs = userCacheMap.entrySet()
                            .stream()
                            .map(it -> GroupMemberVO.valueOf(groupEntity.toGroupAuthIds(it.getKey()), it.getValue()))
                            .collect(Collectors.toList());

                    NetContext.getDispatcher().send(session, GroupMemberListResponse.valueOf(groupId, page, memberVOs), gatewayAttachment);
                });
    }

    @PacketReceiver
    public void atGroupMemberInfoRequest(Session session, GroupMemberInfoRequest cm, GatewayPacketAttachment gatewayAttachment) {
        var userId = gatewayAttachment.getUid();
        var groupId = cm.getGroupId();
        var members = cm.getMembers();

        if (!CommonUtils.isGroupIdInRange(List.of(groupId))) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_EXIST.getCode()), gatewayAttachment);
            return;
        }

        if (CollectionUtils.isEmpty(members)) {
            NetContext.getDispatcher().send(session, GroupMemberInfoResponse.valueOf(groupId, Collections.EMPTY_LIST), gatewayAttachment);
            return;
        }

        var groupEntity = groupEntityCaches.load(groupId);
        if (groupEntity.id() == 0L) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.PARAMETER_ERROR.getCode()), gatewayAttachment);
            return;
        }
        if (!groupEntity.getPeople().contains(userId)) {
            NetContext.getDispatcher().send(session, Error.valueOf(cm, CodeEnum.GROUP_NOT_JOIN_ERROR.getCode()), gatewayAttachment);
            return;
        }

        NetContext.getConsumer()
                .asyncAsk(GetUserCacheAsk.valueOf(new HashSet<>(members)), GetUserCacheAnswer.class, groupId)
                .whenComplete(answer -> {
                    var userCacheMap = answer.getUserCacheMap();

                    var memberVOs = userCacheMap.entrySet()
                            .stream()
                            .map(it -> GroupMemberVO.valueOf(groupEntity.toGroupAuthIds(it.getKey()), it.getValue()))
                            .collect(Collectors.toList());

                    NetContext.getDispatcher().send(session, GroupMemberInfoResponse.valueOf(groupId, memberVOs), gatewayAttachment);
                });
    }
}
