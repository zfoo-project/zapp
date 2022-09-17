import router from '@/router/router.js';
import store from '@/store/store.js';
import i18n from '@/i18n/i18n.js';

import { sendPacket, packetReceiver } from '@/util/websocketUtils.js';
import { isBlank } from '@/util/stringUtils.js';
import { updateAllGroupsRedPoint } from '@/util/groupUtils.js';

import WebsocketSignInResponse from '@/jsProtocol/user/WebsocketSignInResponse.js';
import JoinGroupRequest from '@/jsProtocol/group/member/JoinGroupRequest.js';
import RefreshGroupNameRequest from '@/jsProtocol/group/setting/RefreshGroupNameRequest.js';

packetReceiver(WebsocketSignInResponse, (packet) => {
    store.commit('friend/setApplyFriends', packet.applyFriends);
    store.commit('friend/setFriends', packet.friends);
    store.commit('friend/setBlacklist', packet.blacklist);
    store.commit('friend/addFriendInfos', packet.friendInfoMap);
    store.commit('user/setWsLoggedIn', true);

    // 群组相关
    const groups = packet.groups;
    const groupMemberSimpleVOs = packet.groupMemberSimpleVOs;
    updateAllGroupsRedPoint(groups, groupMemberSimpleVOs);
    store.commit('group/setGroups', groups);
    store.commit('group/setGroupMemberSimpleVos', groupMemberSimpleVOs);

    // 判断是否有邀请码，如果有邀请码，则发送加入群组的消息
    const inviteCode = router.currentRoute.query.inviteCode;
    if (!isBlank(inviteCode)) {
        sendPacket(new JoinGroupRequest(inviteCode));
        router.push({
            query: {}
        });
    }

    // 更新群组的最新名称
    const myGroupId = '-' + store.state.user.id;
    const myGroup = _.find(groups, it => it.id === myGroupId);
    if (!_.isNil(myGroup)) {
        const myGroupDefaultName = store.state.user.name + i18n.t('group.operation.create.myGroupSuffix');
        if (!_.isEqual(myGroup.name, myGroupDefaultName)) {
            sendPacket(new RefreshGroupNameRequest(myGroupId));
        }
    }
});
