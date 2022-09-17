<template>
    <v-dialog v-model="dialog" width="700">
        <v-file-input
            id="groupAvatarInput"
            ref="groupAvatarInput"
            v-model="groupAvatarFile"
            style="display: none"
            accept="image/*"
            @change="groupAvatarFileChanged()"
        />

        <v-file-input
            id="groupBackgroundInput"
            ref="groupBackgroundInput"
            v-model="groupBackgroundFile"
            style="display: none"
            accept="image/*"
            @change="groupBackgroundFileChanged()"
        />

        <v-card>
            <v-alert v-show="alert" prominent type="error" transition="scale-transition">
                <v-row align="center">
                    <v-col>{{ $t('group.setting.deleteAlertFirst') }} {{ group.name }} {{ $t('group.setting.deleteAlertSecond') }}</v-col>
                    <v-col class="shrink">
                        <v-btn @click="deleteGroup()">{{ $t('group.setting.deleteGroupButton') }}</v-btn>
                    </v-col>
                </v-row>
            </v-alert>
            <v-list-item>
                <v-list-item-avatar size="50">
                    <v-img :src="toSimpleAvatarUrl(group.avatar)" @click="groupAvatarClick()" />
                </v-list-item-avatar>
                <v-list-item-content>
                    <v-list-item-title class="headline">{{ group.name }}</v-list-item-title>
                    <v-list-item-subtitle>{{ $t('group.setting.subTitle') }}</v-list-item-subtitle>
                </v-list-item-content>
                <v-list-item-icon>
                    <v-icon color="error" @click="toggleAlert()">mdi-delete-off-outline</v-icon>
                </v-list-item-icon>
            </v-list-item>


            <v-img :src="toGroupBackground(group.background)" aspect-ratio="3" @click="groupBackgroundClick()" />


            <v-card-text>
                <v-text-field
                    v-model="groupNameValue"
                    :label="$t('group.operation.create.inputLabel')"
                    prepend-icon="mdi-server-minus"
                    type="text"
                    @blur="onBlur()"
                    @keyup.exact.enter="confirm()"
                />
            </v-card-text>


            <v-card-actions>
                <v-spacer />
                <v-btn class="text-capitalize" color="primary" @click="confirm()">{{ $t('common.confirm') }}</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import { mapState } from 'vuex';
import { sendPacket } from '@/util/websocketUtils.js';
import { isBlank } from '@/util/stringUtils.js';
import { simpleError } from '@/util/noticeUtils.js';
import { fixIosInput } from '@/util/fixBugUtils.js';
import { appConstant } from '@/constant/constant.js';
import { toSimpleAvatarUrl, toGroupBackground } from '@/util/fileUtils.js';
import { policyApi, uploadFileToOssApi } from '@/apiHttp/ossApi.js';
import SaveGroupAvatarRequest from '@/jsProtocol/group/setting/SaveGroupAvatarRequest.js';
import SaveGroupBackgroundRequest from '@/jsProtocol/group/setting/SaveGroupBackgroundRequest.js';
import SaveGroupSettingRequest from '@/jsProtocol/group/setting/SaveGroupSettingRequest.js';
import DeleteGroupRequest from '@/jsProtocol/group/auth/DeleteGroupRequest.js';

export default {
    name: 'GroupSetting',

    props: {
        group: {
            type: Object,
            default: () => {},
            required: true
        }
    },

    data() {
        return {
            toSimpleAvatarUrl,
            toGroupBackground,
            dialog: false,
            groupNameValue: null,

            alert: false,

            groupAvatarFile: null,
            groupAvatarOssPolicy: null,

            groupBackgroundFile: null,
            groupBackgroundOssPolicy: null
        };
    },
    computed: {
        ...mapState('user', ['id']),
        ...mapState('app', ['ios']),
        ...mapState('friend', ['searchFriends'])
    },
    watch: {
        dialog: function() {
            if (this.dialog) {
                this.alert = false;
                this.groupNameValue = null;
            }
        }
    },
    methods: {
        showDialog(value) {
            this.dialog = value;
        },

        toggleAlert() {
            if (this.group.id === '-' + this.id) {
                simpleError(this.$t('code_enum_1313'));
                return;
            }
            this.alert = !this.alert;
        },


        confirm() {
            if (isBlank(this.groupNameValue)) {
                simpleError(this.$t('notice.groupNameEmptyError'));
                return;
            }
            sendPacket(new SaveGroupSettingRequest(this.group.id, this.groupNameValue));
        },

        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        },

        groupAvatarClick() {
            this.$refs.groupAvatarInput.$refs.input.click();
        },

        groupAvatarFileChanged() {
            if (_.isNil(this.groupAvatarFile)) {
                return;
            }

            policyApi(appConstant.ossPolicyEnum.groupAvatar.type).then(response => {
                const data = response.data;
                this.groupAvatarOssPolicy = data;

                const upload = uploadFileToOssApi(this.groupAvatarFile, this.groupAvatarOssPolicy);
                const key = upload.key;
                upload.callback.then(response => {
                    const url = this.groupAvatarOssPolicy.host + '/' + key;
                    sendPacket(new SaveGroupAvatarRequest(url, this.group.id));
                }).catch(error => {
                    console.log(error);
                    simpleError(this.$t('notice.uploadImageSizeLimitError'));
                });
                this.$refs.groupAvatarInput.clearableCallback();
            });
        },

        groupBackgroundClick() {
            this.$refs.groupBackgroundInput.$refs.input.click();
        },

        groupBackgroundFileChanged() {
            if (_.isNil(this.groupBackgroundFile)) {
                return;
            }

            policyApi(appConstant.ossPolicyEnum.groupBackground.type).then(response => {
                const data = response.data;
                this.groupBackgroundOssPolicy = data;

                const upload = uploadFileToOssApi(this.groupBackgroundFile, this.groupBackgroundOssPolicy);
                const key = upload.key;
                upload.callback.then(response => {
                    const url = this.groupBackgroundOssPolicy.host + '/' + key;
                    sendPacket(new SaveGroupBackgroundRequest(url, this.group.id));
                }).catch(error => {
                    console.log(error);
                    simpleError(this.$t('notice.uploadImageSizeLimitError'));
                });
                this.$refs.groupBackgroundInput.clearableCallback();
            });
        },

        deleteGroup() {
            sendPacket(new DeleteGroupRequest(this.group.id));
            this.dialog = false;
        }
    }
};
</script>
