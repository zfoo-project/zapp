<template>
    <div>
        <not-found v-if="_.isEmpty(timeArrays)" :not-found-obj="location404" />
        <div v-else>
            <v-container>
                <v-row>
                    <v-col>
                        <v-card>
                            <v-list-item>
                                <v-list-item-icon>
                                    <v-icon v-text="location.icon" />
                                </v-list-item-icon>
                                <v-list-item-content>
                                    <v-list-item-title class="headline">{{ locationVo.word }}</v-list-item-title>
                                </v-list-item-content>
                            </v-list-item>

                            <v-row>
                                <v-col cols="12" md="4">
                                    <div v-ripple>
                                        <img
                                            ref="img"
                                            :src="toHighImgUrl(background)"
                                            :alt="locationVo.word"
                                            class="imgLocation"
                                        >
                                    </div>
                                </v-col>
                                <v-col cols="12" md="6">
                                    <v-card-text>
                                        {{ description }}
                                    </v-card-text>
                                </v-col>
                            </v-row>

                            <v-card-actions>
                                <v-spacer />
                                <v-btn icon color="primary" :large="large" :to="'/word/' + locationVo.id" :title="locationVo.word">
                                    <v-icon :large="large">mdi-rocket-launch-outline</v-icon>
                                </v-btn>
                                <v-btn icon color="primary" :large="large" :title="$t('appBar.subscribe')" @click="starClick()">
                                    <v-icon v-if="starFlag" :large="large">mdi-bookmark-minus-outline</v-icon>
                                    <v-icon v-else :large="large">mdi-bookmark-plus-outline</v-icon>
                                </v-btn>
                                <v-btn icon color="primary" :large="large" :title="$t('ts.share')" @click="shareClick($event)">
                                    <v-icon :large="large">mdi-share-variant</v-icon>
                                </v-btn>
                            </v-card-actions>
                        </v-card>
                    </v-col>
                </v-row>
            </v-container>

            <time-slice ref="ts" :key="$route.fullPath" :time-slice-array="timeArrays" :bottom-callback="fetchFeed" />
        </div>
    </div>
</template>

<script>
import clipboard from '@/util/clipboardUtils.js';
import { locationInfoApi, locationFeedApi } from '@/apiHttp/feedApi.js';
import { subscribeLocationApi } from '@/apiHttp/userApi.js';
import { chipConstant, appConstant, notFound } from '@/constant/constant.js';
import { isBlank } from '@/util/stringUtils.js';
import { simpleError } from '@/util/noticeUtils.js';
import { toHighImgUrl } from '@/util/fileUtils.js';
import { mapState } from 'vuex';

export default {
    name: 'LocationFeed',

    components: {
        TimeSlice: () => import('@/component/time/TimeSlice.vue'),
        NotFound: () => import('@/component/base/NotFound.vue')
    },

    data() {
        return {
            title: '',
            description: '',
            keywords: '',

            toHighImgUrl,
            location: chipConstant.location,
            location404: notFound.location404,
            background: appConstant.defaultLocation,
            timeArrays: [],
            page: 1,
            locationVo: {},
            large: this.$vuetify.breakpoint.mdAndUp,
            lastLocationId: null
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
        ...mapState('user', ['loggedIn', 'locations']),
        starFlag: function() {
            const locationId = this.$route.params.id;
            if (_.findIndex(this.locations, (it) => it.key === locationId) >= 0) {
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
            if (!this.checkState()) {
                return;
            }

            const locationId = this.$route.params.id;
            if (_.isEqual(locationId, this.lastLocationId)) {
                return;
            }

            this.page = 1;
            locationFeedApi(locationId, this.page).then(response => {
                const data = response.data;
                if (_.isEmpty(data)) {
                    this.setNoData();
                    return;
                }
                this.timeArrays = _.concat(this.timeArrays, data);
                this.lastLocationId = locationId;
            });

            this.page = this.page + 1;

            locationInfoApi(Array.of(locationId)).then(response => {
                const data = response.data;
                const location = _.first(data);
                if (_.isNil(location)) {
                    return;
                }
                this.locationVo = location;
                if (!isBlank(this.locationVo.background)) {
                    this.background = this.locationVo.background;
                }

                // 设置标题
                this.title = this.locationVo.word + ' - ' + this.$t('appUrl.location');

                // 设置描述
                if (!isBlank(this.locationVo.description)) {
                    this.description = this.locationVo.description;
                } else {
                    this.description = this.locationVo.word;
                }

                // 设置关键字
                this.keywords = this.locationVo.word;
            });
        },

        checkState() {
            const locationId = this.$route.params.id;
            if (isBlank(locationId)) {
                return false;
            }
            if (!_.startsWith(this.$route.path, '/location')) {
                return false;
            }
            return true;
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

        fetchFeed(ts) {
            if (!this.checkState()) {
                return;
            }

            locationFeedApi(this.$route.params.id, this.page).then(response => {
                ts.endLoading();

                const data = response.data;

                if (_.isEmpty(data)) {
                    this.setNoData();
                    return;
                }

                this.timeArrays = _.concat(this.timeArrays, data);
            });
            this.page = this.page + 1;
        },

        starClick() {
            if (!this.loggedIn) {
                simpleError(this.$t('notice.notSignInError'));
                return;
            }

            const locationId = this.$route.params.id;
            subscribeLocationApi(locationId).then(response => {
                this.$store.commit('user/updateLocation', {
                    key: locationId,
                    value: this.locationVo.word
                });
            });
        },

        shareClick(event) {
            clipboard(appConstant.homeUrl + this.$route.path, event);
        }
    }
};
</script>

<style scoped>
    .imgLocation {
        display: flex;
        max-width: 100%;
        cursor: pointer;
    }
</style>
