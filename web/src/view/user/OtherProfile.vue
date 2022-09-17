<template>
    <div>
        <not-found v-if="_.isNil(userProfile)" :not-found-obj="user404" />
        <template v-else>
            <v-container v-if="!_.isNil(userProfile)">
                <v-row>
                    <v-col cols="12" md="6">
                        <div v-ripple>
                            <img
                                ref="img"
                                :src="toBackground(userProfile.background)"
                                :alt="userProfile.name"
                                class="imgProfile"
                            >
                        </div>
                    </v-col>
                </v-row>

                <v-row>
                    <v-col cols="12">
                        <v-list-item class="pa-0">
                            <v-list-item-avatar v-ripple size="70">
                                <v-img :src="toSimpleAvatarUrl(userProfile.avatar)" />
                            </v-list-item-avatar>

                            <v-list-item-content>
                                <v-list-item-title>
                                    {{ userProfile.name }}
                                    <v-icon dense :color="genderItems[userProfile.gender].color">{{ genderItems[userProfile.gender].icon }}</v-icon>
                                </v-list-item-title>
                                <v-list-item-subtitle>
                                    {{ userProfile.signature }}
                                    <template v-if="!_.isEmpty(userProfile.custom)">
                                        <v-btn icon color="amber">
                                            <v-icon>mdi-emoticon-outline</v-icon>
                                        </v-btn>
                                        <template v-if="new Date().getTime() <= userProfile.customTime">
                                            {{ userProfile.custom }}
                                            {{ parseTime(userProfile.customTime) }}
                                        </template>
                                    </template>
                                </v-list-item-subtitle>
                            </v-list-item-content>
                        </v-list-item>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col cols="12" class="py-0 my-0">
                        <template v-for="(location) in userProfile.locations">
                            <time-chip :id="location.key" :key="'location' + location.key" :name="location.value" :chip-type="chipConstant.location" />
                        </template>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col cols="12" class="py-0 my-0">
                        <template v-for="(person) in userProfile.persons">
                            <time-chip :id="person.key" :key="'person' + person.key" :name="person.value" :chip-type="chipConstant.person" />
                        </template>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col cols="12" class="py-0 my-0">
                        <template v-for="(item) in userProfile.items">
                            <time-chip :id="item.key" :key="'item' + item.key" :name="item.value" :chip-type="chipConstant.item" />
                        </template>
                    </v-col>
                </v-row>

                <v-row>
                    <v-col cols="12" class="pb-0 mb-0">
                        <v-chip class="mr-3" small>
                            <v-icon left>mdi-noodles</v-icon>
                            {{ numFormat(userProfile.fanNum) }} {{ $t('user.fans') }}
                        </v-chip>
                        <v-chip class="mr-3" small>
                            <v-icon left>mdi-arrow-left-bold-circle-outline</v-icon>
                            {{ numFormat(userProfile.free) }} {{ $t('user.free') }}
                        </v-chip>
                        <v-chip class="mr-3" small>
                            <v-icon left>mdi-arrow-right-bold-circle-outline</v-icon>
                            {{ numFormat(userProfile.normal) }} {{ $t('user.normal') }}
                        </v-chip>
                    </v-col>
                </v-row>

                <v-row>
                    <v-col cols="12" class="py-0 my-0">
                        <v-list-item class="pa-0">
                            <v-list-item-action>
                                <v-btn v-if="followFlag" color="primary" @click="unstarUser()">
                                    <v-icon>mdi-minus</v-icon>
                                    {{ $t('user.unfollowUser') }}
                                </v-btn>
                                <v-btn v-else color="primary" @click="starUser()">
                                    <v-icon>mdi-plus</v-icon>
                                    {{ $t('user.followUser') }}
                                </v-btn>
                            </v-list-item-action>
                            <v-list-item-action>
                                <v-btn color="primary" @click="chatClick()">
                                    <v-icon>mdi-chat-outline</v-icon>
                                    {{ $t('user.chat.info') }}
                                </v-btn>
                            </v-list-item-action>
                        </v-list-item>
                    </v-col>
                </v-row>


                <v-row>
                    <v-col cols="12" md="6">
                        <v-card>
                            <v-tabs v-model="tabs" grow centered>
                                <v-tab>
                                    <v-icon small left>{{ appConstant.timeSliceLinks.icon }}</v-icon>
                                    {{ $t(appConstant.timeSliceLinks.value) }}
                                </v-tab>
                                <v-tab>
                                    <v-icon small left>mdi-plus-box-multiple-outline</v-icon>
                                    {{ $t('user.followTab') }}
                                </v-tab>
                                <v-tab>
                                    <v-icon small left>mdi-noodles</v-icon>
                                    {{ $t('user.fansTab') }}
                                </v-tab>
                            </v-tabs>
                        </v-card>
                    </v-col>
                </v-row>
            </v-container>
            <template v-if="tabs === 0">
                <time-slice ref="ts" :key="$route.fullPath" :time-slice-array="timeArrays" :bottom-callback="fetchTs" />
            </template>
            <template v-else-if="tabs === 1">
                <user-list :key="$route.fullPath" :user-profile-array="userFollowArray" :bottom-callback="fetchUserFollowList" />
            </template>
            <template v-else>
                <user-list :key="$route.fullPath" :user-profile-array="userFanArray" :bottom-callback="fetchUserFanList" />
            </template>
        </template>
    </div>
</template>

