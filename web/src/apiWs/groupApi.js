import store from '@/store/store.js';
import i18n from '@/i18n/i18n.js';

import { packetReceiver } from '@/util/websocketUtils.js';
import { simpleSuccess, simpleError } from '@/util/noticeUtils.js';
import { desktopPush } from '@/util/pushUtils.js';


import CreateGroupResponse from '@/jsProtocol/user/group/CreateGroupResponse.js';
import SaveGroupAvatarResponse from '@/jsProtocol/group/setting/SaveGroupAvatarResponse.js';
import SaveGroupBackgroundResponse from '@/jsProtocol/group/setting/SaveGroupBackgroundResponse.js';
import SaveGroupSettingResponse from '@/jsProtocol/group/setting/SaveGroupSettingResponse.js';
import CreateChannelBoxResponse from '@/jsProtocol/group/channel/CreateChannelBoxResponse.js';
import CreateChannelResponse from '@/jsProtocol/group/channel/CreateChannelResponse.js';
import DeleteChannelBoxResponse from '@/jsProtocol/group/channel/DeleteChannelBoxResponse.js';
import DeleteChannelResponse from '@/jsProtocol/group/channel/DeleteChannelResponse.js';
import CreateInviteGroupCodeResponse from '@/jsProtocol/group/member/CreateInviteGroupCodeResponse.js';
import DeleteInviteGroupCodeResponse from '@/jsProtocol/group/member/DeleteInviteGroupCodeResponse.js';
import JoinGroupResponse from '@/jsProtocol/group/member/JoinGroupResponse.js';
import LeaveGroupResponse from '@/jsProtocol/group/member/LeaveGroupResponse.js';
import AllInviteGroupCodeResponse from '@/jsProtocol/group/member/AllInviteGroupCodeResponse.js';
import SaveGroupAuthResponse from '@/jsProtocol/group/auth/SaveGroupAuthResponse.js';
import GroupHistoryMessageResponse from '@/jsProtocol/group/chat/GroupHistoryMessageResponse.js';
import SaveChannelResponse from '@/jsProtocol/group/channel/SaveChannelResponse.js';
import SaveChannelBoxResponse from '@/jsProtocol/group/channel/SaveChannelBoxResponse.js';
import SaveChannelAuthResponse from '@/jsProtocol/group/auth/SaveChannelAuthResponse.js';
import GroupMemberListResponse from '@/jsProtocol/group/member/GroupMemberListResponse.js';
import AddMemberToGroupAuthResponse from '@/jsProtocol/group/auth/AddMemberToGroupAuthResponse.js';
import RemoveMemberFromGroupAuthResponse from '@/jsProtocol/group/auth/RemoveMemberFromGroupAuthResponse.js';
import GroupMemberInfoResponse from '@/jsProtocol/group/member/GroupMemberInfoResponse.js';
import KickMemberResponse from '@/jsProtocol/group/member/KickMemberResponse.js';
import RefreshChannelTimeResponse from '@/jsProtocol/user/group/RefreshChannelTimeResponse.js';
import MuteGroupResponse from '@/jsProtocol/user/group/MuteGroupResponse.js';
import MuteChannelResponse from '@/jsProtocol/user/group/MuteChannelResponse.js';
import ChangeGroupAdminResponse from '@/jsProtocol/group/auth/ChangeGroupAdminResponse.js';
import GroupHistoryPinMessageResponse from '@/jsProtocol/group/chat/GroupHistoryPinMessageResponse.js';
import DeleteGroupPinMessageResponse from '@/jsProtocol/group/chat/DeleteGroupPinMessageResponse.js';


import DeleteGroupNotice from '@/jsProtocol/group/DeleteGroupNotice.js';
import GroupUpdateNotice from '@/jsProtocol/group/GroupUpdateNotice.js';
import GroupChatMessageNotice from '@/jsProtocol/group/GroupChatMessageNotice.js';
import MemberGroupAuthIdUpdateNotice from '@/jsProtocol/group/MemberGroupAuthIdUpdateNotice.js';
import KickMemberNotice from '@/jsProtocol/group/KickMemberNotice.js';
import DeleteGroupMessageNotice from '@/jsProtocol/group/DeleteGroupMessageNotice.js';
import EditGroupMessageNotice from '@/jsProtocol/group/EditGroupMessageNotice.js';

packetReceiver(CreateGroupResponse, (packet) => {
    store.commit('group/createGroup', packet.group);
    simpleSuccess(i18n.t('notice.groupCreateSuccess'));
    const groupOperation = store.state.app.groupOperation;
    if (!_.isNil(groupOperation)) {
        groupOperation.showGroupCreateDialog(false);
    }
});


