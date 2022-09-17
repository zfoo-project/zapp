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

        <word-search-dialog ref="wordSearchDialog" :callback="addLinkCallback" />
        <category-search-dialog ref="categorySearchDialog" :callback="addCategoryCallback" />
        <external-link-dialog ref="externalLinkDialog" :callback="addExternalLinkCallback" />

        <v-row>
            <v-col cols="12" md="6">
                <v-text-field
                    v-model="word"
                    :counter="27"
                    :label="this.$t('word.wordTitle')"
                    prepend-icon="mdi-lightbulb-on"
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
                <v-radio-group v-model="person" row>
                    <v-radio
                        :label="$t('user.items')"
                        :value="false"
                    />
                    <v-radio
                        :label="$t('user.persons')"
                        :value="true"
                    />
                    <v-tooltip bottom>
                        <template v-slot:activator="{ on }">
                            <v-icon v-on="on">mdi-map-marker-question-outline</v-icon>
                        </template>
                        <p class="my-0 py-0">{{ $t('word.personDescription') }}</p>
                    </v-tooltip>
                </v-radio-group>
            </v-col>
        </v-row>

        <v-row>
            <v-col cols="12" md="6">
                <v-list-item class="mx-0 px-0">
                    <v-list-item-content>
                        <div class="text-h6 font-weight-bold">
                            {{ $t('word.sections') }}
                        </div>
                    </v-list-item-content>
                    <v-list-item-action v-show="_.isEmpty(sections)">
                        <v-btn icon @click="plusSectionClick(0)">
                            <v-icon>mdi-plus-circle-outline</v-icon>
                        </v-btn>
                    </v-list-item-action>
                </v-list-item>
                <v-text-field
                    v-for="(section, index) in sections"
                    :key="'section' + index"
                    v-model="sections[index]"
                    :counter="27"
                    :prepend-inner-icon="toSectionIcon(index)"
                    prepend-icon="mdi-minus-circle-outline"
                    append-outer-icon="mdi-plus-circle-outline"
                    required
                    @click:prepend="minusSectionClick(index)"
                    @click:append-outer="plusSectionClick(index)"
                />
            </v-col>
        </v-row>

        <v-row>
            <v-col>
                <v-list-item class="mx-0 px-0">
                    <v-list-item-content>
                        <div class="text-h6 font-weight-bold">
                            {{ $t('word.paragraphs') }}
                        </div>
                    </v-list-item-content>
                    <v-list-item-action v-show="_.isEmpty(paragraphs)">
                        <v-btn icon @click="plusParagraphClick(0)">
                            <v-icon>mdi-plus-circle-outline</v-icon>
                        </v-btn>
                    </v-list-item-action>
                </v-list-item>
                <v-textarea
                    v-for="(paragraph, index) in paragraphs"
                    :key="'paragraph' + index"
                    v-model="paragraphs[index]"
                    :label="$t('word.paragraph')"
                    type="text"
                    auto-grow
                    full-width
                    outlined
                    rows="2"
                    prepend-icon="mdi-minus-circle-outline"
                    append-outer-icon="mdi-plus-circle-outline"
                    @click:prepend="minusParagraphClick(index)"
                    @click:append-outer="plusParagraphClick(index)"
                />
            </v-col>
        </v-row>

        <v-row>
            <v-col>
                <v-list-item class="mx-0 px-0">
                    <v-list-item-content>
                        <div class="text-h6 font-weight-bold">
                            {{ $t('word.links') }}
                        </div>
                    </v-list-item-content>
                    <v-list-item-action>
                        <v-btn icon @click="minusLinkClick(chipConstant.word)">
                            <v-icon>mdi-minus-circle-outline</v-icon>
                        </v-btn>
                    </v-list-item-action>
                    <v-list-item-action>
                        <v-btn icon @click="plusLinkClick()">
                            <v-icon>mdi-plus-circle-outline</v-icon>
                        </v-btn>
                    </v-list-item-action>
                </v-list-item>
                <template v-for="(link) in links">
                    <time-chip :id="link.key" :key="'link' + link.key" :name="link.value" :chip-type="chipConstant.word" />
                </template>
            </v-col>
        </v-row>

        <v-row>
            <v-col>
                <v-list-item class="mx-0 px-0">
                    <v-list-item-content>
                        <div class="text-h6 font-weight-bold">
                            {{ $t('word.categories') }}
                        </div>
                    </v-list-item-content>
                    <v-list-item-action>
                        <v-btn icon @click="minusCategoryClick(chipConstant.category)">
                            <v-icon>mdi-minus-circle-outline</v-icon>
                        </v-btn>
                    </v-list-item-action>
                    <v-list-item-action>
                        <v-btn icon @click="plusCategoryClick()">
                            <v-icon>mdi-plus-circle-outline</v-icon>
                        </v-btn>
                    </v-list-item-action>
                </v-list-item>
                <template v-for="(category) in categories">
                    <time-chip :id="category.key" :key="'category' + category.key" :name="category.value" :chip-type="chipConstant.category" />
                </template>
            </v-col>
        </v-row>

        <v-row>
            <v-col>
                <v-list-item class="mx-0 px-0">
                    <v-list-item-content>
                        <div class="text-h6 font-weight-bold">
                            {{ $t('word.externalLink') }}
                        </div>
                    </v-list-item-content>
                    <v-list-item-action>
                        <v-btn icon @click="minusExternalLinkClick(chipConstant.externalLink)">
                            <v-icon>mdi-minus-circle-outline</v-icon>
                        </v-btn>
                    </v-list-item-action>
                    <v-list-item-action>
                        <v-btn icon @click="plusExternalLinkClick()">
                            <v-icon>mdi-plus-circle-outline</v-icon>
                        </v-btn>
                    </v-list-item-action>
                </v-list-item>
                <template v-for="(externalLink, index) in externalLinks">
                    <v-chip
                        :key="'externalLink' + externalLink.key + index"
                        class="pl-0 mr-1"
                        :color="chipConstant.externalLink.color"
                        text-color="white"
                        x-small
                        ripple
                        target="_blank"
                        :href="externalLink.key"
                        :title="externalLink.value"
                    >
                        <v-avatar class="pl-3" left>
                            <v-icon x-small>{{ chipConstant.externalLink.icon }}</v-icon>
                        </v-avatar>
                        {{ externalLink.value }}
                    </v-chip>
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
import { wordDetailApi, editWordApi } from '@/apiHttp/wordApi.js';
import { policyApi, uploadFileToOssApi } from '@/apiHttp/ossApi.js';
import { simpleError } from '@/util/noticeUtils.js';
import { isBlank } from '@/util/stringUtils.js';
import { toHighImgUrl } from '@/util/fileUtils.js';

