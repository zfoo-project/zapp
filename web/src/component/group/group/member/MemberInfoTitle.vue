<template>
    <v-list-item v-if="!_.isNil(groupVo)">
        <v-list-item-icon>
            <v-icon color="primary" @click="backClick()">mdi-arrow-left-bold-circle-outline</v-icon>
        </v-list-item-icon>
        <v-list-item-subtitle>
            {{ groupVo.name }}
        </v-list-item-subtitle>
    </v-list-item>
</template>

<script>
import { mapState } from 'vuex';

export default {
    name: 'MemberInfoTitle',


    props: {
        groupId: {
            type: String,
            default: '',
            required: true
        }
    },

    data() {
        return {

            groupOptions: [
            ]
        };
    },

    computed: {
        ...mapState('group', ['groups']),
        groupVo() {
            return _.find(this.groups, it => it.id === this.groupId);
        }
    },


    methods: {
        backClick() {
            this.$store.state.app.group.showGroupHome();
        },

        groupOptionClick(type, event) {
            switch (type) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            }
        }
    }
};
</script>
