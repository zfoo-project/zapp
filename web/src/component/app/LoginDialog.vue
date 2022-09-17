<template>
    <v-dialog v-model="dialog" width="500">
        <v-tabs v-model="tabs">
            <v-tabs-slider />
            <v-tab v-if="isDevelopment()">
                测试环境登录
            </v-tab>
            <v-tab>
                {{ $t('user.loginByPassword') }}
            </v-tab>
            <v-tab>
                {{ $t('user.loginByMobile') }}
            </v-tab>
        </v-tabs>

        <v-tabs-items v-model="tabs">
            <v-tab-item v-if="isDevelopment()">
                <v-card>
                    <v-card-text>
                        <v-form>
                            <v-text-field
                                v-model="account"
                                prepend-icon="person"
                                type="text"
                                required
                            />
                        </v-form>
                    </v-card-text>
                    <v-card-actions>
                        <v-spacer />
                        <v-btn color="primary" :loading="loading" :disabled="loading" @click="loginTest()">{{
                            $t('user.login') }}
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-tab-item>
            <v-tab-item>
                <v-card>
                    <v-card-text>
                        <v-form>
                            <v-text-field
                                v-model="account"
                                :label="$t('user.mobileOrEmail')"
                                :error-messages="accountErrors"
                                prepend-icon="person"
                                type="text"
                                required
                                @input="$v.account.$touch()"
                                @blur="$v.account.$touch()"
                                @keyup.exact.enter="loginByAccount()"
                            />

                            <v-text-field
                                v-model="password"
                                :label="$t('user.password')"
                                :error-messages="passwordErrors"
                                :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                                :type="showPassword ? 'text' : 'password'"
                                prepend-icon="lock"
                                required
                                @input="$v.password.$touch()"
                                @blur="$v.password.$touch()"
                                @click:append="showPassword = !showPassword"
                                @keyup.exact.enter="loginByAccount()"
                            />

                            <v-row>
                                <v-col class="my-0 py-0">
                                    <v-text-field
                                        v-model="accountVerifyCode"
                                        :label="$t('user.verifyCode')"
                                        :error-messages="accountVerifyCodeErrors"
                                        prepend-icon="mdi-code-equal"
                                        type="text"
                                        required
                                        @input="$v.accountVerifyCode.$touch()"
                                        @blur="$v.accountVerifyCode.$touch()"
                                        @keyup.exact.enter="loginByAccount()"
                                    />
                                </v-col>
                                <v-col class="my-0 py-0">
                                    <v-img v-ripple :src="accountVerifyImage" width="130px" height="48px" @click="refreshCaptcha()" />
                                </v-col>
                            </v-row>
                        </v-form>
                    </v-card-text>
                    <v-card-actions>
                        <span class="overline font-weight-light">
                            {{ $t('user.terms') }}
                            <br>
                            {{ $t('user.termsRegister') }}
                            <router-link to="/terms" @click.native="dialog = false">
                                {{ $t('user.termsName') }}
                            </router-link>
                        </span>
                        <v-spacer />
                        <v-btn color="primary" :loading="loading" :disabled="loading" @click="loginByAccount()">
                            {{ $t('user.login') }}
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-tab-item>
            <v-tab-item>
                <v-card flat>
                    <v-card>
                        <v-card-text>
                            <phone-verify ref="phoneVerify" :callback="loginByPhone" :enter-callback="phoneVerifyCallback" />
                        </v-card-text>
                        <v-card-actions>
                            <span class="overline font-weight-light">
                                {{ $t('user.terms') }}
                                <br>
                                {{ $t('user.termsRegister') }}
                                <router-link to="/terms" @click.native="userProtocol()">
                                    {{ $t('user.termsName') }}
                                </router-link>
                            </span>
                            <v-spacer />
                            <v-btn color="primary" :loading="loading" :disabled="loading" @click="phoneVerifyCallback()">
                                {{ $t('user.loginAndRegister') }}
                            </v-btn>
                        </v-card-actions>
                    </v-card>
                </v-card>
            </v-tab-item>
        </v-tabs-items>

        <v-spacer />

        <v-card class="mt-4">
            <v-card-title>
                {{ $t('user.socialLogin') }}
            </v-card-title>
            <v-card-text>
                {{ $t('user.socialLoginSelector') }}
            </v-card-text>
            <v-card-actions>
                <v-btn v-if="$vuetify.breakpoint.mdAndUp" fab :href="weChatLoginUrl">
                    <v-icon color="#71C926" x-large>mdi-wechat</v-icon>
                </v-btn>
                <v-btn v-if="weChat" fab :href="weChatServiceLoginUrl">
                    <v-icon color="#71C926" x-large>mdi-wechat</v-icon>
                </v-btn>
                <v-btn fab :href="weiBoLoginUrl">
                    <v-icon color="#E62226" x-large>mdi-sina-weibo</v-icon>
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>


