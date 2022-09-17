<template>
    <v-row>
        <v-col>
            <message id="friendMessageScrollTarget" message-scroll-target="friendMessageScrollTarget" :friend-id="friendId" :chat-messages="messages" :more-callback="moreCallback" />
        </v-col>
    </v-row>
</template>

<script>
import { mapState } from 'vuex';
import { sendPacket } from '@/util/websocketUtils.js';
import { simpleError } from '@/util/noticeUtils.js';

import FriendHistoryMessageRequest from '@/jsProtocol/friend/chat/FriendHistoryMessageRequest.js';

export default {
    name: 'FriendChat',

    components: {
        Message: () => import('../../common/Message.vue')
    },

    props: {
        friendId: {
            type: String,
            default: '',
            required: true
        }
    },

    data() {
        return {
        };
    },

    computed: {
        ...mapState('friend', ['friends']),
        ...mapState('user', ['id', 'name', 'avatar']),
        messages() {
            const friend = _.find(this.friends, it => it.id === this.friendId);
            if (_.isNil(friend)) {
                return [];
            }
            if (_.isEmpty(friend.chatMessages)) {
                return [];
            }
            return Array.from(friend.chatMessages);
        }
    },

    methods: {
        moreCallback() {
            let lastMessageId = -1;
            if (!_.isEmpty(this.messages)) {
                const firstMessage = _.first(this.messages);
                if (firstMessage.id === '1') {
                    simpleError(this.$t('notice.friendNoMoreHistoryMessage'));
                    return;
                }

                lastMessageId = firstMessage.id;
            }
            sendPacket(new FriendHistoryMessageRequest(this.friendId, lastMessageId, this.$store.state.user.id));
        }
    }
};
</script>
