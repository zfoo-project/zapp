<template>
    <v-dialog v-model="dialog" width="700">
        <v-card>
            <v-card-title>{{ $t('group.operation.join.title') }}</v-card-title>
            <v-card-subtitle>{{ $t('group.operation.join.subTitle') }}</v-card-subtitle>
            <v-card-text>
                <v-text-field
                    v-model="groupInviteCode"
                    :hint="$t('group.operation.join.inputLabel')"
                    :placeholder="$t('group.operation.join.demo')"
                    prepend-icon="mdi-server-minus"
                    type="text"
                    @blur="onBlur()"
                    @keyup.exact.enter="confirm()"
                />
            </v-card-text>


            <v-card-actions>
                <v-spacer />
                <v-btn class="text-capitalize" color="primary" @click="confirm()">{{ $t('group.operation.join.confirm') }}</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import { mapState } from 'vuex';
import { sendPacket } from '@/util/websocketUtils.js';
import { groupConstant } from '@/constant/constant.js';
import { isBlank } from '@/util/stringUtils.js';
import { simpleError } from '@/util/noticeUtils.js';
import { fixIosInput } from '@/util/fixBugUtils.js';
import JoinGroupRequest from '@/jsProtocol/group/member/JoinGroupRequest.js';

export default {
    name: 'GroupCreate',
    data() {
        return {
            dialog: false,
            groupInviteCode: null
        };
    },
    computed: {
        ...mapState('app', ['ios'])
    },
    methods: {
        showDialog(value) {
            this.dialog = value;
        },

        confirm() {
            if (isBlank(this.groupInviteCode)) {
                simpleError(this.$t('notice.groupNameEmptyError'));
                return;
            }
            sendPacket(new JoinGroupRequest(_.replace(this.groupInviteCode, groupConstant.inviteUrl, '')));
        },

        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        }
    }
};
</script>
