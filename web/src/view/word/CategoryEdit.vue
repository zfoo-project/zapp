<template>
    <v-container>
        <v-file-input
            id="backgroundInput"
            ref="backgroundInput"
            v-model="backgroundFile"
            style="display: none"
            accept="image/*"
            @change="backgroundFileChange()"
        />

        <v-dialog v-model="dialog" width="500">
            <v-card>
                <v-list>
                    <v-list-item v-for="(item, index) in dialogItems" :key="'dialog' + index" link>
                        <v-list-item-icon>
                            <v-icon :color="dialogChipType.color">{{ dialogChipType.icon }}</v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                            <v-list-item-title>{{ item.value }}</v-list-item-title>
                        </v-list-item-content>
                        <v-list-item-action>
                            <v-btn icon @click="deleteItem(index)">
                                <v-icon>mdi-close</v-icon>
                            </v-btn>
                        </v-list-item-action>
                    </v-list-item>
                </v-list>
            </v-card>
        </v-dialog>

        <category-search-dialog ref="parentSearchDialog" :callback="addParentCallback" />
        <category-search-dialog ref="childrenSearchDialog" :callback="addChildrenCallback" />

        <word-search-dialog ref="wordSearchDialog" :callback="addWordCallback" />

        <v-row>
            <v-col cols="12" md="6">
                <v-text-field
                    v-model="name"
                    :counter="27"
                    :label="this.$t('word.categoryTitle')"
                    prepend-icon="mdi-lightbulb-group"
                    required
                />
            </v-col>
        </v-row>

        <v-row>
            <v-col cols="12" md="6">
                <v-img v-ripple :src="toHighImgUrl(background)" style="cursor: pointer" @click="backgroundUpload()" />
            </v-col>
        </v-row>

        <v-row>
            <v-col>
                <v-list-item class="mx-0 px-0">
                    <v-list-item-content>
                        <div class="text-h6 font-weight-bold">
                            {{ $t('word.parent') }}
                        </div>
                    </v-list-item-content>
                    <v-list-item-action>
                        <v-btn icon @click="minusParentClick(chipConstant.category)">
                            <v-icon>mdi-minus-circle-outline</v-icon>
                        </v-btn>
                    </v-list-item-action>
                    <v-list-item-action>
                        <v-btn icon @click="plusParentClick()">
                            <v-icon>mdi-plus-circle-outline</v-icon>
                        </v-btn>
                    </v-list-item-action>
                </v-list-item>
                <template v-for="(category) in parent">
                    <time-chip :id="category.key" :key="'parent' + category.key" :name="category.value" :chip-type="chipConstant.category" />
                </template>
            </v-col>
        </v-row>

        <v-row>
            <v-col>
                <v-list-item class="mx-0 px-0">
                    <v-list-item-content>
                        <div class="text-h6 font-weight-bold">
                            {{ $t('word.children') }}
                        </div>
                    </v-list-item-content>
                    <v-list-item-action>
                        <v-btn icon @click="minusChildrenClick(chipConstant.childCategory)">
                            <v-icon>mdi-minus-circle-outline</v-icon>
                        </v-btn>
                    </v-list-item-action>
                    <v-list-item-action>
                        <v-btn icon @click="plusChildrenClick()">
                            <v-icon>mdi-plus-circle-outline</v-icon>
                        </v-btn>
                    </v-list-item-action>
                </v-list-item>
                <template v-for="(category) in children">
                    <time-chip :id="category.key" :key="'children' + category.key" :name="category.value" :chip-type="chipConstant.category" />
                </template>
            </v-col>
        </v-row>

        <v-row>
            <v-col>
                <v-list-item class="mx-0 px-0">
                    <v-list-item-content>
                        <div class="text-h6 font-weight-bold">
                            {{ $t('word.childWord') }}
                        </div>
                    </v-list-item-content>
                    <v-list-item-action>
                        <v-btn icon @click="minusWordClick(chipConstant.word)">
                            <v-icon>mdi-minus-circle-outline</v-icon>
                        </v-btn>
                    </v-list-item-action>
                    <v-list-item-action>
                        <v-btn icon @click="plusWordClick()">
                            <v-icon>mdi-plus-circle-outline</v-icon>
                        </v-btn>
                    </v-list-item-action>
                </v-list-item>
                <template v-for="(word) in words">
                    <time-chip :id="word.key" :key="'word' + word.key" :name="word.value" :chip-type="chipConstant.word" />
                </template>
            </v-col>
        </v-row>

        <v-row>
            <v-col>
                <v-list-item class="mx-0 px-0">
                    <v-list-item-action>
                        <v-btn color="primary" @click="confirm()">
                            {{ $t('common.confirmEdit') }}
                        </v-btn>
                    </v-list-item-action>
                    <v-list-item-content />
                </v-list-item>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import { appConstant, chipConstant } from '@/constant/constant.js';
