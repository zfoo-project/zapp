import store from '@/store/store.js';
import i18n from '@/i18n/i18n.js';
import { packetReceiver } from '@/util/websocketUtils.js';
import { simpleSuccess, simpleError } from '@/util/noticeUtils.js';
import { desktopPush } from '@/util/pushUtils.js';

import SearchUserResponse from '@/jsProtocol/cache/SearchUserResponse.js';
import RejectFriendResponse from '@/jsProtocol/friend/operation/RejectFriendResponse.js';
import FriendHistoryMessageResponse from '@/jsProtocol/friend/chat/FriendHistoryMessageResponse.js';
import ReadFriendMessageResponse from '@/jsProtocol/friend/chat/ReadFriendMessageResponse.js';
import DeleteFriendResponse from '@/jsProtocol/friend/operation/DeleteFriendResponse.js';
import BlacklistResponse from '@/jsProtocol/friend/operation/BlacklistResponse.js';
import BlacklistCancelResponse from '@/jsProtocol/friend/operation/BlacklistCancelResponse.js';
import MarkFriendResponse from '@/jsProtocol/friend/operation/MarkFriendResponse.js';


import NewApplyFriendNotice from '@/jsProtocol/friend/NewApplyFriendNotice.js';
import FriendMessageNotice from '@/jsProtocol/friend/FriendMessageNotice.js';
import NewFriendNotice from '@/jsProtocol/friend/NewFriendNotice.js';
import DeleteFriendMessageNotice from '@/jsProtocol/friend/DeleteFriendMessageNotice.js';
import EditFriendMessageNotice from '@/jsProtocol/friend/EditFriendMessageNotice.js';

packetReceiver(ReadFriendMessageResponse, (packet) => {
    store.commit('friend/removeFriendRedPoint', packet.friendId);
});

packetReceiver(FriendMessageNotice, (packet) => {
    store.commit('friend/addChatMessage', packet);
    desktopPush(i18n.t('user.chat.newFriendMessage'), packet.chatMessage.message, packet.chatMessage.avatar);
});

packetReceiver(DeleteFriendMessageNotice, (packet) => {
    store.commit('friend/removeChatMessage', packet);
});

packetReceiver(EditFriendMessageNotice, (packet) => {
    store.commit('friend/editChatMessage', packet);
});

packetReceiver(FriendHistoryMessageResponse, (packet) => {
    if (_.isEmpty(packet.chatMessages)) {
        simpleError(i18n.t('notice.friendNoMoreHistoryMessage'));
        return;
    }
    store.commit('friend/addHistoryMessage', packet);
});


packetReceiver(SearchUserResponse, (packet) => {
    const userCaches = packet.userCaches;
    store.commit('friend/setSearchFriends', userCaches);
});

packetReceiver(NewApplyFriendNotice, (packet) => {
    const applyFriendVO = packet.applyFriendVO;
    store.commit('friend/addApplyFriend', applyFriendVO);
    desktopPush(i18n.t('friend.operation.apply.title'), applyFriendVO.friendCache.name, applyFriendVO.friendCache.avatar);
});

packetReceiver(NewFriendNotice, (packet) => {
    const userCacheA = packet.userCacheA;
    const userCacheB = packet.userCacheB;
    const friend = _.toString(store.state.user.id) === userCacheA.id ? userCacheB : userCacheA;
    store.commit('friend/removeApplyFriend', friend.id);
    store.commit('friend/addFriend', friend);
});

packetReceiver(RejectFriendResponse, (packet) => {
    store.commit('friend/removeApplyFriend', packet.friendId);
    simpleSuccess(i18n.t('notice.rejectFriendSuccess'));
});

packetReceiver(DeleteFriendResponse, (packet) => {
    store.commit('friend/removeFriend', packet.friendId);
    simpleSuccess(i18n.t('notice.deleteFriendSuccess'));
});

packetReceiver(BlacklistResponse, (packet) => {
    store.commit('friend/addBlacklist', packet.userCache);
    simpleSuccess(i18n.t('notice.blacklistSuccess'));
});

packetReceiver(BlacklistCancelResponse, (packet) => {
    store.commit('friend/removeBlacklist', packet.targetId);
    simpleSuccess(i18n.t('notice.blacklistCancelSuccess'));
});

packetReceiver(MarkFriendResponse, (packet) => {
    store.commit('friend/addFriendInfo', packet);
    simpleSuccess(i18n.t('notice.friendTagAddSuccess'));
    const friendChatTitle = store.state.app.friendChatTitle;
    if (!_.isNil(friendChatTitle)) {
        friendChatTitle.showSettingDialog(false);
    }
});