export default {
    name: 'WordEdit',

    components: {
        WordSearchDialog: () => import('./component/WordSearchDialog.vue'),
        CategorySearchDialog: () => import('./component/CategorySearchDialog.vue'),
        ExternalLinkDialog: () => import('./component/ExternalLinkDialog.vue'),
        TimeChip: () => import('@/component/time/TimeChip.vue')
    },

    data() {
        return {
            toHighImgUrl,
            chipConstant,
            dialog: false,
            dialogChipType: null,
            dialogItems: [],

            backgroundFile: null,
            backgroundOssPolicy: {
                expire: 0
            },


            id: '',
            word: '',
            person: false,
            background: appConstant.defaultWord,
            paragraphs: [],
            sections: [],
            links: [],
            categories: [],
            externalLinks: []
        };
    },

    created() {
        this.init();
    },

    methods: {
        init() {
            const wordId = this.$route.params.id;
            if (_.isNaN(wordId)) {
                return;
            }

            wordDetailApi(wordId).then(response => {
                const data = response.data;
                this.person = data.person;
                const word = data.wordVO;

                this.id = word.id;
                this.word = word.word;
                if (!isBlank(word.background)) {
                    this.background = word.background;
                }
                this.paragraphs = word.paragraphs;
                this.sections = word.sections;
                this.links = word.links;
                this.categories = word.categories;
                this.externalLinks = word.externalLinks;
            });
        },

        toSectionIcon(index) {
            if (index <= 8) {
                return 'mdi-numeric-' + (index + 1) + '-circle-outline';
            } else {
                return 'mdi-numeric-9-plus-circle-outline';
            }
        },

        minusSectionClick(index) {
            this.sections.splice(index, 1);
        },

        plusSectionClick(index) {
            const left = this.sections.slice(0, index + 1);
            const right = this.sections.slice(index + 1, this.sections.length);
            left.push('');
            this.sections = _.concat(left, right);
        },

        minusParagraphClick(index) {
            this.paragraphs.splice(index, 1);
        },

        plusParagraphClick(index) {
            const left = this.paragraphs.slice(0, index + 1);
            const right = this.paragraphs.slice(index + 1, this.paragraphs.length);
            left.push('');
            this.paragraphs = _.concat(left, right);
        },

        minusLinkClick(chipType) {
            this.dialogChipType = chipType;
            this.dialogItems = this.links;
            this.dialog = true;
        },

        plusLinkClick() {
            this.$refs.wordSearchDialog.showDialog();
        },

        addLinkCallback(word) {
            if (_.isNil(word)) {
                return;
            }
            this.links.push({
                key: word.value,
                value: word.text
            });
        },

        minusCategoryClick(chipType) {
            this.dialogChipType = chipType;
            this.dialogItems = this.categories;
            this.dialog = true;
        },

        plusCategoryClick() {
            this.$refs.categorySearchDialog.showDialog();
        },

        addCategoryCallback(category) {
            if (_.isNil(category)) {
                return;
            }
            this.categories.push({
                key: category.value,
                value: category.text
            });
        },

        minusExternalLinkClick(chipType) {
            this.dialogChipType = chipType;
            this.dialogItems = this.externalLinks;
            this.dialog = true;
        },

        plusExternalLinkClick() {
            this.$refs.externalLinkDialog.showDialog();
        },

        addExternalLinkCallback(externalLink) {
            if (_.isNil(externalLink)) {
                return;
            }
            this.externalLinks.push({
                key: externalLink.key,
                value: externalLink.value
            });
        },

        deleteItem(index) {
            switch (this.dialogChipType.type) {
            case chipConstant.word.type:
                this.links.splice(index, 1);
                break;
            case chipConstant.category.type:
                this.categories.splice(index, 1);
                break;
            case chipConstant.externalLink.type:
                this.externalLinks.splice(index, 1);
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
            if (isBlank(this.word)) {
                simpleError(this.$t('code_enum_3004'));
                return;
            }
            const background = this.background === appConstant.defaultWord ? '' : this.background;
            if (_.some(this.paragraphs, it => isBlank(it))) {
                simpleError(this.$t('code_enum_3000'));
                return;
            }
            if (_.some(this.sections, it => isBlank(it))) {
                simpleError(this.$t('code_enum_3001'));
                return;
            }
            if (_.some(this.externalLinks, it => isBlank(it.key) || isBlank(it.value))) {
                simpleError(this.$t('code_enum_3003'));
                return;
            }
            const links = _.isEmpty(this.links) ? [] : this.links.map(it => it.key);
            const categories = _.isEmpty(this.categories) ? [] : this.categories.map(it => it.key);

            editWordApi(this.id, this.word, this.person, background, this.paragraphs, this.sections, links, categories, this.externalLinks).then(response => {
                this.$router.push({ path: '/word/' + this.id });
            });
        }
    }
};
</script>
