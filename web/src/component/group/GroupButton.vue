<template>
    <v-fab-transition>
        <v-btn
            v-if="wsLoggedIn"
            color="primary"
            fab
            bottom
            right
            fixed
            @click="groupClick()"
        >
            <v-badge v-show="redPointGroupButton" dot color="error">
                <v-icon>mdi-account-multiple</v-icon>
            </v-badge>
            <v-icon v-show="!redPointGroupButton">mdi-account-multiple</v-icon>
        </v-btn>
    </v-fab-transition>
</template>

<script>
// Utilities
import { mapState } from 'vuex';

export default {
    name: 'CoreGroupButton',

    computed: {
        ...mapState('app', ['group']),
        ...mapState('user', ['wsLoggedIn']),

        redPointGroupButton() {
            if (_.isNil(this.group)) {
                return false;
            }
            return this.group.getRedPoint();
        }
    },

    methods: {
        groupClick() {
            if (this.group.getGroupDrawer()) {
                switch (this.group.getShow()) {
                case 0:
                    this.group.setGroupDrawer(false);
                    break;
                case 1:
                    this.group.setShow(0);
                    break;
                default:
                }
            } else {
                this.group.setGroupDrawer(true);
            }
        }
    }
};
</script>
