import Vue from 'vue';
import store from '@/store/store.js';
import { set } from '@/util/vuexUtils.js';
import { findChannelFromGroupVo, updateGroupRedPoint } from '@/util/groupUtils.js';
import { groupConstant } from '@/constant/constant.js';

export default {
    namespaced: true,

    state: {
        groups: [],
        channels: [],
        groupMemberSimpleVos: [],

        // 用来标记groupMemberVos是哪一个群组
        groupIdOfGroupMemberVos: '',
        groupMemberVos: []
    },

    mutations: {
        setGroups: set('groups'),
        setGroupMemberSimpleVos: set('groupMemberSimpleVos'),

        createGroup(state, groupVo) {
            state.groups.push(groupVo);
            state.groupMemberSimpleVos.push({
                groupId: groupVo.id,
                memberId: groupVo.adminId,
                groupTime: {
                    groupId: groupVo.id,
                    refreshTime: '0',
                    channelTimes: []
                },
                groupAuthIds: groupVo.groupAuths.map(it => it.id)
            });
        },

        removeGroup(state, groupId) {
            if (_.isNil(groupId)) {
                return;
            }

            const groupVo = _.find(state.groups, it => it.id === groupId);
            if (_.isNil(groupVo)) {
                return;
            }

            // 移除全部的channel
            for (const channelBox of groupVo.channelBoxes) {
                for (const channel of channelBox.channels) {
                    const channelIndex = _.findIndex(state.channels, it => it.id === channel.id);
                    state.channels.splice(channelIndex, 1);
                }
            }

            // 移除全部的groupSimpleVo
            const groupMemberSimpleVoIndex = _.findIndex(state.groupMemberSimpleVos, it => it.groupId === groupId);
            if (groupMemberSimpleVoIndex >= 0) {
                state.groupMemberSimpleVos.splice(groupMemberSimpleVoIndex, 1);
            }

            // 移除全部的group
            const groupIndex = _.findIndex(state.groups, it => it.id === groupId);
            if (groupIndex >= 0) {
                state.groups.splice(groupIndex, 1);
            }
        },

        updateGroup(state, groupVo) {
            if (_.isNil(groupVo)) {
                return;
            }

            const groupMemberSimpleVo = _.find(state.groupMemberSimpleVos, it => it.groupId === groupVo.id);
            updateGroupRedPoint(groupVo, groupMemberSimpleVo);

            const index = _.findIndex(state.groups, it => it.id === groupVo.id);
            if (index < 0) {
                state.groups.push(groupVo);
                return;
            }
            Vue.set(state.groups, index, groupVo);
        },

        updateGroupMemberSimpleVo(state, memberGroupAuthIdUpdateNotice) {
            if (_.isNil(memberGroupAuthIdUpdateNotice)) {
                return;
            }

            const groupId = memberGroupAuthIdUpdateNotice.groupId;
            const memberId = memberGroupAuthIdUpdateNotice.memberId;
            const groupAuthIds = memberGroupAuthIdUpdateNotice.groupAuthIds;

            let groupMemberSimpleVo = _.find(state.groupMemberSimpleVos, it => it.groupId === groupId);
            if (_.isNil(groupMemberSimpleVo)) {
                groupMemberSimpleVo = {
                    groupId: groupId,
                    memberId: memberId,
                    groupTime: {
                        groupId: groupId,
                        channelTimes: []
                    },
                    groupAuthIds: groupAuthIds
                };
                state.groupMemberSimpleVos.push(groupMemberSimpleVo);
            }
            groupMemberSimpleVo.groupAuthIds = groupAuthIds;

            const groupVo = _.find(state.groups, (it) => it.id === groupId);
            updateGroupRedPoint(groupVo, groupMemberSimpleVo);

            const groupTimeIndex = _.findIndex(state.groupMemberSimpleVos, it => it.groupId === memberGroupAuthIdUpdateNotice.groupId);
            Vue.set(state.groupMemberSimpleVos, groupTimeIndex, groupMemberSimpleVo);

            const groupIndex = _.findIndex(state.groups, it => it.id === groupVo.id);
            Vue.set(state.groups, groupIndex, groupVo);
        },

        saveGroupAvatar(state, saveGroupAvatarResponse) {
            if (_.isNil(saveGroupAvatarResponse)) {
                return;
            }

            const groupVo = _.find(state.groups, (it) => it.id === saveGroupAvatarResponse.groupId);
            if (_.isNil(groupVo)) {
                console.error('收到信息，但是找不到群组' + saveGroupAvatarResponse);
                return;
            }

            groupVo.avatar = saveGroupAvatarResponse.avatar;
            const index = _.findIndex(state.groups, it => it.id === saveGroupAvatarResponse.groupId);
            Vue.set(state.groups, index, groupVo);
        },

        saveGroupBackground(state, saveGroupBackgroundResponse) {
            if (_.isNil(saveGroupBackgroundResponse)) {
                return;
            }

            const groupVo = _.find(state.groups, (it) => it.id === saveGroupBackgroundResponse.groupId);
            if (_.isNil(groupVo)) {
                console.error('收到信息，但是找不到群组' + saveGroupBackgroundResponse);
                return;
            }

            groupVo.background = saveGroupBackgroundResponse.background;
            const index = _.findIndex(state.groups, it => it.id === saveGroupBackgroundResponse.groupId);
            Vue.set(state.groups, index, groupVo);
        },

        saveGroupSetting(state, saveGroupSettingResponse) {
            if (_.isNil(saveGroupSettingResponse)) {
                return;
            }

            const groupVo = _.find(state.groups, (it) => it.id === saveGroupSettingResponse.groupId);
            if (_.isNil(groupVo)) {
                console.error('收到信息，但是找不到群组' + saveGroupSettingResponse);
                return;
            }

            groupVo.name = saveGroupSettingResponse.name;
            const index = _.findIndex(state.groups, it => it.id === saveGroupSettingResponse.groupId);
            Vue.set(state.groups, index, groupVo);
        },

        addChatMessage(state, chatMessageNotice) {
            if (_.isNil(chatMessageNotice)) {
                return;
            }

            const groupId = chatMessageNotice.groupId;
            const channelId = chatMessageNotice.channelId;

            let messageChannel = _.find(state.channels, (it) => it.id === channelId);
            if (_.isNil(messageChannel)) {
                messageChannel = {
                    id: channelId,
                    chatMessages: []
                };
                state.channels.push(messageChannel);
            }

            const message = chatMessageNotice.chatMessage;
            messageChannel.chatMessages.push(message);
            if (_.size(messageChannel.chatMessages) > groupConstant.channelMaxMessageNum) {
                messageChannel.chatMessages = _.takeRight(messageChannel.chatMessages, groupConstant.channelMaxMessageNum);
            }
            const channelIndex = _.findIndex(state.channels, it => it.id === channelId);
            Vue.set(state.channels, channelIndex, messageChannel);

            // 小红点逻辑
            const groupVo = _.find(state.groups, it => it.id === groupId);
            const groupMemberSimpleVo = _.find(state.groupMemberSimpleVos, it => it.groupId === groupId);
            if (_.isNil(groupVo) || _.isNil(groupMemberSimpleVo)) {
                return;
            }
            // 群组静音不显示小红点
            if (groupMemberSimpleVo.groupTime.mute) {
                return;
            }
            // 频道静音不显示小红点
            const channelTime = _.find(groupMemberSimpleVo.groupTime.channelTimes, it => it.channelId === channelId);
            if (!_.isNil(channelTime) && channelTime.mute) {
                return;
            }
            const groupChannel = findChannelFromGroupVo(groupVo, channelId);
            groupChannel.refreshTime = message.timestamp;

            const groupVue = store.state.app.group;
            if (groupVue.getGroupDrawer() && groupVue.getGroupId() === groupId && groupVue.getChannelId() === channelId) {
                // do nothing
            } else {
                updateGroupRedPoint(groupVo, groupMemberSimpleVo);
            }

            const groupIndex = _.findIndex(state.groups, it => it.id === groupId);
            Vue.set(state.groups, groupIndex, groupVo);
        },

        deleteChatMessage(state, deleteGroupChatMessageNotice) {
            if (_.isNil(deleteGroupChatMessageNotice)) {
                return;
            }

            const channelId = deleteGroupChatMessageNotice.channelId;
            const messageId = deleteGroupChatMessageNotice.messageId;

            const messageChannel = _.find(state.channels, (it) => it.id === channelId);
            if (_.isNil(messageChannel)) {
                return;
            }

            const messageIndex = _.findIndex(messageChannel.chatMessages, it => it.id === messageId);
            messageChannel.chatMessages.splice(messageIndex, 1);

            const channelIndex = _.findIndex(state.channels, it => it.id === channelId);
            Vue.set(state.channels, channelIndex, messageChannel);
        },

        editChatMessage(state, editGroupMessageNotice) {
            if (_.isNil(editGroupMessageNotice)) {
                return;
            }

            const channelId = editGroupMessageNotice.channelId;
            const messageId = editGroupMessageNotice.messageId;
            const chatMessage = editGroupMessageNotice.chatMessage;

            const messageChannel = _.find(state.channels, (it) => it.id === channelId);
            if (_.isNil(messageChannel)) {
                return;
            }
            const chatMessages = messageChannel.chatMessages;
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

            const channelIndex = _.findIndex(state.channels, it => it.id === channelId);
            Vue.set(state.channels, channelIndex, messageChannel);
        },

        refreshTimeGroup(state, refreshChannelTimeResponse) {
            if (_.isNil(refreshChannelTimeResponse)) {
                return;
            }

            const groupId = refreshChannelTimeResponse.groupId;
            const channelId = refreshChannelTimeResponse.channelId;
            const refreshTime = refreshChannelTimeResponse.refreshTime;

            // 小红点逻辑
            const groupVo = _.find(state.groups, it => it.id === groupId);
            const groupMemberSimpleVo = _.find(state.groupMemberSimpleVos, it => it.groupId === groupId);
            if (_.isNil(groupVo) || _.isNil(groupMemberSimpleVo)) {
                return;
            }

            const channelTimeVo = _.find(groupMemberSimpleVo.groupTime.channelTimes, it => it.channelId === channelId);
            if (_.isNil(channelTimeVo)) {
                groupMemberSimpleVo.groupTime.channelTimes.push({
                    channelId: channelId,
                    mute: false,
                    refreshTime: refreshTime
                });
            } else {
                channelTimeVo.refreshTime = refreshTime;
            }

            updateGroupRedPoint(groupVo, groupMemberSimpleVo);

            const groupIndex = _.findIndex(state.groups, it => it.id === groupId);
            Vue.set(state.groups, groupIndex, groupVo);
            const groupTimeIndex = _.findIndex(state.groupMemberSimpleVos, it => it.groupId === groupId);
            Vue.set(state.groupMemberSimpleVos, groupTimeIndex, groupMemberSimpleVo);
        },

        muteGroup(state, muteGroupResponse) {
            if (_.isNil(muteGroupResponse)) {
                return;
            }

            const groupId = muteGroupResponse.groupId;
            const mute = muteGroupResponse.mute;

            const groupMemberSimpleVo = _.find(state.groupMemberSimpleVos, it => it.groupId === groupId);
            if (_.isNil(groupMemberSimpleVo)) {
                return;
            }

            groupMemberSimpleVo.groupTime.mute = mute;

            const groupTimeIndex = _.findIndex(state.groupMemberSimpleVos, it => it.groupId === groupId);
            Vue.set(state.groupMemberSimpleVos, groupTimeIndex, groupMemberSimpleVo);
        },

        muteChannel(state, muteChannelResponse) {
            if (_.isNil(muteChannelResponse)) {
                return;
            }

            const groupId = muteChannelResponse.groupId;
            const channelId = muteChannelResponse.channelId;
            const mute = muteChannelResponse.mute;
            const refreshTime = muteChannelResponse.refreshTime;

            const groupVo = _.find(state.groups, it => it.id === groupId);
            const groupMemberSimpleVo = _.find(state.groupMemberSimpleVos, it => it.groupId === groupId);
            if (_.isNil(groupVo) || _.isNil(groupMemberSimpleVo)) {
                return;
            }

            const channelTimeVo = _.find(groupMemberSimpleVo.groupTime.channelTimes, it => it.channelId === channelId);
            if (_.isNil(channelTimeVo)) {
                groupMemberSimpleVo.groupTime.channelTimes.push({
                    channelId: channelId,
                    mute: mute,
                    refreshTime: refreshTime
                });
            } else {
                channelTimeVo.mute = mute;
            }

            const groupTimeIndex = _.findIndex(state.groupMemberSimpleVos, it => it.groupId === groupId);
            Vue.set(state.groupMemberSimpleVos, groupTimeIndex, groupMemberSimpleVo);
        },

        addHistoryMessage(state, groupHistoryMessageResponse) {
            if (_.isNil(groupHistoryMessageResponse)) {
                return;
            }

            let messageChannel = _.find(state.channels, (it) => it.id === groupHistoryMessageResponse.channelId);
            if (_.isNil(messageChannel)) {
                messageChannel = {
                    id: groupHistoryMessageResponse.channelId,
                    chatMessages: []
                };
                state.channels.push(messageChannel);
            }

            const messages = groupHistoryMessageResponse.chatMessages;
            messageChannel.chatMessages = _.concat(messages, messageChannel.chatMessages);

            const index = _.findIndex(state.channels, it => it.id === groupHistoryMessageResponse.channelId);
            Vue.set(state.channels, index, messageChannel);
        },

        addGroupMembers(state, groupMemberListResponse) {
            if (_.isNil(groupMemberListResponse.groupId !== state.groupIdOfGroupMemberVos) || groupMemberListResponse.page === 1) {
                state.groupIdOfGroupMemberVos = groupMemberListResponse.groupId;
                state.groupMemberVos = [];
            }

            state.groupMemberVos = _.concat(state.groupMemberVos, groupMemberListResponse.members);
        },

        removeGroupMember(state, kickMemberResponse) {
            const groupId = kickMemberResponse.groupId;
            const memberId = kickMemberResponse.memberId;

            if (groupId === state.groupIdOfGroupMemberVos) {
                const index = _.findIndex(state.groupMemberVos, it => it.userCache.id === memberId);
                if (index < 0) {
                    return;
                }
                state.groupMemberVos.splice(index, 1);
            }
        },

        addGroupAuthToMember(state, addMemberToGroupAuthResponse) {
            const groupAuthId = addMemberToGroupAuthResponse.groupAuthId;
            const memberId = addMemberToGroupAuthResponse.memberId;

            const groupMemberVo = _.find(state.groupMemberVos, (it) => it.userCache.id === memberId);
            if (_.isNil(groupMemberVo)) {
                return;
            }
            const groupAuthIds = groupMemberVo.groupAuthIds;
            _.remove(groupAuthIds, it => it === groupAuthId);
            groupAuthIds.push(groupAuthId);

            const index = _.findIndex(state.groupMemberVos, it => it.userCache.id === memberId);
            Vue.set(state.groupMemberVos, index, groupMemberVo);
        },

        removeGroupAuthFromMember(state, removeMemberFromGroupAuthResponse) {
            const groupAuthId = removeMemberFromGroupAuthResponse.groupAuthId;
            const memberId = removeMemberFromGroupAuthResponse.memberId;

            const groupMemberVo = _.find(state.groupMemberVos, (it) => it.userCache.id === memberId);
            if (_.isNil(groupMemberVo)) {
                return;
            }
            const groupAuthIds = groupMemberVo.groupAuthIds;
            _.remove(groupAuthIds, it => it === groupAuthId);

            const index = _.findIndex(state.groupMemberVos, it => it.userCache.id === memberId);
            Vue.set(state.groupMemberVos, index, groupMemberVo);
        },

        clearGroupState(state) {
            state.groups = [];
            state.channels = [];
            state.groupMemberSimpleVos = [];
            state.groupIdOfGroupMemberVos = '';
            state.groupMemberVos = [];
        }

    }

};
