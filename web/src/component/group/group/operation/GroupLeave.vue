<template>
    <v-dialog v-model="dialog" width="700">
        <v-card>
            <v-card-title>{{ $t('group.operation.leaveGroup.title') }} {{ groupName }}</v-card-title>
            <v-card-subtitle>{{ $t('group.operation.leaveGroup.subTitle') }}</v-card-subtitle>

            <v-card-actions>
                <v-spacer />
                <v-btn class="text-capitalize" color="primary" @click="confirm()">{{ $t('group.operation.leaveGroup.confirm') }}</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import { sendPacket } from '@/util/websocketUtils.js';
import LeaveGroupRequest from '@/jsProtocol/group/member/LeaveGroupRequest.js';

export default {
    name: 'GroupLeave',

    props: {
        groupId: {
            type: String,
            default: '',
            required: true
        },
        groupName: {
            type: String,
            default: '',
            required: true
        }
    },

    data() {
        return {
            dialog: false
        };
    },
    methods: {
        showDialog(value) {
            this.dialog = value;
        },

        confirm() {
            sendPacket(new LeaveGroupRequest(this.groupId));
        }
    }
};
</script>
