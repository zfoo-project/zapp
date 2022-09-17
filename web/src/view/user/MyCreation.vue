<template>
    <div>
        <template v-for="(tsReject, rejectIndex) in tsReviewRejects">
            <time-slice ref="ts" :key="'tsReviewReject' + rejectIndex" :time-slice-array="Array.of(tsReject.timeSlice)" :divider="false" :operation="false" />

            <v-container :key="'reviewReject' + rejectIndex">
                <v-row>
                    <v-col offset-md="1">
                        <v-chip color="error" outlined pill>
                            <v-icon left>mdi-shower-head</v-icon>
                            {{ optionTypeName(tsReject.optionType) }}
                        </v-chip>
                        <v-chip
                            v-for="(child, childIndex) in optionTypeOfChildren(tsReject.optionType, tsReject.selectedItems)"
                            :key="childIndex"
                            small
                            outlined
                            pill
                            class="ma-2"
                            color="error"
                        >
                            {{ child }}
                        </v-chip>
                        {{ tsReject.content }}
                    </v-col>
                </v-row>
                <v-row>
                    <v-col offset-md="1" md="2" cols="12">
                        <v-chip color="error" outlined pill>
                            <v-icon left>mdi-campfire</v-icon>
                            {{ $t('ts.review.reviewFail') }}
                        </v-chip>
                    </v-col>
                    <v-col md="2" cols="6">
                        <v-btn color="primary" @click="deleteReviewReject(tsReject)">
                            <v-icon>mdi-delete-empty-outline</v-icon>
                            {{ $t('ts.review.deleteReview') }}
                        </v-btn>
                    </v-col>
                    <v-col md="2" cols="6">
                        <v-btn color="primary" @click="recommitReject(tsReject)">
                            <v-icon>mdi-image-edit-outline</v-icon>
                            {{ $t('ts.review.editAndCommit') }}
                        </v-btn>
                    </v-col>
                </v-row>
                <v-divider />
            </v-container>
        </template>
        <template v-for="(tsReject, rejectIndex) in tsEditRejects">
            <time-slice ref="ts" :key="'tsEditReject' + rejectIndex" :time-slice-array="Array.of(tsReject.timeSlice)" :divider="false" :operation="false" />

            <v-container :key="'editReject' + rejectIndex">
                <v-row>
                    <v-col offset-md="1">
                        <v-chip color="error" outlined pill>
                            <v-icon left>mdi-shower-head</v-icon>
                            {{ optionTypeName(tsReject.optionType) }}
                        </v-chip>
                        <v-chip
                            v-for="(child, childIndex) in optionTypeOfChildren(tsReject.optionType, tsReject.selectedItems)"
                            :key="childIndex"
                            small
                            outlined
                            pill
                            class="ma-2"
                            color="error"
                        >
                            {{ child }}
                        </v-chip>
                        {{ tsReject.content }}
                    </v-col>
                </v-row>
                <v-row>
                    <v-col offset-md="1" md="2" cols="12">
                        <v-chip color="error" outlined pill>
                            <v-icon left>mdi-campfire</v-icon>
                            {{ $t('ts.review.reviewFail') }}
                        </v-chip>
                    </v-col>
                    <v-col md="2" cols="6">
                        <v-btn color="primary" @click="deleteEditReject(tsReject)">
                            <v-icon>mdi-delete-empty-outline</v-icon>
                            {{ $t('ts.review.deleteReview') }}
                        </v-btn>
                    </v-col>
                    <v-col md="2" cols="6">
                        <v-btn color="primary" @click="recommitEdit(tsReject)">
                            <v-icon>mdi-image-edit-outline</v-icon>
                            {{ $t('ts.review.editAndCommit') }}
                        </v-btn>
                    </v-col>
                </v-row>
                <v-divider />
            </v-container>
        </template>
        <template v-for="(tsReview, reviewIndex) in tsReviews">
            <time-slice ref="ts" :key="'tsReview' + reviewIndex" :time-slice-array="Array.of(tsReview.timeSlice)" :divider="false" :operation="false" />
            <v-container :key="'review' + reviewIndex">
                <v-row>
                    <v-col offset-md="1" md="2">
                        <v-chip color="primary" outlined pill>
                            <v-icon left>mdi-fire</v-icon>
                            {{ $t('ts.review.reviewing') }}
                        </v-chip>
                    </v-col>
                    <v-col md="2" cols="6">
                        <v-btn color="primary" @click="deleteReview(tsReview)">
                            <v-icon>mdi-delete-empty-outline</v-icon>
                            {{ $t('ts.review.deleteReview') }}
                        </v-btn>
                    </v-col>
                    <v-col md="2" cols="6">
                        <v-btn color="primary" @click="recommitReview(tsReview)">
                            <v-icon>mdi-image-edit-outline</v-icon>
                            {{ $t('ts.review.editAndCommit') }}
                        </v-btn>
                    </v-col>
                </v-row>
                <v-divider />
            </v-container>
        </template>
        <template v-for="(tsReview, reviewIndex) in tsEdits">
            <time-slice ref="ts" :key="'tsEdit' + reviewIndex" :time-slice-array="Array.of(tsReview.timeSlice)" :divider="false" :operation="false" />
            <v-container :key="'edit' + reviewIndex">
                <v-row>
                    <v-col offset-md="1" md="2">
                        <v-chip color="primary" outlined pill>
                            <v-icon left>mdi-fire</v-icon>
                            {{ $t('ts.review.reviewing') }}
                        </v-chip>
                    </v-col>
                    <v-col md="2" cols="6">
                        <v-btn color="primary" @click="deleteEdit(tsReview)">
                            <v-icon>mdi-delete-empty-outline</v-icon>
                            {{ $t('ts.review.deleteReview') }}
                        </v-btn>
                    </v-col>
                    <v-col md="2" cols="6">
                        <v-btn color="primary" @click="recommitEdit(tsReview)">
                            <v-icon>mdi-image-edit-outline</v-icon>
                            {{ $t('ts.review.editAndCommit') }}
                        </v-btn>
                    </v-col>
                </v-row>
                <v-divider />
            </v-container>
        </template>
        <template v-if="_.isEmpty(tsReviews) && _.isEmpty(tsReviewRejects)">
            <v-container>
                <v-row>
                    <v-col>
                        <v-card>
                            <v-card-title>
                                {{ $t('ts.review.noTsToShow') }}
                            </v-card-title>
                        </v-card>
                    </v-col>
                </v-row>
            </v-container>
        </template>
    </div>