packetReceiver(SaveGroupAvatarResponse, (packet) => {
    store.commit('group/saveGroupAvatar', packet);
    simpleSuccess(i18n.t('notice.groupAvatarSaveSuccess'));
});

packetReceiver(SaveGroupBackgroundResponse, (packet) => {
    store.commit('group/saveGroupBackground', packet);
    simpleSuccess(i18n.t('notice.groupBackgroundSaveSuccess'));
});

packetReceiver(SaveGroupSettingResponse, (packet) => {
    store.commit('group/saveGroupSetting', packet);
    simpleSuccess(i18n.t('notice.groupSettingSaveSuccess'));
    const groupInfo = store.state.app.groupInfo;
    if (!_.isNil(groupInfo)) {
        groupInfo.showGroupSettingDialog(false);
    }
});

packetReceiver(CreateChannelBoxResponse, (packet) => {
    store.commit('group/updateGroup', packet.groupVO);
    simpleSuccess(i18n.t('notice.groupChannelBoxCreateSuccess'));
    const groupInfo = store.state.app.groupInfo;
    if (!_.isNil(groupInfo)) {
        groupInfo.showChannelBoxCreateDialog(false);
    }
});

packetReceiver(CreateChannelResponse, (packet) => {
    store.commit('group/updateGroup', packet.groupVO);
    simpleSuccess(i18n.t('notice.groupChannelCreateSuccess'));
    const groupInfo = store.state.app.groupInfo;
    if (!_.isNil(groupInfo)) {
        groupInfo.showMainChannelCreateDialog(false);
        groupInfo.showSubChannelCreateDialog(false, '');
    }
});

packetReceiver(SaveChannelResponse, (packet) => {
    store.commit('group/updateGroup', packet.groupVO);
    simpleSuccess(i18n.t('notice.groupChannelRenameSuccess'));
    const groupChatTitle = store.state.app.groupChatTitle;
    if (!_.isNil(groupChatTitle)) {
        groupChatTitle.showSettingDialog(false);
    }
});

packetReceiver(SaveChannelBoxResponse, (packet) => {
    store.commit('group/updateGroup', packet.groupVO);
    simpleSuccess(i18n.t('notice.groupChannelBoxRenameSuccess'));
    const groupInfo = store.state.app.groupInfo;
    if (!_.isNil(groupInfo)) {
        groupInfo.showChannelBoxEditDialog(false);
    }
});

packetReceiver(DeleteChannelBoxResponse, (packet) => {
    store.commit('group/updateGroup', packet.groupVO);
    simpleSuccess(i18n.t('notice.groupChannelBoxDeleteSuccess'));
    const groupInfo = store.state.app.groupInfo;
    if (!_.isNil(groupInfo)) {
        groupInfo.showChannelBoxEditDialog(false);
    }
});

packetReceiver(DeleteChannelResponse, (packet) => {
    store.commit('group/updateGroup', packet.groupVO);
    simpleSuccess(i18n.t('notice.groupChannelDeleteSuccess'));
    const group = store.state.app.group;
    if (!_.isNil(group) && packet.groupId === group.getGroupId()) {
        group.clearGroupState();
        group.showGroupHome();
    }
});

packetReceiver(DeleteGroupNotice, (packet) => {
    const groupId = packet.groupId;

    const group = store.state.app.group;
    if (!_.isNil(group) && groupId === group.getGroupId()) {
        group.clearGroupState();
        group.showGroupHome();
    }

    store.commit('group/removeGroup', groupId);
    simpleSuccess(packet.groupName + i18n.t('notice.groupDeleteSuccess'));
});


packetReceiver(GroupUpdateNotice, (packet) => {
    store.commit('group/updateGroup', packet.groupVO);
});

packetReceiver(KickMemberNotice, (packet) => {
    const group = store.state.app.group;
    if (!_.isNil(group) && packet.groupId === group.getGroupId()) {
        group.clearGroupState();
        group.showGroupHome();
    }
    store.commit('group/removeGroup', packet.groupId);
});

packetReceiver(ChangeGroupAdminResponse, (packet) => {
    const group = store.state.app.group;
    if (!_.isNil(group) && packet.groupId === group.getGroupId()) {
        group.getMemberInfo().showGroupChangeAdminDialog(false);
        group.showGroupHome();
    }
    store.commit('group/removeGroup', packet.groupId);
});

packetReceiver(MemberGroupAuthIdUpdateNotice, (packet) => {
    store.commit('group/updateGroupMemberSimpleVo', packet);
});

packetReceiver(CreateInviteGroupCodeResponse, (packet) => {
    const groupInfo = store.state.app.groupInfo;
    if (!_.isNil(groupInfo)) {
        groupInfo.setInviteCodes(packet.inviteCodes);
    }
});

