<template>
    <v-list-item v-if="!_.isNil(titleName)">
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

        <v-dialog v-model="settingDialog" width="700">
            <v-card>
                <v-card-title>{{ $t('user.chat.userTag') }}</v-card-title>
                <v-card-text>
                    <v-text-field
                        v-model="friendTag"
                        :label="$t('user.chat.tag')"
                        prepend-icon="mdi-tag-text-outline"
                        type="text"
                        @blur="onBlur()"
                        @keyup.exact.enter="confirmTag()"
                    />
                </v-card-text>


                <v-card-actions>
                    <v-spacer />
                    <v-btn class="text-capitalize" color="primary" @click="confirmTag()">{{ $t('common.confirm') }}</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <v-list-item-icon>
            <v-icon color="primary" @click="backClick()">mdi-arrow-left-bold-circle-outline</v-icon>
        </v-list-item-icon>
        <v-list-item-subtitle>
            {{ titleName }}
        </v-list-item-subtitle>
        <v-list-item-icon class="px-6">
            <v-menu offset-x left>
                <template v-slot:activator="{ on }">
                    <v-icon color="primary" v-on="on">mdi-plus-circle-outline</v-icon>
                </template>

                <v-list>
                    <v-list-item v-for="(option, index) in messageOptions" :key="index" @click="messageOptionClick(option.type, $event)">
                        <v-list-item-icon>
                            <v-icon>{{ option.icon }}</v-icon>
                        </v-list-item-icon>
                        <v-list-item-title>{{ $t(option.text) }}</v-list-item-title>
                    </v-list-item>
                </v-list>
            </v-menu>
        </v-list-item-icon>
        <v-spacer />
        <v-list-item-icon>
            <v-menu offset-y bottom>
                <template v-slot:activator="{ on }">
                    <v-icon color="primary" v-on="on">mdi-emoticon-happy-outline</v-icon>
                </template>

                <v-card>
                    <v-container>
                        <v-row v-for="(emotionArray, index) in emotions.normal" :key="'row' + index">
                            <v-col v-for="(emotionIcon) in emotionArray" :key="emotionIcon" cols="1">
                                <v-icon :color="emotions.color" @click="emotionClick(emotionIcon)">{{ emotionIcon }}</v-icon>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card>
            </v-menu>
        </v-list-item-icon>
        <v-list-item-icon>
            <v-menu offset-x left>
                <template v-slot:activator="{ on }">
                    <v-icon color="primary" v-on="on">mdi-dots-horizontal-circle-outline</v-icon>
                </template>

                <v-list>
                    <v-list-item v-for="(option, index) in friendOptions" :key="'listItem' + index" @click="friendOptionClick(option.type, $event)">
                        <v-list-item-icon>
                            <v-icon>{{ option.icon }}</v-icon>
                        </v-list-item-icon>
                        <v-list-item-title>{{ $t(option.text) }}</v-list-item-title>
                    </v-list-item>
                </v-list>
            </v-menu>
        </v-list-item-icon>
    </v-list-item>
</template>

<script>
import clipboard from '@/util/clipboardUtils.js';

import { policyApi, uploadFileToOssApi } from '@/apiHttp/ossApi.js';
import { mapState } from 'vuex';
import { simpleSuccess, simpleError } from '@/util/noticeUtils.js';
import { appConstant, groupConstant, emotions } from '@/constant/constant.js';
import { sendPacket } from '@/util/websocketUtils.js';
import { isBlank, toTagOrName } from '@/util/stringUtils.js';
import { fixIosInput } from '@/util/fixBugUtils.js';

import FriendChatRequest from '@/jsProtocol/friend/chat/FriendChatRequest.js';
import DeleteFriendRequest from '@/jsProtocol/friend/operation/DeleteFriendRequest.js';
import BlacklistRequest from '@/jsProtocol/friend/operation/BlacklistRequest.js';
import BlacklistCancelRequest from '@/jsProtocol/friend/operation/BlacklistCancelRequest.js';
import MarkFriendRequest from '@/jsProtocol/friend/operation/MarkFriendRequest.js';

