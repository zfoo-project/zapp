<template>
    <v-dialog v-model="dialog" width="700">
        <v-card>
            <v-card-title>{{ $t('group.setting.createChannel') }}</v-card-title>
            <v-card-subtitle v-if="channelBoxName !== groupConstant.channel.main">{{ $t('group.setting.createChannelSubTitleFirst') }} {{ channelBoxName }} {{ $t('group.setting.createChannelSubTitle') }}</v-card-subtitle>
            <v-card-text>
                <v-text-field
                    v-model="channelNameValue"
                    :label="$t('group.setting.createChannelInputLabel')"
                    prepend-icon="mdi-text"
                    type="text"
                    @blur="onBlur()"
                    @keyup.exact.enter="confirm()"
                />
            </v-card-text>


            <v-card-actions>
                <v-spacer />
                <v-btn class="text-capitalize" color="primary" @click="confirm()">{{ $t('common.confirm') }}</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import { mapState } from 'vuex';
import { sendPacket } from '@/util/websocketUtils.js';
import { isBlank } from '@/util/stringUtils.js';
import { simpleError } from '@/util/noticeUtils.js';
import { fixIosInput } from '@/util/fixBugUtils.js';
import { groupConstant } from '@/constant/constant.js';
import CreateChannelRequest from '@/jsProtocol/group/channel/CreateChannelRequest.js';

export default {
    name: 'ChannelCreate',
    props: {
        groupId: {
            type: String,
            default: '',
            required: true
        },
        channelBoxName: {
            type: String,
            default: '',
            required: true
        }
    },
    data() {
        return {
            groupConstant,
            dialog: false,
            channelNameValue: null
        };
    },
    computed: {
        ...mapState('app', ['ios'])
    },
    watch: {
        dialog() {
            if (this.dialog) {
                this.channelNameValue = null;
            }
        }
    },
    methods: {
        showDialog(value) {
            this.dialog = value;
        },

        confirm() {
            if (isBlank(this.channelNameValue)) {
                simpleError(this.$t('notice.groupChannelNameEmptyError'));
                return;
            }
            sendPacket(new CreateChannelRequest(this.channelBoxName, this.channelNameValue, this.groupId));
        },

        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        }
    }
};
</script>