<script>
import { mapState } from 'vuex';
import { appConstant, chipConstant, genderItems, notFound } from '@/constant/constant.js';
import { parseTime } from '@/util/timeUtils.js';
import { getUserInfoApi, followUserApi, unfollowUserApi, userFollowListApi, userFanListApi } from '@/apiHttp/userApi.js';
import { userTsApi } from '@/apiHttp/timeApi.js';
import { numFormat } from '@/util/stringUtils.js';
import { toSimpleAvatarUrl, toBackground } from '@/util/fileUtils.js';
import { sendPacket } from '@/util/websocketUtils.js';
import { simpleError } from '@/util/noticeUtils.js';
import { isBlank } from '@/util/stringUtils.js';

import JoinGroupByUserIdRequest from '@/jsProtocol/group/member/JoinGroupByUserIdRequest.js';

export default {
    name: 'OtherProfile',
    components: {
        UserList: () => import('@/component/base/UserList.vue'),
        TimeChip: () => import('@/component/time/TimeChip.vue'),
        TimeSlice: () => import('@/component/time/TimeSlice.vue'),
        NotFound: () => import('@/component/base/NotFound.vue')
    },
    data() {
        return {
            title: '',
            description: '',
            keywords: '',

            genderItems,
            appConstant,
            chipConstant,
            parseTime,
            numFormat,
            toSimpleAvatarUrl,
            toBackground,
            tabs: null,
            user404: notFound.user404,
            userProfile: null,

            timeArrays: null,
            page: 1,

            userFollowArray: [],
            userFollowPage: 1,

            userFanArray: [],
            userFanPage: 1

        };
    },

    metaInfo() {
        return {
            title: this.title,
            meta: [
                {
                    vmid: 'description',
                    name: 'description',
                    content: this.description
                },
                {
                    vmid: 'keywords',
                    name: 'keywords',
                    content: this.keywords
                }
            ]
        };
    },

    computed: {
        ...mapState('user', ['loggedIn', 'id', 'follows']),
        ...mapState('group', ['groups']),
        ...mapState('app', ['group']),

        followFlag: function() {
            const userId = this.$route.params.id;
            if (_.findIndex(this.follows, (it) => it === userId) >= 0) {
                return true;
            } else {
                return false;
            }
        }
    },

    watch: {
        $route() {
            this.init();
        }
    },

    created() {
        this.init();
    },

    methods: {
        init() {
            const userId = this.$route.params.id;
            if (!this.checkState()) {
                return;
            }

            if (userId === this.$store.state.user.id) {
                this.$router.push({ path: '/user/profile' });
                return;
            }

            if (!_.isNil(this.userProfile) && _.isEqual(userId, this.userProfile.id)) {
                return;
            }

            getUserInfoApi(Array.of(userId)).then(response => {
                const data = response.data;
                const findResult = _.find(data, (it) => userId === it.id);
                if (_.isNil(findResult)) {
                    this.userProfile = null;
                    return;
                }
                this.userProfile = findResult;

                // 设置标题
                this.title = this.userProfile.name + ' - ' + this.$t('common.brand');

                // 设置描述
                if (!isBlank(this.userProfile.signature)) {
                    this.description = this.userProfile.signature;
                } else {
                    this.description = this.userProfile.name;
                }
                if (!isBlank(this.userProfile.custom)) {
                    this.description = this.description + ' | ' + this.userProfile.custom;
                }

                // 设置关键字
                this.keywords = this.userProfile.name;
            });

            // 初始化时间片
            this.page = 1;
            this.timeArrays = [];
            userTsApi(userId, this.page).then(response => {
                const data = response.data;
                this.timeArrays = _.concat(this.timeArrays, data);
                if (_.size(data) < appConstant.feed.userPageSize) {
                    setTimeout(() => {
                        this.$refs.ts.setNoData();
                    }, 500);
                }
            });
            this.page = this.page + 1;
        },

        checkState() {
            const userId = this.$route.params.id;
            if (isBlank(userId)) {
                return false;
            }
            if (!_.startsWith(this.$route.path, '/user/info')) {
                return false;
            }
            return true;
        },

        fetchTs(ts) {
            if (!this.checkState()) {
                return;
            }

            const userId = this.$route.params.id;
            userTsApi(userId, this.page).then(response => {
                ts.endLoading();

                const data = response.data;
                this.timeArrays = _.concat(this.timeArrays, data);

                if (_.size(data) < appConstant.feed.userPageSize) {
                    ts.setNoData();
                }
            });
            this.page = this.page + 1;
        },

        starUser() {
            const userId = this.$route.params.id;
            if (_.isEqual(userId, this.id)) {
                simpleError(this.$t('code_enum_1012'));
                return;
            }
            followUserApi(userId).then(response => {
                this.$store.commit('user/updateFollows', userId);
            });
        },

        unstarUser() {
            const userId = this.$route.params.id;
            unfollowUserApi(userId).then(response => {
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

        fetchUserFollowList(ul) {
            if (!this.checkState()) {
                return;
            }

            const userId = this.$route.params.id;
            userFollowListApi(userId, this.userFollowPage).then(response => {
                ul.endLoading();

                const data = response.data;
                this.userFollowArray = _.concat(this.userFollowArray, data);

                if (_.size(data) < appConstant.userListPageSize) {
                    ul.setNoData();
                }
            });
            this.userFollowPage = this.userFollowPage + 1;
        },

        fetchUserFanList(ul) {
            if (!this.checkState()) {
                return;
            }

            const userId = this.$route.params.id;
            userFanListApi(userId, this.userFanPage).then(response => {
                ul.endLoading();

                const data = response.data;
                this.userFanArray = _.concat(this.userFanArray, data);

                if (_.size(data) < appConstant.userListPageSize) {
                    ul.setNoData();
                }
            });
            this.userFanPage = this.userFanPage + 1;
        }
    }
};
</script>

<style scoped>
    .imgProfile {
        display: flex;
        max-width: 100%;
        cursor:pointer;
    }
</style>
