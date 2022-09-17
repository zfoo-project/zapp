import Vue from 'vue';
import store from '@/store/store.js';
import { set } from '@/util/vuexUtils.js';
import { isBlank } from '@/util/stringUtils.js';

export default {
    namespaced: true,

    state: {
        applyFriends: [],
        friends: [],
        blacklist: [],

        searchFriends: []
    },


    actions: {
    },

    mutations: {
        setApplyFriends: set('applyFriends'),
        setFriends: set('friends'),
        setBlacklist: set('blacklist'),
        setSearchFriends: set('searchFriends'),

        addApplyFriend(state, applyFriendVO) {
            state.applyFriends.push(applyFriendVO);
        },

        removeApplyFriend(state, friendId) {
            const newApplyFriends = Array.from(state.applyFriends);
            _.remove(newApplyFriends, (it) => _.isEqual(it.friendId, friendId));
            state.applyFriends = newApplyFriends;
        },


        addFriend(state, friend) {
            if (_.every(state.friends, it => it.id !== friend.id)) {
                state.friends.push(friend);
            }
        },

        removeFriend(state, friendId) {
            const index = _.findIndex(state.friends, it => it.id === friendId);
            if (index < 0) {
                return;
            }
            state.friends.splice(index, 1);
        },

        addBlacklist(state, userCache) {
            state.blacklist.push(userCache);
        },

        removeBlacklist(state, targetId) {
            const index = _.findIndex(state.blacklist, it => it.id === targetId);
            if (index < 0) {
                return;
            }
            state.blacklist.splice(index, 1);
        },

        addChatMessage(state, friendMessageNotice) {
            if (_.isNil(friendMessageNotice)) {
                return;
            }

            let friend = _.find(state.friends, (it) => it.id === friendMessageNotice.uidA);
            if (_.isNil(friend)) {
                friend = _.find(state.friends, (it) => it.id === friendMessageNotice.uidB);
            }
            if (_.isNil(friend)) {
                console.error('收到信息，但是找不到好友' + friendMessageNotice);
                return;
            }

            const message = friendMessageNotice.chatMessage;
            let chatMessages = friend.chatMessages;
            if (_.isNil(chatMessages) || _.isEmpty(chatMessages)) {
                chatMessages = [];
                friend.chatMessages = chatMessages;
            }

            chatMessages.push(message);
            if (!store.state.app.group.getGroupDrawer() || store.state.app.group.getShow() !== 1) {
                friend.redPoint = true;
            } else {
                if (store.state.app.group.getFriendId() !== friend.id) {
                    friend.redPoint = true;
                }
            }

            const index = _.findIndex(state.friends, it => it.id === friend.id);
            Vue.set(state.friends, index, friend);
        },

        removeChatMessage(state, deleteFriendMessageNotice) {
            if (_.isNil(deleteFriendMessageNotice)) {
                return;
            }

            let friend = _.find(state.friends, (it) => it.id === deleteFriendMessageNotice.uidA);
            if (_.isNil(friend)) {
                friend = _.find(state.friends, (it) => it.id === deleteFriendMessageNotice.uidB);
            }
            if (_.isNil(friend)) {
                console.error('收到信息，但是找不到好友' + deleteFriendMessageNotice);
                return;
            }

            const messageId = deleteFriendMessageNotice.messageId;
            const chatMessages = friend.chatMessages;
            if (_.isNil(chatMessages) || _.isEmpty(chatMessages)) {
                return;
            }
            const messageIndex = _.findIndex(chatMessages, it => it.id === messageId);
            chatMessages.splice(messageIndex, 1);

            const friendIndex = _.findIndex(state.friends, it => it.id === friend.id);
            Vue.set(state.friends, friendIndex, friend);
        },

        editChatMessage(state, editFriendMessageNotice) {
            if (_.isNil(editFriendMessageNotice)) {
                return;
            }

            let friend = _.find(state.friends, (it) => it.id === editFriendMessageNotice.uidA);
            if (_.isNil(friend)) {
                friend = _.find(state.friends, (it) => it.id === editFriendMessageNotice.uidB);
            }
            if (_.isNil(friend)) {
                console.error('收到信息，但是找不到好友' + editFriendMessageNotice);
                return;
            }

            const messageId = editFriendMessageNotice.messageId;
            const chatMessage = editFriendMessageNotice.chatMessage;
            const chatMessages = friend.chatMessages;
            if (_.isNil(chatMessages) || _.isEmpty(chatMessages)) {
                return;
            }
            const messageVo = _.find(chatMessages, it => it.id === messageId);
            if (_.isNil(messageVo)) {
                return;
            }
            messageVo.message = chatMessage;
            const messageIndex = _.findIndex(chatMessages, it => it.id === messageId);
            Vue.set(chatMessages, messageIndex, messageVo);

            const friendIndex = _.findIndex(state.friends, it => it.id === friend.id);
            Vue.set(state.friends, friendIndex, friend);
        },

        addHistoryMessage(state, historyMessage) {
            if (_.isNil(historyMessage)) {
                return;
            }

            let friend = _.find(state.friends, (it) => it.id === historyMessage.uidA);
            if (_.isNil(friend)) {
                friend = _.find(state.friends, (it) => it.id === historyMessage.uidB);
            }
            if (_.isNil(friend)) {
                console.error('收到信息，但是找不到好友' + historyMessage);
                return;
            }

            const messages = historyMessage.chatMessages;
            let chatMessages = friend.chatMessages;
            if (_.isNil(chatMessages) || _.isEmpty(chatMessages)) {
                chatMessages = [];
                friend.chatMessages = chatMessages;
            }

            friend.chatMessages = _.concat(messages, chatMessages);
            state.friends.forEach((it, index) => {
                if (it.id === friend.id) {
                    Vue.set(state.friends, index, it);
                }
            });
        },

        addFriendInfos(state, friendInfoMap) {
            if (_.isEmpty(friendInfoMap)) {
                return;
            }

            for (const [key, value] of friendInfoMap) {
                if (_.isEmpty(value)) {
                    continue;
                }
                const friend = _.find(state.friends, (it) => it.id === key);
                if (_.isNil(friend)) {
                    continue;
                }
                const refreshTime = value.refreshTime;
                const readTime = value.readTime;
                friend.refreshTime = refreshTime;
                friend.readTime = readTime;
                if (_.toNumber(refreshTime) > _.toNumber(readTime)) {
                    friend.redPoint = true;
                }
                if (isBlank(value.tag)) {
                    continue;
                }
                friend.tag = value.tag;
            }
        },

        addFriendInfo(state, friendInfo) {
            if (_.isNil(friendInfo)) {
                return;
            }

            const friend = _.find(state.friends, (it) => it.id === friendInfo.friendId);
            if (_.isNil(friend)) {
                return;
            }

            if (isBlank(friendInfo.tag)) {
                return;
            }
            friend.tag = friendInfo.tag;

            const index = _.findIndex(state.friends, it => it.id === friendInfo.friendId);
            Vue.set(state.friends, index, friend);
        },


        removeFriendRedPoint(state, friendId) {
            if (_.isNil(friendId)) {
                return;
            }

            const friend = _.find(state.friends, it => it.id === friendId);
            if (_.isNil(friend)) {
                return;
            }

            friend.redPoint = false;
            if (!_.isEmpty(friend.chatMessages)) {
                friend.chatMessages.forEach(it => {
                    if (it.sendId === friendId) {
                        it.read = true;
                    }
                });
            }
            const index = _.findIndex(state.friends, it => it.id === friendId);
            Vue.set(state.friends, index, friend);
        }
    }
};
