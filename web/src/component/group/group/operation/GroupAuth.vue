<template>
    <v-dialog v-model="dialog" width="700">
        <v-card>
            <v-tabs :vertical="$vuetify.breakpoint.mdAndUp ? true : false">
                <v-tab v-for="(groupAuthVo, index) in group.groupAuths" :key="'tab' + index">
                    {{ groupAuthVo.name }}
                </v-tab>

                <v-tab-item v-for="(groupAuthVo, index) in group.groupAuths" :key="'tabItem' + index">
                    <v-container>
                        <v-row v-show="groupConstant.groupAuthDefaultId !== groupAuthVo.id">
                            <v-col cols="10">
                                <group-auth-name-input ref="groupAuthNameInput" :group-auth-id="groupAuthVo.id" :group-auth-name="groupAuthVo.name" />
                            </v-col>
                            <v-col cols="2" class="mt-4">
                                <v-tooltip bottom>
                                    <template v-slot:activator="{ on }">
                                        <v-icon v-on="on">mdi-map-marker-question-outline</v-icon>
                                    </template>
                                    <p class="my-0 py-0">{{ $t('group.operation.groupAuth.nameDesc0') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.groupAuth.nameDesc1') }}</p>
                                </v-tooltip>
                            </v-col>
                        </v-row>
                        <v-row>
                            <v-col cols="10">
                                <group-auth-selector ref="groupAuthSelector" :group-auth-id="groupAuthVo.id" :group-auth="groupAuthVo.groupAuth" />
                            </v-col>
                            <v-col cols="2" class="mt-4">
                                <v-tooltip bottom>
                                    <template v-slot:activator="{ on }">
                                        <v-icon v-on="on">mdi-map-marker-question-outline</v-icon>
                                    </template>
                                    <p class="my-0 py-0">{{ $t('group.operation.groupAuth.auth0') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.groupAuth.auth1') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.groupAuth.auth2') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.groupAuth.auth3') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.groupAuth.auth4') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.groupAuth.auth5') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.groupAuth.auth6') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.groupAuth.auth7') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.groupAuth.auth8') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.groupAuth.auth9') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.groupAuth.auth10') }}</p>
                                    <p class="my-0 py-0">{{ $t('group.operation.groupAuth.auth11') }}</p>
                                </v-tooltip>
                            </v-col>
                        </v-row>
                        <group-color-selector
                            v-show="groupConstant.groupAuthDefaultId !== groupAuthVo.id"
                            ref="groupColorSelector"
                            :group-auth-id="groupAuthVo.id"
                            :group-auth-color="groupAuthVo.color"
                        />
                        <v-row>
                            <v-col>
                                <div v-if="groupConstant.groupAuthDefaultId === groupAuthVo.id">
                                    <v-sheet color="orange lighten-2">{{ $t('group.operation.groupAuth.defaultGroupAuth0') }}</v-sheet>
                                    <v-sheet color="orange lighten-2">{{ $t('group.operation.groupAuth.defaultGroupAuth1') }}</v-sheet>
                                    <v-sheet color="orange lighten-2">{{ $t('group.operation.groupAuth.defaultGroupAuth2') }}</v-sheet>
                                </div>
                                <v-btn v-else class="text-capitalize" color="error" @click="deleteGroupAuthClick(groupAuthVo.id)">
                                    <v-icon left>mdi-delete-circle-outline</v-icon>
                                    {{ $t('group.operation.groupAuth.deleteAuth') }}
                                </v-btn>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-tab-item>
            </v-tabs>
            <v-card-actions>
                <v-btn class="text-capitalize" color="primary" @click="newGroupAuthClick()">
                    <v-icon left>mdi-account-box-outline</v-icon>
                    {{ $t('group.operation.groupAuth.newAuth') }}
                </v-btn>
                <v-spacer />
                <v-btn class="text-capitalize" color="primary" @click="saveGroupAuthClick()">
                    <v-icon left>mdi-content-save-edit-outline</v-icon>
                    {{ $t('common.save') }}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import { mapState } from 'vuex';
import { groupConstant } from '@/constant/constant.js';
import { sendPacket } from '@/util/websocketUtils.js';
import { fixIosInput } from '@/util/fixBugUtils.js';
import CreateGroupAuthRequest from '@/jsProtocol/group/auth/CreateGroupAuthRequest.js';
import SaveGroupAuthRequest from '@/jsProtocol/group/auth/SaveGroupAuthRequest.js';
import DeleteGroupAuthRequest from '@/jsProtocol/group/auth/DeleteGroupAuthRequest.js';
import GroupAuthVO from '@/jsProtocol/group/model/GroupAuthVO.js';
export default {
    name: 'GroupAuth',

    components: {
        GroupAuthNameInput: () => import('./base/GroupAuthNameInput.vue'),
        GroupAuthSelector: () => import('./base/GroupAuthSelector.vue'),
        GroupColorSelector: () => import('./base/GroupColorSelector.vue')
    },

    props: {
        group: {
            type: Object,
            default: () => {},
            required: true
        }
    },

    data() {
        return {
            groupConstant,
            dialog: false
        };
    },

    computed: {
        ...mapState('app', ['ios'])
    },

    watch: {
        dialog() {
            if (this.dialog === false) {
                this.$refs.groupAuthNameInput.forEach(it => it.init());
                this.$refs.groupAuthSelector.forEach(it => it.init());
                this.$refs.groupColorSelector.forEach(it => it.init());
            }
        }
    },

    methods: {
        showDialog(value) {
            this.dialog = value;
        },

        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        },

        newGroupAuthClick() {
            const newGroupAuthName = this.$t(groupConstant.newGroupAuthName);
            sendPacket(new CreateGroupAuthRequest(this.group.id, newGroupAuthName));
        },

        saveGroupAuthClick() {
            const groupAuths = [];
            for (let i = 0; i < this.$refs.groupAuthSelector.length; i++) {
                const id = this.$refs.groupAuthNameInput[i].getGroupAuthId();
                const name = this.$refs.groupAuthNameInput[i].getGroupAuthNameValue();
                const groupAuth = this.$refs.groupAuthSelector[i].getGroupAuthValue();
                const color = this.$refs.groupColorSelector[i].getGroupAuthColorValue();
                groupAuths.push(new GroupAuthVO(color, groupAuth, id, name));
            }
            sendPacket(new SaveGroupAuthRequest(groupAuths, this.group.id));
        },

        deleteGroupAuthClick(groupAuthId) {
            sendPacket(new DeleteGroupAuthRequest(groupAuthId, this.group.id));
        }
    }
};
</script>
