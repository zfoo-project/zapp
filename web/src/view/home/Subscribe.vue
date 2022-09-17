<template>
    <div>
        <time-slice ref="ts" :key="$route.fullPath" :time-slice-array="timeArrays" :bottom-callback="fetchFeed" />
        <app-footer />
    </div>
</template>

<script>
import { subscribeFeedApi } from '@/apiHttp/homeApi.js';
export default {
    name: 'Subscribe',

    components: {
        TimeSlice: () => import('@/component/time/TimeSlice.vue'),
        AppFooter: () => import('@/component/base/AppFooter.vue')
    },

    data() {
        return {
            feed: -1,
            page: 1,
            timeArrays: []
        };
    },

    created() {
        subscribeFeedApi(this.page).then(response => {
            const data = response.data;
            if (_.isEmpty(data)) {
                this.setNoData();
                return;
            }
            this.timeArrays = _.concat(this.timeArrays, data);
        });
        this.page = this.page + 1;
    },

    methods: {
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
            if (!_.startsWith(this.$route.path, '/subscribe')) {
                return;
            }

            subscribeFeedApi(this.page).then(response => {
                ts.endLoading();

                const data = response.data;

                if (_.isEmpty(data)) {
                    this.setNoData();
                    return;
                }

                this.timeArrays = _.concat(this.timeArrays, data);
            });
            this.page = this.page + 1;
        }
    }

};
</script>
