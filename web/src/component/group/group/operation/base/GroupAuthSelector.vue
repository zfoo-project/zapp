<template>
    <v-select
        v-model="groupAuthValue"
        :items="groupAuthItems"
        :item-text="geItemText"
        item-value="type"
        prepend-icon="mdi-numeric-5-box-outline"
        :label="$t('group.auth.groupAuthDesc')"
        return-object
        @blur="onBlur()"
    />
</template>

<script>
import { mapState } from 'vuex';
import { groupConstant } from '@/constant/constant.js';
import { fixIosInput } from '@/util/fixBugUtils.js';
export default {
    name: 'GroupAuthSelector',

    props: {
        groupAuthId: {
            type: String,
            default: '',
            required: true
        },
        groupAuth: {
            type: Number,
            default: 0,
            required: true
        }
    },

    data() {
        return {
            groupAuthValue: null,
            groupAuthItems: [
                groupConstant.groupAuthEnum.noAuth,
                groupConstant.groupAuthEnum.baseAuth,
                groupConstant.groupAuthEnum.middleAuth,
                groupConstant.groupAuthEnum.highAuth,
                groupConstant.groupAuthEnum.specialAuth
            ]
        };
    },
    computed: {
        ...mapState('app', ['ios'])
    },

    watch: {
        groupAuth() {
            this.init();
        }
    },

    created() {
        this.init();
    },

    methods: {
        init() {
            this.groupAuthValue = _.find(this.groupAuthItems, it => it.type === this.groupAuth);
        },

        geItemText(item) {
            return this.$t(item.text);
        },

        getGroupAuthValue() {
            return this.groupAuthValue.type;
        },

        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        }
    }
};
</script>
