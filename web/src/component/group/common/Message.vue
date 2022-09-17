<template>
    <v-card :max-height="maxHeight" class="overflow-y-auto">
        <message-edit ref="messageEdit" :edit-message="editMessage" :edit-callback="editCallback" />
        <v-list>
            <v-list-item @click="moreClick">
                <v-list-item-content>
                    <div class="text-center">
                        {{ $t('user.chat.moreMessage') }}
                        <v-icon color="primary">
                            mdi-more
                        </v-icon>
                    </div>
                </v-list-item-content>
            </v-list-item>
            <v-divider />

            <template v-for="(message, index) in chatMessages">
                <v-hover :key="index" v-slot:default="{ hover }">
                    <v-list-item selectable link>
                        <template v-if="message.sendId === id">
                            <v-list-item-icon v-show="hover">
                                <v-menu offset-y bottom :close-on-content-click="false">
                                    <template v-slot:activator="{ on }">
                                        <v-icon v-on="on">mdi-dots-horizontal-circle-outline</v-icon>
                                    </template>
                                    <v-list>
                                        <v-list-item @click.stop="copyMessageClick(message.type, message.message, $event)">
                                            <v-list-item-icon>
                                                <v-icon>mdi-content-copy</v-icon>
                                            </v-list-item-icon>
                                            <v-list-item-title>{{ $t('user.chat.copyMessage') }}</v-list-item-title>
                                        </v-list-item>
                                        <template v-if="(!isGroup && message.sendId === id) || (isGroup && groupMessageEdit)">
                                            <v-list-item v-if="isGroup && !isPin" @click.stop="pinMessageClick(message.id)">
                                                <v-list-item-icon>
                                                    <v-icon>mdi-pin-outline</v-icon>
                                                </v-list-item-icon>
                                                <v-list-item-title>{{ $t('user.chat.pinMessage') }}</v-list-item-title>
                                            </v-list-item>
                                            <v-list-item v-if="isGroup && isPin" @click.stop="deletePinMessageClick(message.id)">
                                                <v-list-item-icon>
                                                    <v-icon>mdi-pin-off-outline</v-icon>
                                                </v-list-item-icon>
                                                <v-list-item-title>{{ $t('user.chat.deletePinMessage') }}</v-list-item-title>
                                            </v-list-item>
                                            <v-list-item v-if="message.type === groupConstant.messageEnum.text.type" @click.stop="editMessageClick(message.id, message.message)">
                                                <v-list-item-icon>
                                                    <v-icon>mdi-square-edit-outline</v-icon>
                                                </v-list-item-icon>
                                                <v-list-item-title>{{ $t('user.chat.editMessage') }}</v-list-item-title>
                                            </v-list-item>
                                            <v-list-item @click.stop="deleteMessageClick(message.id)">
                                                <v-list-item-icon>
                                                    <v-icon>mdi-delete-forever-outline</v-icon>
                                                </v-list-item-icon>
                                                <v-list-item-title>{{ $t('user.chat.deleteMessage') }}</v-list-item-title>
                                            </v-list-item>
                                        </template>
                                    </v-list>
                                </v-menu>
                            </v-list-item-icon>
                            <v-list-item-content>
                                <v-list-item-subtitle class="text-right overline">
                                    {{ getCustomizedTime(message.timestamp) }}
                                </v-list-item-subtitle>
                            </v-list-item-content>
                            <v-list-item-avatar>
                                <v-menu offset-x left :close-on-content-click="false">
                                    <template v-slot:activator="{ on }">
                                        <v-img :src="toSimpleAvatarUrl(avatar)" v-on="on" @click="avatarClick(id)" />
                                    </template>
                                    <group-avatar v-if="isGroup" :group-id="groupId" :group-member-vo="groupMemberVo" />
                                    <time-avatar v-else :user-profile="profile" />
                                </v-menu>
                            </v-list-item-avatar>
                        </template>
                        <template v-else>
                            <v-list-item-avatar>
                                <v-menu offset-y right :close-on-content-click="false">
                                    <template v-slot:activator="{ on }">
                                        <v-img :src="toSimpleAvatarUrl(message.avatar)" v-on="on" @click="avatarClick(message.sendId)" />
                                    </template>
                                    <group-avatar v-if="isGroup" :group-id="groupId" :group-member-vo="groupMemberVo" />
                                    <time-avatar v-else :user-profile="profile" />
                                </v-menu>
                            </v-list-item-avatar>
                            <v-list-item-content>
                                <v-list-item-subtitle class="overline">
                                    {{ getCustomizedTime(message.timestamp) }}
                                </v-list-item-subtitle>
                            </v-list-item-content>
                            <v-list-item-icon v-show="hover">
                                <v-menu offset-y bottom :close-on-content-click="false">
                                    <template v-slot:activator="{ on }">
                                        <v-icon v-on="on">mdi-dots-horizontal-circle-outline</v-icon>
                                    </template>
                                    <v-list>
                                        <v-list-item @click.stop="copyMessageClick(message.type, message.message, $event)">
                                            <v-list-item-icon>
                                                <v-icon>mdi-content-copy</v-icon>
                                            </v-list-item-icon>
                                            <v-list-item-title>{{ $t('user.chat.copyMessage') }}</v-list-item-title>
                                        </v-list-item>
                                        <template v-if="(!isGroup && message.sendId === id) || (isGroup && groupMessageEdit)">
                                            <v-list-item v-if="isGroup && !isPin" @click.stop="pinMessageClick(message.id)">
                                                <v-list-item-icon>
                                                    <v-icon>mdi-pin-outline</v-icon>
                                                </v-list-item-icon>
                                                <v-list-item-title>{{ $t('user.chat.pinMessage') }}</v-list-item-title>
                                            </v-list-item>
                                            <v-list-item v-if="isGroup && isPin" @click.stop="deletePinMessageClick(message.id)">
                                                <v-list-item-icon>
                                                    <v-icon>mdi-pin-off-outline</v-icon>
                                                </v-list-item-icon>
                                                <v-list-item-title>{{ $t('user.chat.deletePinMessage') }}</v-list-item-title>
                                            </v-list-item>
                                            <v-list-item v-if="message.type === groupConstant.messageEnum.text.type" @click.stop="editMessageClick(message.id)">
                                                <v-list-item-icon>
                                                    <v-icon>mdi-square-edit-outline</v-icon>
                                                </v-list-item-icon>
                                                <v-list-item-title>{{ $t('user.chat.editMessage') }}</v-list-item-title>
                                            </v-list-item>
                                            <v-list-item @click.stop="deleteMessageClick(message.id)">
                                                <v-list-item-icon>
                                                    <v-icon>mdi-delete-forever-outline</v-icon>
                                                </v-list-item-icon>
                                                <v-list-item-title>{{ $t('user.chat.deleteMessage') }}</v-list-item-title>
                                            </v-list-item>
                                        </template>
                                    </v-list>
                                </v-menu>
                            </v-list-item-icon>
                        </template>
                    </v-list-item>
                </v-hover>

                <template v-if="message.type === groupConstant.messageEnum.text.type">
                    <v-list-item :key="'text' + index">
                        <v-list-item-content>
                            <display-content :content="message.message" />
                        </v-list-item-content>
                    </v-list-item>
                </template>

                <template v-else-if="message.type === groupConstant.messageEnum.image.type">
                    <v-list-item :key="'image' + index">
                        <v-img v-ripple :src="toHighImgUrl(message.message)" contain />
                    </v-list-item>
                </template>

                <template v-else-if="message.type === groupConstant.messageEnum.video.type">
                    <v-list-item :key="'chatVideo' + index">
                        <time-video
                            :id="'chatVideo' + index"
                            ref="myVideos"
                            :poster="message.message + '?x-oss-process=video/snapshot,t_1000,f_jpg,m_fast'"
                            :url="message.message"
                            :cols="12"
                            :play-callback="playCallback"
                        />
                    </v-list-item>
                </template>

                <v-divider :key="'divider' + index" />
            </template>
        </v-list>
    </v-card>
