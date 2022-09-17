<template>
    <v-container fluid>
        <v-row align="center" justify="center" style="height: 10em" />
        <v-row align="center" justify="center">
            <v-col md="4">
                <v-card>
                    <v-card-text>
                        <phone-verify ref="phoneVerify" :callback="confirm" />
                    </v-card-text>

                    <v-card-actions>
                        <span class="overline font-weight-light">
                            {{ $t('user.terms') }}
                            <br>
                            {{ $t('user.termsRegister') }}
                            <router-link to="/terms">
                                {{ $t('user.termsName') }}
                            </router-link>
                        </span>
                        <v-spacer />
                        <v-btn color="primary" :loading="loading" :disabled="loading" @click="phoneVerifyCallback()">
                            {{ $t('user.register') }}
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>


<script>
import { registerWeChatApi, registerWeChatServiceApi, registerWeiBoApi } from '@/apiHttp/loginApi.js';
import { simpleSuccess, simpleError } from '@/util/noticeUtils.js';
import { isBlank } from '@/util/stringUtils.js';
import { setXToken } from '@/util/authUtils.js';
import { mapState } from 'vuex';
export default {
    name: 'Register',

    components: {
        PhoneVerify: () => import('@/component/base/PhoneVerify.vue')
    },


    data: () => ({
        loading: false
    }),

    computed: {
        ...mapState('user', ['loggedIn'])
    },

    created() {
        setTimeout(() => {
            this.init();
        }, 500);
    },

    methods: {
        init() {
            if (this.loggedIn) {
                this.$router.push({ path: '/' });
                return;
            }

            this.loading = false;
            const phoneVerify = this.$refs.phoneVerify;
            if (!_.isNil(phoneVerify)) {
                phoneVerify.init();
            }

            const type = this.$route.query.type;
            const verifyKey = this.$route.query.verifyKey;
            if (isBlank(type) || isBlank(verifyKey)) {
                this.$router.push({ path: '/' });
                return;
            }
        },

        phoneVerifyCallback() {
            this.$refs.phoneVerify.call();
        },

        confirm(phoneNumber, phoneCode) {
            const type = this.$route.query.type;
            const verifyKey = this.$route.query.verifyKey;
            if (isBlank(type) || isBlank(verifyKey)) {
                simpleError(this.$t('notice.loginRegisterParamEmptyError'));
                return;
            }

            this.loading = true;
            if (_.isEqual(type, 'weChat')) {
                registerWeChatApi(verifyKey, phoneNumber, phoneCode).then(response => {
                    this.loading = false;
                    this.afterLogin(response.data.token);
                }).catch(() => {
                    this.loading = false;
                });
            } else if (_.isEqual(type, 'weChatService')) {
                registerWeChatServiceApi(verifyKey, phoneNumber, phoneCode).then(response => {
                    this.loading = false;
                    this.afterLogin(response.data.token);
                }).catch(() => {
                    this.loading = false;
                });
            } else if (_.isEqual(type, 'weiBo')) {
                registerWeiBoApi(verifyKey, phoneNumber, phoneCode).then(response => {
                    this.loading = false;
                    this.afterLogin(response.data.token);
                }).catch(() => {
                    this.loading = false;
                });
            } else {
                simpleError(this.$t('notice.loginRegisterParamError'));
            }
        },

        afterLogin(token) {
            // 设置cookie
            setXToken(token);

            simpleSuccess(this.$t('notice.loginSuccess'));

            this.$router.push({ path: '/' });

            setTimeout(() => {
                // 拉取用户信息
                this.$store.dispatch('user/getUserInfo', this.$vuetify);
            }, 500);
        }
    }

};
</script>
