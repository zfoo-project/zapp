<template>
    <v-dialog v-model="dialog" width="700">
        <v-card>
            <v-card-title>{{ $t('group.channel.operation.setting.title') }}</v-card-title>
            <v-card-text>
                <v-text-field
                    v-model="channelNameModel"
                    :label="$t('group.channel.operation.setting.newChannelNameLabel')"
                    prepend-icon="mdi-tag-text-outline"
                    type="text"
                    @blur="onBlur()"
                    @keyup.exact.enter="confirmNewChannelName()"
                />
            </v-card-text>


            <v-card-actions>
                <v-spacer />
                <v-btn class="text-capitalize" color="primary" @click="confirmNewChannelName()">{{ $t('common.confirm') }}</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import { mapState } from 'vuex';
import { isBlank } from '@/util/stringUtils.js';
import { sendPacket } from '@/util/websocketUtils.js';
import { fixIosInput } from '@/util/fixBugUtils.js';
import { simpleError } from '@/util/noticeUtils.js';

import SaveChannelRequest from '@/jsProtocol/group/channel/SaveChannelRequest.js';

export default {
    name: 'ChannelSetting',

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
            dialog: false,
            channelNameModel: ''
        };
    },

    computed: {
        ...mapState('app', ['ios']),
        ...mapState('group', ['groups'])
    },

    watch: {
        dialog() {
            this.channelNameModel = this.channelName;
        }
    },


    methods: {
        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        },

        showSettingDialog(value) {
            this.dialog = value;
        },

        confirmNewChannelName() {
            if (isBlank(this.channelNameModel)) {
                simpleError(this.$t('notice.groupChannelNameEmptyError'));
                return;
            }
            if (_.isEqual(this.channelNameModel, this.titleName)) {
                simpleError(this.$t('notice.groupChannelNameEqualError'));
                return;
            }
            sendPacket(new SaveChannelRequest(this.channelId, this.channelNameModel, this.groupId));
        }
    }
};
</script>