<script>
import { mapMutations, mapState } from 'vuex';
import { email, maxLength, minLength, numeric, required } from 'vuelidate/lib/validators';
import { appConstant } from '@/constant/constant.js';
import { captchaVerifyApi, loginByAccountApi, loginByPhoneApi, loginTestApi, logoutApi } from '@/apiHttp/loginApi.js';
import { removeXToken, setXToken } from '@/util/authUtils.js';
import { isDevelopment } from '@/util/environmentUtils.js';
import { simpleError, simpleSuccess } from '@/util/noticeUtils.js';
import { closeWebsocket } from '@/util/websocketUtils.js';

export default {
    name: 'LoginDialog',

    components: {
        PhoneVerify: () => import('@/component/base/PhoneVerify.vue')
    },

    validations: {
        account: {
            required,
            email,
            minLength: minLength(11),
            maxLength: maxLength(11),
            numeric
        },

        password: {
            required,
            minLength: minLength(8),
            maxLength: maxLength(16)
        },

        accountVerifyCode: {
            required,
            minLength: minLength(1),
            maxLength: maxLength(4),
            numeric
        }
    },

    data: () => ({
        isDevelopment,

        tabs: null,
        loading: false,

        showPassword: false,

        dialog: false,

        account: '',
        password: '',

        accountVerifyCode: '',
        accountVerifyKey: '',
        accountVerifyImage: '',

        weChatLoginUrl: appConstant.weChatLoginUrl + '&state=' + new Date().getTime(),
        weChatServiceLoginUrl: appConstant.weChatServiceLoginUrl + '&state=' + new Date().getTime(),
        weiBoLoginUrl: appConstant.weiBoLoginUrl + '&state=' + new Date().getTime()
    }),
    computed: {
        ...mapState('app', ['group', 'weChat']),
        ...mapState('user', ['loggedIn', 'avatar']),

        accountErrors() {
            const errors = [];
            if (!this.$v.account.$dirty) {
                return errors;
            }

            if (!this.$v.account.required) {
                errors.push(this.$t('user.mobileOrEmailEmpty'));
                return errors;
            }

            if (!this.$v.account.email && !(this.$v.account.numeric && this.$v.account.minLength && this.$v.account.maxLength)) {
                errors.push(this.$t('user.mobileOrEmailError'));
                return errors;
            }

            return errors;
        },

        passwordErrors() {
            const errors = [];
            if (!this.$v.password.$dirty) {
                return errors;
            }
            if (!this.$v.password.required) {
                errors.push(this.$t('user.passwordEmpty'));
                return errors;
            }
            if (!this.$v.password.minLength) {
                errors.push(this.$t('user.passwordMinLengthError'));
                return errors;
            }
            if (!this.$v.password.maxLength) {
                errors.push(this.$t('user.passwordMaxLengthError'));
                return errors;
            }
            const regx = new RegExp(/^(?=.*[a-zA-Z])(?=.*\d)[^]{8,16}$/);
            if (!regx.test(this.password)) {
                errors.push(this.$t('user.passwordFormatError'));
                return errors;
            }
            return errors;
        },

        accountVerifyCodeErrors() {
            const errors = [];
            if (!this.$v.accountVerifyCode.$dirty) {
                return errors;
            }
            if (!this.$v.accountVerifyCode.required) {
                errors.push(this.$t('user.verifyCodeEmpty'));
                return errors;
            }
            if (!(this.$v.accountVerifyCode.numeric && this.$v.accountVerifyCode.minLength && this.$v.accountVerifyCode.maxLength)) {
                errors.push(this.$t('user.verifyCodeError'));
                return errors;
            }
            return errors;
        }
    },

    methods: {
        ...mapMutations('user', ['setLoggedIn', 'setWsLoggedIn', 'clearUserState']),
        ...mapMutations('group', ['clearGroupState']),

        init() {
            this.$v.$reset();

            this.showPassword = false;
            this.loading = false;
            this.account = '';
            this.password = '';
            this.accountVerifyCode = '';

            this.refreshCaptcha();

            const phoneVerify = this.$refs.phoneVerify;
            if (!_.isNil(phoneVerify)) {
                phoneVerify.init();
            }
        },

        loginByAccount() {
            this.$v.$touch();
            if (!_.isEmpty(this.accountErrors) || !_.isEmpty(this.passwordErrors) || !_.isEmpty(this.accountVerifyCodeErrors)) {
                return;
            }

            if (_.isEmpty(this.account)) {
                simpleError(this.$t('notice.loginNameEmptyError'));
                return;
            }

            if (_.isEmpty(this.password)) {
                simpleError(this.$t('notice.loginPasswordEmptyError'));
                return;
            }

            this.loading = true;

            // 登录
            loginByAccountApi(this.account, this.password, this.accountVerifyKey, this.accountVerifyCode).then(response => {
                this.afterLogin(response);
                this.loading = false;
            }).catch(() => {
                this.loading = false;
            });
        },

        phoneVerifyCallback() {
            this.$refs.phoneVerify.call();
        },

        loginByPhone(phoneNumber, phoneCode) {
            this.loading = true;

            // 登录
            loginByPhoneApi(phoneNumber, phoneCode).then(response => {
                this.afterLogin(response);
                this.loading = false;
            }).catch(() => {
                this.loading = false;
            });
        },

        afterLogin(loginResponse) {
            const data = loginResponse.data;

            // 设置cookie
            setXToken(data.token);

            this.dialog = false;
            this.init();

            simpleSuccess(this.$t('notice.loginSuccess'));

            // 拉取用户信息
            this.$store.dispatch('user/getUserInfo', this.$vuetify);
        },

        loginTest() {
            if (_.isEmpty(this.account)) {
                simpleError(this.$t('notice.loginNameEmptyError'));
                return;
            }

            this.loading = true;

            // 登录
            loginTestApi(this.account, this.password, this.accountVerifyKey, this.accountVerifyCode).then(response => {
                const data = response.data;

                // 设置cookie
                setXToken(data.token);

                this.dialog = false;
                this.init();

                simpleSuccess(this.$t('notice.loginSuccess'));

                // 拉取用户信息
                this.$store.dispatch('user/getUserInfo', this.$vuetify);
            });

            setTimeout(() => {
                this.loading = false;
            }, 3000);
        },

        logout() {
            logoutApi().then(response => {
                this.setLoggedIn(false);
                this.setWsLoggedIn(false);

                if (!_.isNil(this.group)) {
                    this.group.clearGroupState();
                    this.group.showGroupHome();
                }

                removeXToken();

                this.clearUserState(this.$vuetify);
                this.clearGroupState();

                simpleSuccess(this.$t('notice.logoutSuccess'));

                // 用户退出，则关闭websocket
                closeWebsocket();

                if (!_.isEqual(this.$route.path, '/')) {
                    this.$router.push({ path: '/' });
                }
            });
        },

        refreshCaptcha() {
            captchaVerifyApi().then(response => {
                const data = response.data;
                this.accountVerifyKey = data.verifyKey;
                this.accountVerifyImage = data.verifyImage;
            });
        },

        showDialog(value) {
            if (_.startsWith(this.$route.path, '/register')) {
                simpleError(this.$t('notice.registerNotCompletedError'));
                return;
            }

            this.dialog = value;
            if (this.dialog === true) {
                this.init();
            }
        }
    }

};
</script>
