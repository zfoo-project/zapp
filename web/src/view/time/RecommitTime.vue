<template>
    <creation :create-time-mode="appConstant.createTimeMode.recommit.type" :callback="recommitTime" />
</template>

<script>
import { appConstant } from '@/constant/constant.js';
import { recommitTimeSliceApi } from '@/apiHttp/timeApi.js';
import { simpleSuccess } from '@/util/noticeUtils.js';
export default {
    name: 'RecommitTime',

    components: {
        Creation: () => import('@/component/base/Creation.vue')
    },

    data() {
        return {
            appConstant
        };
    },

    created() {
        if (_.isNaN(this.$route.params.rejectId) || _.isNaN(this.$route.params.reviewId) || _.isNil(this.$route.params.timeSlice)) {
            this.$router.push({ path: '/' });
        }
    },

    methods: {
        recommitTime(type, start, end, locations, persons, items, notExistItems, content, images, video, key, albums) {
            const rejectId = this.$route.params.rejectId;
            const reviewId = this.$route.params.reviewId;
            recommitTimeSliceApi(rejectId, reviewId, type, start, end, locations, persons, items, notExistItems, content
                , images, video, key, albums).then(response => {
                simpleSuccess(this.$t('notice.recommitSuccess'));
                this.$router.push({ path: '/' });
            });
        }
    }

};
</script>
