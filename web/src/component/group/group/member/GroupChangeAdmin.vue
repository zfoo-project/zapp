<template>
    <v-dialog v-model="dialog" width="700">
        <v-card>
            <v-card-title>{{ $t('group.operation.changeGroup.title') }} ： {{ adminName }}</v-card-title>
            <v-card-text>
                <v-avatar size="70">
                    <v-img :src="toSimpleAvatarUrl(avatar)" />
                </v-avatar>
                <v-icon x-large>mdi-chevron-triple-right</v-icon>
                <v-avatar size="70">
                    <v-img :src="toSimpleAvatarUrl(adminAvatar)" />
                </v-avatar>
            </v-card-text>
            <v-card-subtitle>
                {{ $t('group.operation.changeGroup.subTitleOne') }}
                {{ groupName }}
                {{ $t('group.operation.changeGroup.subTitleTwo') }}
                {{ adminName }}
                {{ $t('group.operation.changeGroup.subTitleThree') }}
                {{ adminName }}
                {{ $t('group.operation.changeGroup.subTitleFour') }}
            </v-card-subtitle>
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
import { toSimpleAvatarUrl } from '@/util/fileUtils.js';

import ChangeGroupAdminRequest from '@/jsProtocol/group/auth/ChangeGroupAdminRequest.js';

export default {
    name: 'GroupChangeAdmin',

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
        },
        adminId: {
            type: String,
            default: '',
            required: true
        },
        adminName: {
            type: String,
            default: '',
            required: true
        },
        adminAvatar: {
            type: String,
            default: '',
            required: true
        }
    },

    data() {
        return {
            toSimpleAvatarUrl,

            dialog: false
        };
    },

    computed: {
        ...mapState('user', ['avatar'])
    },

    methods: {
        showDialog(value) {
            this.dialog = value;
        },

        confirm() {
            sendPacket(new ChangeGroupAdminRequest(this.adminId, this.groupId));
        }
    }
};
</script>
