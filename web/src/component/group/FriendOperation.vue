<template>
    <v-list two-line>
        <!-- 好友操作 -->
        <!-- 全部好友 -->
        <v-list-item @click="currentOperation=operations.allFriend.type">
            <v-list-item-icon>
                <v-icon color="primary">{{ operations.allFriend.icon }}</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
                <v-list-item-title>{{ $t(operations.allFriend.title) }}</v-list-item-title>
                <v-list-item-subtitle>{{ $t(operations.allFriend.subTitle) }}</v-list-item-subtitle>
            </v-list-item-content>
        </v-list-item>
        <!-- 好友申请列表 -->
        <v-list-item @click="currentOperation=operations.applyFriend.type">
            <v-list-item-icon>
                <v-icon color="primary">{{ operations.applyFriend.icon }}</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
                <v-list-item-title>{{ $t(operations.applyFriend.title) }}</v-list-item-title>
                <v-list-item-subtitle>{{ $t(operations.applyFriend.subTitle) }}</v-list-item-subtitle>
            </v-list-item-content>
            <v-list-item-icon v-show="redPointApplyFriends">
                <v-icon color="error">mdi-new-box</v-icon>
            </v-list-item-icon>
        </v-list-item>
        <!-- 黑名单 -->
        <v-list-item @click="currentOperation=operations.blacklist.type">
            <v-list-item-icon>
                <v-icon color="primary">{{ operations.blacklist.icon }}</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
                <v-list-item-title>{{ $t(operations.blacklist.title) }}</v-list-item-title>
                <v-list-item-subtitle>{{ $t(operations.blacklist.subTitle) }}</v-list-item-subtitle>
            </v-list-item-content>
        </v-list-item>
        <!-- 搜索好友 -->
        <v-list-item @click="showSearchDialog()">
            <v-list-item-icon>
                <v-icon color="primary">{{ operations.searchFriend.icon }}</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
                <v-list-item-title>{{ $t(operations.searchFriend.title) }}</v-list-item-title>
                <v-list-item-subtitle>{{ $t(operations.searchFriend.subTitle) }}</v-list-item-subtitle>
            </v-list-item-content>
        </v-list-item>

        <v-divider />
        <v-divider />

        <friend-all v-show="currentOperation===operations.allFriend.type" />

        <friend-apply v-show="currentOperation===operations.applyFriend.type" />

        <blacklist v-show="currentOperation===operations.blacklist.type" />

        <friend-search ref="friendSearch" />
    </v-list>
</template>

<script>

import { mapState } from 'vuex';

export default {
    name: 'FriendOperation',

    components: {
        FriendSearch: () => import('./friend/operation/FriendSearch.vue'),
        FriendApply: () => import('./friend/operation/FriendApply.vue'),
        FriendAll: () => import('./friend/operation/FriendAll.vue'),
        Blacklist: () => import('./friend/operation/Blacklist.vue')
    },

    data() {
        return {
            currentOperation: 0,
            operations: {
                allFriend: {
                    type: 0,
                    icon: 'mdi-account-settings',
                    title: 'friend.operation.all.title',
                    subTitle: 'friend.operation.all.subTitle'
                },
                applyFriend: {
                    type: 1,
                    icon: 'mdi-account-plus',
                    title: 'friend.operation.apply.title',
                    subTitle: 'friend.operation.apply.subTitle'
                },
                blacklist: {
                    type: 2,
                    icon: 'mdi-account-off',
                    title: 'friend.operation.blackList.title',
                    subTitle: 'friend.operation.blackList.subTitle'
                },
                searchFriend: {
                    type: 3,
                    icon: 'mdi-account-search',
                    title: 'friend.operation.search.title',
                    subTitle: 'friend.operation.search.subTitle'
                }
            }
        };
    },
    computed: {
        ...mapState('friend', ['applyFriends']),
        redPointApplyFriends() {
            if (_.isEmpty(this.applyFriends)) {
                return false;
            }
            return true;
        }
    },
    methods: {
        showSearchDialog() {
            this.$refs.friendSearch.showSearchDialog();
        }
    }
};
</script>
