<template>
    <v-dialog v-model="dialog" width="700">
        <v-card>
            <v-card-title>{{ $t('user.chat.editMessage') }}</v-card-title>

            <v-card-text>
                <v-text-field
                    v-model="messageValue"
                    prepend-icon="mdi-calendar-edit"
                    type="text"
                    @blur="onBlur()"
                    @keyup.exact.enter="confirm()"
                />
            </v-card-text>

            <v-card-actions>
                <v-spacer />
                <v-btn class="text-capitalize" color="primary" @click="confirm()">{{ $t('common.confirm') }}</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import { mapState } from 'vuex';
import { fixIosInput } from '@/util/fixBugUtils.js';

export default {
    name: 'MessageEdit',

    props: {
        editMessage: {
            type: String,
            default: '',
            required: true
        },
        editCallback: {
            type: Function,
            default: () => () => {},
            required: true
        }
    },


    data() {
        return {
            dialog: false,
            messageValue: null
        };
    },
    computed: {
        ...mapState('app', ['ios'])
    },
    watch: {
        editMessage: function() {
            this.messageValue = this.editMessage;
        }
    },
    methods: {
        showDialog(value) {
            this.dialog = value;
        },

        confirm() {
            this.editCallback(this.messageValue);
            this.showDialog(false);
        },

        onBlur() {
            if (this.ios) {
                fixIosInput();
            }
        }
    }
};
</script>
