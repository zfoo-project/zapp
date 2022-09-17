<template>
    <div>
        <report-dialog ref="reviewReportDialog" :show-file="false" :option="currentOption" :callback="reviewReject" />
        <report-dialog ref="editReportDialog" :show-file="false" :option="currentOption" :callback="editReject" />

        <template v-if="_.isEmpty(reviews) && _.isEmpty(edits)">
            <v-container>
                <v-row>
                    <v-col>
                        <v-card>
                            <v-card-title>
                                {{ $t('ts.review.noTsReview') }}
                            </v-card-title>
                        </v-card>
                    </v-col>
                </v-row>
            </v-container>
        </template>

        <template v-else>
            <template v-for="(timeSliceReview, reviewIndex) in reviews">
                <time-slice ref="ts" :key="'tsReview' + reviewIndex" :time-slice-array="Array.of(timeSliceReview.timeSlice)" :divider="false" :operation="false" />

                <v-container :key="'review' + reviewIndex">
                    <v-row>
                        <v-col offset-md="2" md="2">
                            <v-menu right offset-x>
                                <template v-slot:activator="{ on }">
                                    <v-btn color="error" v-on="on">
                                        <v-icon>mdi-hand-pointing-down</v-icon>
                                        {{ $t('ts.review.reject') }}
                                    </v-btn>
                                </template>
                                <v-list>
                                    <v-list-item v-for="(option, optionIndex) in moreOptions" :key="'reviewItem' + optionIndex" @click="reviewOptionClick(option.id, timeSliceReview.id)">
                                        <v-list-item-title>{{ option.name }}</v-list-item-title>
                                    </v-list-item>
                                </v-list>
                            </v-menu>
                        </v-col>
                        <v-col md="2">
                            <v-btn color="info" @click="passReview(timeSliceReview.id)">
                                <v-icon>mdi-hand-okay</v-icon>
                                {{ $t('ts.review.pass') }}
                            </v-btn>
                        </v-col>
                        <v-col md="2">
                            <v-btn color="success" @click="recommendReview(timeSliceReview.id)">
                                <v-icon>mdi-hand-heart</v-icon>
                                {{ $t('ts.review.recommend') }}
                            </v-btn>
                        </v-col>
                    </v-row>
                    <v-divider />
                </v-container>

            </template>
            <template v-for="(editTimeSlice, editIndex) in edits">
                <time-slice ref="ts" :key="'tsEdit' + editIndex" :time-slice-array="Array.of(editTimeSlice.timeSlice)" :divider="false" :operation="false" />

                <v-container :key="'edit' + editIndex">
                    <v-row>
                        <v-col offset-md="2" md="2">
                            <v-menu right offset-x>
                                <template v-slot:activator="{ on }">
                                    <v-btn color="error" v-on="on">
                                        <v-icon>mdi-hand-pointing-down</v-icon>
                                        {{ $t('ts.review.reject') }}
                                    </v-btn>
                                </template>
                                <v-list>
                                    <v-list-item v-for="(option, optionIndex) in moreOptions" :key="'editItem' + optionIndex" @click="editOptionClick(option.id, editTimeSlice.id)">
                                        <v-list-item-title>{{ option.name }}</v-list-item-title>
                                    </v-list-item>
                                </v-list>
                            </v-menu>
                        </v-col>
                        <v-col md="2">
                            <v-btn color="info" @click="passEdit(editTimeSlice.id)">
                                <v-icon>mdi-hand-okay</v-icon>
                                {{ $t('ts.review.pass') }}
                            </v-btn>
                        </v-col>
                        <v-col md="2">
                            <v-btn color="success" @click="recommendEdit(editTimeSlice.id)">
                                <v-icon>mdi-hand-heart</v-icon>
                                {{ $t('ts.review.recommend') }}
                            </v-btn>
                        </v-col>
                    </v-row>
                    <v-divider />
                </v-container>
            </template>
            <v-container>
                <v-row>
                    <v-col>
                        <v-card>
                            <v-card-actions>
                                <v-spacer />
                                <v-btn color="primary" @click="nextPage()">{{ $t('ts.review.nextPage') }}</v-btn>
                            </v-card-actions>
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
import { simpleError } from '@/util/noticeUtils.js';
import { reviewTsApi, passTimeSliceReviewApi, rejectTimeSliceReviewApi, passTimeSliceEditApi, rejectTimeSliceEditApi } from '@/apiHttp/timeApi.js';
import { mapState } from 'vuex';

