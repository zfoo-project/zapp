<template>
    <div>
        <v-dialog v-model="dialog" width="700">
            <v-card>
                <v-card-title>{{ $t('group.operation.invite.title') }}</v-card-title>
                <v-card-subtitle>{{ $t('group.operation.invite.subTitle') }}</v-card-subtitle>

                <v-list v-if="!_.isEmpty(inviteCodes)">
                    <v-list-item v-for="(inviteCode, index) in inviteCodes" :key="index">
                        <v-list-item-content>
                            <v-list-item-title>{{ inviteCode.code }}</v-list-item-title>
                            <v-list-item-subtitle>
                                {{ $t('group.inviteExpire.vo') }}{{ toExpireText(inviteCode) }}
                            </v-list-item-subtitle>
                            <v-list-item-subtitle>
                                {{ $t('group.inviteCount.vo') }}{{ inviteCode.count }}/{{ getMaxInviteCount(inviteCode) }}
                            </v-list-item-subtitle>
                        </v-list-item-content>
                        <v-list-item-action>
                            <v-btn icon @click="copyInviteCode(inviteCode)">
                                <v-icon>mdi-content-copy</v-icon>
                            </v-btn>
                        </v-list-item-action>
                        <v-list-item-action>
                            <v-btn icon @click="deleteInviteCode(inviteCode)">
                                <v-icon>mdi-close</v-icon>
                            </v-btn>
                        </v-list-item-action>
                    </v-list-item>
                </v-list>
                <v-card-text>
                    <v-select
                        v-model="expireSelect"
                        :items="expireItems"
                        :item-text="geItemText"
                        item-value="type"
                        :label="$t('group.inviteExpire.desc')"
                        return-object
                        @blur="onBlur()"
                    />
                    <v-select
                        v-model="countSelect"
                        :items="countItems"
                        :item-text="geItemText"
                        item-value="type"
                        :label="$t('group.inviteCount.desc')"
                        return-object
                        @blur="onBlur()"
                    />
                </v-card-text>


                <v-card-actions>
                    <v-spacer />
                    <v-btn class="text-capitalize" color="primary" @click="confirm()">{{ $t('group.operation.invite.confirm') }}</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <button ref="groupCodeButton" style="display: none" @click="doCopyInviteCode($event)" />
    </div>
</template>

<script>
import clipboard from '@/util/clipboardUtils.js';
import { mapState } from 'vuex';
import { sendPacket } from '@/util/websocketUtils.js';
import { appConstant, groupConstant } from '@/constant/constant.js';
import { fixIosInput } from '@/util/fixBugUtils.js';
import { parseTime } from '@/util/timeUtils.js';
import CreateInviteGroupCodeRequest from '@/jsProtocol/group/member/CreateInviteGroupCodeRequest.js';
import DeleteInviteGroupCodeRequest from '@/jsProtocol/group/member/DeleteInviteGroupCodeRequest.js';

export default {
    name: 'GroupCode',

    props: {
        group: {
            type: Object,
            default: () => {},
            required: true
        }
    },

    data() {
        return {
            dialog: false,
            groupName: null,
            inviteCodes: [],

            inviteCodeStr: '',

            expireSelect: groupConstant.inviteExpireEnum.expireUnlimited,
            expireItems: [
                groupConstant.inviteExpireEnum.expireUnlimited,
                groupConstant.inviteExpireEnum.expireHalfHour,
                groupConstant.inviteExpireEnum.expireOneHour,
                groupConstant.inviteExpireEnum.expireSixHour,
                groupConstant.inviteExpireEnum.expireTwelveHour,
                groupConstant.inviteExpireEnum.expireOneDay
            ],
            countSelect: groupConstant.inviteCountEnum.countUnlimited,
            countItems: [
                groupConstant.inviteCountEnum.countUnlimited,
                groupConstant.inviteCountEnum.count1,
                groupConstant.inviteCountEnum.count5,
                groupConstant.inviteCountEnum.count10,
                groupConstant.inviteCountEnum.count25,
                groupConstant.inviteCountEnum.count50,
                groupConstant.inviteCountEnum.count100
            ]
        };
    },
    computed: {
        ...mapState('app', ['ios']),
        ...mapState('friend', ['searchFriends'])
    },
    methods: {
        showDialog(value) {
            this.dialog = value;
        },

        geItemText(item) {
            return this.$t(item.text);
        },

        toExpireText(inviteCode) {
            if (inviteCode.expireType === groupConstant.inviteExpireEnum.expireUnlimited.type) {
                return this.$t(groupConstant.inviteExpireEnum.expireUnlimited.text);
            } else {
                return parseTime(inviteCode.expireTime);
            }
        },

        setInviteCodes(inviteCodes) {
            this.inviteCodes = inviteCodes;
        },

        getMaxInviteCount(inviteCode) {
            let maxInviteCount = null;
            switch (inviteCode.countType) {
            case groupConstant.inviteCountEnum.countUnlimited.type:
                maxInviteCount = this.$t('group.inviteCount.countUnlimited');
                break;
            case groupConstant.inviteCountEnum.count1.type:
                maxInviteCount = groupConstant.inviteCountEnum.count1.value;
                break;
            case groupConstant.inviteCountEnum.count5.type:
                maxInviteCount = groupConstant.inviteCountEnum.count5.value;
                break;
            case groupConstant.inviteCountEnum.count10.type:
                maxInviteCount = groupConstant.inviteCountEnum.count10.value;
                break;
            case groupConstant.inviteCountEnum.count25.type:
                maxInviteCount = groupConstant.inviteCountEnum.count25.value;
                break;
            case groupConstant.inviteCountEnum.count50.type:
                maxInviteCount = groupConstant.inviteCountEnum.count50.value;
                break;
            case groupConstant.inviteCountEnum.count100.type:
                maxInviteCount = groupConstant.inviteCountEnum.count100.value;
                break;
            default:
            }
            return maxInviteCount;
        },

        copyInviteCode(inviteCode) {
            const code = appConstant.homeUrl + '/invite/' + inviteCode.code;
            this.inviteCodeStr = code;
            this.dialog = false;
            setTimeout(() => {
                this.$refs.groupCodeButton.click();
            }, 300);
        },

        doCopyInviteCode(event) {
            clipboard(this.inviteCodeStr, event);
        },

        deleteInviteCode(inviteCode) {
            sendPacket(new DeleteInviteGroupCodeRequest(inviteCode.code));
        },

        confirm() {
            sendPacket(new CreateInviteGroupCodeRequest(this.countSelect.type, this.expireSelect.type, this.group.id));
        },

        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        }
    }
};
</script>