</template>

<script>
import clipboard from '@/util/clipboardUtils.js';
import { mapMutations, mapState } from 'vuex';
import { groupConstant } from '@/constant/constant.js';
import { getCustomizedTime } from '@/util/timeUtils.js';
import { isBlank } from '@/util/stringUtils.js';
import { toSimpleAvatarUrl, toHighImgUrl } from '@/util/fileUtils.js';
import { sendPacket } from '@/util/websocketUtils.js';
import { simpleError } from '@/util/noticeUtils.js';

import GetUserCacheRequest from '@/jsProtocol/cache/GetUserCacheRequest.js';
import GroupMemberInfoRequest from '@/jsProtocol/group/member/GroupMemberInfoRequest.js';
import DeleteGroupMessageRequest from '@/jsProtocol/group/chat/DeleteGroupMessageRequest.js';
import DeleteFriendMessageRequest from '@/jsProtocol/friend/chat/DeleteFriendMessageRequest.js';
import EditFriendMessageRequest from '@/jsProtocol/friend/chat/EditFriendMessageRequest.js';
import EditGroupMessageRequest from '@/jsProtocol/group/chat/EditGroupMessageRequest.js';
import PinGroupMessageRequest from '@/jsProtocol/group/chat/PinGroupMessageRequest.js';
import DeleteGroupPinMessageRequest from '@/jsProtocol/group/chat/DeleteGroupPinMessageRequest.js';

