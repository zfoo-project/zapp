<template>
    <v-dialog v-model="dialog" width="700">
        <v-card>
            <v-card-title>{{ $t('group.channel.pin.title') }}</v-card-title>
            <message
                id="pinMessageScrollTarget"
                ref="pinMessageScrollTarget"
                message-scroll-target="pinMessageScrollTarget"
                :is-group="true"
                :is-pin="true"
                :group-id="groupId"
                :channel-id="channelId"
                :group-message-edit="userHasChannelAuth(groupConstant.channelAuthEnum.specialAuth, channelId, groupVo, groupMemberSimpleVo)"
                :chat-messages="pinMessages"
                :more-callback="moreCallback"
            />
        </v-card>
    </v-dialog></template>

<script>
import { mapState } from 'vuex';
import { groupConstant } from '@/constant/constant.js';
import { userHasChannelAuth } from '@/util/groupUtils.js';
import { sendPacket } from '@/util/websocketUtils.js';

import GroupHistoryPinMessageRequest from '@/jsProtocol/group/chat/GroupHistoryPinMessageRequest.js';

export default {
    name: 'ChannelPin',

    components: {
        Message: () => import('../../../common/Message.vue')
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
        },
        channelName: {
            type: String,
            default: '',
            required: true
        }
    },

    data() {
        return {
            groupConstant,
            userHasChannelAuth,

            dialog: false,
            channelNameModel: ''
        };
    },

    computed: {
        ...mapState('app', ['pinMessages']),
        ...mapState('group', ['groups', 'groupMemberSimpleVos']),
        groupVo() {
            return _.find(this.groups, it => it.id === this.groupId);
        },
        groupMemberSimpleVo() {
            return _.find(this.groupMemberSimpleVos, it => it.groupId === this.groupId);
        }
    },

    watch: {
        dialog() {
            this.channelNameModel = this.channelName;
            if (this.dialog === false) {
                this.$refs.pinMessageScrollTarget.stopAllVideo();
            }
        }
    },


    methods: {

        showPinDialog(value) {
            this.dialog = value;
        },

        moreCallback() {
            let lastMessageId = -1;
            if (!_.isEmpty(this.pinMessages)) {
                const firstMessage = _.first(this.pinMessages);
                lastMessageId = firstMessage.id;
            }
            sendPacket(new GroupHistoryPinMessageRequest(this.channelId, this.groupId, lastMessageId));
        }

    }
};
</script>
