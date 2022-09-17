<template>
    <creation :create-time-mode="appConstant.createTimeMode.edit.type" :callback="editTime" />
</template>

<script>
import { appConstant } from '@/constant/constant.js';
import { editTimeSliceApi } from '@/apiHttp/timeApi.js';
import { simpleSuccess } from '@/util/noticeUtils.js';
export default {
    name: 'EditTime',

    components: {
        Creation: () => import('@/component/base/Creation.vue')
    },

    data() {
        return {
            appConstant
        };
    },

    created() {
        if (_.isNaN(this.$route.params.editId) || _.isNil(this.$route.params.timeSlice)) {
            this.$router.push({ path: '/' });
        }
    },

    methods: {
        editTime(type, start, end, locations, persons, items, notExistItems, content, images, video, key, albums) {
            const editId = this.$route.params.editId;
            editTimeSliceApi(editId, type, start, end, locations, persons, items, notExistItems, content
                , images, video, key, albums).then(response => {
                simpleSuccess(this.$t('notice.recommitSuccess'));
                this.$router.push({ path: '/' });
            });
        }
    }

};
</script>
