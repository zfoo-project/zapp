<template>
    <div>
        <v-container>
            <v-row>
                <v-col cols="12" md="6">
                    <div v-ripple>
                        <img
                            ref="img"
                            :src="toBackground(background)"
                            :alt="name"
                            class="imgProfile"
                        >
                    </div>
                </v-col>
            </v-row>

            <v-row>
                <v-dialog v-model="customStatusDialog" width="500">
                    <v-card class="elevation-12">
                        <v-card-title>{{ $t('user.custom') }}</v-card-title>
                        <v-card-text>
                            <v-form>
                                <v-text-field
                                    v-model="userCustomStatus"
                                    :label="$t('user.customLabel')"
                                    type="text"
                                >
                                    <template v-slot:prepend>
                                        <v-icon color="amber">mdi-emoticon-outline</v-icon>
                                    </template>
                                </v-text-field>
                            </v-form>
                            <v-subheader class="pl-0">{{ $t('user.customTimeMinute') }} {{ userCsExpiredTime }}</v-subheader>
                            <v-subheader class="pl-0">{{ $t('user.customTimeHour') }} {{ _.round(userCsExpiredTime / 60, 2) }}</v-subheader>
                            <v-subheader class="pl-0">{{ $t('user.customTimeDay') }} {{ _.round(userCsExpiredTime / 60 / 24, 2) }}</v-subheader>
                            <v-slider v-model="userCsExpiredTime" class="pt-12" thumb-label="always" color="primary" min="1" max="14400" />
                        </v-card-text>
                        <v-card-actions>
                            <v-spacer />
                            <v-btn class="text-capitalize" color="primary" :loading="customStatusLoading" @click="saveCustomStatus()">{{ $t('common.save') }}</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>
                <v-col cols="12" class="py-0 my-0">
                    <v-list-item class="pa-0">
                        <v-list-item-avatar v-ripple size="70">
                            <v-img :src="toSimpleAvatarUrl(avatar)" />
                        </v-list-item-avatar>

                        <v-list-item-content>
                            <v-list-item-title>
                                {{ name }}
                                <v-icon dense :color="genderItems[gender].color">{{ genderItems[gender].icon }}</v-icon>
                            </v-list-item-title>

                            <!-- pc端自定义表情 -->
                            <v-list-item-subtitle>
                                {{ signature }}
                                <template v-if="$vuetify.breakpoint.mdAndUp">
                                    <v-btn icon color="amber" @click="customStatusDialog = !customStatusDialog">
                                        <v-icon>mdi-emoticon-outline</v-icon>
                                    </v-btn>
                                    <template v-if="new Date().getTime() <= _.toNumber(customTime)">
                                        {{ custom }}
                                        {{ parseTime(customTime) }}
                                    </template>
                                </template>
                            </v-list-item-subtitle>
                        </v-list-item-content>
                    </v-list-item>
                </v-col>
            </v-row>

            <!-- 移动端自定义表情-->
            <v-row v-if="!$vuetify.breakpoint.mdAndUp">
                <v-col cols="12" class="py-0 my-0">
                    <v-btn icon color="amber" class="pa-0 ma-0" @click="customStatusDialog = !customStatusDialog">
                        <v-icon>mdi-emoticon-outline</v-icon>
                    </v-btn>
                    <template v-if="new Date().getTime() <= _.toNumber(customTime)">
                        {{ custom }}
                    </template>
                </v-col>
                <v-col cols="12" class="py-0 my-0">
                    <template v-if="new Date().getTime() <= _.toNumber(customTime)">
                        {{ parseTime(customTime) }}
                    </template>
                </v-col>
            </v-row>

            <v-row>
                <v-col cols="12" class="py-0 my-0">
                    <template v-for="(location) in locations">
                        <time-chip :id="location.key" :key="'location' + location.key" :name="location.value" :chip-type="chipConstant.location" />
                    </template>
                </v-col>
            </v-row>
            <v-row>
                <v-col cols="12" class="py-0 my-0">
                    <template v-for="(person) in persons">
                        <time-chip :id="person.key" :key="'person' + person.key" :name="person.value" :chip-type="chipConstant.person" />
                    </template>
                </v-col>
            </v-row>
            <v-row>
                <v-col cols="12" class="py-0 my-0">
                    <template v-for="(item) in items">
                        <time-chip :id="item.key" :key="'item' + item.key" :name="item.value" :chip-type="chipConstant.item" />
                    </template>
                </v-col>
            </v-row>

            <v-row>
                <v-col cols="12" class="pb-0 mb-0">
                    <v-chip class="mr-3" small>
                        <v-icon left>mdi-noodles</v-icon>
                        {{ numFormat(fanNum) }} {{ $t('user.fans') }}
                    </v-chip>
                    <v-chip class="mr-3" small>
                        <v-icon left>mdi-arrow-left-bold-circle-outline</v-icon>
                        {{ numFormat(free) }} {{ $t('user.free') }}
                    </v-chip>
                    <v-chip class="mr-3" small>
                        <v-icon left>mdi-arrow-right-bold-circle-outline</v-icon>
                        {{ numFormat(normal) }} {{ $t('user.normal') }}
                    </v-chip>
                    <v-chip class="mr-3" small>
                        <v-icon left>mdi-diamond-stone</v-icon>
                        {{ coin }} {{ $t('user.coin') }}
                    </v-chip>
                    <v-tooltip bottom>
                        <template v-slot:activator="{ on }">
                            <v-icon v-on="on">mdi-map-marker-question-outline</v-icon>
                        </template>
                        <p class="my-0 py-0">{{ $t('user.subLoveDesc') }}</p>
                        <p class="my-0 py-0">{{ $t('user.objLoveDesc') }}</p>
                        <p class="my-0 py-0">{{ $t('user.coinRewardDesc') }}</p>
                        <p class="my-0 py-0">{{ $t('user.coinLoveConsumeDesc') }}</p>
                        <p class="my-0 py-0">{{ $t('user.coinDownloadConsumeDesc') }}</p>
                        <p class="my-0 py-0">{{ $t('user.coinDownloadSelfDesc') }}</p>
                        <p class="my-0 py-0">{{ $t('user.coinCreditDesc') }}</p>
                        <p class="my-0 py-0">{{ $t('user.coinCreditOtherDesc') }}</p>
                    </v-tooltip>
                </v-col>
            </v-row>

            <v-row>
                <v-col cols="12">
                    <v-btn color="primary" to="/user/edit" link>
                        <v-icon>mdi-circle-edit-outline</v-icon>
                        {{ $t('user.edit') }}
                    </v-btn>
                    <v-btn color="primary" to="/user/star" link class="ml-2">
                        <v-icon>mdi-star-circle-outline</v-icon>
                        {{ $t('ts.myStar') }}
                    </v-btn>
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
            <user-list :key="$route.fullPath" :user-profile-array="userFollowArray" :bottom-callback="fetchUserProfileFollow" />
        </template>
        <template v-else>
            <user-list :key="$route.fullPath" :user-profile-array="userFanArray" :bottom-callback="fetchUserProfileFan" />
        </template>
    </div>
