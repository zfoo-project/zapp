<template>
    <chat-input ref="chatInput" :send-callback="send" :input-click-callback="inputClickCallback" />
</template>

<script>
import { mapState } from 'vuex';
import { isBlank } from '@/util/stringUtils.js';
import { sendPacket } from '@/util/websocketUtils.js';
import { groupConstant } from '@/constant/constant.js';
import { userHasChannelAuth } from '@/util/groupUtils.js';
import { simpleError } from '@/util/noticeUtils.js';

import GroupChatRequest from '@/jsProtocol/group/chat/GroupChatRequest.js';

export default {
    name: 'GroupChatInput',

    components: {
        ChatInput: () => import('../../common/ChatInput.vue')
    },

    props: {
        groupId: {
            type: String,
            default: '',
            required: true
        },
        channelId: {
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
        ...mapState('group', ['groups', 'groupMemberSimpleVos']),
        groupVo() {
            return _.find(this.groups, it => it.id === this.groupId);
        },
        groupMemberSimpleVo() {
            return _.find(this.groupMemberSimpleVos, it => it.groupId === this.groupId);
        }
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
            if (!userHasChannelAuth(groupConstant.channelAuthEnum.middleAuth, this.channelId, this.groupVo, this.groupMemberSimpleVo)) {
                simpleError(this.$t('notice.groupChannelAuthSendMessageError'));
                return;
            }
            sendPacket(new GroupChatRequest(this.channelId, chatMessage, this.groupId, type, this.$store.state.user.id));
        },

        inputClickCallback() {
            const div = document.getElementById('groupMessageScrollTarget');
            div.scrollTop = div.scrollHeight;
        }
    }
};
</script>
