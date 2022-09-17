<template>
    <v-dialog v-model="dialog" width="500">
        <v-card>
            <v-card-text>
                <phone-verify ref="phoneVerify" :callback="savePhoneNumber" />
            </v-card-text>

            <v-card-actions>
                <v-spacer />
                <v-btn color="primary" :loading="loading" :disabled="loading" @click="phoneVerifyCallback()">
                    {{ $t('user.nextStep') }}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>


<script>
import { savePhoneNumberApi } from '@/apiHttp/userApi.js';
import { simpleSuccess } from '@/util/noticeUtils.js';
export default {
    name: 'PhoneSetDialog',

    components: {
        PhoneVerify: () => import('@/component/base/PhoneVerify.vue')
    },


    data: () => ({
        dialog: false,
        loading: false
    }),

    methods: {
        init() {
            this.loading = false;
            const phoneVerify = this.$refs.phoneVerify;
            if (!_.isNil(phoneVerify)) {
                phoneVerify.init();
            }
        },

        showDialog(value) {
            this.dialog = value;
            if (this.dialog === true) {
                this.init();
            }
        },

        phoneVerifyCallback() {
            this.$refs.phoneVerify.call();
        },

        savePhoneNumber(phoneNumber, phoneCode) {
            this.loading = true;
            savePhoneNumberApi(phoneNumber, phoneCode).then(response => {
                this.loading = false;
                this.showDialog(false);
                simpleSuccess(this.$t('notice.phoneSetSuccess'));
            }).catch(() => {
                this.loading = false;
            });
        }
    }

};
</script>
