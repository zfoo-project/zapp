<template>
    <v-dialog v-model="dialog" width="700">
        <v-card>
            <v-card-title>{{ $t('friend.operation.search.title') }}</v-card-title>
            <v-card-text>
                <v-text-field
                    v-model="query"
                    :label="$t('friend.operation.search.inputLabel')"
                    prepend-icon="mdi-cloud-search"
                    type="text"
                    @blur="onBlur()"
                    @keyup.exact.enter="search()"
                />
            </v-card-text>


            <v-card-actions>
                <v-spacer />
                <v-btn class="text-capitalize" color="primary" @click="search()">{{ $t('friend.operation.search.searchButton') }}</v-btn>
            </v-card-actions>

            <user-list :user-profile-array="searchFriends" :md-cols="12" :user-actions="userActions" />
        </v-card>
    </v-dialog>
</template>

<script>
import { sendPacket } from '@/util/websocketUtils.js';
import { mapState } from 'vuex';
import { simpleError } from '@/util/noticeUtils.js';
import { fixIosInput } from '@/util/fixBugUtils.js';

import SearchUserRequest from '@/jsProtocol/cache/SearchUserRequest.js';
import ApplyFriendRequest from '@/jsProtocol/friend/operation/ApplyFriendRequest.js';

export default {
    name: 'FriendSearch',
    components: {
        UserList: () => import('@/component/base/UserList.vue')
    },
    data() {
        return {
            dialog: false,
            query: null,
            userActions: [
                {
                    icon: 'mdi-plus-circle-multiple-outline',
                    color: 'primary',
                    callback: this.addFriend
                }
            ]
        };
    },
    computed: {
        ...mapState('app', ['ios']),
        ...mapState('friend', ['searchFriends'])
    },
    methods: {
        showSearchDialog() {
            this.dialog = true;
        },
        search() {
            sendPacket(new SearchUserRequest(this.query));
        },
        addFriend(userCache) {
            if (userCache.id === this.$store.state.user.id) {
                simpleError(this.$t('notice.friendAddSelfError'));
                return;
            }
            sendPacket(new ApplyFriendRequest(userCache.id, this.$store.state.user.id));
        },
        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        }
    }
};
</script>
