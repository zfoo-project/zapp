<template>
    <v-container>
        <v-dialog v-model="dialog" width="500">
            <time-avatar :user-profile="currentUserProfile" />
        </v-dialog>

        <v-row>
            <v-col>
                <v-card>
                    <v-card-title>{{ $t('admin.authManager') }}</v-card-title>
                    <v-card-text>
                        <v-text-field
                            v-model="query"
                            :label="$t('admin.inputLabel')"
                            prepend-icon="mdi-cloud-search"
                            type="text"
                            @keyup.exact.enter="search()"
                        />
                    </v-card-text>


                    <v-card-actions>
                        <v-spacer />
                        <v-btn class="text-capitalize" color="primary" @click="search()">{{ $t('admin.searchButton') }}</v-btn>
                    </v-card-actions>

                    <v-container>
                        <v-row>
                            <v-col cols="12">
                                <v-list three-line dense>
                                    <template v-for="(userCache, userIndex) in searchUsers">
                                        <v-list-item :key="userIndex" @click="userClick(userCache)">
                                            <v-list-item-avatar>
                                                <v-img :src="toSimpleAvatarUrl(userCache.avatar)" />
                                            </v-list-item-avatar>

                                            <v-list-item-content>
                                                <v-list-item-title>
                                                    {{ toTagOrName(userCache) }}
                                                    <v-icon dense :color="genderItems[userCache.gender].color">{{ genderItems[userCache.gender].icon }}</v-icon>
                                                </v-list-item-title>
                                                <v-list-item-subtitle>
                                                    {{ userCache.signature }}
                                                </v-list-item-subtitle>
                                                <v-list-item-subtitle v-if="new Date().getTime() <= userCache.customTime">
                                                    <v-icon color="amber">mdi-emoticon-outline</v-icon>
                                                    {{ userCache.custom }}
                                                </v-list-item-subtitle>
                                            </v-list-item-content>

                                            <v-list-item-action>
                                                <v-menu offset-x left>
                                                    <template v-slot:activator="{ on }">
                                                        <v-btn fab small icon color="primary" v-on="on">
                                                            <icon-with-tooltip icon="mdi-dots-horizontal-circle-outline" :tooltip="$t('ts.more')" />
                                                        </v-btn>
                                                    </template>

                                                    <v-list>
                                                        <v-list-item @click="changeUserAdmin(userCache.id, appConstant.adminAuthEnum.noAuth.type)">
                                                            <v-list-item-icon>
                                                                <v-icon>mdi-numeric-0-circle-outline</v-icon>
                                                            </v-list-item-icon>
                                                            <v-list-item-title>{{ $t(appConstant.adminAuthEnum.noAuth.text) }}</v-list-item-title>
                                                        </v-list-item>
                                                        <v-list-item @click="changeUserAdmin(userCache.id, appConstant.adminAuthEnum.baseAuth.type)">
                                                            <v-list-item-icon>
                                                                <v-icon>mdi-numeric-1-circle-outline</v-icon>
                                                            </v-list-item-icon>
                                                            <v-list-item-title>{{ $t(appConstant.adminAuthEnum.baseAuth.text) }}</v-list-item-title>
                                                        </v-list-item>
                                                        <v-list-item @click="changeUserAdmin(userCache.id, appConstant.adminAuthEnum.middleAuth.type)">
                                                            <v-list-item-icon>
                                                                <v-icon>mdi-numeric-2-circle-outline</v-icon>
                                                            </v-list-item-icon>
                                                            <v-list-item-title>{{ $t(appConstant.adminAuthEnum.middleAuth.text) }}</v-list-item-title>
                                                        </v-list-item>
                                                        <v-list-item @click="changeUserAdmin(userCache.id, appConstant.adminAuthEnum.highAuth.type)">
                                                            <v-list-item-icon>
                                                                <v-icon>mdi-numeric-3-circle-outline</v-icon>
                                                            </v-list-item-icon>
                                                            <v-list-item-title>{{ $t(appConstant.adminAuthEnum.highAuth.text) }}</v-list-item-title>
                                                        </v-list-item>
                                                        <v-list-item @click="changeUserAdmin(userCache.id, appConstant.adminAuthEnum.specialAuth.type)">
                                                            <v-list-item-icon>
                                                                <v-icon>mdi-numeric-4-circle-outline</v-icon>
                                                            </v-list-item-icon>
                                                            <v-list-item-title>{{ $t(appConstant.adminAuthEnum.specialAuth.text) }}</v-list-item-title>
                                                        </v-list-item>
                                                        <v-list-item @click="changeUserAdmin(userCache.id, appConstant.adminAuthEnum.adminAuth.type)">
                                                            <v-list-item-icon>
                                                                <v-icon>mdi-numeric-5-circle-outline</v-icon>
                                                            </v-list-item-icon>
                                                            <v-list-item-title>{{ $t(appConstant.adminAuthEnum.adminAuth.text) }}</v-list-item-title>
                                                        </v-list-item>
                                                    </v-list>
                                                </v-menu>
                                            </v-list-item-action>
                                        </v-list-item>
                                        <v-divider :key="'divider' + userIndex" />
                                    </template>
                                </v-list>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import { mapState } from 'vuex';
import { appConstant } from '@/constant/constant.js';
import { adminUserAuthApi } from '@/apiHttp/adminApi.js';
import { searchUserApi } from '@/apiHttp/userApi.js';
import { simpleError } from '@/util/noticeUtils.js';
import { isBlank } from '@/util/stringUtils.js';
import { toSimpleAvatarUrl } from '@/util/fileUtils.js';
import { toTagOrName } from '@/util/stringUtils.js';
import { genderItems } from '@/constant/constant.js';
export default {
    name: 'AdminUserAuth',
    components: {
        IconWithTooltip: () => import('@/component/base/IconWithTooltip.vue'),
        TimeAvatar: () => import('@/component/time/TimeAvatar.vue')
    },

    data() {
        return {
            appConstant,
            genderItems,
            toSimpleAvatarUrl,
            toTagOrName,

            dialog: false,
            query: null,
            currentUserProfile: null,
            searchUsers: []
        };
    },

    computed: {
        ...mapState('user', ['loggedIn', 'adminAuth'])
    },

    created() {
        this.init();
    },

    methods: {

        init() {
            if (!this.loggedIn || this.adminAuth !== appConstant.adminAuthEnum.adminAuth.type) {
                this.$router.push({ path: '/' });
                return;
            }
        },

        search() {
            if (isBlank(this.query)) {
                simpleError(this.$t('notice.searchUserEmptyError'));
                return;
            }
            searchUserApi(this.query).then(response => {
                const data = response.data;
                this.searchUsers = data;
            });
        },

        userClick(userCache) {
            this.currentUserProfile = userCache;
            this.dialog = true;
        },

        changeUserAdmin(targetId, auth) {
            adminUserAuthApi(targetId, auth).then(response => {
            });
        }
    }
};
</script>
