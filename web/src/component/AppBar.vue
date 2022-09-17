<template>
    <div>
        <v-navigation-drawer
            v-model="drawer"
            color="appBarDrawerColor"
            :expand-on-hover="$vuetify.breakpoint.mdAndUp ? true : false"
            app
            clipped
        >
            <template v-if="!$vuetify.breakpoint.mdAndUp">
                <v-list-item>
                    <v-list-item-avatar>
                        <v-img :src="require(`@/asset/logo/zfoo_normal.jpg`)" />
                    </v-list-item-avatar>
                    <v-list-item-title>
                        {{ appConstant.appName }}
                    </v-list-item-title>
                </v-list-item>

                <v-divider />
            </template>

            <v-list dense color="appBarDrawerColor">
                <v-list-item v-for="item in items" :key="item.text" @click="goTo(item.to)">
                    <v-list-item-action>
                        <v-icon>{{ item.icon }}</v-icon>
                    </v-list-item-action>
                    <v-list-item-content>
                        <v-list-item-title>
                            {{ $t(item.text) }}
                        </v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
            </v-list>

            <template v-slot:append>
                <v-btn text small class="pa-0 ma-0">
                    {{ appConstant.appVersion }}
                </v-btn>
            </template>
        </v-navigation-drawer>

        <v-app-bar app clipped-left :dense="$vuetify.breakpoint.mdAndUp ? false : true">
            <v-app-bar-nav-icon @click.stop="drawer = !drawer" />

            <v-avatar v-if="$vuetify.breakpoint.mdAndUp" v-ripple size="40" @click="goHome()">
                <img :src="require(`@/asset/logo/zfoo_normal.jpg`)">
            </v-avatar>

            <!--            <v-toolbar-title v-if="$vuetify.breakpoint.mdAndUp">-->
            <!--                {{ appConstant.appName }}-->
            <!--            </v-toolbar-title>-->

            <v-spacer />

            <!-- 搜索 -->
            <app-search ref="appSearch" />

            <v-spacer />

            <!-- 右上角用户头像点击-->
            <v-menu v-if="loggedIn" offset-x left>
                <template v-slot:activator="{ on }">
                    <v-avatar v-ripple size="40" v-on="on">
                        <v-img :src="toSimpleAvatarUrl(avatar)" />
                    </v-avatar>
                </template>

                <v-list>
                    <v-list-item @click="goTo('/user/profile')">
                        <v-list-item-icon>
                            <v-icon>mdi-face</v-icon>
                        </v-list-item-icon>
                        <v-list-item-title>{{ $t('user.userCenter') }}</v-list-item-title>
                    </v-list-item>

                    <v-list-item @click="goTo('/user/creation')">
                        <v-list-item-icon>
                            <v-icon>mdi-bookshelf</v-icon>
                        </v-list-item-icon>
                        <v-list-item-title>{{ $t('user.createCenter') }}</v-list-item-title>
                    </v-list-item>

                    <v-list-item v-if="loggedIn && adminAuth >= appConstant.adminAuthEnum.baseAuth.type" @click="goTo('/review')">
                        <v-list-item-icon>
                            <v-icon>mdi-hammer</v-icon>
                        </v-list-item-icon>
                        <v-list-item-title>{{ $t('user.tsReview') }}</v-list-item-title>
                    </v-list-item>

                    <v-list-item v-if="loggedIn && adminAuth === appConstant.adminAuthEnum.adminAuth.type" @click="goTo('/admin')">
                        <v-list-item-icon>
                            <v-icon>mdi-ferris-wheel</v-icon>
                        </v-list-item-icon>
                        <v-list-item-title>{{ $t('user.manager') }}</v-list-item-title>
                    </v-list-item>

                    <v-list-item @click="$refs.settingDialog.showDialog(true)">
                        <v-list-item-icon>
                            <v-icon>mdi-cog-outline</v-icon>
                        </v-list-item-icon>
                        <v-list-item-title>{{ $t('user.userSetting') }}</v-list-item-title>
                    </v-list-item>

                    <v-list-item @click="goTo('/user/security')">
                        <v-list-item-icon>
                            <v-icon>mdi-security</v-icon>
                        </v-list-item-icon>
                        <v-list-item-title>{{ $t('appUrl.userSecurity') }}</v-list-item-title>
                    </v-list-item>

                    <v-list-item @click="$refs.loginDialog.logout()">
                        <v-list-item-icon>
                            <v-icon>mdi-logout</v-icon>
                        </v-list-item-icon>
                        <v-list-item-title>{{ $t('user.logout') }}</v-list-item-title>
                    </v-list-item>
                </v-list>
            </v-menu>
            <v-btn v-else icon color="primary" @click.stop="$refs.loginDialog.showDialog(true)">
                <v-icon large>mdi-account-circle</v-icon>
            </v-btn>

            <!-- 登录界面 -->
            <login-dialog ref="loginDialog" />

            <!-- 设置界面 -->
            <setting-dialog ref="settingDialog" />
        </v-app-bar>
    </div>
</template>

<script>
import { appConstant } from '@/constant/constant.js';
import { toSimpleAvatarUrl } from '@/util/fileUtils.js';
import { mapMutations, mapState } from 'vuex';
import { simpleError } from '@/util/noticeUtils.js';

export default {
    name: 'AppBar',
    components: {
        AppSearch: () => import('./app/AppSearch.vue'),
        LoginDialog: () => import('./app/LoginDialog.vue'),
        SettingDialog: () => import('./app/SettingDialog.vue')
    },
    data: () => ({
        appConstant,
        toSimpleAvatarUrl,

        drawer: null,
        items: [
            { icon: 'mdi-home-outline', text: 'appBar.feed', to: '/' },
            { icon: 'mdi-zodiac-cancer', text: 'appBar.subscribe', to: '/subscribe' },
            { icon: 'mdi-timer-outline', text: 'appBar.creation', to: '/creation' }
        ]
    }),
    computed: {
        ...mapState('user', ['loggedIn', 'adminAuth', 'avatar'])
    },
    created() {
        this.init();
    },
    methods: {
        ...mapMutations('app', ['setAppSearch', 'setLoginDialog']),

        init() {
            setTimeout(() => {
                const appSearch = this.$refs.appSearch;
                const loginDialog = this.$refs.loginDialog;
                if (_.isNil(appSearch) || _.isNil(loginDialog)) {
                    this.init();
                    return;
                }
                this.setAppSearch(appSearch);
                this.setLoginDialog(loginDialog);
            }, 500);
        },

        goTo(to) {
            if (_.isEqual(to, '/')) {
                this.goHome();
                return;
            }
            if (!this.loggedIn) {
                simpleError(this.$t('notice.notSignInError'));
                return;
            }
            if (_.isEqual(this.$route.path, to)) {
                return;
            }
            this.$router.push({ path: to });
        },

        goHome() {
            if (_.isEqual(this.$route.path, '/')) {
                this.$store.state.app.home.init();
            } else {
                this.$router.push({ path: '/' });
            }
        }
    }

};
</script>
