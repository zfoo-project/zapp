<template>
    <v-main class="backgroundColor">
        <app-search-selector ref="appSearchSelector" />
        <keep-alive>
            <router-view v-if="$route.meta.keepAlive" />
        </keep-alive>
        <router-view v-if="!$route.meta.keepAlive" />
    </v-main>
</template>

<script>
import { mapMutations } from 'vuex';
export default {
    name: 'CoreView',
    components: {
        AppSearchSelector: () => import('./app/AppSearchSelector.vue')
    },
    created() {
        this.init();
    },
    methods: {
        ...mapMutations('app', ['setAppSearchSelector']),

        init() {
            setTimeout(() => {
                const appSearchSelector = this.$refs.appSearchSelector;
                if (_.isNil(appSearchSelector)) {
                    this.init();
                    return;
                }
                this.setAppSearchSelector(appSearchSelector);
            }, 500);
        }
    }
};
</script>
