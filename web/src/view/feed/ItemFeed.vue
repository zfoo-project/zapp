<template>
    <div>
        <not-found v-if="_.isEmpty(timeArrays)" :not-found-obj="item404" />
        <div v-else>
            <v-container>
                <v-row>
                    <v-col>
                        <v-card>
                            <v-list-item>
                                <v-list-item-icon>
                                    <v-icon v-text="item.icon" />
                                </v-list-item-icon>
                                <v-list-item-content>
                                    <v-list-item-title class="headline">{{ itemVo.word }}</v-list-item-title>
                                </v-list-item-content>
                            </v-list-item>

                            <v-row>
                                <v-col cols="12" md="4">
                                    <div v-ripple>
                                        <img
                                            ref="img"
                                            :src="toHighImgUrl(background)"
                                            :alt="itemVo.word"
                                            class="imgItem"
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
                                <v-btn icon color="primary" :large="large" :to="'/word/' + itemVo.id" :title="itemVo.word">
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
import { itemInfoApi, itemFeedApi } from '@/apiHttp/feedApi.js';
import { chipConstant, appConstant, notFound } from '@/constant/constant.js';
import { subscribeItemApi } from '@/apiHttp/userApi.js';
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
            item: chipConstant.item,
            item404: notFound.item404,
            background: appConstant.defaultItem,
            timeArrays: [],
            page: 1,
            itemVo: {},
            large: this.$vuetify.breakpoint.mdAndUp,
            lastItemId: null
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
        ...mapState('user', ['loggedIn', 'items']),
        starFlag: function() {
            const itemId = this.$route.params.id;
            if (_.findIndex(this.items, (it) => it.key === itemId) >= 0) {
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

            const itemId = this.$route.params.id;
            if (_.isEqual(itemId, this.lastItemId)) {
                return;
            }

            this.page = 1;
            itemFeedApi(itemId, this.page).then(response => {
                const data = response.data;
                if (_.isEmpty(data)) {
                    this.setNoData();
                    return;
                }
                this.timeArrays = _.concat(this.timeArrays, data);
                this.lastItemId = itemId;
            });
            this.page = this.page + 1;

            itemInfoApi(Array.of(itemId)).then(response => {
                const data = response.data;
                const item = _.find(data, (it) => itemId === it.id);
                if (_.isNil(item)) {
                    return;
                }
                this.itemVo = item;
                if (!isBlank(this.itemVo.background)) {
                    this.background = this.itemVo.background;
                }

                // 设置标题
                this.title = this.itemVo.word + ' - ' + this.$t('appUrl.item');

                // 设置描述
                if (!isBlank(this.itemVo.description)) {
                    this.description = this.itemVo.description;
                } else {
                    this.description = this.itemVo.word;
                }

                // 设置关键字
                this.keywords = this.itemVo.word;
            });
        },

        checkState() {
            const itemId = this.$route.params.id;
            if (isBlank(itemId)) {
                return false;
            }
            if (!_.startsWith(this.$route.path, '/item')) {
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

            itemFeedApi(this.$route.params.id, this.page).then(response => {
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

            const itemId = this.$route.params.id;
            subscribeItemApi(itemId).then(response => {
                this.$store.commit('user/updateItem', {
                    key: itemId,
                    value: this.itemVo.word
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
    .imgItem {
        display: flex;
        max-width: 100%;
        cursor: pointer;
    }
</style>
