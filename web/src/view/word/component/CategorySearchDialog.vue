<template>
    <v-dialog v-model="dialog" width="500">
        <v-card>
            <v-card-title>
                {{ $t('word.categorySearchTitle') }}
            </v-card-title>
            <v-card-text>
                <v-autocomplete
                    v-model="word"
                    :items="wordTags"
                    :loading="loading"
                    :search-input.sync="wordHintValue"
                    hide-no-data
                    hide-selected
                    :label="this.$t('word.categoryTitle')"
                    prepend-icon="mdi-database-search"
                    return-object
                    @blur="onBlur()"
                />
            </v-card-text>
            <v-card-actions>
                <v-spacer />
                <v-btn color="primary" @click="confirm()">
                    {{ $t('common.confirm') }}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import { categoryHintApi } from '@/apiHttp/wordApi.js';
import { chipConstant } from '@/constant/constant.js';
import { isBlank } from '@/util/stringUtils.js';
import { simpleError } from '@/util/noticeUtils.js';
import { fixIosInput } from '@/util/fixBugUtils.js';
import { mapState } from 'vuex';

export default {
    name: 'CategorySearchDialog',

    props: {
        callback: {
            type: Function,
            default: () => () => {},
            required: false
        }
    },

    data() {
        return {
            dialog: false,

            chipConstant,

            loading: false,
            word: null,
            wordTags: [],
            wordHintValue: ''
        };
    },

    computed: {
        ...mapState('app', ['ios'])
    },

    watch: {
        wordHintValue() {
            if (isBlank(this.wordHintValue)) {
                return;
            }
            this.loading = true;
            categoryHintApi(this.wordHintValue).then(response => {
                this.loading = false;
                const data = response.data;
                if (_.isEmpty(data)) {
                    this.wordTags = [];
                    return;
                }
                const tags = new Set();
                data.forEach(it => tags.add({
                    value: it.key,
                    text: it.value
                }));
                this.wordTags = Array.from(tags);
            });
        }
    },

    methods: {
        showDialog() {
            this.dialog = true;
        },

        confirm() {
            if (_.isEmpty(this.wordTags)) {
                simpleError(this.$t('notice.wordSearchEmptyError'));
                return;
            }
            this.callback(this.word);
            this.dialog = false;
        },

        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        }
    }
};
</script>