import { categoryInfoApi, editCategoryApi } from '@/apiHttp/wordApi.js';
import { policyApi, uploadFileToOssApi } from '@/apiHttp/ossApi.js';
import { simpleError } from '@/util/noticeUtils.js';
import { isBlank } from '@/util/stringUtils.js';
import { toHighImgUrl } from '@/util/fileUtils.js';

export default {
    name: 'CategoryEdit',

    components: {
        WordSearchDialog: () => import('./component/WordSearchDialog.vue'),
        CategorySearchDialog: () => import('./component/CategorySearchDialog.vue'),
        TimeChip: () => import('@/component/time/TimeChip.vue')
    },

    data() {
        return {
            chipConstant,
            toHighImgUrl,
            dialog: false,
            dialogChipType: null,
            dialogItems: [],

            backgroundFile: null,
            backgroundOssPolicy: {
                expire: 0
            },


            id: '',
            name: '',
            background: appConstant.defaultCategory,
            parent: [],
            children: [],
            words: []
        };
    },

    created() {
        this.init();
    },

    methods: {
        init() {
            const categoryId = this.$route.params.id;
            if (_.isNaN(categoryId)) {
                return;
            }

            categoryInfoApi(Array.of(categoryId)).then(response => {
                const data = response.data;
                const category = _.find(data, (it) => categoryId === it.id);
                if (_.isNil(category)) {
                    return;
                }
                this.id = category.id;
                this.name = category.name;
                if (!isBlank(category.background)) {
                    this.background = category.background;
                }
                this.parent = category.parent;
                this.children = category.children;
                this.words = category.words;
            });
        },

        minusWordClick(chipType) {
            this.dialogChipType = chipType;
            this.dialogItems = this.words;
            this.dialog = true;
        },

        plusWordClick() {
            this.$refs.wordSearchDialog.showDialog();
        },

        addWordCallback(word) {
            if (_.isNil(word)) {
                return;
            }
            this.words.push({
                key: word.value,
                value: word.text
            });
        },

        minusParentClick(chipType) {
            this.dialogChipType = chipType;
            this.dialogItems = this.parent;
            this.dialog = true;
        },

        plusParentClick() {
            this.$refs.parentSearchDialog.showDialog();
        },

        addParentCallback(category) {
            if (_.isNil(category)) {
                return;
            }
            this.parent.push({
                key: category.value,
                value: category.text
            });
        },

        minusChildrenClick(chipType) {
            this.dialogChipType = chipType;
            this.dialogItems = this.children;
            this.dialog = true;
        },

        plusChildrenClick() {
            this.$refs.childrenSearchDialog.showDialog();
        },

        addChildrenCallback(category) {
            if (_.isNil(category)) {
                return;
            }
            this.children.push({
                key: category.value,
                value: category.text
            });
        },

        deleteItem(index) {
            switch (this.dialogChipType.type) {
            case chipConstant.category.type:
                this.parent.splice(index, 1);
                break;
            case chipConstant.childCategory.type:
                this.children.splice(index, 1);
                break;
            case chipConstant.word.type:
                this.words.splice(index, 1);
                break;
            }
        },

        backgroundUpload() {
            if (this.backgroundOssPolicy.expire < new Date().getTime() / 1000 + 5) {
                policyApi(appConstant.ossPolicyEnum.wordBackground.type).then(response => {
                    const data = response.data;
                    this.backgroundOssPolicy = data;
                    this.$refs.backgroundInput.$refs.input.click();
                }).catch(error => {
                    console.log(error);
                    simpleError(this.$t('notice.uploadImageSizeLimitError'));
                });
            } else {
                this.$refs.backgroundInput.$refs.input.click();
            }
        },

        backgroundFileChange() {
            if (_.isNil(this.backgroundFile)) {
                return;
            }
            const file = this.backgroundFile;

            const upload = uploadFileToOssApi(file, this.backgroundOssPolicy);
            const key = upload.key;
            upload.callback.then(response => {
                const backgroundUrl = this.backgroundOssPolicy.host + '/' + key;
                this.background = backgroundUrl;
            }).catch(error => {
                console.log(error);
                simpleError(this.$t('notice.fileUploadError'));
            });

            this.$refs.backgroundInput.clearableCallback();
        },

        confirm() {
            if (isBlank(this.name)) {
                simpleError(this.$t('code_enum_3004'));
                return;
            }
            const background = this.background === appConstant.defaultCategory ? '' : this.background;
            const parent = _.isEmpty(this.parent) ? [] : this.parent.map(it => it.key);
            const children = _.isEmpty(this.children) ? [] : this.children.map(it => it.key);
            const words = _.isEmpty(this.words) ? [] : this.words.map(it => it.key);

            editCategoryApi(this.id, this.name, background, parent, children, words).then(response => {
            });
        }
    }
};
</script>
