<template>
    <v-dialog v-model="dialog" width="500">
        <v-card>
            <v-card-title>
                {{ $t('word.externalLinkTitle') }}
            </v-card-title>
            <v-card-text>
                <v-text-field
                    v-model="key"
                    :label="this.$t('word.externalLinkValue')"
                    prepend-icon="mdi-shield-link-variant-outline"
                    required
                    @blur="onBlur()"
                />
                <v-text-field
                    v-model="value"
                    :counter="27"
                    :label="this.$t('word.externalLinkKey')"
                    prepend-icon="mdi-shield-key-outline"
                    required
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
import { isBlank } from '@/util/stringUtils.js';
import { simpleError } from '@/util/noticeUtils.js';
import { fixIosInput } from '@/util/fixBugUtils.js';
import { mapState } from 'vuex';

export default {
    name: 'ExternalLinkDialog',

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
            key: '',
            value: ''
        };
    },

    computed: {
        ...mapState('app', ['ios'])
    },


    methods: {
        showDialog() {
            this.dialog = true;
        },

        confirm() {
            if (isBlank(this.key)) {
                simpleError(this.$t('notice.wordExternalLinkKeyEmptyError'));
                return;
            }
            if (isBlank(this.value)) {
                simpleError(this.$t('notice.wordExternalLinkValueEmptyError'));
                return;
            }
            this.callback({
                key: this.key,
                value: this.value
            });
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
