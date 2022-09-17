<template>
    <v-dialog v-model="dialog" width="700">
        <v-card>
            <v-card-title>{{ $t('group.operation.create.title') }}</v-card-title>
            <v-card-subtitle>{{ $t('group.operation.create.subTitle') }}</v-card-subtitle>
            <v-card-text>
                <v-text-field
                    v-model="groupName"
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
import CreateGroupRequest from '@/jsProtocol/user/group/CreateGroupRequest.js';

export default {
    name: 'GroupJoin',
    data() {
        return {
            dialog: false,
            groupName: null
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

        confirm() {
            if (isBlank(this.groupName)) {
                simpleError(this.$t('notice.groupNameEmptyError'));
                return;
            }
            sendPacket(new CreateGroupRequest(this.groupName));
        },

        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        }
    }
};
</script>
