<template>
    <v-row v-if="!isBlank(groupId) && !isBlank(channelId)">
        <v-col>
            <message
                id="groupMessageScrollTarget"
                message-scroll-target="groupMessageScrollTarget"
                :is-group="true"
                :group-id="groupId"
                :channel-id="channelId"
                :group-message-edit="userHasChannelAuth(groupConstant.channelAuthEnum.specialAuth, channelId, groupVo, groupMemberSimpleVo)"
                :chat-messages="messages"
                :more-callback="moreCallback"
            />
        </v-col>
    </v-row>
</template>

<script>
import { mapState } from 'vuex';
import { sendPacket } from '@/util/websocketUtils.js';
import { simpleError } from '@/util/noticeUtils.js';
import { groupConstant } from '@/constant/constant.js';
import { userHasChannelAuth } from '@/util/groupUtils.js';
import { isBlank } from '@/util/stringUtils.js';

import GroupHistoryMessageRequest from '@/jsProtocol/group/chat/GroupHistoryMessageRequest.js';

export default {
    name: 'GroupChat',

    components: {
        Message: () => import('../../common/Message.vue')
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
            isBlank,
            groupConstant,
            userHasChannelAuth
        };
    },

    computed: {
        ...mapState('group', ['groups', 'groupMemberSimpleVos', 'channels']),
        ...mapState('user', ['id', 'name', 'avatar']),
        messages() {
            const messageChannel = _.find(this.channels, it => it.id === this.channelId);
            if (_.isNil(messageChannel)) {
                return [];
            }
            if (_.isEmpty(messageChannel.chatMessages)) {
                return [];
            }
            return Array.from(messageChannel.chatMessages);
        },

        groupVo() {
            return _.find(this.groups, it => it.id === this.groupId);
        },

        groupMemberSimpleVo() {
            return _.find(this.groupMemberSimpleVos, it => it.groupId === this.groupId);
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
            sendPacket(new GroupHistoryMessageRequest(this.channelId, this.groupId, lastMessageId));
        }
    }
};
</script>
