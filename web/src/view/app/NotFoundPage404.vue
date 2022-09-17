<template>
    <not-found :not-found-obj="page404" />
</template>

<script>
import { notFound } from '@/constant/constant.js';
export default {
    name: 'NotFoundPage404',
    components: {
        NotFound: () => import('@/component/base/NotFound.vue')
    },
    data() {
        return {
            page404: notFound.page404
        };
    }
};
</script>
