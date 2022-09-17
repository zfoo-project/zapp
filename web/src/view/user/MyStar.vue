<template>
    <div>
        <v-container>
            <v-row>
                <v-col>
                    <v-card>
                        <v-card-title v-if="!_.isEmpty(timeArrays)">
                            {{ $t('ts.tsStarToShow') }}
                        </v-card-title>
                        <v-card-title v-else>
                            {{ $t('ts.noTsStarToShow') }}
                        </v-card-title>
                    </v-card>
                </v-col>
            </v-row>
        </v-container>
        <time-slice ref="ts" :time-slice-array="timeArrays" :bottom-callback="fetchFeed" />
    </div>
</template>

<script>
import { userStarListApi } from '@/apiHttp/userApi.js';
import { mapState } from 'vuex';

export default {
    name: 'MyStar',

    components: {
        TimeSlice: () => import('@/component/time/TimeSlice.vue')
    },

    data() {
        return {
            timeArrays: [],
            page: 1
        };
    },

    computed: {
        ...mapState('user', ['loggedIn'])
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

            this.page = 1;
            this.timeArrays = [];
            userStarListApi(this.page).then(response => {
                const data = response.data;
                if (_.isEmpty(data)) {
                    this.setNoData();
                    return;
                }
                this.timeArrays = _.concat(this.timeArrays, data);
            });
            this.page = this.page + 1;
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
            userStarListApi(this.page).then(response => {
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