export default {
    name: 'Message',
    components: {
        GroupAvatar: () => import('../group/GroupAvatar.vue'),
        MessageEdit: () => import('./MessageEdit.vue'),
        TimeAvatar: () => import('@/component/time/TimeAvatar.vue'),
        TimeVideo: () => import('@/component/time/TimeVideo.vue'),
        DisplayContent: () => import('@/component/base/DisplayContent.vue')
    },
    props: {
        isGroup: {
            type: Boolean,
            default: false,
            required: false
        },
        isPin: {
            type: Boolean,
            default: false,
            required: false
        },
        groupId: {
            type: String,
            default: '',
            required: false
        },
        channelId: {
            type: String,
            default: '',
            required: false
        },
        groupMessageEdit: {
            type: Boolean,
            default: false,
            required: false
        },
        friendId: {
            type: String,
            default: '',
            required: false
        },
        messageScrollTarget: {
            type: String,
            default: '',
            required: true
        },
        chatMessages: {
            type: Array,
            default: () => [],
            required: true
        },
        moreCallback: {
            type: Function,
            default: () => () => {},
            required: true
        }
    },

    data() {
        return {
            groupConstant,
            getCustomizedTime,
            toSimpleAvatarUrl,
            toHighImgUrl,

            maxHeight: document.documentElement.clientHeight >= 728 ? this.$vuetify.breakpoint.height * 0.8 : this.$vuetify.breakpoint.height * 0.73,

            messageId: '',
            editMessageId: '',
            editMessage: ''
        };
    },

    computed: {
        ...mapState('app', ['profile', 'groupMemberVo']),
        ...mapState('user', ['id', 'avatar']),
        ...mapState('group', ['groupMemberSimpleVos'])
    },

    watch: {
        chatMessages: function() {
            this.$nextTick(() => {
                const lastMessage = _.last(this.chatMessages);
                if (_.isNil(lastMessage)) {
                    return;
                }
                const div = document.getElementById(this.messageScrollTarget);
                if (lastMessage.id === this.messageId) {
                    div.scrollTop = 0;
                } else {
                    div.scrollTop = div.scrollHeight;
                }
                this.messageId = lastMessage.id;
            });
        }
    },
    methods: {
        ...mapMutations('app', ['setCopyMessage', 'setProfile', 'setGroupMemberVo']),

        moreClick() {
            if (_.isNil(this.moreCallback)) {
                return;
            }
            this.moreCallback();
        },

        playCallback(myPlayer) {
            this.$refs.myVideos.forEach(it => {
                if (!_.isEqual(myPlayer.id(), it.player.id())) {
                    it.player.pause();
                }
            });
        },

        stopAllVideo() {
            const myVideos = this.$refs.myVideos;
            if (_.isNil(myVideos) || _.isEmpty(myVideos)) {
                return;
            }
            myVideos.forEach(it => {
                it.player.pause();
            });
        },

        avatarClick(id) {
            if (this.isGroup) {
                this.groupMemberClick(id);
            } else {
                this.friendClick(id);
            }
        },

        friendClick(id) {
            if (!_.isEmpty(this.profile) && id === this.profile.id) {
                return;
            }

            if (this.$store.state.user.loggedIn) {
                if (this.$store.state.user.id === id) {
                    this.setProfile(this.$store.getters['user/myProfile']);
                    return;
                }
            }

            sendPacket(new GetUserCacheRequest(new Set([id])));
        },

        groupMemberClick(id) {
            if (!_.isEmpty(this.groupMemberVo)) {
                if (id === this.groupMemberVo.userCache.id) {
                    return;
                }
                if (this.$store.state.user.loggedIn) {
                    if (this.$store.state.user.id === id) {
                        const groupMemberSimpleVo = _.find(this.groupMemberSimpleVos, it => it.groupId === this.groupId);
                        this.setGroupMemberVo({
                            groupAuthIds: groupMemberSimpleVo.groupAuthIds,
                            userCache: this.$store.getters['user/myProfile']
                        });
                        return;
                    }
                }
            }

            sendPacket(new GroupMemberInfoRequest(this.groupId, [id]));
        },

        copyMessageClick(type, message, event) {
            this.setCopyMessage({
                type: type,
                message: message
            });
            clipboard(message, event);
        },

        editCallback(chatMessage) {
            if (isBlank(chatMessage)) {
                simpleError(this.$t('notice.friendChatMessageEmptyError'));
                return;
            }
            if (this.isGroup) {
                sendPacket(new EditGroupMessageRequest(this.channelId, chatMessage, this.groupId, this.editMessageId));
            } else {
                sendPacket(new EditFriendMessageRequest(chatMessage, this.friendId, this.editMessageId, this.id));
            }
        },

        editMessageClick(id, message) {
            this.editMessageId = id;
            this.editMessage = message;
            this.$refs.messageEdit.showDialog(true);
        },

        pinMessageClick(id) {
            sendPacket(new PinGroupMessageRequest(this.channelId, this.groupId, id));
        },

        deletePinMessageClick(id) {
            sendPacket(new DeleteGroupPinMessageRequest(this.channelId, this.groupId, id));
        },

        deleteMessageClick(id) {
            if (this.isGroup) {
                sendPacket(new DeleteGroupMessageRequest(this.channelId, this.groupId, id));
            } else {
                sendPacket(new DeleteFriendMessageRequest(this.friendId, id, this.id));
            }
        }
    }
};
</script>