export default {
    name: 'FriendChatTitle',

    props: {
        friendId: {
            type: String,
            default: '',
            required: true
        }
    },

    data() {
        return {
            emotions,

            settingDialog: false,
            friendTag: '',

            titleName: null,

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
            ],
            friendOptions: [
                {
                    type: 0,
                    icon: 'mdi-tag-text-outline',
                    text: 'user.chat.userTag'
                },
                {
                    type: 1,
                    icon: 'mdi-link-box-outline',
                    text: 'user.chat.userInfoLink'
                },
                {
                    type: 2,
                    icon: 'mdi-delete-forever-outline',
                    text: 'user.chat.deleteFriend'
                },
                {
                    type: 3,
                    icon: 'mdi-account-off-outline',
                    text: 'user.chat.addBlacklist'
                },
                {
                    type: 4,
                    icon: 'mdi-account-check-outline',
                    text: 'user.chat.removeBlacklist'
                },
                {
                    type: 5,
                    icon: 'mdi-mail-ru',
                    text: 'user.chat.userReport'
                }
            ]
        };
    },

    computed: {
        ...mapState('app', ['friendChatInput', 'ios']),
        ...mapState('friend', ['friends', 'blacklist'])
    },

    watch: {
        friendId: function() {
            const friendCache = this.friends.find((it) => _.isEqual(it.id, this.friendId));
            if (!_.isNil(friendCache)) {
                this.titleName = toTagOrName(friendCache);
            }
        },
        friends: function() {
            const friendCache = this.friends.find((it) => _.isEqual(it.id, this.friendId));
            if (!_.isNil(friendCache)) {
                const titleName = toTagOrName(friendCache);
                if (titleName !== this.titleName) {
                    this.titleName = titleName;
                }
            }
        }
    },

    methods: {
        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        },
        confirmTag() {
            if (isBlank(this.friendTag)) {
                simpleError(this.$t('notice.friendTagEmptyError'));
                return;
            }
            sendPacket(new MarkFriendRequest(this.friendId, this.friendTag, this.$store.state.user.id));
        },

        showSettingDialog(value) {
            this.settingDialog = value;
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

            policyApi(appConstant.ossPolicyEnum.chatImage.type).then(response => {
                const data = response.data;
                this.chatImageOssPolicy = data;

                const upload = uploadFileToOssApi(this.chatImageFile, this.chatImageOssPolicy);
                const key = upload.key;
                upload.callback.then(response => {
                    const url = this.chatImageOssPolicy.host + '/' + key;
                    sendPacket(new FriendChatRequest(url, this.friendId, groupConstant.messageEnum.image.type, this.$store.state.user.id));
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

            policyApi(appConstant.ossPolicyEnum.chatVideo.type).then(response => {
                const data = response.data;
                this.chatVideoOssPolicy = data;

                const upload = uploadFileToOssApi(this.chatVideoFile, this.chatVideoOssPolicy);
                const key = upload.key;
                upload.callback.then(response => {
                    const url = this.chatVideoOssPolicy.host + '/' + key;
                    sendPacket(new FriendChatRequest(url, this.friendId, groupConstant.messageEnum.video.type, this.$store.state.user.id));
                }).catch(error => {
                    console.log(error);
                    simpleError(this.$t('notice.uploadVideoSizeLimitError'));
                });
                this.$refs.chatVideoInput.clearableCallback();
            });
        },

        emotionClick(emotionIcon) {
            this.friendChatInput.appendMessage('[' + emotionIcon + ']');
        },

        friendOptionClick(type, event) {
            switch (type) {
            case 0:
                this.settingDialog = true;
                break;
            case 1:
                clipboard(appConstant.homeUrl + '/user/info/' + this.friendId, event);
                break;
            case 2:
                // 删除好友
                sendPacket(new DeleteFriendRequest(this.friendId, this.$store.state.user.id));
                this.$store.state.app.group.showGroupHome();
                break;
            case 3:
                // 加入黑名单
                if (_.findIndex(this.blacklist, it => it.id === this.friendId) >= 0) {
                    simpleSuccess(this.$t('notice.blacklistSuccess'));
                    return;
                }
                sendPacket(new BlacklistRequest(this.friendId, this.$store.state.user.id));
                break;
            case 4:
                // 取消黑名单
                if (_.findIndex(this.blacklist, it => it.id === this.friendId) < 0) {
                    simpleSuccess(this.$t('notice.blacklistCancelSuccess'));
                    return;
                }
                sendPacket(new BlacklistCancelRequest(this.friendId, this.$store.state.user.id));
                break;
            case 5:
                simpleSuccess(this.$t('notice.userReport'));
                break;
            }
        }
    }
};
</script>