</template>

<script>
import { appConstant, chipConstant, genderItems } from '@/constant/constant.js';
import { mapMutations, mapState } from 'vuex';
import { saveCustomStatusApi, userProfileFollowApi, userProfileFanApi } from '@/apiHttp/userApi.js';
import { userTsApi } from '@/apiHttp/timeApi.js';
import { simpleSuccess, simpleError } from '@/util/noticeUtils.js';
import { parseTime } from '@/util/timeUtils.js';
import { numFormat } from '@/util/stringUtils.js';
import { toSimpleAvatarUrl, toBackground } from '@/util/fileUtils.js';
export default {
    name: 'MyProfile',
    components: {
        UserList: () => import('@/component/base/UserList.vue'),
        TimeChip: () => import('@/component/time/TimeChip.vue'),
        TimeSlice: () => import('@/component/time/TimeSlice.vue')
    },
    data() {
        return {
            appConstant,
            genderItems,
            chipConstant,
            parseTime,
            numFormat,
            toSimpleAvatarUrl,
            toBackground,

            tabs: null,

            customStatusDialog: false,
            customStatusLoading: false,
            userCustomStatus: '',
            userCsExpiredTime: 30,

            timeArrays: [],
            page: 1,

            userFollowArray: [],
            userFollowPage: 1,

            userFanArray: [],
            userFanPage: 1
        };
    },
    computed: {
        ...mapState('user', ['loggedIn', 'id', 'name', 'coin', 'signature', 'avatar', 'background', 'gender',
            'customTime', 'custom',
            'fanNum', 'starNum', 'free', 'normal',
            'locations', 'persons', 'items'])
    },

    created() {
        this.init();
        this.setMyProfile(this);
    },

    destroyed() {
        this.setMyProfile(null);
    },

    methods: {
        ...mapMutations('app', ['setMyProfile']),
        ...mapMutations('user', ['setCustomTime', 'setCustom']),

        init() {
            if (!this.loggedIn) {
                this.$router.push({ path: '/' });
                return;
            }
            this.page = 1;
            this.timeArrays = [];
            userTsApi(this.id, this.page).then(response => {
                const data = response.data;
                this.timeArrays = _.concat(this.timeArrays, data);
                if (_.size(data) < appConstant.feed.userPageSize) {
                    this.setNoData();
                }
            });
            this.page = this.page + 1;
        },

        setNoData() {
            setTimeout(() => {
                const ts = this.$refs.ts;
                if (_.isNil(ts)) {
                    this.setNoData();
                    return;
                }
                ts.setNoData();
            }, 500);
        },

        saveCustomStatus() {
            const userCsExpiredTime = this.userCsExpiredTime * 60 * 1000;
            const userCustomStatus = this.userCustomStatus;
            if (_.isEmpty(userCustomStatus)) {
                simpleError(this.$t('notice.userCustomEmptyError'));
                return;
            }
            this.customStatusLoading = true;
            saveCustomStatusApi(userCsExpiredTime, userCustomStatus).then(response => {
                const data = response.data;

                this.customStatusLoading = false;
                this.customStatusDialog = false;
                this.setCustomTime(data.customTime);
                this.setCustom(userCustomStatus);
                simpleSuccess(this.$t('notice.userCustomSuccess'));
            });
        },

        fetchTs(ts) {
            if (!this.loggedIn) {
                return;
            }
            userTsApi(this.id, this.page).then(response => {
                ts.endLoading();

                const data = response.data;
                this.timeArrays = _.concat(this.timeArrays, data);

                if (_.size(data) < appConstant.feed.userPageSize) {
                    ts.setNoData();
                }
            });
            this.page = this.page + 1;
        },

        fetchUserProfileFollow(ul) {
            if (!this.loggedIn) {
                return;
            }
            userProfileFollowApi(this.userFollowPage).then(response => {
                ul.endLoading();

                const data = response.data;
                this.userFollowArray = _.concat(this.userFollowArray, data);

                if (_.size(data) < appConstant.userListPageSize) {
                    ul.setNoData();
                }
            });
            this.userFollowPage = this.userFollowPage + 1;
        },

        fetchUserProfileFan(ul) {
            if (!this.loggedIn) {
                return;
            }
            userProfileFanApi(this.userFanPage).then(response => {
                ul.endLoading();

                const data = response.data;
                this.userFanArray = _.concat(this.userFanArray, data);

                if (_.size(data) < appConstant.userListPageSize) {
                    ul.setNoData();
                }
            });
            this.userFanPage = this.userFanPage + 1;
        },

        deleteTimeSlice(tsId) {
            const index = _.findIndex(this.timeArrays, it => it.id === tsId);
            if (index < 0) {
                return;
            }
            this.timeArrays.splice(index, 1);
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
