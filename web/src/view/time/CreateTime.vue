<template>
    <creation :create-callback="createTime" />
</template>

<script>
import { createTimeSliceApi } from '@/apiHttp/timeApi.js';
import { simpleSuccess } from '@/util/noticeUtils.js';
export default {
    name: 'CreateTime',

    components: {
        Creation: () => import('@/component/base/Creation.vue')
    },

    methods: {

        createTime(createList) {
            createTimeSliceApi(createList).then(response => {
                simpleSuccess(this.$t('notice.publicSuccess'));
                this.$router.push({ path: '/' });
            });
        }

    }

};
</script>
