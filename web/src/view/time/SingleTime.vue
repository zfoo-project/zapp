<template>
    <div>
        <not-found v-if="_.isEmpty(timeArrays)" :not-found-obj="singleTime404" />
        <div v-else>
            <time-slice ref="ts" :time-slice-array="timeArrays" :divider="false" />
            <time-calendar :time-slice-id="timeSliceId" :dimension="dimension" />
        </div>
    </div>
</template>

<script>
import { timeSliceInfoApi } from '@/apiHttp/timeApi.js';
import { appConstant, notFound } from '@/constant/constant.js';
import { isBlank } from '@/util/stringUtils.js';

export default {
    name: 'SingleTime',
    components: {
        TimeCalendar: () => import('./component/TimeCalendar.vue'),
        TimeSlice: () => import('@/component/time/TimeSlice.vue'),
        NotFound: () => import('@/component/base/NotFound.vue')
    },
    data() {
        return {
            title: '',
            description: '',
            keywords: '',

            timeArrays: [],
            timeSliceId: this.$route.params.id,
            dimension: _.isNil(this.$route.query.dimension) ? appConstant.dimensionDefault : _.toNumber(this.$route.query.dimension),
            singleTime404: notFound.singleTime404
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
            if (isBlank(this.timeSliceId) || _.isNaN(this.dimension)) {
                return;
            }

            this.timeSliceId = this.$route.params.id;
            this.dimension = _.isNil(this.$route.query.dimension) ? appConstant.dimensionDefault : _.toNumber(this.$route.query.dimension);
            timeSliceInfoApi(Array.of(this.timeSliceId)).then(response => {
                const data = response.data;
                if (_.isEmpty(data)) {
                    return;
                }
                this.timeArrays = data;

                // 使用时间片的内容创建网页的标题
                const ts = _.first(this.timeArrays);
                if (_.isNil(ts)) {
                    return;
                }

                // 设置标题
                let tsTitle = ts.content;
                // 如果内容为空，则使用用户的名称当作标题
                if (isBlank(tsTitle)) {
                    this.title = ts.userInfo.name + ' - ' + this.$t('appUrl.time');
                } else {
                    if (tsTitle.length >= appConstant.seoTitleLengthLimit) {
                        tsTitle = tsTitle.substring(0, appConstant.seoTitleLengthLimit);
                    }
                    this.title = tsTitle;
                }

                // 设置描述
                if (isBlank(ts.content)) {
                    this.description = tsTitle;
                } else {
                    this.description = ts.content;
                }

                // 设置keywords
                const keywordArray = [];
                _.forEach(ts.locations, it => keywordArray.push(it.value));
                _.forEach(ts.persons, it => keywordArray.push(it.value));
                _.forEach(ts.items, it => keywordArray.push(it.value));
                this.keywords = _.join(keywordArray, ',');
                if (isBlank(this.keywords)) {
                    this.keywords = tsTitle;
                }
            });
        }
    }
};
</script>
