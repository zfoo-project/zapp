<template>
    <div>
        <template v-for="(friend) in friends">
            <user-item :key="friend.id" :user-cache="friend" :click-callback="openChatDialog" />
            <v-divider :key="'divider' + friend.id" />
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
        ...mapState('friend', ['friends'])
    },
    methods: {
        openChatDialog(userCache) {
            this.$store.state.app.group.showFriendChatPage(userCache.id);
        }
    }
};
</script>
