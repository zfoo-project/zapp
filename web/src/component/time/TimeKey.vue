<template>
    <v-data-table :headers="headers" :items="rows" calculate-widths>
        <template v-if="editModel" v-slot:top>
            <v-toolbar flat>
                <v-toolbar-title>{{ timeKeyTitle }}</v-toolbar-title>
                <v-spacer />
                <v-dialog v-model="colDialog" max-width="500px">
                    <template v-slot:activator="{ on }">
                        <v-btn color="primary" class="ma-1" v-on="on">{{ $t('ts.key.newCol') }}</v-btn>
                    </template>
                    <v-card>
                        <v-card-title>{{ $t('ts.key.newColEdit') }}</v-card-title>
                        <v-card-text>
                            <v-text-field v-model="colName" :label="$t('ts.key.colName')" />
                        </v-card-text>
                        <v-card-actions>
                            <v-spacer />
                            <v-btn color="primary" @click="colDialogClose()">{{ $t('common.cancel') }}</v-btn>
                            <v-btn color="primary" @click="colDialogSave()">{{ $t('common.confirm') }}</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>

                <v-dialog v-model="dialog" max-width="500px">
                    <template v-slot:activator="{ on }">
                        <v-btn color="primary" class="ma-1" v-on="on">{{ $t('ts.key.newRow') }}</v-btn>
                    </template>
                    <v-card>
                        <v-card-title>
                            <span class="headline">{{ formTitle }}</span>
                        </v-card-title>

                        <v-card-text>
                            <v-container>
                                <v-row>
                                    <template v-for="(header, index) in rawHeaders">
                                        <v-col :key="index" cols="12" sm="6" md="4">
                                            <v-text-field v-model="editedItem[header.value]" :label="header.text" />
                                        </v-col>
                                    </template>
                                </v-row>
                            </v-container>
                        </v-card-text>

                        <v-card-actions>
                            <v-spacer />
                            <v-btn color="primary" @click="close">{{ $t('common.cancel') }}</v-btn>
                            <v-btn color="primary" @click="save">{{ $t('common.confirm') }}</v-btn>
                        </v-card-actions>
                    </v-card>
                </v-dialog>
            </v-toolbar>
        </template>
        <template v-if="editModel" v-slot:item.action="{ item }">
            <v-icon small class="mr-2" @click="editItem(item)">
                edit
            </v-icon>
            <v-icon small class="mr-2" @click="copyItem(item)">
                mdi-content-copy
            </v-icon>
            <v-icon small @click="deleteItem(item)">
                delete
            </v-icon>
        </template>
        <template v-if="editModel" v-slot:no-data>
            {{ noDataNotice }}
        </template>
    </v-data-table>
</template>

<script>
import { appConstant } from '@/constant/constant.js';
import { simpleError } from '@/util/noticeUtils.js';
import { isBlank } from '@/util/stringUtils.js';
export default {
    name: 'TimeKeyEdit',
    props: {
        timeKey: {
            type: Object,
            default: () => {},
            required: false
        },
        editModel: {
            type: Boolean,
            default: true
        }
    },
    data: () => ({
        str: 'abcdefghijklmnopqrstuvwxyz',
        colDialog: false,
        colIndex: 0,
        colName: '',

        dialog: false,
        headers: [],
        rows: [],
        editedIndex: -1,
        editedItem: {},
        defaultItem: {}
    }),

    computed: {
        timeKeyTitle() {
            return this.editModel ? this.$t('ts.key.keyEdit') : this.$t('ts.key.keyName');
        },
        formTitle() {
            return this.editedIndex === -1 ? this.$t('ts.key.newData') : this.$t('ts.key.rowEdit');
        },
        noDataNotice() {
            return this.headers.length <= 1 ? this.$t('ts.key.newColTitle') : this.$t('ts.key.newRowTitle');
        },
        rawHeaders() {
            return _.dropRight(this.headers);
        }
    },

    watch: {
        timeKey: function() {
            this.headers = this.timeKey.headers;
            this.rows = this.timeKey.rows;
        }
    },

    mounted() {
        if (_.isEmpty(this.timeKey)) {
            return;
        }
        this.headers = this.timeKey.headers;
        this.rows = this.timeKey.rows;
    },

    methods: {
        toTimeKey() {
            if (_.isEmpty(this.headers)) {
                return null;
            }
            if (_.isEmpty(this.rows)) {
                return null;
            }

            return {
                headers: _.dropRight(this.headers).map((it) => {
                    return {
                        text: it.text,
                        value: it.value
                    };
                }),
                rows: this.rows
            };
        },

        toNumberSystem26(n) {
            let s = '';
            while (n > 0) {
                let m = n % 26;
                if (_.isEqual(m, 0)) {
                    m = 26;
                }
                s = this.str.charAt(m - 1) + s;
                n = (n - m) / 26;
            }
            return s;
        },

        copyItem(item) {
            this.editedItem = Object.assign({}, item);
            this.save();
        },

        editItem(item) {
            this.editedIndex = this.rows.indexOf(item);
            this.editedItem = Object.assign({}, item);
            this.dialog = true;
        },

        deleteItem(item) {
            const index = this.rows.indexOf(item);
            this.rows.splice(index, 1);
        },

        close() {
            this.dialog = false;
            setTimeout(() => {
                this.editedItem = Object.assign({}, this.defaultItem);
                this.editedIndex = -1;
            }, 500);
        },

        save() {
            if (_.isEmpty(this.editedItem)) {
                return;
            }
            if (this.editedIndex > -1) {
                Object.assign(this.rows[this.editedIndex], this.editedItem);
            } else {
                this.rows.push(this.editedItem);
            }
            this.close();
        },

        colDialogClose() {
            this.colDialog = false;
        },

        colDialogSave() {
            if (isBlank(this.colName)) {
                simpleError(this.$t('notice.timeKeyColEmptyError'));
                return;
            }

            if (_.findIndex(this.headers, (it) => _.isEqual(it.text, this.colName)) >= 0) {
                simpleError(this.$t('notice.timeKeyEmptyError'));
                return;
            }

            if (this.colIndex >= appConstant.timeKeyLimit) {
                simpleError(this.$t('notice.timeKeyLimitError'));
                return;
            }

            this.colName = this.colName.trim();
            if (_.isEmpty(this.headers)) {
                this.headers.push({
                    text: this.$t('ts.key.operation'),
                    value: 'action',
                    sortable: false
                });
            }

            const lastElement = _.last(this.headers);
            const newHeaders = _.dropRight(this.headers);

            this.colIndex = this.colIndex + 1;
            const colValue = this.toNumberSystem26(this.colIndex);
            newHeaders.push({
                text: this.colName,
                value: colValue
            });
            newHeaders.push(lastElement);
            this.headers = newHeaders;

            this.editedItem[colValue] = '';
            this.defaultItem[colValue] = '';
            this.colDialogClose();

            this.colName = '';
        }
    }
};
</script>