packetReceiver(DeleteInviteGroupCodeResponse, (packet) => {
    const groupInfo = store.state.app.groupInfo;
    if (!_.isNil(groupInfo)) {
        groupInfo.setInviteCodes(packet.inviteCodes);
    }
});

packetReceiver(JoinGroupResponse, (packet) => {
    const groupOperation = store.state.app.groupOperation;
    if (!_.isNil(groupOperation)) {
        groupOperation.showGroupJoinDialog(false);
        simpleSuccess(i18n.t('notice.groupJoinSuccess'));
    }
});

packetReceiver(LeaveGroupResponse, (packet) => {
    store.commit('group/removeGroup', packet.groupId);
    simpleSuccess(i18n.t('notice.groupLeaveSuccess') + packet.groupName);
    const groupInfo = store.state.app.groupInfo;
    if (!_.isNil(groupInfo)) {
        groupInfo.showGroupLeaveDialog(false);
    }
});

packetReceiver(AllInviteGroupCodeResponse, (packet) => {
    const groupInfo = store.state.app.groupInfo;
    if (!_.isNil(groupInfo)) {
        groupInfo.setInviteCodes(packet.inviteCodes);
    }
});

packetReceiver(SaveGroupAuthResponse, (packet) => {
    simpleSuccess(i18n.t('notice.groupAuthSaveSuccess'));
    const groupInfo = store.state.app.groupInfo;
    if (!_.isNil(groupInfo)) {
        groupInfo.showGroupAuthDialog(false);
    }
});

packetReceiver(DeleteGroupMessageNotice, (packet) => {
    store.commit('group/deleteChatMessage', packet);
    store.commit('app/removePinMessages', packet);
});

packetReceiver(EditGroupMessageNotice, (packet) => {
    store.commit('group/editChatMessage', packet);
    store.commit('app/editChatMessage', packet);
});

packetReceiver(GroupChatMessageNotice, (packet) => {
    store.commit('group/addChatMessage', packet);
    desktopPush(i18n.t('user.chat.newGroupMessage'), packet.chatMessage.message, packet.chatMessage.avatar);
});

packetReceiver(GroupHistoryMessageResponse, (packet) => {
    if (_.isEmpty(packet.chatMessages)) {
        simpleError(i18n.t('notice.groupChannelNoMoreHistoryMessage'));
        return;
    }
    store.commit('group/addHistoryMessage', packet);
});

packetReceiver(SaveChannelAuthResponse, (packet) => {
    simpleSuccess(i18n.t('notice.groupChannelAuthEditSuccess'));
    const groupChatTitle = store.state.app.groupChatTitle;
    if (!_.isNil(groupChatTitle)) {
        groupChatTitle.showChannelAuthDialog(false);
    }
});

packetReceiver(GroupMemberListResponse, (packet) => {
    if (_.isEmpty(packet.members)) {
        simpleError(i18n.t('notice.groupNoMemberError'));
        return;
    }
    store.commit('group/addGroupMembers', packet);
});

packetReceiver(KickMemberResponse, (packet) => {
    store.commit('group/removeGroupMember', packet);
    simpleSuccess(i18n.t('notice.groupKickMemberSuccess'));
});

packetReceiver(AddMemberToGroupAuthResponse, (packet) => {
    store.commit('group/addGroupAuthToMember', packet);
    store.commit('app/addGroupAuthToMemberVo', packet);
});

packetReceiver(RemoveMemberFromGroupAuthResponse, (packet) => {
    store.commit('group/removeGroupAuthFromMember', packet);
    store.commit('app/removeGroupAuthFromMemberVo', packet);
});

packetReceiver(GroupMemberInfoResponse, (packet) => {
    const groupId = packet.groupId;
    const members = packet.members;
    for (const groupMemberVo of members) {
        store.commit('app/setGroupMemberVo', groupMemberVo);
    }
    store.commit('app/setGroupIdOfGroupMemberVo', groupId);
});

packetReceiver(RefreshChannelTimeResponse, (packet) => {
    store.commit('group/refreshTimeGroup', packet);
});

packetReceiver(MuteGroupResponse, (packet) => {
    store.commit('group/muteGroup', packet);
});

packetReceiver(MuteChannelResponse, (packet) => {
    store.commit('group/muteChannel', packet);
});

packetReceiver(GroupHistoryPinMessageResponse, (packet) => {
    if (_.isEmpty(packet.chatMessages)) {
        simpleError(i18n.t('notice.groupChannelNoMoreHistoryPinMessage'));
        return;
    }
    store.commit('app/addPinMessages', packet);
});

packetReceiver(DeleteGroupPinMessageResponse, (packet) => {
    store.commit('app/removePinMessages', packet);
});
