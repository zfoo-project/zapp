<template>
    <v-container v-if="!_.isNil(groupVo) && !_.isNil(groupMemberSimpleVo)">

        <v-dialog v-model="dialog" width="500">
            <group-avatar :group-id="groupId" :group-member-vo="currentGroupMemberVo" />
        </v-dialog>

        <group-change-admin
            ref="groupChangeAdmin"
            :group-id="groupId"
            :group-name="groupVo.name"
            :admin-id="adminId"
            :admin-name="adminName"
            :admin-avatar="adminAvatar"
        />

        <v-row>
            <v-col>
                <v-list three-line dense>
                    <template v-for="(groupMemberVo, index) in groupMemberVos">
                        <!--                            <user-item :key="userProfile.id" :user-cache="userProfile" :click-callback="userClick" :user-actions="userActions" />-->
                        <v-list-item :key="'member' + index">
                            <v-list-item-avatar @click.stop="memberClick(groupMemberVo)">
                                <v-img v-ripple :src="toSimpleAvatarUrl(groupMemberVo.userCache.avatar)" />
                            </v-list-item-avatar>

                            <v-list-item-content>
                                <v-list-item-title>
                                    {{ toTagOrName(groupMemberVo.userCache) }}
                                    <v-icon dense :color="genderItems[groupMemberVo.userCache.gender].color">{{ genderItems[groupMemberVo.userCache.gender].icon }}</v-icon>
                                </v-list-item-title>
                                <v-list-item-subtitle>
                                    <v-chip
                                        v-for="(groupAuthVo, groupAuthVoIndex) in groupAuthIdsToGroupAuthVos(groupVo, groupMemberVo.groupAuthIds)"
                                        :key="'chip' + groupAuthVoIndex"
                                        class="mt-1 mr-1"
                                        :color="groupAuthVo.color"
                                        text-color="white"
                                        x-small
                                        ripple
                                        :close="userHasGroupAuth(groupConstant.groupAuthEnum.specialAuth, groupVo, groupMemberSimpleVo)"
                                        @click:close="removeMemberFromGroupAuthClick(groupMemberVo, groupAuthVo)"
                                    >
                                        {{ groupAuthVo.name }}
                                    </v-chip>
                                </v-list-item-subtitle>
                                <v-list-item-subtitle v-if="new Date().getTime() <= groupMemberVo.userCache.customTime">
                                    <v-icon color="amber">mdi-emoticon-outline</v-icon>
                                    {{ groupMemberVo.userCache.custom }}
                                </v-list-item-subtitle>
                            </v-list-item-content>

                            <v-list-item-action v-if="userHasGroupAuth(groupConstant.groupAuthEnum.specialAuth, groupVo, groupMemberSimpleVo)">
                                <v-menu offset-x left>
                                    <template v-slot:activator="{ on }">
                                        <v-btn fab small icon color="primary" v-on="on">
                                            <icon-with-tooltip icon="mdi-plus-circle-outline" :tooltip="$t('group.operation.member.addGroupAuthTip')" />
                                        </v-btn>
                                    </template>

                                    <v-list>
                                        <v-list-item v-for="(groupAuthVo, groupAuthVoIndex) in remainGroupAuthsOfMember(groupVo, groupMemberVo)" :key="groupAuthVoIndex" @click="addMemberToGroupAuthClick(groupMemberVo, groupAuthVo)">
                                            <v-list-item-icon>
                                                <v-icon>mdi-badge-account-outline</v-icon>
                                            </v-list-item-icon>
                                            <v-list-item-title>{{ groupAuthVo.name }}</v-list-item-title>
                                        </v-list-item>
                                    </v-list>
                                </v-menu>
                            </v-list-item-action>
                            <v-list-item-action v-if="userHasGroupAuth(groupConstant.groupAuthEnum.specialAuth, groupVo, groupMemberSimpleVo)">
                                <v-menu offset-x left>
                                    <template v-slot:activator="{ on }">
                                        <v-btn fab small icon color="primary" v-on="on">
                                            <icon-with-tooltip icon="mdi-dots-horizontal-circle-outline" :tooltip="$t('ts.more')" />
                                        </v-btn>
                                    </template>

                                    <v-list>
                                        <v-list-item @click="kickMemberClick(groupMemberVo)">
                                            <v-list-item-icon>
                                                <v-icon>mdi-delete-alert-outline</v-icon>
                                            </v-list-item-icon>
                                            <v-list-item-title>{{ $t('group.operation.member.deleteMemberTip') }}</v-list-item-title>
                                        </v-list-item>

                                        <v-list-item
                                            v-if="(groupVo.adminId === id) && (groupVo.id !== '-' + id) && (id !== groupMemberVo.userCache.id)"
                                            @click="showGroupChangeAdminDialog(true, groupMemberVo.userCache.id, groupMemberVo.userCache.name, groupMemberVo.userCache.avatar)"
                                        >
                                            <v-list-item-icon>
                                                <v-icon>mdi-account-convert</v-icon>
                                            </v-list-item-icon>
                                            <v-list-item-title>{{ $t('group.operation.changeGroup.title') }}</v-list-item-title>
                                        </v-list-item>
                                    </v-list>
                                </v-menu>
                            </v-list-item-action>
                        </v-list-item>
                        <v-divider :key="'divider' + index" />
                    </template>
                </v-list>
            </v-col>
        </v-row>

        <v-row>
            <v-col>
                <v-list dense>
                    <v-list-item @click="moreMembers">
                        <v-list-item-content>
                            <div class="text-center">
                                {{ $t('group.operation.member.more') }}
                                <v-icon color="primary">
                                    mdi-more
                                </v-icon>
                            </div>
                        </v-list-item-content>
                    </v-list-item>
                </v-list>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import { mapState } from 'vuex';
