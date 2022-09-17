<template>
    <v-textarea
        v-model="chatMessage"
        class="px-2"
        :label="$t('user.chat.inputLabel')"
        append-icon="mdi-send"
        type="text"
        rows="1"
        auto-grow
        @click:append="send(groupConstant.messageEnum.text.type)"
        @click="onClick()"
        @blur="onBlur()"
        @keyup.exact.enter="send(groupConstant.messageEnum.text.type)"
    />
</template>

<script>
import { groupConstant } from '@/constant/constant.js';
import { mapState } from 'vuex';
import { fixIosInput } from '@/util/fixBugUtils.js';
export default {
    name: 'ChatInput',

    props: {
        sendCallback: {
            type: Function,
            default: () => () => {},
            required: true
        },
        inputClickCallback: {
            type: Function,
            default: () => () => {},
            required: true
        }
    },

    data() {
        return {
            groupConstant,
            chatMessage: ''
        };
    },

    computed: {
        ...mapState('app', ['ios', 'copyMessage'])
    },

    methods: {
        appendMessage(message) {
            this.chatMessage = this.chatMessage + message;
        },

        send(type) {
            if (!_.isNil(this.copyMessage) && _.isEqual(this.copyMessage.message, this.chatMessage)) {
                this.sendCallback(this.copyMessage.message, this.copyMessage.type);
            } else {
                this.sendCallback(this.chatMessage, type);
            }
            this.chatMessage = '';
        },

        onClick() {
            if (!_.isNil(this.inputClickCallback)) {
                this.inputClickCallback();
            }
        },
        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        }
    }
};
</script>
