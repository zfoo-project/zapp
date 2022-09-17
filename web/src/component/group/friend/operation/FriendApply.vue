<template>
    <div>
        <v-dialog v-model="dialog" width="500">
            <time-avatar :user-profile="currentUserProfile" />
        </v-dialog>

        <template v-for="(friend) in applyFriends">

            <v-list-item :key="friend.friendId" @click="friendInfo(friend)">
                <v-list-item-avatar>
                    <v-img :src="toSimpleAvatarUrl(friend.friendCache.avatar)" />
                </v-list-item-avatar>

                <v-list-item-content>
                    <v-list-item-title>{{ toTagOrName(friend.friendCache) }}</v-list-item-title>
                    <v-list-item-subtitle>
                        <span
                            class="d-inline-block text-truncate"
                            :style="subTitleWidthStyle"
                        >
                            {{ getCustomizedTime(friend.timestamp) }}
                        </span>
                    </v-list-item-subtitle>
                </v-list-item-content>

                <v-list-item-action class="ma-0 pa-0">
                    <v-btn fab x-small icon color="green lighten-1" @click.stop="acceptFriend(friend.friendId)">
                        <v-icon>mdi-check</v-icon>
                    </v-btn>
                </v-list-item-action>

                <v-list-item-action class="ma-0 pa-0">
                    <v-btn fab x-small icon color="red lighten-1" @click.stop="rejectFriend(friend.friendId)">
                        <v-icon>mdi-close</v-icon>
                    </v-btn>
                </v-list-item-action>

            </v-list-item>

            <v-divider :key="'time' + friend.timestamp" />
        </template>
    </div>
</template>

<script>
import { mapState } from 'vuex';
import { getCustomizedTime } from '@/util/timeUtils.js';
import { sendPacket } from '@/util/websocketUtils.js';
import { toSimpleAvatarUrl } from '@/util/fileUtils.js';
import { toTagOrName } from '@/util/stringUtils.js';

import AcceptFriendRequest from '@/jsProtocol/friend/operation/AcceptFriendRequest.js';
import RejectFriendRequest from '@/jsProtocol/friend/operation/RejectFriendRequest.js';

export default {
    name: 'FriendChat',
    components: {
        TimeAvatar: () => import('@/component/time/TimeAvatar.vue')
    },
    data() {
        return {
            getCustomizedTime,
            toSimpleAvatarUrl,
            toTagOrName,
            dialog: false,
            currentUserProfile: null,
            subTitleWidthStyle: this.$vuetify.breakpoint.mdAndUp ? 'max-width: 160px;' : 'max-width: 120px;'
        };
    },
    computed: {
        ...mapState('user', ['id']),
        ...mapState('friend', ['applyFriends'])
    },
    methods: {
        friendInfo(friend) {
            this.currentUserProfile = friend.friendCache;
            this.dialog = true;
        },

        acceptFriend(friendId) {
            sendPacket(new AcceptFriendRequest(friendId, this.id));
        },
        rejectFriend(friendId) {
            sendPacket(new RejectFriendRequest(friendId, this.id));
        }
    }
};
</script>
