<template>
    <v-dialog v-model="dialog" width="700">
        <v-card>
            <v-alert v-show="alert" prominent type="error" transition="scale-transition">
                <v-row align="center">
                    <v-col>{{ $t('group.setting.deleteAlertFirst') }} {{ channelBoxName }} {{ $t('group.setting.deleteAlertSecond') }}</v-col>
                    <v-col class="shrink">
                        <v-btn @click="deleteChannelBoxClick()">{{ $t('group.setting.deleteGroupButton') }}</v-btn>
                    </v-col>
                </v-row>
            </v-alert>

            <v-list-item>
                <v-list-item-content>
                    <v-list-item-title class="headline">{{ $t('group.setting.editChannelBox') }}</v-list-item-title>
                    <v-list-item-subtitle>{{ $t('group.setting.editChannelBoxSubTitle') }}</v-list-item-subtitle>
                </v-list-item-content>
                <v-list-item-icon>
                    <v-icon color="error" @click="alert = !alert">mdi-delete-off-outline</v-icon>
                </v-list-item-icon>
            </v-list-item>

            <v-card-text>
                <v-text-field
                    v-model="channelBoxNameValue"
                    :label="$t('group.setting.createChannelBoxInputLabel')"
                    prepend-icon="mdi-folder-open-outline"
                    type="text"
                    @blur="onBlur()"
                    @keyup.exact.enter="saveChanelBoxClick()"
                />
            </v-card-text>


            <v-card-actions>
                <v-spacer />
                <v-btn class="text-capitalize" color="primary" @click="saveChanelBoxClick()">{{ $t('common.confirm') }}</v-btn>
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
import SaveChannelBoxRequest from '@/jsProtocol/group/channel/SaveChannelBoxRequest.js';
import DeleteChannelBoxRequest from '@/jsProtocol/group/channel/DeleteChannelBoxRequest.js';

export default {
    name: 'ChannelBoxEdit',
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
            dialog: false,
            alert: false,
            channelBoxNameValue: null
        };
    },

    computed: {
        ...mapState('app', ['ios'])
    },

    watch: {
        dialog() {
            if (this.dialog) {
                this.init();
            }
        },
        channelBoxName() {
            this.init();
        }
    },

    created() {
        this.init();
    },

    methods: {
        init() {
            this.channelBoxNameValue = this.channelBoxName;
        },

        showDialog(value) {
            this.dialog = value;
        },

        saveChanelBoxClick() {
            if (isBlank(this.channelBoxNameValue)) {
                simpleError(this.$t('notice.groupChannelBoxNameEmptyError'));
                return;
            }
            if (_.isEqual(this.channelBoxNameValue, this.channelBoxName)) {
                simpleError(this.$t('notice.groupChannelBoxNameEqualError'));
                return;
            }
            sendPacket(new SaveChannelBoxRequest(this.groupId, this.channelBoxNameValue, this.channelBoxName));
        },

        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        },

        deleteChannelBoxClick() {
            sendPacket(new DeleteChannelBoxRequest(this.channelBoxName, this.groupId));
        }
    }
};
</script>