import { sendPacket } from '@/util/websocketUtils.js';
import { toSimpleAvatarUrl } from '@/util/fileUtils.js';
import { remainGroupAuthsOfMember, groupAuthIdsToGroupAuthVos, userHasGroupAuth, sortedGroupAuths } from '@/util/groupUtils.js';
import { groupConstant, genderItems } from '@/constant/constant.js';
import { toTagOrName } from '@/util/stringUtils.js';

import AddMemberToGroupAuthRequest from '@/jsProtocol/group/auth/AddMemberToGroupAuthRequest.js';
import RemoveMemberFromGroupAuthRequest from '@/jsProtocol/group/auth/RemoveMemberFromGroupAuthRequest.js';
import GroupMemberListRequest from '@/jsProtocol/group/member/GroupMemberListRequest.js';
import KickMemberRequest from '@/jsProtocol/group/member/KickMemberRequest.js';

export default {
    name: 'MemberInfo',

    components: {
        GroupAvatar: () => import('../GroupAvatar.vue'),
        GroupChangeAdmin: () => import('./GroupChangeAdmin.vue'),
        IconWithTooltip: () => import('@/component/base/IconWithTooltip.vue')
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
            toSimpleAvatarUrl,
            toTagOrName,

            groupConstant,
            genderItems,

            remainGroupAuthsOfMember,
            groupAuthIdsToGroupAuthVos,
            userHasGroupAuth,
            sortedGroupAuths,

            dialog: false,
            currentGroupMemberVo: null,
            page: 1,

            adminId: '',
            adminName: '',
            adminAvatar: ''
        };
    },

    computed: {
        ...mapState('user', ['id']),
        ...mapState('app', ['groupChatInput', 'ios']),
        ...mapState('group', ['groups', 'groupMemberVos', 'groupMemberSimpleVos']),

        groupVo() {
            return _.find(this.groups, it => it.id === this.groupId);
        },
        groupMemberSimpleVo() {
            return _.find(this.groupMemberSimpleVos, it => it.groupId === this.groupId);
        }
    },


    methods: {
        init(groupId) {
            sendPacket(new GroupMemberListRequest(groupId, 1));
            this.page = 1;
        },

        backClick() {
            this.$store.state.app.group.showGroupHome();
        },

        moreMembers() {
            this.page = this.page + 1;
            sendPacket(new GroupMemberListRequest(this.groupId, this.page));
        },

        memberClick(groupMemberVo) {
            this.currentGroupMemberVo = groupMemberVo;
            this.dialog = true;
        },

        kickMemberClick(groupMemberVo) {
            sendPacket(new KickMemberRequest(this.groupId, groupMemberVo.userCache.id));
        },

        showGroupChangeAdminDialog(value, adminId, adminName, adminAvatar) {
            this.adminId = adminId;
            this.adminName = adminName;
            this.adminAvatar = adminAvatar;
            this.$refs.groupChangeAdmin.showDialog(value);
        },

        addMemberToGroupAuthClick(groupMemberVo, groupAuthVo) {
            sendPacket(new AddMemberToGroupAuthRequest(groupAuthVo.id, this.groupId, groupMemberVo.userCache.id));
        },

        removeMemberFromGroupAuthClick(groupMemberVo, groupAuthVo) {
            sendPacket(new RemoveMemberFromGroupAuthRequest(groupAuthVo.id, this.groupId, groupMemberVo.userCache.id));
        }
    }
};
</script>
