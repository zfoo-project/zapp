<template>
    <v-list two-line>
        <v-list-item @click="operationClick(operations.create.type)">
            <v-list-item-icon>
                <v-icon color="primary">{{ operations.create.icon }}</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
                <v-list-item-title>{{ $t(operations.create.title) }}</v-list-item-title>
                <v-list-item-subtitle>{{ $t(operations.create.subTitle) }}</v-list-item-subtitle>
            </v-list-item-content>
        </v-list-item>
        <v-list-item @click="operationClick(operations.join.type)">
            <v-list-item-icon>
                <v-icon color="primary">{{ operations.join.icon }}</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
                <v-list-item-title>{{ $t(operations.join.title) }}</v-list-item-title>
                <v-list-item-subtitle>{{ $t(operations.join.subTitle) }}</v-list-item-subtitle>
            </v-list-item-content>
        </v-list-item>

        <group-create ref="groupCreate" />
        <group-join ref="groupJoin" />
    </v-list>
</template>

<script>
import { mapMutations } from 'vuex';
export default {
    name: 'GroupOperation',

    components: {
        GroupCreate: () => import('./group/operation/GroupCreate.vue'),
        GroupJoin: () => import('./group/operation/GroupJoin.vue')
    },

    data() {
        return {
            currentOperation: 0,
            operations: {
                create: {
                    type: 0,
                    icon: 'mdi-eye-plus-outline',
                    title: 'group.operation.create.title',
                    subTitle: 'group.operation.create.subTitle'
                },
                join: {
                    type: 1,
                    icon: 'mdi-magnify-scan',
                    title: 'group.operation.join.title',
                    subTitle: 'group.operation.join.subTitle'
                }
            }
        };
    },
    computed: {
    },

    created() {
        this.setGroupOperation(this);
    },

    destroyed() {
        this.setGroupOperation(null);
    },

    methods: {
        ...mapMutations('app', ['setGroupOperation']),

        showGroupCreateDialog(value) {
            this.$refs.groupCreate.showDialog(value);
        },

        showGroupJoinDialog(value) {
            this.$refs.groupJoin.showDialog(value);
        },

        operationClick(type) {
            this.currentOperation = type;
            switch (type) {
            case this.operations.create.type:
                this.showGroupCreateDialog(true);
                break;
            case this.operations.join.type:
                this.showGroupJoinDialog(true);
                break;
            default:
            }
        }
    }
};
</script>
