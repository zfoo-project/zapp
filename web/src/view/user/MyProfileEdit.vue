<template>
    <v-container>
        <v-row>
            <v-file-input
                id="backgroundInput"
                ref="backgroundInput"
                v-model="backgroundFile"
                style="display: none"
                accept="image/*"
                @change="backgroundFileChange()"
            />
            <v-col cols="12" md="6">
                <v-img
                    v-ripple
                    :src="toBackground(background) + '?' + _.random(0, 10000)"
                    style="cursor: pointer"
                    @click="backgroundUpload()"
                />
            </v-col>
        </v-row>

        <v-row>
            <v-file-input
                id="avatarInput"
                ref="avatarInput"
                v-model="avatarFile"
                style="display: none"
                accept="image/*"
                @change="avatarFileChange()"
            />
            <v-col>
                <v-avatar v-ripple size="70" @click="avatarUpload()">
                    <v-img :src="toSimpleAvatarUrl(avatar) + '?' + _.random(0, 10000)" />
                </v-avatar>
                {{ $t('user.editPicture') }}
            </v-col>
        </v-row>

        <v-row class="pb-12 mb-12">
            <v-col cols="12" md="5">
                <v-form
                    ref="form"
                    v-model="valid"
                    lazy-validation
                >
                    <v-card>
                        <v-card-title>
                            {{ $t('user.editBaseInfo') }}
                        </v-card-title>
                        <v-card-text>
                            <v-text-field
                                v-model="name"
                                :counter="10"
                                :rules="nameRules"
                                :label="this.$t('user.name')"
                                prepend-inner-icon="mdi-badge-account-horizontal-outline"
                                required
                            />

                            <v-select
                                v-model="gender"
                                :label="this.$t('user.gender')"
                                :prepend-inner-icon="genderIcon"
                                :items="genderItems"
                                :item-text="getText"
                                return-object
                                @change="genderChange()"
                            />

                            <v-textarea
                                v-model="signature"
                                :label="this.$t('user.signature')"
                                :rules="signatureRules"
                                :counter="100"
                                type="text"
                                auto-grow
                                rows="1"
                                prepend-inner-icon="comment"
                            />

                        </v-card-text>

                        <v-card-actions>
                            <v-btn :disabled="!valid" color="primary" @click="save()">
                                {{ $t('common.save') }}
                            </v-btn>
                            <v-spacer />
                        </v-card-actions>
                    </v-card>
                </v-form>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import { policyApi, uploadFileToOssApi } from '@/apiHttp/ossApi.js';
import { saveBackgroundApi, saveAvatarApi, saveBaseInfoApi } from '@/apiHttp/userApi.js';
import { simpleSuccess, simpleError } from '@/util/noticeUtils.js';
import { toSimpleAvatarUrl, toBackground } from '@/util/fileUtils.js';
import { mapMutations, mapState } from 'vuex';
import { appConstant, genderItems } from '@/constant/constant.js';

export default {
    name: 'MyProfileEdit',
    data() {
        return {
            genderItems,
            toSimpleAvatarUrl,
            toBackground,
            name: '',
            signature: '',
            gender: null,
            genderIcon: 'mdi-gender-male-female-variant',

            valid: false,
            nameRules: [
                v => !!v || this.$t('notice.userNameEmptyError'),
                v => (v && appConstant.userNameMinLength <= v.length && v.length <= appConstant.userNameMaxLength) || this.$t('notice.userNameLengthError')
            ],
            signatureRules: [
                v => (v && v.length <= appConstant.userSignatureMaxLength) || this.$t('notice.userSignatureLengthError')
            ],


            backgroundFile: null,
            backgroundOssPolicy: {
                expire: 0
            },

            avatarFile: null,
            avatarOssPolicy: {
                expire: 0
            }

        };
    },
    computed: {
        ...mapState('user', ['loggedIn', 'avatar', 'background'])
    },

    mounted() {
        this.name = this.$store.state.user.name;
        this.signature = this.$store.state.user.signature;
        this.gender = this.genderItems[this.$store.state.user.gender];
    },

    methods: {
        ...mapMutations('user', ['setAvatar', 'setBackground', 'setName', 'setSignature', 'setLocations', 'setGender']),

        genderChange() {
            this.genderIcon = this.gender.icon;
        },

        backgroundUpload() {
            if (this.backgroundOssPolicy.expire < new Date().getTime() / 1000 + 5) {
                policyApi(appConstant.ossPolicyEnum.background.type).then(response => {
                    const data = response.data;
                    this.backgroundOssPolicy = data;
                    this.$refs.backgroundInput.$refs.input.click();
                }).catch(error => {
                    console.log(error);
                    simpleError(this.$t('notice.uploadImageSizeLimitError'));
                });
            } else {
                this.$refs.backgroundInput.$refs.input.click();
            }
        },

        backgroundFileChange() {
            if (_.isNil(this.backgroundFile)) {
                return;
            }
            const file = this.backgroundFile;

            const upload = uploadFileToOssApi(file, this.backgroundOssPolicy);
            const key = upload.key;
            upload.callback.then(response => {
                const backgroundUrl = this.backgroundOssPolicy.host + '/' + key;
                saveBackgroundApi(backgroundUrl).then(response => {
                    this.setBackground(backgroundUrl);
                    simpleSuccess(this.$t('notice.changeBackgroundSuccess'));
                });
            }).catch(error => {
                console.log(error);
                simpleError(this.$t('notice.fileUploadError'));
            });

            this.$refs.backgroundInput.clearableCallback();
        },

        avatarUpload() {
            if (this.avatarOssPolicy.expire < new Date().getTime() / 1000 + 5) {
                policyApi(appConstant.ossPolicyEnum.avatar.type).then(response => {
                    const data = response.data;
                    this.avatarOssPolicy = data;
                    this.$refs.avatarInput.$refs.input.click();
                }).catch(error => {
                    console.log(error);
                    simpleError(this.$t('notice.uploadImageSizeLimitError'));
                });
            } else {
                this.$refs.avatarInput.$refs.input.click();
            }
        },

        avatarFileChange() {
            if (_.isNil(this.avatarFile)) {
                return;
            }
            const file = this.avatarFile;

            const upload = uploadFileToOssApi(file, this.avatarOssPolicy);
            const key = upload.key;
            upload.callback.then(response => {
                const avatarUrl = this.avatarOssPolicy.host + '/' + key;
                saveAvatarApi(avatarUrl).then(response => {
                    this.setAvatar(avatarUrl);
                    simpleSuccess(this.$t('notice.changeAvatarSuccess'));
                });
            }).catch(error => {
                console.log(error);
                simpleError(this.$t('notice.fileUploadError'));
            });

            this.$refs.avatarInput.clearableCallback();
        },

        getText(item) {
            return this.$t(item.text);
        },

        save() {
            if (!this.$refs.form.validate()) {
                simpleError(this.$t('notice.inputFormatError'));
                return;
            }

            const name = this.name;
            const gender = this.gender;
            const signature = this.signature;

            if (_.isEmpty(this.name)) {
                simpleError(this.$t('notice.userNameEmptyError'));
                return;
            }

            if (_.isEmpty(this.signature)) {
                simpleError(this.$t('notice.userSignatureError'));
                return;
            }

            if (_.isNil(this.gender)) {
                simpleError(this.$t('notice.userGenderError'));
                return;
            }

            saveBaseInfoApi(name, gender.value, signature).then(response => {
                this.setName(name);
                this.setSignature(signature);
                this.setGender(gender.value);
                this.$router.push({ path: '/user/profile' });
            });
        }
    }

};
</script>
