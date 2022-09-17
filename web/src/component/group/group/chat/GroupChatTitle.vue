<template>
    <v-list-item v-if="!isBlank(groupId) && !isBlank(channelId)">
        <v-file-input
            id="chatImageInput"
            ref="chatImageInput"
            v-model="chatImageFile"
            style="display: none"
            accept="image/*"
            @change="chatImageFileChanged()"
        />

        <v-file-input
            id="chatVideoInput"
            ref="chatVideoInput"
            v-model="chatVideoFile"
            style="display: none"
            accept="video/*"
            @change="chatVideoFileChanged()"
        />

        <channel-auth
            ref="channelAuth"
            :group-id="groupId"
            :channel-id="channelId"
            :channel-name="channelName"
            :channel-auth-vos="channelAuthVos"
            :group-auth-vos="groupAuthVos"
        />
        <channel-setting ref="channelSetting" :group-id="groupId" :channel-id="channelId" :channel-name="channelName" />
        <channel-pin ref="channelPin" :group-id="groupId" :channel-id="channelId" :channel-name="channelName" />

        <v-list-item-icon>
            <v-icon color="primary" @click="backClick()">mdi-arrow-left-bold-circle-outline</v-icon>
        </v-list-item-icon>
        <v-list-item-subtitle>
            {{ channelName }}
        </v-list-item-subtitle>
        <v-list-item-icon v-if="userHasChannelAuth(groupConstant.channelAuthEnum.middleAuth, channelId, groupVo, groupMemberSimpleVo)" class="px-2 mx-0">
            <v-menu offset-x left>
                <template v-slot:activator="{ on }">
                    <v-icon color="primary" v-on="on">mdi-plus-circle-outline</v-icon>
                </template>

                <v-list>
                    <v-list-item v-for="(option, index) in messageOptions" :key="'listItem' + index" @click="messageOptionClick(option.type, $event)">
                        <v-list-item-icon>
                            <v-icon>{{ option.icon }}</v-icon>
                        </v-list-item-icon>
                        <v-list-item-title>{{ $t(option.text) }}</v-list-item-title>
                    </v-list-item>
                </v-list>
            </v-menu>
        </v-list-item-icon>
        <v-list-item-icon class="px-2 mx-0">
            <v-menu offset-y bottom>
                <template v-slot:activator="{ on }">
                    <v-icon color="primary" v-on="on">mdi-emoticon-happy-outline</v-icon>
                </template>

                <v-card>
                    <v-container>
                        <v-row v-for="(emotionArray, index) in emotions.normal" :key="index">
                            <v-col v-for="(emotionIcon) in emotionArray" :key="emotionIcon" cols="1">
                                <v-icon :color="emotions.color" @click="emotionClick(emotionIcon)">{{ emotionIcon }}</v-icon>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card>
            </v-menu>
        </v-list-item-icon>
        <v-list-item-icon class="px-2 mx-0">
            <v-icon v-if="channelMute" color="primary" @click="muteChannel()">mdi-bell-off-outline</v-icon>
            <v-icon v-else color="primary" @click="muteChannel()">mdi-bell-outline</v-icon>
        </v-list-item-icon>
        <v-list-item-icon class="pl-2 pr-0 mx-0">
            <v-icon color="primary" @click="showChannelPinDialog(true)">mdi-clock-outline</v-icon>
        </v-list-item-icon>
        <v-list-item-icon v-if="userHasGroupAuth(groupConstant.groupAuthEnum.specialAuth, groupVo, groupMemberSimpleVo)">
            <v-menu offset-x left>
                <template v-slot:activator="{ on }">
                    <v-icon color="primary" v-on="on">mdi-dots-horizontal-circle-outline</v-icon>
                </template>

                <v-list>
                    <v-list-item @click="showSettingDialog(true)">
                        <v-list-item-icon>
                            <v-icon>mdi-table-border</v-icon>
                        </v-list-item-icon>
                        <v-list-item-title>{{ $t('group.channel.operation.setting.title') }}</v-list-item-title>
                    </v-list-item>

                    <v-list-item @click="showChannelAuthDialog(true)">
                        <v-list-item-icon>
                            <v-icon>mdi-account-box-multiple-outline</v-icon>
                        </v-list-item-icon>
                        <v-list-item-title>{{ $t('group.channel.operation.channelAuth.title') }}</v-list-item-title>
                    </v-list-item>

                    <v-list-item @click="deleteChannelClick()">
                        <v-list-item-icon>
                            <v-icon>mdi-delete-sweep-outline</v-icon>
                        </v-list-item-icon>
                        <v-list-item-title>{{ $t('group.channel.operation.deleteChannelTitle') }}</v-list-item-title>
                    </v-list-item>
                </v-list>
            </v-menu>
        </v-list-item-icon>
    </v-list-item>
</template>

<script>
import { policyApi, uploadFileToOssApi } from '@/apiHttp/ossApi.js';
import { mapMutations, mapState } from 'vuex';
import { appConstant, groupConstant, emotions } from '@/constant/constant.js';
import { isBlank } from '@/util/stringUtils.js';
import { sendPacket } from '@/util/websocketUtils.js';
import { fixIosInput } from '@/util/fixBugUtils.js';
import { userHasGroupAuth, userHasChannelAuth } from '@/util/groupUtils.js';
import { simpleError } from '@/util/noticeUtils.js';

import GroupChatRequest from '@/jsProtocol/group/chat/GroupChatRequest.js';
import DeleteChannelRequest from '@/jsProtocol/group/channel/DeleteChannelRequest.js';
import GroupHistoryPinMessageRequest from '@/jsProtocol/group/chat/GroupHistoryPinMessageRequest.js';
import MuteChannelRequest from '@/jsProtocol/user/group/MuteChannelRequest.js';

