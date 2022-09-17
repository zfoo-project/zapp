<template>
    <v-list>
        <template v-if="!_.isEmpty(messageFriends)">
            <template v-for="(friend) in messageFriends">
                <v-list-item :key="friend.id" @click="openChatDialog(friend)">
                    <v-list-item-avatar>
                        <v-img :src="toSimpleAvatarUrl(friend.avatar)" />
                    </v-list-item-avatar>

                    <v-list-item-content>
                        <v-list-item-title>
                            {{ toTagOrName(friend) }}
                        </v-list-item-title>
                        <v-list-item-subtitle>
                            <span class="d-inline-block text-truncate" :style="subTitleWidthStyle">
                                {{ lastMessage(friend) }}
                            </span>
                        </v-list-item-subtitle>
                    </v-list-item-content>

                    <v-list-item-icon v-show="friend.redPoint">
                        <v-icon color="error">mdi-new-box</v-icon>
                    </v-list-item-icon>
                </v-list-item>
                <v-divider :key="'divider' + friend.id" />
            </template>
        </template>
        <v-list-item v-else link>
            <v-list-item-content>
                <div class="text-center">
                    {{ $t('user.chat.noChatFriends') }}
                </div>
            </v-list-item-content>
        </v-list-item>
    </v-list>
</template>

<script>
import { mapState } from 'vuex';
import { toSimpleAvatarUrl } from '@/util/fileUtils.js';
import { toTagOrName } from '@/util/stringUtils.js';
import { sendPacket, getServerTime } from '@/util/websocketUtils.js';
import { groupConstant } from '@/constant/constant.js';

import FriendHistoryMessageRequest from '@/jsProtocol/friend/chat/FriendHistoryMessageRequest.js';
import ReadFriendMessageRequest from '@/jsProtocol/friend/chat/ReadFriendMessageRequest.js';

export default {
    name: 'FriendHot',


    data() {
        return {
            toSimpleAvatarUrl,
            toTagOrName,
            subTitleWidthStyle: this.$vuetify.breakpoint.mdAndUp ? 'max-width: 160px;' : 'max-width: 120px;'
        };
    },

    computed: {
        ...mapState('user', ['id']),
        ...mapState('friend', ['friends']),
        messageFriends() {
            if (_.isEmpty(this.friends)) {
                return [];
            }
            const serverTime = getServerTime();
            const minRecentTime = serverTime - groupConstant.friendRecentChatMessageTime;

            const friends = _.filter(this.friends, it => !_.isEmpty(it.chatMessages) || it.redPoint === true || _.toNumber(it.refreshTime) >= minRecentTime);
            friends.sort((a, b) => {
                if (_.isEmpty(a.chatMessages)) {
                    return b;
                }
                if (_.isEmpty(b.chatMessages)) {
                    return a;
                }
                if (_.toNumber(_.last(a.chatMessages)) > _.toNumber(_.last(b.chatMessages))) {
                    return b;
                } else {
                    return a;
                }
            });
            return friends;
        }
    },

    methods: {
        lastMessage(userCache) {
            const chatMessage = _.last(userCache.chatMessages);
            if (_.isNil(chatMessage)) {
                return '';
            }
            return chatMessage.message;
        },

        openChatDialog(friend) {
            // 如果好友里一条消息也没有，则请求一次历史消息
            if (_.isEmpty(friend.chatMessages)) {
                sendPacket(new FriendHistoryMessageRequest(friend.id, -1, this.id));
            }

            this.$store.state.app.group.showFriendChatPage(friend.id);
            if (friend.redPoint) {
                sendPacket(new ReadFriendMessageRequest(friend.id, this.id));
                return;
            }
            if (_.some(friend.chatMessages, it => it.sendId !== this.id && it.read === false)) {
                sendPacket(new ReadFriendMessageRequest(friend.id, this.id));
                return;
            }
        }
    }
};
</script>
