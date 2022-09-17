<template>
    <div>
        <not-found v-if="_.isEmpty(timeArrays)" :not-found-obj="search404" />
        <time-slice v-else ref="ts" :key="$route.fullPath" :time-slice-array="timeArrays" :bottom-callback="fetchFeed" />
    </div>
</template>

<script>
import { searchApi } from '@/apiHttp/homeApi.js';
import { appConstant, notFound } from '@/constant/constant.js';
import { isBlank } from '@/util/stringUtils';
export default {
    name: 'Search',

    components: {
        TimeSlice: () => import('@/component/time/TimeSlice.vue'),
        NotFound: () => import('@/component/base/NotFound.vue')
    },

    data() {
        return {
            search404: notFound.search404,
            timeArrays: [],
            page: 1,
            lastQuery: null
        };
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

            const query = this.$route.query.query;
            if (_.isEqual(query, this.lastQuery)) {
                return;
            }

            this.page = 1;
            searchApi(query, this.page).then(response => {
                const data = response.data;
                if (_.isEmpty(data)) {
                    return;
                }
                this.timeArrays = _.concat(this.timeArrays, data);
                this.lastQuery = query;
                if (_.size(data) < appConstant.feed.pageSize) {
                    this.setNoData();
                }
            });
            this.page = this.page + 1;
        },

        checkState() {
            const query = this.$route.query.query;
            if (isBlank(query)) {
                return false;
            }

            if (!_.startsWith(this.$route.path, '/search')) {
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

            searchApi(this.$route.query.query, this.page).then(response => {
                ts.endLoading();
                const data = response.data;
                this.timeArrays = _.concat(this.timeArrays, data);

                if (_.size(data) < appConstant.feed.pageSize) {
                    ts.setNoData();
                }
            });
            this.page = this.page + 1;
        }
    }
};
</script>
