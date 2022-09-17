<template>
    <v-container v-if="!_.isNil(groupVo) && !_.isNil(groupMemberSimpleVo)">
        <!-- 群组的设置 -->
        <channel-box-create ref="channelBoxCreate" :group-id="groupVo.id" />
        <channel-box-edit ref="channelBoxEdit" :group-id="groupVo.id" :channel-box-name="channelBoxName" />
        <channel-create ref="channelCreate" :group-id="groupVo.id" :channel-box-name="channelBoxName" />
        <group-setting ref="groupSetting" :group="groupVo" />
        <group-code ref="groupCode" :group="groupVo" />
        <group-auth ref="groupAuth" :group="groupVo" />
        <group-leave ref="groupLeave" :group-id="groupVo.id" :group-name="groupVo.name" />

        <v-row>
            <v-col>
                <v-img
                    v-ripple
                    :src="toGroupBackground(groupVo.background)"
                    aspect-ratio="3"
                >
                    <v-row>
                        <v-col cols="9" class="py-0 my-0">
                            <v-icon>mdi-check-decagram</v-icon>
                            <span class="subheading text-left">{{ groupVo.name }}</span>
                        </v-col>
                        <v-col cols="3" class="text-right py-0 my-0">
                            <v-menu offset-x left>
                                <template v-slot:activator="{ on }">
                                    <v-btn icon color="primary" v-on="on">
                                        <v-icon>mdi-gesture-tap-button</v-icon>
                                    </v-btn>
                                </template>

                                <v-list>
                                    <v-list-item v-if="userHasGroupAuth(groupConstant.groupAuthEnum.adminAuth, groupVo, groupMemberSimpleVo)" @click="showGroupSettingDialog(true)">
                                        <v-list-item-icon>
                                            <v-icon>mdi-table-settings</v-icon>
                                        </v-list-item-icon>
                                        <v-list-item-title>{{ $t('group.setting.desc') }}</v-list-item-title>
                                    </v-list-item>
                                    <v-list-item v-if="userHasGroupAuth(groupConstant.groupAuthEnum.specialAuth, groupVo, groupMemberSimpleVo)" @click="showGroupAuthDialog(true)">
                                        <v-list-item-icon>
                                            <v-icon>mdi-badge-account-horizontal-outline</v-icon>
                                        </v-list-item-icon>
                                        <v-list-item-title>{{ $t('group.setting.groupAuth') }}</v-list-item-title>
                                    </v-list-item>
                                    <v-list-item v-if="userHasGroupAuth(groupConstant.groupAuthEnum.baseAuth, groupVo, groupMemberSimpleVo)" @click="showGroupCodeDialog(true)">
                                        <v-list-item-icon>
                                            <v-icon>mdi-qrcode-plus</v-icon>
                                        </v-list-item-icon>
                                        <v-list-item-title>{{ $t('group.setting.inviteCode') }}</v-list-item-title>
                                    </v-list-item>
                                    <v-list-item @click="showMemberInfoPage(groupVo.id)">
                                        <v-list-item-icon>
                                            <v-icon>mdi-account-group-outline</v-icon>
                                        </v-list-item-icon>
                                        <v-list-item-title>{{ $t('group.setting.memberInfo') }}</v-list-item-title>
                                    </v-list-item>
                                    <v-list-item v-if="userHasGroupAuth(groupConstant.groupAuthEnum.specialAuth, groupVo, groupMemberSimpleVo)" @click="showMainChannelCreateDialog(true)">
                                        <v-list-item-icon>
                                            <v-icon>mdi-plus-box-outline</v-icon>
                                        </v-list-item-icon>
                                        <v-list-item-title>{{ $t('group.setting.createChannel') }}</v-list-item-title>
                                    </v-list-item>
                                    <v-list-item v-if="userHasGroupAuth(groupConstant.groupAuthEnum.specialAuth, groupVo, groupMemberSimpleVo)" @click="showChannelBoxCreateDialog(true)">
                                        <v-list-item-icon>
                                            <v-icon>mdi-folder-plus-outline</v-icon>
                                        </v-list-item-icon>
                                        <v-list-item-title>{{ $t('group.setting.createChannelBox') }}</v-list-item-title>
                                    </v-list-item>
                                    <v-list-item @click="muteGroup()">
                                        <template v-if="groupMemberSimpleVo.groupTime.mute">
                                            <v-list-item-icon>
                                                <v-icon>mdi-bell-off-outline</v-icon>
                                            </v-list-item-icon>
                                            <v-list-item-title>{{ $t('group.operation.mute.muteGroupClose') }}</v-list-item-title>
                                        </template>
                                        <template v-else>
                                            <v-list-item-icon>
                                                <v-icon>mdi-bell-outline</v-icon>
                                            </v-list-item-icon>
                                            <v-list-item-title>{{ $t('group.operation.mute.muteGroupOpen') }}</v-list-item-title>
                                        </template>
                                    </v-list-item>
                                    <v-list-item v-if="groupVo.adminId !== id" @click="showGroupLeaveDialog(true)">
                                        <v-list-item-icon>
                                            <v-icon>mdi-arrow-left-bold-box-outline</v-icon>
                                        </v-list-item-icon>
                                        <v-list-item-title>{{ $t('group.setting.leaveGroup') }}</v-list-item-title>
                                    </v-list-item>
                                </v-list>
                            </v-menu>
                        </v-col>
                    </v-row>
                    <v-spacer />
                </v-img>
            </v-col>
        </v-row>
        <v-row>
            <v-col>
                <template v-for="(channelBox, channelBoxIndex) in groupVo.channelBoxes">
                    <v-list v-if="channelBox.name === groupConstant.channel.main && !_.isEmpty(channelBox.channels)" :key="'main' + channelBoxIndex">
                        <template v-for="(channel, channelIndex) in channelBox.channels">
                            <v-list-item v-if="userHasChannelAuth(groupConstant.channelAuthEnum.baseAuth, channel.id, groupVo, groupMemberSimpleVo)" :key="channelIndex" @click="openChatDialog(groupVo.id, channel.id)">
                                <v-list-item-icon>
                                    <v-icon>mdi-text</v-icon>
                                </v-list-item-icon>
                                <v-list-item-title>
                                    {{ channel.name }}
                                </v-list-item-title>
                                <v-list-item-icon v-show="channel.redPoint" :key="'redPoint' + channelIndex" class="pr-2 mr-1">
                                    <v-icon color="error">mdi-new-box</v-icon>
                                </v-list-item-icon>
                            </v-list-item>
                        </template>
                    </v-list>
                </template>
                <v-expansion-panels focusable multiple accordion>
                    <template v-for="(channelBox, channelBoxIndex) in groupVo.channelBoxes">
                        <v-expansion-panel v-if="channelBox.name !== groupConstant.channel.main" :key="'box' + channelBoxIndex">
                            <v-expansion-panel-header class="pl-4">
                                <span>
                                    {{ channelBox.name }}
                                    <v-icon v-if="userHasGroupAuth(groupConstant.groupAuthEnum.specialAuth, groupVo, groupMemberSimpleVo)" small @click="showChannelBoxEditDialog(true, channelBox.name)">mdi-pencil-outline</v-icon>
                                    <v-icon v-show="_.some(channelBox.channels, it => it.redPoint === true)" color="error">mdi-new-box</v-icon>
                                </span>
                                <template v-if="userHasGroupAuth(groupConstant.groupAuthEnum.specialAuth, groupVo, groupMemberSimpleVo)" v-slot:actions>
                                    <v-icon @click="showSubChannelCreateDialog(true, channelBox.name)">mdi-plus</v-icon>
                                </template>
                            </v-expansion-panel-header>
                            <v-expansion-panel-content>
                                <v-list>
                                    <template v-for="(channel, channelIndex) in channelBox.channels">
                                        <v-list-item v-if="userHasChannelAuth(groupConstant.channelAuthEnum.baseAuth, channel.id, groupVo, groupMemberSimpleVo)" :key="channelIndex" class="pa-0 ma-0" @click="openChatDialog(groupVo.id, channel.id)">
                                            <v-list-item-icon>
                                                <v-icon>mdi-text</v-icon>
                                            </v-list-item-icon>
                                            <v-list-item-title>
                                                {{ channel.name }}
                                            </v-list-item-title>
                                            <v-list-item-icon v-show="channel.redPoint" :key="'redPoint' + channelIndex">
                                                <v-icon color="error">mdi-new-box</v-icon>
                                            </v-list-item-icon>
                                        </v-list-item>
                                    </template>
                                </v-list>
                            </v-expansion-panel-content>
                        </v-expansion-panel>
                    </template>
                </v-expansion-panels>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
