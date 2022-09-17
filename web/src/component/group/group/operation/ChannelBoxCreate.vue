<template>
    <v-dialog v-model="dialog" width="700">
        <v-card>
            <v-card-title>{{ $t('group.setting.createChannelBox') }}</v-card-title>
            <v-card-subtitle>{{ $t('group.setting.createChannelBoxSubTitle') }}</v-card-subtitle>
            <v-card-text>
                <v-text-field
                    v-model="channelBoxNameValue"
                    :label="$t('group.setting.createChannelBoxInputLabel')"
                    prepend-icon="mdi-folder-open-outline"
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
import CreateChannelBoxRequest from '@/jsProtocol/group/channel/CreateChannelBoxRequest.js';

export default {
    name: 'ChannelBoxCreate',
    props: {
        groupId: {
            type: String,
            default: '',
            required: true
        }
    },
    data() {
        return {
            dialog: false,
            channelBoxNameValue: null
        };
    },
    computed: {
        ...mapState('app', ['ios'])
    },
    watch: {
        dialog() {
            if (this.dialog) {
                this.channelBoxNameValue = null;
            }
        }
    },
    methods: {
        showDialog(value) {
            this.dialog = value;
        },

        confirm() {
            if (isBlank(this.channelBoxNameValue)) {
                simpleError(this.$t('notice.groupChannelBoxNameEmptyError'));
                return;
            }
            sendPacket(new CreateChannelBoxRequest(this.channelBoxNameValue, this.groupId));
        },

        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        }
    }
};
</script>