</template>

<script>
import reports from '@/i18n/cn/reports.js';
import { appConstant } from '@/constant/constant.js';
import { getUserCreationApi } from '@/apiHttp/userApi.js';
import { deleteTimeSliceReviewApi, deleteTimeSliceReviewRejectApi, deleteTimeSliceEditApi, deleteTimeSliceEditRejectApi } from '@/apiHttp/timeApi.js';
import { simpleSuccess } from '@/util/noticeUtils.js';
import { mapState } from 'vuex';

export default {
    name: 'MyCreation',

    components: {
        TimeSlice: () => import('@/component/time/TimeSlice.vue')
    },

    data() {
        return {
            tsReviews: [],
            tsEdits: [],
            tsReviewRejects: [],
            tsEditRejects: []
        };
    },

    computed: {
        ...mapState('user', ['loggedIn', 'adminAuth'])
    },

    created() {
        this.init();
    },

    methods: {

        init() {
            if (!this.loggedIn) {
                this.$router.push({ path: '/' });
                return;
            }

            getUserCreationApi().then(response => {
                const data = response.data;
                this.tsReviews = data.tsReviews;
                this.tsEdits = data.tsEdits;
                this.tsReviewRejects = data.tsReviewRejects;
                this.tsEditRejects = data.tsEditRejects;
            });
        },

        optionTypeName(optionType) {
            let option = _.find(reports, it => it.id === optionType);
            if (_.isNil(option)) {
                option = _.find(reports, it => it.id === appConstant.otherOptionId);
            }
            return option.name;
        },

        optionTypeOfChildren(optionType, selectedItems) {
            if (_.isEmpty(selectedItems)) {
                return [];
            }
            const option = _.find(reports, it => it.id === optionType);
            if (_.isNil(option)) {
                return [];
            }
            const children = [];
            for (const selectedId of selectedItems) {
                const child = _.find(option.children, it => it.id === selectedId);
                if (_.isNil(child)) {
                    continue;
                }
                children.push(child.name);
            }
            return children;
        },

        deleteReview(tsReview) {
            deleteTimeSliceReviewApi(Array.of(tsReview.id)).then(response => {
                simpleSuccess(this.$t('notice.timeSliceDeleteSuccess'));
                const index = _.findIndex(this.tsReviews, it => it.id === tsReview.id);
                this.tsReviews.splice(index, 1);
            });
        },

        deleteReviewReject(tsReject) {
            deleteTimeSliceReviewRejectApi(Array.of(tsReject.id)).then(response => {
                simpleSuccess(this.$t('notice.timeSliceDeleteSuccess'));
                const index = _.findIndex(this.tsReviewRejects, it => it.id === tsReject.id);
                this.tsReviewRejects.splice(index, 1);
            });
        },

        deleteEdit(tsReview) {
            deleteTimeSliceEditApi(Array.of(tsReview.id)).then(response => {
                simpleSuccess(this.$t('notice.timeSliceDeleteSuccess'));
                const index = _.findIndex(this.tsEdits, it => it.id === tsReview.id);
                this.tsEdits.splice(index, 1);
            });
        },

        deleteEditReject(tsReject) {
            deleteTimeSliceEditRejectApi(Array.of(tsReject.id)).then(response => {
                simpleSuccess(this.$t('notice.timeSliceDeleteSuccess'));
                const index = _.findIndex(this.tsEditRejects, it => it.id === tsReject.id);
                this.tsEditRejects.splice(index, 1);
            });
        },

        recommitReject(tsReject) {
            this.$router.push({
                name: 'RecommitTime',
                params: {
                    rejectId: tsReject.id,
                    reviewId: 0,
                    timeSlice: tsReject.timeSlice
                }
            });
        },

        recommitReview(tsReview) {
            this.$router.push({
                name: 'RecommitTime',
                params: {
                    rejectId: 0,
                    reviewId: tsReview.id,
                    timeSlice: tsReview.timeSlice
                }
            });
        },

        recommitEdit(tsReview) {
            this.$router.push({
                name: 'EditTime',
                params: {
                    editId: tsReview.id,
                    timeSlice: tsReview.timeSlice
                }
            });
        }

    }

};
</script>
