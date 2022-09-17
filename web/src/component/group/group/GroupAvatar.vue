<template>
    <v-card v-if="!_.isEmpty(groupMemberVo)" max-width="500" min-width="500">
        <v-list-item>
            <v-list-item-avatar>
                <v-img :src="simpleAvatar(groupMemberVo.userCache)" />
            </v-list-item-avatar>

            <v-list-item-content>
                <v-list-item-title>
                    {{ groupMemberVo.userCache.name }}
                    <v-icon dense :color="genderItems[groupMemberVo.userCache.gender].color">{{ genderItems[groupMemberVo.userCache.gender].icon }}</v-icon>
                </v-list-item-title>
                <v-list-item-subtitle>
                    {{ groupMemberVo.userCache.signature }}
                </v-list-item-subtitle>
            </v-list-item-content>
        </v-list-item>

        <v-img :src="toBackground(groupMemberVo.userCache.background)" />

        <v-container>
            <v-row>
                <v-col cols="12" class="py-0 my-0">
                    <template v-for="(location) in groupMemberVo.userCache.locations">
                        <time-chip :id="location.id" :key="'location' + location.id" :name="location.value" :chip-type="chipConstant.location" />
                    </template>
                    <template v-for="(person) in groupMemberVo.userCache.persons">
                        <time-chip :id="person.id" :key="'person' + person.id" :name="person.value" :chip-type="chipConstant.person" />
                    </template>
                    <template v-for="(item) in groupMemberVo.userCache.items">
                        <time-chip :id="item.id" :key="'item' + item.id" :name="item.value" :chip-type="chipConstant.item" />
                    </template>
                </v-col>
            </v-row>
            <v-row>
                <v-col cols="12" class="py-0 my-0">
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
                </v-col>
            </v-row>
            <v-row v-if="userHasGroupAuth(groupConstant.groupAuthEnum.specialAuth, groupVo, groupMemberSimpleVo)">
                <v-col cols="1" class="py-0 my-0">
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
                </v-col>
                <v-col cols="1" class="py-0 my-0">
                    <v-tooltip bottom>
                        <template v-slot:activator="{ on }">
                            <v-btn fab small icon color="primary" v-on="on" @click.stop="kickMemberClick(groupMemberVo)">
                                <v-icon>mdi-minus-circle-outline</v-icon>
                            </v-btn>
                        </template>
                        <span>{{ $t('group.operation.member.deleteMemberTip') }}</span>
                    </v-tooltip>
                </v-col>
            </v-row>
        </v-container>

        <v-card-text v-if="new Date().getTime() <= groupMemberVo.userCache.customTime" class="py-0 my-0">
            <v-btn icon color="amber">
                <v-icon>mdi-emoticon-outline</v-icon>
            </v-btn>
            {{ groupMemberVo.userCache.custom }}
        </v-card-text>

        <v-card-actions class="py-0 my-0">
            <v-subheader v-if="new Date().getTime() <= groupMemberVo.userCache.customTime">
                {{ parseTime(groupMemberVo.userCache.customTime) }}
            </v-subheader>
            <v-spacer />
            <v-btn v-if="followFlag" icon color="primary" :class="iconClass" @click="unstarUser()">
                <v-icon :large="large">mdi-plus-circle-outline</v-icon>
            </v-btn>
            <v-btn v-else icon color="primary" :class="iconClass" @click="starUser()">
                <v-icon :large="large">mdi-minus-circle-outline</v-icon>
            </v-btn>

            <v-btn icon color="primary" :class="iconClass">
                <v-icon :large="large">mdi-chat-outline</v-icon>
            </v-btn>
            <v-btn icon color="primary" :class="iconClass" @click="userDetail()">
                <v-icon :large="large">mdi-information-outline</v-icon>
            </v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
