<template>
    <v-dialog v-model="dialog" max-width="500">
        <v-card>
            <v-card-title>{{ option.name }}</v-card-title>
            <v-card-text>
                <v-row dense>
                    <template v-for="(item, index) in option.children">
                        <v-col :key="'col' + index" cols="12" xs="6" md="4" class="my-0 py-0">
                            <v-checkbox v-model="selected" :label="item.name" :value="item.id" class="my-0 py-0" />
                        </v-col>
                    </template>
                </v-row>
                <v-row>
                    <v-col>
                        <v-textarea
                            v-model="content"
                            :label="option.id === appConstant.otherOptionId ? $t('ts.operation.dialogInputLabelOne') : $t('ts.operation.dialogInputLabel')"
                            type="text"
                            auto-grow
                            full-width
                            outlined
                        />
                    </v-col>
                </v-row>

                <v-row v-if="!_.isNil(filesProgress)">
                    <v-col cols="12">
                        <v-card-subtitle>
                            <v-progress-circular color="amber" indeterminate />
                            {{ $t('ts.operation.dialogFileProgress') }}
                        </v-card-subtitle>
                    </v-col>
                    <v-col v-for="(fileProgress, index) in filesProgress" :key="index" cols="12">
                        <v-progress-circular
                            :rotate="-90"
                            :size="100"
                            :width="15"
                            :value="fileProgress.value"
                            color="amber"
                        >
                            {{ fileProgress.value }}
                        </v-progress-circular>
                        {{ fileProgress.fileName }}
                    </v-col>
                </v-row>
                <v-row v-if="showFile">
                    <v-col>
                        <v-file-input
                            v-model="files"
                            prepend-icon="mdi-file-multiple-outline"
                            :label="$t('ts.operation.dialogFileInputLabel')"
                            multiple
                            chips
                            counter
                            show-size
                        />
                    </v-col>
                </v-row>
            </v-card-text>
            <v-card-actions>
                <v-spacer />
                <v-btn :loading="loading" color="primary" @click="report()">{{ $t('common.confirm') }}</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>


<script>
import { appConstant } from '@/constant/constant.js';
import { policyApi, uploadFileToOssApi } from '@/apiHttp/ossApi.js';
import { simpleError } from '@/util/noticeUtils.js';
import { isBlank } from '@/util/stringUtils.js';

export default {
    name: 'ReportDialog',
    props: {
        showFile: {
            type: Boolean,
            default: true,
            required: false
        },
        option: {
            type: Object,
            default: () => {},
            required: true
        },
        callback: {
            type: Function,
            default: () => () => {},
            required: false
        }
    },

    data() {
        return {
            appConstant,

            dialog: false,
            loading: false,
            filesProgress: null, // { fileName, value }

            selected: [],
            content: '',
            files: null
        };
    },

    watch: {
        dialog: function() {
            if (this.dialog === true) {
                this.loading = false;
                this.selected = [];
                this.content = '';
                this.files = null;
                this.filesProgress = null;
            }
        }
    },


    methods: {
        showDialog(value) {
            this.dialog = value;
        },

        report() {
            const optionType = this.option.id;
            const selectedItems = this.selected;
            const reportContent = this.content;

            if (optionType === appConstant.otherOptionId) {
                if (isBlank(reportContent)) {
                    simpleError(this.$t('notice.reportContentError'));
                    return;
                }
            } else {
                if (_.isEmpty(selectedItems)) {
                    simpleError(this.$t('notice.reportSelectedItemError'));
                    return;
                }
            }


            this.loading = true;

            if (!_.isEmpty(this.files)) {
                policyApi(appConstant.ossPolicyEnum.report.type).then(response => {
                    const ossPolicy = response.data;
                    this.filesProgress = Array.from(this.files).map(it => {
                        return {
                            fileName: it.name,
                            value: 0
                        };
                    });
                    const fileLinks = [];
                    for (const file of this.files) {
                        const upload = uploadFileToOssApi(file, ossPolicy, (fileName, complete) => {
                            const newFilesProgress = Array.from(this.filesProgress);
                            newFilesProgress.forEach(it => {
                                if (_.isEqual(it.fileName, fileName)) {
                                    it.value = complete;
                                }
                            });
                            this.filesProgress = newFilesProgress;
                        });
                        const key = upload.key;
                        upload.callback.then(response => {
                            fileLinks.push(ossPolicy.host + '/' + key);

                            if (!_.isEqual(this.files.length, fileLinks.length)) {
                                return;
                            }

                            this.callback(optionType, selectedItems, reportContent, Array.from(fileLinks));
                        }).catch(error => {
                            console.log(error);
                            this.init();
                            simpleError(this.$t('notice.fileUploadError'));
                        });
                    }
                });
            } else {
                this.callback(optionType, selectedItems, reportContent, null);
            }
        }
    }
};
</script>
