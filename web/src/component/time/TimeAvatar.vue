<template>
    <v-card v-if="!_.isEmpty(userProfile)" max-width="500">
        <v-list-item>
            <v-list-item-avatar>
                <v-img :src="simpleAvatar(userProfile)" />
            </v-list-item-avatar>

            <v-list-item-content>
                <v-list-item-title>
                    {{ userProfile.name }}
                    <v-icon dense :color="genderItems[userProfile.gender].color">{{ genderItems[userProfile.gender].icon }}</v-icon>
                </v-list-item-title>
                <v-list-item-subtitle>
                    {{ userProfile.signature }}
                </v-list-item-subtitle>
            </v-list-item-content>
        </v-list-item>

        <v-img :src="toBackground(userProfile.background)" />

        <v-container>
            <v-row>
                <v-col cols="12" class="py-0 my-0">
                    <template v-for="(location) in userProfile.locations">
                        <time-chip :id="location.id" :key="'location' + location.id" :name="location.value" :chip-type="chipConstant.location" />
                    </template>
                    <template v-for="(person) in userProfile.persons">
                        <time-chip :id="person.id" :key="'person' + person.id" :name="person.value" :chip-type="chipConstant.person" />
                    </template>
                    <template v-for="(item) in userProfile.items">
                        <time-chip :id="item.id" :key="'item' + item.id" :name="item.value" :chip-type="chipConstant.item" />
                    </template>
                </v-col>
            </v-row>
        </v-container>

        <v-card-text v-if="new Date().getTime() <= userProfile.customTime" class="py-0 my-0">
            <v-btn icon color="amber">
                <v-icon>mdi-emoticon-outline</v-icon>
            </v-btn>
            {{ userProfile.custom }}
        </v-card-text>

        <v-card-actions class="py-0 my-0">
            <v-subheader v-if="new Date().getTime() <= userProfile.customTime">
                {{ parseTime(userProfile.customTime) }}
            </v-subheader>
            <v-spacer />
            <v-btn v-if="followFlag" icon color="primary" :class="iconClass" :title="$t('user.unfollowUser')" @click="unfollowUser()">
                <v-icon :large="large">mdi-minus-circle-outline</v-icon>
            </v-btn>
            <v-btn v-else icon color="primary" :class="iconClass" :title="$t('user.followUser')" @click="followUser()">
                <v-icon :large="large">mdi-plus-circle-outline</v-icon>
            </v-btn>

            <v-btn icon color="primary" :class="iconClass" :title="$t('user.chat.info')" @click="chatClick()">
                <v-icon :large="large">mdi-chat-outline</v-icon>
            </v-btn>
            <v-btn icon color="primary" :class="iconClass" :to="'/user/info/' + userProfile.id" :title="userProfile.name">
                <v-icon :large="large">mdi-information-outline</v-icon>
            </v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
import { appConstant, chipConstant, genderItems } from '@/constant/constant.js';
import { parseTime } from '@/util/timeUtils.js';
import { toSimpleAvatarUrl, toBackground } from '@/util/fileUtils.js';
import { followUserApi, unfollowUserApi } from '@/apiHttp/userApi.js';
import { mapState } from 'vuex';
import { simpleError } from '@/util/noticeUtils.js';
import { sendPacket } from '@/util/websocketUtils.js';
import JoinGroupByUserIdRequest from '@/jsProtocol/group/member/JoinGroupByUserIdRequest.js';

export default {
    name: 'TimeAvatar',
    components: {
        TimeChip: () => import('@/component/time/TimeChip.vue')
    },
    props: {
        userProfile: {
            type: Object,
            default: () => {},
            required: true
        }
    },
    data() {
        return {
            parseTime,
            appConstant,
            chipConstant,
            genderItems,
            toBackground,
            iconClass: this.$vuetify.breakpoint.mdAndUp ? '' : 'px-0 mx-0',
            large: this.$vuetify.breakpoint.mdAndUp
        };
    },

    computed: {
        ...mapState('user', ['loggedIn', 'id', 'avatar', 'follows']),
        ...mapState('group', ['groups']),
        ...mapState('app', ['group']),

        followFlag: function() {
            const userId = this.userProfile.id;
            if (_.findIndex(this.follows, (it) => it === userId) >= 0) {
                return true;
            } else {
                return false;
            }
        }
    },

    methods: {
        followUser() {
            if (!this.loggedIn) {
                simpleError(this.$t('notice.notSignInError'));
                return;
            }

            const userId = this.userProfile.id;

            if (_.isEqual(userId, this.id)) {
                simpleError(this.$t('code_enum_1012'));
                return;
            }

            followUserApi(userId).then(response => {
                this.$store.commit('user/updateFollows', userId);
            });
        },

        chatClick() {
            if (!this.loggedIn) {
                simpleError(this.$t('notice.notSignInError'));
                return;
            }

            this.group.setGroupDrawer(true);
            if (_.findIndex(this.groups, it => it.id === '-' + this.userProfile.id) >= 0) {
                return;
            }

            sendPacket(new JoinGroupByUserIdRequest(this.userProfile.id));
        },

        unfollowUser() {
            if (!this.loggedIn) {
                simpleError(this.$t('notice.notSignInError'));
                return;
            }

            const userId = this.userProfile.id;
            unfollowUserApi(userId).then(response => {
                this.$store.commit('user/updateFollows', userId);
            });
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