export default {
    name: 'Review',

    components: {
        ReportDialog: () => import('@/component/app/ReportDialog.vue'),
        TimeSlice: () => import('@/component/time/TimeSlice.vue')
    },

    data() {
        return {
            moreOptions: reports,
            currentOption: {},
            optionTsReviewId: null,


            page: 1,
            reviews: [],
            edits: []
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
            if (!this.loggedIn || this.adminAuth < appConstant.adminAuthEnum.baseAuth.type) {
                this.$router.push({ path: '/' });
                return;
            }

            this.page = 1;

            reviewTsApi(this.page).then(response => {
                const data = response.data;
                this.reviews = data.reviews;
                this.edits = data.edits;
            });
        },

        reviewOptionClick(optionId, tsReviewId) {
            if (!this.loggedIn) {
                simpleError(this.$t('notice.notSignInError'));
                return;
            }

            this.optionTsReviewId = tsReviewId;
            this.currentOption = _.find(this.moreOptions, it => _.isEqual(it.id, optionId));
            this.$refs.reviewReportDialog.showDialog(true);
        },

        editOptionClick(optionId, tsReviewId) {
            if (!this.loggedIn) {
                simpleError(this.$t('notice.notSignInError'));
                return;
            }

            this.optionTsReviewId = tsReviewId;
            this.currentOption = _.find(this.moreOptions, it => _.isEqual(it.id, optionId));
            this.$refs.editReportDialog.showDialog(true);
        },

        reviewReject(optionType, selectedItems, content, fileLinks) {
            rejectTimeSliceReviewApi(this.optionTsReviewId, optionType, selectedItems, content).then(response => {
                this.$refs.reviewReportDialog.showDialog(false);
                const index = _.findIndex(this.reviews, it => it.id === this.optionTsReviewId);
                this.reviews.splice(index, 1);
                this.refreshPage();
            });
        },

        editReject(optionType, selectedItems, content, fileLinks) {
            rejectTimeSliceEditApi(this.optionTsReviewId, optionType, selectedItems, content).then(response => {
                this.$refs.editReportDialog.showDialog(false);
                const index = _.findIndex(this.edits, it => it.id === this.optionTsReviewId);
                this.edits.splice(index, 1);
                this.refreshPage();
            });
        },

        passReview(tsReviewId) {
            this.doPassReview(tsReviewId, false);
        },

        recommendReview(tsReviewId) {
            this.doPassReview(tsReviewId, true);
        },

        doPassReview(tsReviewId, recommend) {
            passTimeSliceReviewApi(tsReviewId, recommend).then(response => {
                const reviewLinks = response.data.reviewLinks;
                if (!_.isEmpty(reviewLinks)) {
                    for (const reviewLink of reviewLinks) {
                        const index = _.findIndex(this.reviews, it => it.id === reviewLink);
                        if (index > 0) {
                            this.reviews.splice(index, 1);
                        }
                    }
                }
                this.refreshPage();
            });
        },

        passEdit(tsEditId) {
            this.doPassEdit(tsEditId, false);
        },

        recommendEdit(tsEditId) {
            this.doPassEdit(tsEditId, true);
        },

        doPassEdit(tsEditId, recommend) {
            passTimeSliceEditApi(tsEditId, recommend).then(response => {
                const index = _.findIndex(this.edits, it => it.id === tsEditId);
                this.edits.splice(index, 1);
                this.refreshPage();
            });
        },

        refreshPage() {
            if (_.isEmpty(this.reviews) && _.isEmpty(this.edits)) {
                reviewTsApi(this.page).then(response => {
                    const data = response.data;
                    this.reviews = data.reviews;
                    this.edits = data.edits;
                });
            }
        },

        nextPage() {
            this.page = this.page + 1;
            reviewTsApi(this.page).then(response => {
                const data = response.data;
                this.reviews = data.reviews;
                this.edits = data.edits;
            });
        }
    }

};
</script>