import { toGroupBackground } from '@/util/fileUtils.js';
import { userHasGroupAuth, userHasChannelAuth, findChannelFromGroupVo, findChannelTimeFromGroupMemberSimpleVo } from '@/util/groupUtils.js';
import { groupConstant } from '@/constant/constant.js';
import { sendPacket } from '@/util/websocketUtils.js';

import AllInviteGroupCodeRequest from '@/jsProtocol/group/member/AllInviteGroupCodeRequest.js';
import RefreshChannelTimeRequest from '@/jsProtocol/user/group/RefreshChannelTimeRequest.js';
import GroupHistoryMessageRequest from '@/jsProtocol/group/chat/GroupHistoryMessageRequest.js';
import MuteGroupRequest from '@/jsProtocol/user/group/MuteGroupRequest.js';

export default {
    name: 'GroupInfo',

    components: {
        ChannelBoxCreate: () => import('./group/operation/ChannelBoxCreate.vue'),
        ChannelBoxEdit: () => import('./group/operation/ChannelBoxEdit.vue'),
        ChannelCreate: () => import('./group/operation/ChannelCreate.vue'),
        GroupSetting: () => import('./group/operation/GroupSetting.vue'),
        GroupCode: () => import('./group/operation/GroupCode.vue'),
        GroupAuth: () => import('./group/operation/GroupAuth.vue'),
        GroupLeave: () => import('./group/operation/GroupLeave.vue')
    },

    props: {
        groupId: {
            type: String,
            default: '',
            required: true
        }
    },

    data() {
        return {
            toGroupBackground,
            groupConstant,

            userHasGroupAuth,
            userHasChannelAuth,

            channelBoxName: groupConstant.channel.main
        };
    },

    computed: {
        ...mapState('user', ['id']),
        ...mapState('group', ['groups', 'groupMemberSimpleVos', 'channels']),
        groupVo() {
            return _.find(this.groups, it => it.id === this.groupId);
        },
        groupMemberSimpleVo() {
            return _.find(this.groupMemberSimpleVos, it => it.groupId === this.groupId);
        },
        allChannelIds() {
            const channelIds = [];
            const groupVo = _.find(this.groups, it => it.id === this.groupId);
            console.log(groupVo);
            if (_.isNil(groupVo)) {
                return channelIds;
            }
            for (const channelBox of groupVo.channelBoxes) {
                for (const channel of channelBox.channels) {
                    channelIds.push(channel.id);
                }
            }
            return channelIds;
        }
    },

    created() {
        this.setGroupInfo(this);
    },

    destroyed() {
        this.setGroupInfo(null);
    },

    methods: {
        ...mapMutations('app', ['setGroupInfo']),

        showChannelBoxCreateDialog(value) {
            this.$refs.channelBoxCreate.showDialog(value);
        },
        showChannelBoxEditDialog(value, channelBoxName) {
            this.channelBoxName = channelBoxName;
            this.$refs.channelBoxEdit.showDialog(value, channelBoxName);
        },
        showMainChannelCreateDialog(value) {
            this.channelBoxName = groupConstant.channel.main;
            this.$refs.channelCreate.showDialog(value);
        },
        showSubChannelCreateDialog(value, channelBoxName) {
            this.channelBoxName = channelBoxName;
            this.$refs.channelCreate.showDialog(value);
        },
        showGroupSettingDialog(value) {
            this.$refs.groupSetting.showDialog(value);
        },
        showGroupCodeDialog(value) {
            this.$refs.groupCode.showDialog(value);
            sendPacket(new AllInviteGroupCodeRequest(this.groupVo.id));
        },
        showMemberInfoPage(groupId) {
            this.$store.state.app.group.showMemberInfoPage(groupId);
        },
        showGroupAuthDialog(value) {
            this.$refs.groupAuth.showDialog(value);
        },
        showGroupLeaveDialog(value) {
            this.$refs.groupLeave.showDialog(value);
        },
        setInviteCodes(inviteCodes) {
            this.$refs.groupCode.setInviteCodes(inviteCodes);
        },
        openChatDialog(groupId, channelId) {
            // 如果频道里一条消息也没有，则请求一次历史消息
            const messageChannel = _.find(this.channels, it => it.id === channelId);
            if (_.isNil(messageChannel)) {
                sendPacket(new GroupHistoryMessageRequest(channelId, groupId, '-1'));
            }

            this.$store.state.app.group.showGroupChatPage(groupId, channelId);

            const channel = findChannelFromGroupVo(this.groupVo, channelId);
            const channelTime = findChannelTimeFromGroupMemberSimpleVo(this.groupMemberSimpleVo, channelId);
            if (_.isNil(channel)) {
                return;
            }
            if (_.isNil(channelTime)) {
                sendPacket(new RefreshChannelTimeRequest(this.allChannelIds, channelId, groupId, channel.refreshTime));
                return;
            }
            if (channel.redPoint) {
                sendPacket(new RefreshChannelTimeRequest(this.allChannelIds, channelId, groupId, channel.refreshTime));
                return;
            }
            if (_.toNumber(channel.refreshTime) > _.toNumber(channelTime.refreshTime)) {
                sendPacket(new RefreshChannelTimeRequest(this.allChannelIds, channelId, groupId, channel.refreshTime));
                return;
            }
        },
        muteGroup() {
            sendPacket(new MuteGroupRequest(this.groupId, !this.groupMemberSimpleVo.groupTime.mute));
        }
    }
};
</script>
