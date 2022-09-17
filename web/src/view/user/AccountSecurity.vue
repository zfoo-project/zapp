<template>
    <v-container>
        <password-set-dialog ref="passwordSetDialog" />
        <phone-set-dialog ref="phoneSetDialog" />
        <a ref="bindWeiBo" :href="bindWeiBoUrl" />
        <a ref="bindWeChat" :href="bindWeChatUrl" />

        <v-row align="center" justify="center" class="pt-6">
            <v-col>
                <v-icon v-if="passwordSet === true" color="success" class="pr-2">
                    mdi-checkbox-marked-circle-outline
                </v-icon>
                <v-icon v-else color="error" class="pr-2">mdi-shield-alert-outline</v-icon>
                <span>{{ $t('user.passwordSet') }}</span>
            </v-col>
            <v-col>
                <span v-if="passwordSet === true">{{ $t('user.alreadySetPassword') }}</span>
                <span v-else>{{ $t('user.notSetting') }}</span>
            </v-col>
            <v-col>
                <v-btn small color="primary" @click="showPasswordSetDialog()">{{ $t('user.setting') }}</v-btn>
            </v-col>
        </v-row>
        <v-divider />
        <v-row>
            <v-col>
                <v-icon v-if="!_.isEmpty(phoneNumber)" color="success" class="pr-2">
                    mdi-checkbox-marked-circle-outline
                </v-icon>
                <v-icon v-else color="error" class="pr-2">mdi-shield-alert-outline</v-icon>
                <span>{{ $t('user.bindPhone') }}</span>
            </v-col>
            <v-col>
                <span v-if="!_.isEmpty(phoneNumber)">{{ $t('user.alreadySetPhone') }}</span>
                <span v-else>{{ $t('user.notSetting') }}</span>
            </v-col>
            <v-col>
                <v-btn v-if="!_.isEmpty(phoneNumber)" small color="primary" @click="showPhoneSetDialog()">{{ $t('user.change') }}</v-btn>
                <v-btn v-else small color="primary" @click="showPhoneSetDialog()">{{ $t('user.notSetting') }}</v-btn>
            </v-col>
        </v-row>
        <v-divider />
        <v-row>
            <v-col>
                <v-icon v-if="weiBoBind === true" color="success" class="pr-2">
                    mdi-checkbox-marked-circle-outline
                </v-icon>
                <v-icon v-else color="error" class="pr-2">mdi-shield-alert-outline</v-icon>
                <span>{{ $t('user.bindWeiBo') }}</span>
            </v-col>
            <v-col>
                <span v-if="weiBoBind === true">{{ $t('user.alreadySetWeiBo') }}</span>
                <span v-else>{{ $t('user.notBind') }}</span>
            </v-col>
            <v-col>
                <v-btn v-if="weiBoBind === true" small color="primary" @click="unbindWeiBo()">{{ $t('user.unbind') }}</v-btn>
                <v-btn v-else small color="primary" @click="bindWeiBo()">{{ $t('user.bind') }}</v-btn>
            </v-col>
        </v-row>
        <v-divider />
        <v-row>
            <v-col>
                <v-icon v-if="weChatBind === true" color="success" class="pr-2">
                    mdi-checkbox-marked-circle-outline
                </v-icon>
                <v-icon v-else color="error" class="pr-2">mdi-shield-alert-outline</v-icon>
                <span>{{ $t('user.bindWeiChat') }}</span>
            </v-col>
            <v-col>
                <span v-if="weChatBind === true">{{ $t('user.alreadySetWeChat') }}</span>
                <span v-else>{{ $t('user.notBind') }}</span>
            </v-col>
            <v-col>
                <v-btn v-if="weChatBind === true" small color="primary" @click="unbindWeChat()">{{ $t('user.unbind') }}</v-btn>
                <v-btn v-else small color="primary" @click="bindWeChat()">{{ $t('user.bind') }}</v-btn>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import { appConstant } from '@/constant/constant.js';
import { accountSecurityApi } from '@/apiHttp/userApi.js';
import { bindVerifyApi, unbindWeiBoApi, unbindWeChatApi } from '@/apiHttp/loginApi.js';

export default {
    name: 'AccountSecurity',

    components: {
        PasswordSetDialog: () => import('./component/PasswordSetDialog.vue'),
        PhoneSetDialog: () => import('./component/PhoneSetDialog.vue')
    },

    data() {
        return {
            phoneNumber: '',
            weChatBind: false,
            weiBoBind: false,
            passwordSet: false,

            bindWeiBoUrl: '',
            bindWeChatUrl: ''
        };
    },

    created() {
        accountSecurityApi().then(response => {
            const data = response.data;
            this.phoneNumber = data.phoneNumber;
            this.weChatBind = data.weChatBind;
            this.weiBoBind = data.weiBoBind;
            this.passwordSet = data.passwordSet;
        });
    },

    methods: {

        showPasswordSetDialog() {
            this.$refs.passwordSetDialog.showDialog(true);
        },

        showPhoneSetDialog() {
            this.$refs.phoneSetDialog.showDialog(true);
        },

        bindWeiBo() {
            bindVerifyApi().then(response => {
                const data = response.data;
                const verifyKey = data.verifyKey;
                this.bindWeiBoUrl = appConstant.weiBoBindUrl + '&state=' + verifyKey;
                const bindWeiBo = this.$refs.bindWeiBo;
                setTimeout(function() {
                    bindWeiBo.click();
                }, 300);
            });
        },

        bindWeChat() {
            bindVerifyApi().then(response => {
                const data = response.data;
                const verifyKey = data.verifyKey;
                this.bindWeChatUrl = appConstant.weChatBindUrl + '&state=' + verifyKey;
                const bindWeChat = this.$refs.bindWeChat;
                setTimeout(function() {
                    bindWeChat.click();
                }, 300);
            });
        },

        unbindWeiBo() {
            unbindWeiBoApi().then(response => {
                this.weiBoBind = false;
            });
        },

        unbindWeChat() {
            unbindWeChatApi().then(response => {
                this.weChatBind = false;
            });
        }

    }
};
</script>
