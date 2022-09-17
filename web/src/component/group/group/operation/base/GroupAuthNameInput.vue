<template>
    <v-text-field
        v-model="groupAuthNameValue"
        :label="$t('group.auth.groupAuthName')"
        type="text"
        prepend-icon="mdi-badge-account-outline"
        @blur="onBlur()"
    />
</template>

<script>
import { mapState } from 'vuex';
import { fixIosInput } from '@/util/fixBugUtils.js';
export default {
    name: 'GroupAuthNameInput',

    props: {
        groupAuthId: {
            type: String,
            default: '',
            required: true
        },
        groupAuthName: {
            type: String,
            default: '',
            required: true
        }
    },

    data() {
        return {
            groupAuthNameValue: null
        };
    },
    computed: {
        ...mapState('app', ['ios'])
    },

    watch: {
        groupAuthName() {
            this.init();
        }
    },

    created() {
        this.init();
    },


    methods: {
        init() {
            this.groupAuthNameValue = this.groupAuthName;
        },

        getGroupAuthId() {
            return this.groupAuthId;
        },

        getGroupAuthNameValue() {
            return this.groupAuthNameValue;
        },

        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        }
    }
};
</script>
