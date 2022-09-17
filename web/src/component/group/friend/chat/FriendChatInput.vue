<template>
    <chat-input ref="chatInput" :send-callback="send" :input-click-callback="inputClickCallback" />
</template>

<script>
import { isBlank } from '@/util/stringUtils.js';
import { sendPacket } from '@/util/websocketUtils.js';

import FriendChatRequest from '@/jsProtocol/friend/chat/FriendChatRequest.js';

export default {
    name: 'FriendChatInput',

    components: {
        ChatInput: () => import('../../common/ChatInput.vue')
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

    methods: {
        appendMessage(message) {
            if (isBlank(message)) {
                return;
            }

            this.$refs.chatInput.appendMessage(message);
        },

        send(chatMessage, type) {
            if (isBlank(chatMessage)) {
                return;
            }

            sendPacket(new FriendChatRequest(chatMessage, this.friendId, type, this.$store.state.user.id));
        },

        inputClickCallback() {
            const div = document.getElementById('friendMessageScrollTarget');
            div.scrollTop = div.scrollHeight;
        }
    }
};
</script>
