<template>
    <v-form>
        <v-text-field
            v-model="phoneNumber"
            :label="$t('user.phoneNum')"
            :error-messages="phoneNumberErrors"
            prepend-icon="person"
            type="text"
            required
            @input="$v.phoneNumber.$touch()"
            @blur="$v.phoneNumber.$touch()"
            @keyup.exact.enter="enterClick()"
        />

        <v-row>
            <v-col class="my-0 py-0">
                <v-text-field
                    v-model="phoneVerifyCode"
                    :label="$t('user.verifyCode')"
                    :error-messages="phoneVerifyCodeErrors"
                    prepend-icon="mdi-code-equal"
                    type="text"
                    required
                    @input="$v.phoneVerifyCode.$touch()"
                    @blur="$v.phoneVerifyCode.$touch()"
                    @keyup.exact.enter="enterClick()"
                />
            </v-col>
            <v-col class="my-0 py-0">
                <v-img v-ripple :src="phoneVerifyImage" width="130px" height="48px" @click="refreshCaptcha()" />
            </v-col>
        </v-row>

        <v-row>
            <v-col class="my-0 py-0">
                <v-text-field
                    v-model="phoneCode"
                    :label="$t('user.phoneVerifyCode')"
                    :error-messages="phoneCodeErrors"
                    prepend-icon="lock"
                    type="text"
                    required
                    @input="$v.phoneCode.$touch()"
                    @blur="$v.phoneCode.$touch()"
                    @keyup.exact.enter="enterClick()"
                />
            </v-col>
            <v-col class="my-0 py-0">
                <v-btn
                    :loading="phoneVerifyLoading"
                    :disabled="phoneVerifyLoading"
                    outlined
                    color="primary"
                    @click="phoneVerify()"
                >
                    {{ $t('user.phoneVerifyCodeGet') }}
                    <v-icon right>mdi-cellphone-arrow-down</v-icon>
                </v-btn>
            </v-col>
        </v-row>
    </v-form>
</template>

<script>
import { required, minLength, maxLength, numeric } from 'vuelidate/lib/validators';
import { captchaVerifyApi, phoneVerifyApi } from '@/apiHttp/loginApi.js';
import { simpleSuccess, simpleError } from '@/util/noticeUtils.js';
export default {
    name: 'PhoneVerify',

    props: {
        callback: {
            type: Function,
            default: () => () => {},
            required: false
        },
        enterCallback: {
            type: Function,
            default: () => () => {},
            required: false
        }
    },

    validations: {
        phoneNumber: {
            required,
            minLength: minLength(11),
            maxLength: maxLength(11),
            numeric
        },

        phoneVerifyCode: {
            required,
            minLength: minLength(1),
            maxLength: maxLength(4),
            numeric
        },

        phoneCode: {
            required,
            minLength: minLength(6),
            maxLength: maxLength(6),
            numeric
        }
    },

    data: () => ({
        phoneNumber: '',
        phoneVerifyCode: '',
        phoneCode: '',
        phoneVerifyKey: '',
        phoneVerifyImage: '',
        phoneVerifyLoading: false
    }),

    computed: {
        phoneNumberErrors() {
            const errors = [];
            if (!this.$v.phoneNumber.$dirty) {
                return errors;
            }
            if (!this.$v.phoneNumber.required) {
                errors.push(this.$t('user.mobileEmpty'));
                return errors;
            }
            if (!(this.$v.phoneNumber.numeric && this.$v.phoneNumber.minLength && this.$v.phoneNumber.maxLength)) {
                errors.push(this.$t('user.mobileError'));
                return errors;
            }
            return errors;
        },

        phoneVerifyCodeErrors() {
            const errors = [];
            if (!this.$v.phoneVerifyCode.$dirty) {
                return errors;
            }
            if (!this.$v.phoneVerifyCode.required) {
                errors.push(this.$t('user.verifyCodeEmpty'));
                return errors;
            }
            if (!(this.$v.phoneVerifyCode.numeric && this.$v.phoneVerifyCode.minLength && this.$v.phoneVerifyCode.maxLength)) {
                errors.push(this.$t('user.verifyCodeError'));
                return errors;
            }
            return errors;
        },

        phoneCodeErrors() {
            const errors = [];
            if (!this.$v.phoneCode.$dirty) {
                return errors;
            }
            if (!this.$v.phoneCode.required) {
                errors.push(this.$t('user.phoneCodeEmpty'));
                return errors;
            }
            if (!(this.$v.phoneCode.numeric && this.$v.phoneCode.minLength && this.$v.phoneCode.maxLength)) {
                errors.push(this.$t('user.phoneCodeError'));
                return errors;
            }
            return errors;
        }
    },

    created() {
        this.init();
    },

    methods: {
        init() {
            this.$v.$reset();

            this.phoneNumber = '';
            this.phoneCode = '';
            this.phoneVerifyCode = '';
            this.phoneVerifyKey = '';
            this.phoneVerifyImage = '';
            this.phoneVerifyLoading = false;

            this.refreshCaptcha();
        },

        refreshCaptcha() {
            captchaVerifyApi().then(response => {
                const data = response.data;
                this.phoneVerifyKey = data.verifyKey;
                this.phoneVerifyImage = data.verifyImage;
            });
        },

        phoneVerify() {
            this.$v.$touch();
            if (!_.isEmpty(this.phoneNumberErrors) || !_.isEmpty(this.phoneVerifyCodeErrors)) {
                return;
            }
            this.phoneVerifyLoading = true;
            phoneVerifyApi(this.phoneNumber, this.phoneVerifyKey, this.phoneVerifyCode).then(response => {
                simpleSuccess(this.$t('notice.phoneCodeGetSuccess'));

                // 一分钟过后才能重复请求验证码
                setTimeout(() => {
                    this.phoneVerifyLoading = false;
                }, 60000);
            }).catch(() => {
                this.phoneVerifyLoading = false;
            });
        },

        call() {
            this.$v.$touch();

            if (!_.isEmpty(this.phoneNumberErrors) || !_.isEmpty(this.phoneVerifyCodeErrors)) {
                return;
            }

            if (_.isEmpty(this.phoneCode)) {
                simpleError(this.$t('notice.loginPhoneCodeEmptyError'));
                return;
            }

            this.callback(this.phoneNumber, this.phoneCode);
        },

        enterClick() {
            this.enterCallback();
        }
    }

};
</script>
