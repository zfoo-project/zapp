<template>
    <div v-scroll="onScroll">
        <v-dialog v-model="dialog" width="500">
            <time-avatar :user-profile="currentUserProfile" />
        </v-dialog>

        <v-container>
            <v-row>
                <v-col cols="12" :md="mdCols">
                    <v-list three-line dense>
                        <template v-for="(userProfile, index) in userProfileArray">
                            <user-item :key="userProfile.id" :user-cache="userProfile" :click-callback="userClick" :user-actions="userActions" />
                            <v-divider :key="'divider' + index" />
                        </template>
                    </v-list>
                </v-col>
            </v-row>
        </v-container>

        <v-footer v-if="noData" padless>
            <v-spacer />
            {{ $t('ts.bottomDisplay') }}
            <v-spacer />
        </v-footer>
        <v-progress-linear v-else-if="loading" color="amber" indeterminate />
        <v-divider v-else />
    </div>
</template>

<script>
export default {
    name: 'UserList',
    components: {
        TimeAvatar: () => import('@/component/time/TimeAvatar.vue'),
        UserItem: () => import('./UserItem.vue')
    },
    props: {
        userProfileArray: {
            type: Array,
            default: () => []
        },

        bottomCallback: {
            type: Function,
            default: null,
            required: false
        },

        mdCols: {
            type: Number,
            default: 6,
            required: false
        },

        userActions: {
            type: Array,
            default: () => [],
            required: false
        }
    },
    data() {
        return {
            dialog: false,
            loading: false,
            noData: false,
            currentUserProfile: null
        };
    },

    created() {
        if (_.isNil(this.bottomCallback)) {
            return;
        }
        this.bottomCallback(this);
    },

    methods: {
        endLoading() {
            this.loading = false;
        },

        setNoData() {
            this.noData = true;
        },

        onScroll(e) {
            if (_.isNil(this.bottomCallback)) {
                return;
            }
            // 如果v-scroll没有绑定任何对象，默认绑定window
            const doc = e.target;

            const tolerance = this.$vuetify.breakpoint.mdAndUp ? 0 : 10;

            // scrollTop 滚动条滚动时，距离顶部的距离
            const scrollTop = doc.documentElement.scrollTop || doc.body.scrollTop;
            // windowHeight 可视区的高度
            const windowHeight = doc.documentElement.clientHeight || doc.body.clientHeight;
            // scrollHeight 滚动条的总高度
            const scrollHeight = doc.documentElement.scrollHeight || doc.body.scrollHeight;
            // 滚动条到底部的条件
            if (scrollTop + windowHeight + tolerance >= scrollHeight && this.loading === false && this.noData === false) {
                // 加载数据
                this.loading = true;
                this.bottomCallback(this);
                // console.log('onScroll:' + (scrollTop + windowHeight) + '->' + scrollHeight);
            }
        },

        userClick(userProfile) {
            this.currentUserProfile = userProfile;
            this.dialog = true;
        }
    }
};

</script>
