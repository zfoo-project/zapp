<template>
    <v-select
        v-model="channelAuthValue"
        :items="channelAuthItems"
        :item-text="geItemText"
        item-value="type"
        prepend-icon="mdi-numeric-5-box-outline"
        :label="$t('group.auth.textChannelAuthDesc')"
        return-object
        @blur="onBlur()"
    />
</template>

<script>
import { mapState } from 'vuex';
import { groupConstant } from '@/constant/constant.js';
import { fixIosInput } from '@/util/fixBugUtils.js';
export default {
    name: 'ChannelAuthSelector',

    props: {
        groupAuthId: {
            type: String,
            default: '',
            required: true
        },
        channelAuthVos: {
            type: Array,
            default: () => [],
            required: true
        }
    },

    data() {
        return {
            channelAuthValue: null,
            channelAuthItems: [
                groupConstant.channelAuthEnum.noAuth,
                groupConstant.channelAuthEnum.baseAuth,
                groupConstant.channelAuthEnum.middleAuth,
                groupConstant.channelAuthEnum.highAuth,
                groupConstant.channelAuthEnum.specialAuth
            ]
        };
    },
    computed: {
        ...mapState('app', ['ios'])
    },

    created() {
        this.init();
    },

    methods: {
        init() {
            const channelAuthVo = _.find(this.channelAuthVos, it => it.id === this.groupAuthId);
            if (_.isNil(channelAuthVo)) {
                this.channelAuthValue = groupConstant.channelAuthEnum.noAuth;
            } else {
                this.channelAuthValue = _.find(this.channelAuthItems, it => it.type === channelAuthVo.channelAuth);
            }
        },

        geItemText(item) {
            return this.$t(item.text);
        },

        getGroupAuthId() {
            return this.groupAuthId;
        },

        getChannelAuthValue() {
            return this.channelAuthValue.type;
        },

        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        }
    }
};
</script>