import { mapState } from 'vuex';
import { sendPacket } from '@/util/websocketUtils.js';
import { remainGroupAuthsOfMember, groupAuthIdsToGroupAuthVos, userHasGroupAuth } from '@/util/groupUtils.js';
import { appConstant, groupConstant, chipConstant, genderItems } from '@/constant/constant.js';
import { parseTime } from '@/util/timeUtils.js';
import { toSimpleAvatarUrl, toBackground } from '@/util/fileUtils.js';
import { followUserApi, unfollowUserApi } from '@/apiHttp/userApi.js';
import { simpleError } from '@/util/noticeUtils.js';

import AddMemberToGroupAuthRequest from '@/jsProtocol/group/auth/AddMemberToGroupAuthRequest.js';
import RemoveMemberFromGroupAuthRequest from '@/jsProtocol/group/auth/RemoveMemberFromGroupAuthRequest.js';
import KickMemberRequest from '@/jsProtocol/group/member/KickMemberRequest.js';

export default {
    name: 'GroupAvatar',
    components: {
        TimeChip: () => import('@/component/time/TimeChip.vue'),
        IconWithTooltip: () => import('@/component/base/IconWithTooltip.vue')
    },
    props: {
        groupId: {
            type: String,
            default: '',
            required: true
        },
        groupMemberVo: {
            type: Object,
            default: () => {},
            required: true
        }
    },
    data() {
        return {
            parseTime,
            appConstant,
            groupConstant,
            chipConstant,
            genderItems,
            toBackground,

            remainGroupAuthsOfMember,
            groupAuthIdsToGroupAuthVos,
            userHasGroupAuth,

            iconClass: this.$vuetify.breakpoint.mdAndUp ? '' : 'px-0 mx-0',
            large: this.$vuetify.breakpoint.mdAndUp
        };
    },

    computed: {
        ...mapState('user', ['id', 'avatar', 'follows']),
        ...mapState('group', ['groups', 'groupMemberVos', 'groupMemberSimpleVos']),

        followFlag: function() {
            const userId = this.groupMemberVo.userCache.id;
            if (_.findIndex(this.follows, (it) => it === userId) >= 0) {
                return true;
            } else {
                return false;
            }
        },

        groupVo() {
            return _.find(this.groups, it => it.id === this.groupId);
        },
        groupMemberSimpleVo() {
            return _.find(this.groupMemberSimpleVos, it => it.groupId === this.groupId);
        }
    },


    methods: {
        userDetail() {
            if (this.groupMemberVo.userCache.id === this.$store.state.user.id) {
                this.$router.push({ path: '/user/profile' });
            } else {
                this.$router.push({ path: '/user/info/' + this.groupMemberVo.userCache.id });
            }
        },

        starUser() {
            const userId = this.groupMemberVo.userCache.id;
            if (_.isEqual(userId, this.id)) {
                simpleError(this.$t('code_enum_1012'));
                return;
            }

            followUserApi(userId).then(response => {
                this.$store.commit('user/updateFollows', userId);
            });
        },

        unstarUser() {
            const userId = this.groupMemberVo.userCache.id;
            unfollowUserApi(userId).then(response => {
                this.$store.commit('user/updateFollows', userId);
            });
        },

        addMemberToGroupAuthClick(groupMemberVo, groupAuthVo) {
            sendPacket(new AddMemberToGroupAuthRequest(groupAuthVo.id, this.groupId, groupMemberVo.userCache.id));
        },

        kickMemberClick(groupMemberVo) {
            sendPacket(new KickMemberRequest(this.groupId, groupMemberVo.userCache.id));
        },

        removeMemberFromGroupAuthClick(groupMemberVo, groupAuthVo) {
            sendPacket(new RemoveMemberFromGroupAuthRequest(groupAuthVo.id, this.groupId, groupMemberVo.userCache.id));
        },

        simpleAvatar(userInfo) {
            if (this.id === userInfo.id) {
                return toSimpleAvatarUrl(this.avatar);
            } else {
                return toSimpleAvatarUrl(userInfo.avatar);
            }
        }
    }
};
</script>
