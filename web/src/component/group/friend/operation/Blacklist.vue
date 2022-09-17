<template>
    <div>
        <template v-for="(userCache) in blacklist">
            <user-item :key="userCache.id" :user-cache="userCache" :click-callback="openChatDialog" />
            <v-divider :key="'divider' + userCache.id" />
        </template>
    </div>
</template>

<script>
import { mapState } from 'vuex';
export default {
    components: {
        UserItem: () => import('@/component/base/UserItem.vue')
    },
    computed: {
        ...mapState('friend', ['blacklist'])
    },
    methods: {
        openChatDialog(userCache) {
            this.$store.state.app.group.showFriendChatPage(userCache.id);
        }
    }
};
</script>
