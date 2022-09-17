<template>
    <div>
        <v-navigation-drawer
            v-model="groupDrawer"
            app
            temporary
            right
            :width="groupWidth"
        >
            <v-tabs v-show="show === 0" vertical icons-and-text>
                <v-tab>
                    <v-badge v-show="redPointTab0" dot color="error">
                        <v-avatar size="50">
                            <v-img :src="require(`@/asset/logo/zfoo_normal.jpg`)" />
                        </v-avatar>
                    </v-badge>
                    <v-avatar v-show="!redPointTab0" size="50">
                        <v-img :src="require(`@/asset/logo/zfoo_normal.jpg`)" />
                    </v-avatar>
                </v-tab>

                <v-tab>
                    <v-badge v-show="redPointTab1" dot color="error">
                        <v-icon x-large>mdi-waves</v-icon>
                    </v-badge>
                    <v-icon v-show="!redPointTab1" x-large>mdi-waves</v-icon>
                </v-tab>

                <v-tab>
                    <v-icon x-large>mdi-pinwheel-outline</v-icon>
                    <!--                    <v-icon x-large>mdi-pinwheel-outline mdi-spin</v-icon>-->
                </v-tab>

                <v-tab v-for="(group, index) in groups" :key="'tab' + index">
                    <v-badge v-show="getRedPointFromGroup(group)" dot color="error">
                        <v-avatar size="50">
                            <!--TODO: 为了让测试环境也能看到头像，先用固定的图片-->
                            <v-img :src="require(`@/asset/icon/default_avatar.gif`)" />
                            <!--<v-img :src="toSimpleAvatarUrl(group.avatar)" />-->
                        </v-avatar>
                    </v-badge>
                    <v-avatar v-show="!getRedPointFromGroup(group)" size="50">
                        <v-img :src="toSimpleAvatarUrl(group.avatar)" />
                    </v-avatar>
                </v-tab>


                <v-tab-item>
                    <friend-hot />
                </v-tab-item>

                <v-tab-item>
                    <friend-operation />
                </v-tab-item>

                <!-- 群组的操作，创建和查找 -->
                <v-tab-item>
                    <group-operation />
                </v-tab-item>

                <v-tab-item v-for="(group, index) in groups" :key="'tabItem' + index">
                    <group-info :group-id="group.id" />
                </v-tab-item>
            </v-tabs>

            <!-- 好友聊天界面 -->
            <friend-chat v-show="show===1" :friend-id="friendId" />

            <!-- 群组聊天界面 -->
            <group-chat v-show="show===2" :group-id="groupId" :channel-id="channelId" />

            <!-- 群组的组员界面 -->
            <member-info v-show="show===3" ref="memberInfo" :group-id="groupId" />

            <!-- 好友聊天界面的header和footer -->
            <!-- 群组聊天界面的header和footer -->
            <template v-slot:prepend>
                <friend-chat-title v-show="show===1" ref="friendChatTitle" :friend-id="friendId" />
                <group-chat-title v-show="show===2" ref="groupChatTitle" :group-id="groupId" :channel-id="channelId" />
                <member-info-title v-show="show===3" ref="memberInfoTitle" :group-id="groupId" />
            </template>
            <template v-slot:append>
                <friend-chat-input v-show="show===1" ref="friendChatInput" :friend-id="friendId" />
                <group-chat-input v-show="show===2" ref="groupChatInput" :group-id="groupId" :channel-id="channelId" />
            </template>
        </v-navigation-drawer>

        <!-- 右下角的群组按钮 -->
        <group-button />
    </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
import { toSimpleAvatarUrl } from '@/util/fileUtils.js';