export default {
    name: 'GroupChatTitle',

    components: {
        ChannelAuth: () => import('./channel/ChannelAuth.vue'),
        ChannelSetting: () => import('./channel/ChannelSetting.vue'),
        ChannelPin: () => import('./channel/ChannelPin.vue')
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
            emotions,

            userHasGroupAuth,
            userHasChannelAuth,
            groupConstant,

            channelName: null,
            channelAuthVos: [],
            groupAuthVos: [],

            chatImageFile: null,
            chatImageOssPolicy: {
                expire: 0
            },

            chatVideoFile: null,
            chatVideoOssPolicy: {
                expire: 0
            },

            messageOptions: [
                {
                    type: groupConstant.messageEnum.image.type,
                    icon: groupConstant.messageEnum.image.icon,
                    text: 'user.chat.sendImage'
                },
                {
                    type: groupConstant.messageEnum.video.type,
                    icon: groupConstant.messageEnum.video.icon,
                    text: 'user.chat.sendVideo'
                }
            ]
        };
    },

    computed: {
        ...mapState('app', ['groupChatInput', 'ios']),
        ...mapState('group', ['groups', 'groupMemberSimpleVos']),
        groupVo() {
            return _.find(this.groups, it => it.id === this.groupId);
        },
        groupMemberSimpleVo() {
            return _.find(this.groupMemberSimpleVos, it => it.groupId === this.groupId);
        },
        channelMute() {
            const groupSimpleVo = _.find(this.groupMemberSimpleVos, it => it.groupId === this.groupId);
            const channelTime = _.find(groupSimpleVo.groupTime.channelTimes, it => it.channelId === this.channelId);
            const mute = _.isNil(channelTime) ? false : channelTime.mute;
            return mute;
        }
    },

    watch: {
        groupId: function() {
            this.refreshChannel();
        },
        channelId: function() {
            this.refreshChannel();
        },
        groups: function() {
            this.refreshChannel();
        }
    },

    methods: {
        ...mapMutations('app', ['setPinMessages']),

        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        },

        refreshChannel() {
            const group = this.groups.find((it) => _.isEqual(it.id, this.groupId));
            if (!_.isNil(group)) {
                for (const channelBox of group.channelBoxes) {
                    for (const channel of channelBox.channels) {
                        if (channel.id === this.channelId) {
                            this.channelAuthVos = channel.channelAuths;
                            this.channelName = channel.name;
                        }
                    }
                }
                this.groupAuthVos = group.groupAuths;
            }
        },

        showSettingDialog(value) {
            this.$refs.channelSetting.showSettingDialog(value);
        },
        showChannelAuthDialog(value) {
            this.$refs.channelAuth.showChannelAuthDialog(value);
        },
        showChannelPinDialog(value) {
            this.setPinMessages([]);
            sendPacket(new GroupHistoryPinMessageRequest(this.channelId, this.groupId, -1));
            this.$refs.channelPin.showPinDialog(value);
        },

        muteChannel() {
            const allChannelIds = [];
            for (const channelBox of this.groupVo.channelBoxes) {
                for (const channel of channelBox.channels) {
                    allChannelIds.push(channel.id);
                }
            }
            sendPacket(new MuteChannelRequest(allChannelIds, this.channelId, this.groupId, !this.channelMute));
        },

        backClick() {
            this.$store.state.app.group.showGroupHome();
        },

        messageOptionClick(type) {
            switch (type) {
            case groupConstant.messageEnum.image.type:
                this.$refs.chatImageInput.$refs.input.click();
                break;
            case groupConstant.messageEnum.video.type:
                this.$refs.chatVideoInput.$refs.input.click();
                break;
            }
        },

        chatImageFileChanged() {
            if (_.isNil(this.chatImageFile)) {
                return;
            }

            policyApi(appConstant.ossPolicyEnum.groupImage.type).then(response => {
                const data = response.data;
                this.chatImageOssPolicy = data;

                const upload = uploadFileToOssApi(this.chatImageFile, this.chatImageOssPolicy);
                const key = upload.key;
                upload.callback.then(response => {
                    const url = this.chatImageOssPolicy.host + '/' + key;
                    sendPacket(new GroupChatRequest(this.channelId, url, this.groupId, groupConstant.messageEnum.image.type, this.$store.state.user.id));
                }).catch(error => {
                    console.log(error);
                    simpleError(this.$t('notice.uploadImageSizeLimitError'));
                });
                this.$refs.chatImageInput.clearableCallback();
            });
        },

        chatVideoFileChanged() {
            if (_.isNil(this.chatVideoFile)) {
                return;
            }

            policyApi(appConstant.ossPolicyEnum.groupVideo.type).then(response => {
                const data = response.data;
                this.chatVideoOssPolicy = data;

                const upload = uploadFileToOssApi(this.chatVideoFile, this.chatVideoOssPolicy);
                const key = upload.key;
                upload.callback.then(response => {
                    const url = this.chatVideoOssPolicy.host + '/' + key;
                    sendPacket(new GroupChatRequest(this.channelId, url, this.groupId, groupConstant.messageEnum.video.type, this.$store.state.user.id));
                }).catch(error => {
                    console.log(error);
                    simpleError(this.$t('notice.uploadVideoSizeLimitError'));
                });
                this.$refs.chatVideoInput.clearableCallback();
            });
        },

        emotionClick(emotionIcon) {
            this.groupChatInput.appendMessage('[' + emotionIcon + ']');
        },

        deleteChannelClick() {
            sendPacket(new DeleteChannelRequest(this.channelId, this.groupId));
        }
    }
};
</script>
