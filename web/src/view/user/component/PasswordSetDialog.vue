<template>
    <v-dialog v-model="dialog" width="500">
        <v-stepper v-model="step" vertical>
            <v-stepper-step :complete="step > 1" step="1">
                {{ $t('user.verifyUser') }}
            </v-stepper-step>

            <v-stepper-step :complete="step > 2" step="2">
                {{ $t('user.passwordSet') }}
            </v-stepper-step>

            <v-stepper-step step="3">
                {{ $t('user.passwordSetSuccess') }}
            </v-stepper-step>
        </v-stepper>

        <v-card v-if="step === 1">
            <v-card-text>
                <phone-verify ref="phoneVerify" :callback="checkPhoneCode" />
            </v-card-text>

            <v-card-actions>
                <v-spacer />
                <v-btn color="primary" :loading="loading" :disabled="loading" @click="phoneVerifyCallback()">
                    {{ $t('user.nextStep') }}
                </v-btn>
            </v-card-actions>
        </v-card>

        <v-card v-else-if="step === 2">
            <v-card-text>
                <v-text-field
                    v-model="firstPassword"
                    :label="$t('user.password')"
                    :error-messages="firstPasswordErrors"
                    append-icon="mdi-eye-off"
                    type="password"
                    prepend-icon="lock"
                    required
                    @input="$v.firstPassword.$touch()"
                    @blur="$v.firstPassword.$touch()"
                />
                <v-text-field
                    v-model="secondPassword"
                    :label="$t('user.password')"
                    :error-messages="secondPasswordErrors"
                    append-icon="mdi-eye-off"
                    type="password"
                    prepend-icon="lock"
                    required
                    @input="$v.secondPassword.$touch()"
                    @blur="$v.secondPassword.$touch()"
                />
            </v-card-text>

            <v-card-actions>
                <v-spacer />
                <v-btn color="primary" :loading="loading" :disabled="loading" @click="savePassword()">
                    {{ $t('user.nextStep') }}
                </v-btn>
            </v-card-actions>
        </v-card>

        <v-card v-else>
            <v-card-actions>
                <v-spacer />
                <v-btn color="primary" @click="showDialog(false)">
                    {{ $t('user.complete') }}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>


<script>
import { checkPhoneCodeApi } from '@/apiHttp/loginApi.js';
import { savePasswordApi } from '@/apiHttp/userApi.js';
import { maxLength, minLength, required } from 'vuelidate/lib/validators';
import { simpleSuccess, simpleError } from '@/util/noticeUtils.js';
export default {
    name: 'PasswordSetDialog',

    components: {
        PhoneVerify: () => import('@/component/base/PhoneVerify.vue')
    },

    validations: {
        firstPassword: {
            required,
            minLength: minLength(8),
            maxLength: maxLength(16)
        },

        secondPassword: {
            required,
            minLength: minLength(8),
            maxLength: maxLength(16)
        }
    },

    data: () => ({
        dialog: false,
        loading: false,
        step: 1,

        phoneNumber: '',
        phoneCode: '',

        firstPassword: '',
        secondPassword: ''
    }),

    computed: {
        firstPasswordErrors() {
            const errors = [];
            if (!this.$v.firstPassword.$dirty) {
                return errors;
            }
            if (!this.$v.firstPassword.required) {
                errors.push(this.$t('user.passwordEmpty'));
                return errors;
            }
            if (!this.$v.firstPassword.minLength) {
                errors.push(this.$t('user.passwordMinLengthError'));
                return errors;
            }
            if (!this.$v.firstPassword.maxLength) {
                errors.push(this.$t('user.passwordMaxLengthError'));
                return errors;
            }
            const regx = new RegExp(/^(?=.*[a-zA-Z])(?=.*\d)[^]{8,16}$/);
            if (!regx.test(this.firstPassword)) {
                errors.push(this.$t('user.passwordFormatError'));
                return errors;
            }
            return errors;
        },

        secondPasswordErrors() {
            const errors = [];
            if (!this.$v.secondPassword.$dirty) {
                return errors;
            }
            if (!this.$v.secondPassword.required) {
                errors.push(this.$t('user.passwordEmpty'));
                return errors;
            }
            if (!this.$v.secondPassword.minLength) {
                errors.push(this.$t('user.passwordMinLengthError'));
                return errors;
            }
            if (!this.$v.secondPassword.maxLength) {
                errors.push(this.$t('user.passwordMaxLengthError'));
                return errors;
            }
            const regx = new RegExp(/^(?=.*[a-zA-Z])(?=.*\d)[^]{8,16}$/);
            if (!regx.test(this.secondPassword)) {
                errors.push(this.$t('user.passwordFormatError'));
                return errors;
            }
            return errors;
        }
    },

    methods: {
        init() {
            this.step = 1;
            this.loading = false;
            this.phoneNumber = '';
            this.phoneCode = '';
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

        checkPhoneCode(phoneNumber, phoneCode) {
            this.loading = true;

            // 登录
            checkPhoneCodeApi(phoneNumber, phoneCode).then(response => {
                this.step = this.step + 1;
                this.loading = false;
                this.phoneNumber = phoneNumber;
                this.phoneCode = phoneCode;
            }).catch(() => {
                this.loading = false;
            });
        },

        phoneVerifyCallback() {
            this.$refs.phoneVerify.call();
        },

        savePassword() {
            if (!_.isEqual(this.firstPassword, this.secondPassword)) {
                simpleError(this.$t('notice.passwordResetError'));
                return;
            }
            this.loading = true;
            savePasswordApi(this.phoneNumber, this.phoneCode, this.firstPassword).then(response => {
                this.step = this.step + 1;
                this.loading = false;
                this.$parent.passwordSet = true;
                simpleSuccess(this.$t('notice.passwordResetSuccess'));
            }).catch(() => {
                this.loading = false;
            });
        }
    }

};
</script>
