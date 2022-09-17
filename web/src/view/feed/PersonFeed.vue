<template>
    <div>
        <not-found v-if="timeArrays" :not-found-obj="person404" />
        <div v-else>
            <v-container>
                <v-row>
                    <v-col>
                        <v-card>
                            <v-list-item>
                                <v-list-item-icon>
                                    <v-icon v-text="person.icon" />
                                </v-list-item-icon>
                                <v-list-item-content>
                                    <v-list-item-title class="headline">{{ personVo.word }}</v-list-item-title>
                                </v-list-item-content>
                            </v-list-item>

                            <v-row>
                                <v-col cols="12" md="4">
                                    <div v-ripple>
                                        <img
                                            ref="img"
                                            :src="toHighImgUrl(background)"
                                            :alt="personVo.word"
                                            class="imgPerson"
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
                                <v-btn icon color="primary" :large="large" :to="'/word/' + personVo.id" :title="personVo.word">
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
import { personInfoApi, personFeedApi } from '@/apiHttp/feedApi.js';
import { subscribePersonApi } from '@/apiHttp/userApi.js';
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
            person: chipConstant.person,
            person404: notFound.person404,
            background: appConstant.defaultPerson,
            timeArrays: [],
            page: 1,
            personVo: {},
            large: this.$vuetify.breakpoint.mdAndUp,
            lastPersonId: null
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
        ...mapState('user', ['loggedIn', 'persons']),
        starFlag: function() {
            const personId = this.$route.params.id;
            if (_.findIndex(this.persons, (it) => it.key === personId) >= 0) {
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

            const personId = this.$route.params.id;
            if (_.isEqual(personId, this.lastPersonId)) {
                return;
            }

            this.page = 1;
            personFeedApi(personId, this.page).then(response => {
                const data = response.data;
                if (_.isEmpty(data)) {
                    this.setNoData();
                    return;
                }
                this.timeArrays = _.concat(this.timeArrays, data);
            });
            this.page = this.page + 1;

            personInfoApi(Array.of(personId)).then(response => {
                const data = response.data;
                const person = _.find(data, (it) => personId === it.id);
                if (_.isNil(person)) {
                    return;
                }
                this.personVo = person;
                if (!isBlank(this.personVo.background)) {
                    this.background = this.personVo.background;
                }

                // 设置标题
                this.title = this.personVo.word + ' - ' + this.$t('appUrl.person');

                // 设置描述
                if (!isBlank(this.personVo.description)) {
                    this.description = this.personVo.description;
                } else {
                    this.description = this.personVo.word;
                }

                // 设置关键字
                this.keywords = this.personVo.word;
            });
        },

        checkState() {
            const personId = this.$route.params.id;
            if (isBlank(personId)) {
                return false;
            }
            if (!_.startsWith(this.$route.path, '/person')) {
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

            personFeedApi(this.$route.params.id, this.page).then(response => {
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

            const personId = this.$route.params.id;
            subscribePersonApi(personId).then(response => {
                this.$store.commit('user/updatePerson', {
                    key: personId,
                    value: this.personVo.word
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
    .imgPerson {
        display: flex;
        max-width: 100%;
        cursor: pointer;
    }
</style>
