<template>
    <v-container>
        <v-row>
            <v-col>
                <v-card>
                    <v-card-title>{{ $t('admin.timeReview') }}</v-card-title>
                    <v-card-text>
                        <v-text-field
                            v-model="query"
                            :label="$t('admin.timeInputLabel')"
                            prepend-icon="mdi-cloud-search"
                            type="text"
                            @keyup.exact.enter="search()"
                        />
                    </v-card-text>


                    <v-card-actions>
                        <v-spacer />
                        <v-btn class="text-capitalize" color="primary" @click="search()">{{ $t('common.confirm') }}</v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import { mapState } from 'vuex';
import { appConstant } from '@/constant/constant.js';
import { adminTimeReviewApi } from '@/apiHttp/adminApi.js';
import { simpleError } from '@/util/noticeUtils.js';
import { isBlank } from '@/util/stringUtils.js';
export default {
    name: 'AdminTimeDelete',

    data() {
        return {
            appConstant,

            query: null,
            currentUserProfile: null,
            searchUsers: []
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
            if (!this.loggedIn || this.adminAuth !== appConstant.adminAuthEnum.adminAuth.type) {
                this.$router.push({ path: '/' });
                return;
            }
        },

        search() {
            if (isBlank(this.query)) {
                simpleError(this.$t('notice.searchUserEmptyError'));
                return;
            }
            adminTimeReviewApi(this.query).then(response => {
            });
        }
    }
};
</script>