export default {
    name: 'CoreGroup',

    components: {
        GroupButton: () => import('./group/GroupButton.vue'),
        FriendHot: () => import('./group/FriendHot.vue'),
        FriendOperation: () => import('./group/FriendOperation.vue'),
        FriendChat: () => import('./group/friend/chat/FriendChat.vue'),
        FriendChatTitle: () => import('./group/friend/chat/FriendChatTitle.vue'),
        FriendChatInput: () => import('./group/friend/chat/FriendChatInput.vue'),
        GroupOperation: () => import('./group/GroupOperation.vue'),
        GroupInfo: () => import('./group/GroupInfo.vue'),
        GroupChat: () => import('./group/group/chat/GroupChat.vue'),
        GroupChatTitle: () => import('./group/group/chat/GroupChatTitle.vue'),
        GroupChatInput: () => import('./group/group/chat/GroupChatInput.vue'),
        MemberInfo: () => import('./group/group/member/MemberInfo.vue'),
        MemberInfoTitle: () => import('./group/group/member/MemberInfoTitle.vue')
    },

    data() {
        return {
            toSimpleAvatarUrl,

            groupDrawer: false,
            groupWidth: this.$vuetify.breakpoint.mdAndUp ? this.$vuetify.breakpoint.width * 0.3 : this.$vuetify.breakpoint.width * 0.9,

            show: 0,
            friendId: '',
            groupId: '',
            channelId: ''
        };
    },

    computed: {
        ...mapState('friend', ['friends', 'applyFriends']),
        ...mapState('group', ['groups', 'groupMemberSimpleVos']),

        redPointTab0() {
            if (_.isEmpty(this.friends)) {
                return false;
            }
            return _.some(this.friends, it => it.redPoint === true);
        },
        redPointTab1() {
            if (_.isEmpty(this.applyFriends)) {
                return false;
            }
            return true;
        },
        redPointTabGroups() {
            if (_.isEmpty(this.groups)) {
                return false;
            }
            return _.some(this.groups, it => it.redPoint === true);
        }
    },

    created() {
        this.init();
    },

    methods: {
        ...mapMutations('app', ['setFriendChatTitle', 'setFriendChatInput', 'setGroupChatTitle', 'setGroupChatInput']),

        init() {
            setTimeout(() => {
                if (_.isNil(this.$refs.groupChatInput)) {
                    this.init();
                    return;
                }
                this.setFriendChatTitle(this.$refs.friendChatTitle);
                this.setFriendChatInput(this.$refs.friendChatInput);
                this.setGroupChatTitle(this.$refs.groupChatTitle);
                this.setGroupChatInput(this.$refs.groupChatInput);

                const refreshTitle = this.refreshTitle;
                setInterval(function() {
                    refreshTitle();
                }, 500);
            }, 500);
        },

        refreshTitle() {
            // 如果有新的消息则闪烁标题栏
            if (this.getRedPoint() === true) {
                if (document.titleFlag === true) {
                    document.title = this.$t(this.$route.meta.title);
                    document.titleFlag = false;
                } else {
                    document.title = this.$t('user.chat.newMessage');
                    document.titleFlag = true;
                }
            } else {
                if (document.titleFlag === true) {
                    document.title = this.$t(this.$route.meta.title);
                    document.titleFlag = false;
                }
            }
        },

        clearGroupState() {
            this.show = 0;
            this.friendId = '';
            this.groupId = '';
            this.channelId = '';
        },

        getRedPointFromGroup(groupVo) {
            for (const channelBox of groupVo.channelBoxes) {
                for (const channel of channelBox.channels) {
                    if (channel.redPoint === true) {
                        return true;
                    }
                }
            }
            return false;
        },

        getGroupId() {
            return this.groupId;
        },

        getChannelId() {
            return this.channelId;
        },

        getGroupDrawer() {
            return this.groupDrawer;
        },

        getMemberInfo() {
            return this.$refs.memberInfo;
        },

        getShow() {
            return this.show;
        },

        getFriendId() {
            return this.friendId;
        },

        getRedPoint() {
            return this.redPointTab0 || this.redPointTab1 || this.redPointTabGroups;
        },

        setGroupDrawer(value) {
            this.groupDrawer = value;
        },

        setShow(value) {
            this.show = value;
        },

        showGroupHome() {
            this.clearGroupState();
        },

        showFriendChatPage(friendId) {
            this.friendId = friendId;
            this.show = 1;
        },

        showGroupChatPage(groupId, channelId) {
            this.groupId = groupId;
            this.channelId = channelId;
            this.show = 2;
        },

        showMemberInfoPage(groupId) {
            this.groupId = groupId;
            this.show = 3;
            this.$refs.memberInfo.init(groupId);
        }

    }
};
</script>
