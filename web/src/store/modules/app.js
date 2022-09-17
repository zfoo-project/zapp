import Vue from 'vue';
import { set } from '@/util/vuexUtils.js';

// vue 包含的数组操作方法（变异方法）将会触发视图更新
// push() 从数组末端添加项
// pop() 从数组末端删除项
// shift() 从数组头部删除项
// unshift() 从数组头部添加项
// splice(index,num) 删除 ,num希望删除元素的数量
// sort() 排序
// reverse() 反转
export default {
    namespaced: true,

    state: {
        snackbar: null,
        appSearch: null,
        appSearchSelector: null,
        loginDialog: null,

        // home页
        home: null,

        // 我的个人信息主页
        myProfile: null,

        // 好友信息的头部
        friendChatTitle: null,
        // 好友聊天输入框
        friendChatInput: null,
        // 群组信息的头部
        groupChatTitle: null,
        // 群组聊天输入框
        groupChatInput: null,

        // 是否是移动端
        mobile: false,
        // 是否是ios系统
        ios: false,
        // 是否是safari浏览器
        safari: false,
        // 是否是微信内置浏览器
        weChat: false,

        group: null,
        groupOperation: null,
        groupInfo: null,

        // 通过websocket获取到的用户profile，用于在message中显示头像
        profile: {},
        // 通过websocket获取到的群组的组员信息，用于在message中显示头像
        groupIdOfGroupMemberVo: '',
        groupMemberVo: {},
        // 通过websocket获取到的pin消息
        pinMessages: [],
        // 复制聊天信息的缓存，有两个值type和message
        copyMessage: null
    },

    mutations: {
        setSnackbar: set('snackbar'),
        setAppSearch: set('appSearch'),
        setAppSearchSelector: set('appSearchSelector'),
        setLoginDialog: set('loginDialog'),

        setHome: set('home'),

        setMyProfile: set('myProfile'),

        setFriendChatTitle: set('friendChatTitle'),
        setFriendChatInput: set('friendChatInput'),
        setGroupChatTitle: set('groupChatTitle'),
        setGroupChatInput: set('groupChatInput'),

        setMobile: set('mobile'),
        setIos: set('ios'),
        setSafari: set('safari'),
        setWeChat: set('weChat'),

        setGroup: set('group'),
        setGroupOperation: set('groupOperation'),
        setGroupInfo: set('groupInfo'),

        setProfile: set('profile'),
        setGroupIdOfGroupMemberVo: set('groupIdOfGroupMemberVo'),
        setGroupMemberVo: set('groupMemberVo'),
        setPinMessages: set('pinMessages'),
        setCopyMessage: set('copyMessage'),

        addGroupAuthToMemberVo(state, addMemberToGroupAuthResponse) {
            if (_.isEmpty(state.groupMemberVo)) {
                return;
            }
            if (state.groupIdOfGroupMemberVo !== addMemberToGroupAuthResponse.groupId) {
                return;
            }
            if (state.groupMemberVo.userCache.id !== addMemberToGroupAuthResponse.memberId) {
                return;
            }

            const groupAuthIds = state.groupMemberVo.groupAuthIds;
            _.remove(groupAuthIds, it => it === addMemberToGroupAuthResponse.groupAuthId);
            groupAuthIds.push(addMemberToGroupAuthResponse.groupAuthId);
            // 更新一个属性
            Vue.set(state.groupMemberVo, 'groupAuthIds', groupAuthIds);
        },

        removeGroupAuthFromMemberVo(state, removeMemberFromGroupAuthResponse) {
            if (_.isEmpty(state.groupMemberVo)) {
                return;
            }
            if (state.groupIdOfGroupMemberVo !== removeMemberFromGroupAuthResponse.groupId) {
                return;
            }
            if (state.groupMemberVo.userCache.id !== removeMemberFromGroupAuthResponse.memberId) {
                return;
            }

            const groupAuthIds = state.groupMemberVo.groupAuthIds;
            const index = _.findIndex(groupAuthIds, it => it === removeMemberFromGroupAuthResponse.groupAuthId);
            if (index < 0) {
                return;
            }
            groupAuthIds.splice(index, 1);

            // 更新一个属性
            Vue.set(state.groupMemberVo, 'groupAuthIds', groupAuthIds);
        },

        addPinMessages(state, groupHistoryPinMessageResponse) {
            if (_.isNil(groupHistoryPinMessageResponse)) {
                return;
            }
            const lastMessageId = groupHistoryPinMessageResponse.lastMessageId;
            const chatMessages = groupHistoryPinMessageResponse.chatMessages;

            if (_.toNumber(lastMessageId) === -1) {
                state.pinMessages = [];
            }

            state.pinMessages = _.concat(chatMessages, state.pinMessages);
        },

        removePinMessages(state, deleteGroupChatMessageNotice) {
            if (_.isNil(deleteGroupChatMessageNotice)) {
                return;
            }

            const messageId = deleteGroupChatMessageNotice.messageId;

            const messageIndex = _.findIndex(state.pinMessages, it => it.id === messageId);
            if (messageIndex < 0) {
                return;
            }
            state.pinMessages.splice(messageIndex, 1);
        },

        editChatMessage(state, editGroupMessageNotice) {
            if (_.isNil(editGroupMessageNotice)) {
                return;
            }

            const messageId = editGroupMessageNotice.messageId;
            const chatMessage = editGroupMessageNotice.chatMessage;

            const messageVo = _.find(state.pinMessages, it => it.id === messageId);
            if (_.isNil(messageVo)) {
                return;
            }
            messageVo.message = chatMessage;

            const messageIndex = _.findIndex(state.pinMessages, it => it.id === messageId);
            Vue.set(state.pinMessages, messageIndex, messageVo);
        }
    }
};
