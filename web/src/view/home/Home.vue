<template>
    <div>
        <time-slice
            ref="ts"
            :key="$route.fullPath"
            :time-slice-array="timeArrays"
            :bottom-callback="fetchFeed"
        />
        <app-footer />
    </div>
</template>

<script>
import Cookies from 'js-cookie';
import { homeFeedApi } from '@/apiHttp/homeApi.js';
import { isBlank } from '@/util/stringUtils.js';
import { mapMutations } from 'vuex';
export default {
    name: 'Home',

    components: {
        TimeSlice: () => import('@/component/time/TimeSlice.vue'),
        AppFooter: () => import('@/component/base/AppFooter.vue')
    },

    data() {
        return {
            homeFeedKey: 'zappHome',

            description: this.$t('appUrl.homeDescription'),
            keywords: this.$t('appUrl.homeKeywords'),

            feed: -1,
            page: 1,
            timeArrays: []
        };
    },

    metaInfo() {
        return {
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

    created() {
        this.setHome(this);
        this.init();
    },
    destroyed() {
        this.setHome(null);
    },

    methods: {
        ...mapMutations('app', ['setHome']),

        init() {
            this.feed = -1;
            this.page = 1;

            const homeFeedValue = Cookies.get(this.homeFeedKey);
            if (!isBlank(homeFeedValue)) {
                const homeFeedValueSplits = _.split(homeFeedValue, '-');
                const feedCookie = _.toInteger(homeFeedValueSplits[0]);
                const pageCookie = _.toInteger(homeFeedValueSplits[1]);
                if (_.isNumber(feedCookie)) {
                    this.feed = feedCookie;
                }
                if (_.isNumber(pageCookie)) {
                    this.page = pageCookie;
                }
            }

            this.timeArrays = [];
            homeFeedApi(this.feed, this.page).then(response => {
                this.doFeedResponse(response);
            });
            this.page = this.page + 1;
        },

        fetchFeed(ts) {
            // console.log('fetchFeed:' + this.feed + '->' + this.page);
            if (this.feed < 0) {
                ts.endLoading();
                return;
            }
            if (!_.isEqual(this.$route.path, '/')) {
                return;
            }
            homeFeedApi(this.feed, this.page).then(response => {
                ts.endLoading();

                this.doFeedResponse(response);
            });
            this.page = this.page + 1;
        },

        doFeedResponse(response) {
            const data = response.data;
            const feed = data.feed;
            const newFeed = data.newFeed;
            const timeArrays = data.timeArrays;

            if (newFeed) {
                this.page = 2;
            }

            this.feed = feed;
            this.timeArrays = _.concat(this.timeArrays, timeArrays);
            Cookies.set(this.homeFeedKey, this.feed + '-' + this.page, { expires: 7 });
        }
    }

};
</script>
