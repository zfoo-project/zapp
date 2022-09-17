<template>
    <v-combobox
        v-model="items"
        :items="itemTags"
        :search-input.sync="itemHintValue"
        :value="inputValue"
        return-object
        hide-selected
        :label="this.$t(chipConstant.item.text)"
        :prepend-icon="chipConstant.item.icon"
        multiple
        persistent-hint
        small-chips
        clearable
        @click="onClick()"
    />
</template>

<script>
import { wordHintApi } from '@/apiHttp/wordApi.js';
import { chipConstant } from '@/constant/constant.js';
import { isBlank } from '@/util/stringUtils.js';
export default {
    name: 'TimeSliceItemSelector',

    data() {
        return {
            chipConstant,
            items: [],
            itemTags: [],
            itemHintValue: '',
            inputValue: ''
        };
    },

    watch: {
        itemHintValue() {
            if (isBlank(this.itemHintValue)) {
                return;
            }
            wordHintApi(this.itemHintValue).then(response => {
                const data = response.data;
                if (_.isEmpty(data)) {
                    return;
                }
                const tags = new Set();
                data.forEach(it => tags.add({
                    value: it.key,
                    text: it.value
                }));
                this.itemTags = Array.from(tags);
            });
        },

        items() {
            this.itemHintValue = '';
            this.itemTags = [];
        }
    },

    methods: {
        setItems(value) {
            this.items = value;
        },

        setItemTags(value) {
            this.itemTags = value;
        },

        getItems() {
            return this.items;
        },

        onClick() {
            if (isBlank(this.inputValue)) {
                this.itemTags = [];
            }
        }
    }
};
</script>
