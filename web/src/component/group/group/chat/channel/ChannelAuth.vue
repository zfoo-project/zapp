<template>
    <v-dialog v-model="dialog" width="700">
        <v-card>
            <v-tabs :vertical="$vuetify.breakpoint.mdAndUp ? true : false">
                <v-tab v-for="(groupAuthVo, index) in groupAuthVos" :key="'tab' + index">
                    {{ groupAuthVo.name }}
                </v-tab>

                <v-tab-item v-for="(groupAuthVo, index) in groupAuthVos" :key="'tabItem' + index">
                    <v-container>
                        <v-row>
                            <v-col cols="10">
                                <channel-auth-selector
                                    ref="channelAuthSelector"
                                    :group-auth-id="groupAuthVo.id"
                                    :channel-auth-vos="channelAuthVos"
                                />
                            </v-col>
                            <v-col cols="2" class="mt-4">
                                <v-tooltip bottom>
                                    <template v-slot:activator="{ on }">
                                        <v-icon v-on="on">mdi-map-marker-question-outline</v-icon>
                                    </template>
                                    <p class="my-0 py-0">{{ $t('group.operation.textChannelAuth.auth0') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.textChannelAuth.auth1') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.textChannelAuth.auth2') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.textChannelAuth.auth3') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.textChannelAuth.auth4') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.textChannelAuth.auth5') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.textChannelAuth.auth6') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.textChannelAuth.auth7') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.textChannelAuth.auth8') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.textChannelAuth.auth9') }}</p>
                                </v-tooltip>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-tab-item>
            </v-tabs>
            <v-card-actions>
                <v-spacer />
                <v-btn class="text-capitalize" color="primary" @click="saveChannelAuthsClick()">
                    <v-icon left>mdi-content-save-edit-outline</v-icon>
                    {{ $t('common.save') }}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import { mapState } from 'vuex';
import { sendPacket } from '@/util/websocketUtils.js';
import { groupConstant } from '@/constant/constant.js';
import { fixIosInput } from '@/util/fixBugUtils.js';

import SaveChannelAuthRequest from '@/jsProtocol/group/auth/SaveChannelAuthRequest.js';
import ChannelAuthVO from '@/jsProtocol/group/model/ChannelAuthVO.js';

export default {
    name: 'ChannelAuth',

    components: {
        ChannelAuthSelector: () => import('./ChannelAuthSelector.vue')
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
        },
        channelAuthVos: {
            type: Array,
            default: () => [],
            required: true
        },
        groupAuthVos: {
            type: Array,
            default: () => [],
            required: true
        }
    },

    data() {
        return {
            groupConstant,

            dialog: false
        };
    },

    computed: {
        ...mapState('app', ['ios']),
        ...mapState('group', ['groups'])
    },

    watch: {
        dialog() {
            if (this.dialog) {
                this.init();
            }
        }
    },


    methods: {
        init() {
            const channelAuthSelectors = this.$refs.channelAuthSelector;
            if (_.isNil(channelAuthSelectors)) {
                return;
            }
            for (let i = 0; i < channelAuthSelectors.length; i++) {
                const channelAuthSelector = this.$refs.channelAuthSelector[i];
                channelAuthSelector.init();
            }
        },

        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        },

        showChannelAuthDialog(value) {
            this.dialog = value;
        },

        saveChannelAuthsClick() {
            const channelAuths = [];
            for (let i = 0; i < this.$refs.channelAuthSelector.length; i++) {
                const channelAuthSelector = this.$refs.channelAuthSelector[i];
                const id = channelAuthSelector.getGroupAuthId();
                const channelAuth = channelAuthSelector.getChannelAuthValue();
                channelAuths.push(new ChannelAuthVO(channelAuth, id));
            }
            sendPacket(new SaveChannelAuthRequest(channelAuths, this.channelId, this.groupId));
        }
    }
};
</script>
