<template>
    <div>
        <not-found v-if="_.isNil(wordVo)" :not-found-obj="word404" />
        <div v-else>
            <v-container>
                <v-row>
                    <v-col>
                        <v-card>
                            <v-list-item>
                                <v-list-item-icon>
                                    <v-icon>mdi-lightbulb-on</v-icon>
                                </v-list-item-icon>
                                <v-list-item-content>
                                    <v-list-item-title class="headline">{{ wordVo.word }}</v-list-item-title>
                                </v-list-item-content>
                                <v-list-item-action>
                                    <v-btn icon color="primary" @click="editClick()">
                                        <v-icon>mdi-pencil-outline</v-icon>
                                    </v-btn>
                                </v-list-item-action>
                                <v-list-item-action>
                                    <v-btn icon color="primary" @click="shareClick($event)">
                                        <v-icon>mdi-share-variant</v-icon>
                                    </v-btn>
                                </v-list-item-action>
                            </v-list-item>
                        </v-card>
                    </v-col>
                </v-row>

                <v-row>
                    <v-col cols="12" md="4">
                        <div v-ripple>
                            <img
                                ref="img"
                                :src="toHighImgUrl(background)"
                                :alt="wordVo.word"
                                class="imgWord"
                            >
                        </div>
                    </v-col>
                    <v-col cols="12" md="6">
                        <div class="text-h6 font-weight-bold">{{ $t('word.sections') }}</div>
                        <ol type="1">
                            <li v-for="(section, index) in wordVo.sections" :key="'section' + index">{{ section }}</li>
                        </ol>
                    </v-col>
                </v-row>

                <v-row>
                    <v-col>
                        <div v-for="(paragraph, index) in wordVo.paragraphs" :key="'paragraph' + index" class="divParagraph">{{ paragraph }}</div>
                    </v-col>
                </v-row>

                <v-row>
                    <v-col>
                        <div class="text-h6 font-weight-bold">{{ $t('word.links') }}</div>
                        <template v-for="(link) in wordVo.links">
                            <time-chip :id="link.key" :key="'link' + link.key" :name="link.value" :chip-type="chipConstant.word" />
                        </template>
                    </v-col>
                </v-row>

                <v-row>
                    <v-col>
                        <div class="text-h6 font-weight-bold">{{ $t('word.categories') }}</div>
                        <template v-for="(category) in wordVo.categories">
                            <time-chip :id="category.key" :key="'category' + category.key" :name="category.value" :chip-type="chipConstant.category" />
                        </template>
                    </v-col>
                </v-row>

                <v-row>
                    <v-col>
                        <div class="text-h6 font-weight-bold">{{ $t('word.externalLink') }}</div>
                        <template v-for="(externalLink, index) in wordVo.externalLinks">
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
            </v-container>
        </div>
    </div>
</template>

<script>
import { appConstant, chipConstant, notFound } from '@/constant/constant.js';
import { wordInfoApi } from '@/apiHttp/wordApi.js';
import { isBlank } from '@/util/stringUtils.js';
import { simpleError } from '@/util/noticeUtils.js';
import clipboard from '@/util/clipboardUtils.js';
import { toHighImgUrl } from '@/util/fileUtils.js';
import { mapState } from 'vuex';

export default {
    name: 'WordEdit',

    components: {
        TimeChip: () => import('@/component/time/TimeChip.vue'),
        NotFound: () => import('@/component/base/NotFound.vue')
    },

    data() {
        return {
            title: '',
            description: '',
            keywords: '',

            toHighImgUrl,
            chipConstant,
            background: appConstant.defaultWord,
            word404: notFound.word404,
            wordVo: null
        };
    },

    metaInfo() {
        return {
            title: this.title,
            meta: [
                {
                    vmid: 'description',
                    name: 'description',
                    content: this.description
                },
                {
                    vmid: 'keywords',
                    name: 'keywords',
                    content: this.keywords
                }
            ]
        };
    },

    computed: {
        ...mapState('user', ['loggedIn', 'adminAuth'])
    },

    watch: {
        $route() {
            this.init();
        }
    },

    created() {
        this.init();
    },

    methods: {
        init() {
            const wordId = this.$route.params.id;
            if (isBlank(wordId)) {
                return;
            }

            wordInfoApi(Array.of(wordId)).then(response => {
                const data = response.data;
                const word = _.find(data, (it) => wordId === it.id);
                if (_.isNil(word)) {
                    this.wordVo = null;
                    return;
                }
                this.wordVo = word;
                if (!isBlank(this.wordVo.background)) {
                    this.background = this.wordVo.background;
                }

                // 设置标题
                this.title = this.wordVo.word + ' - ' + this.$t('appUrl.word');

                // 设置描述
                if (!_.isEmpty(this.wordVo.paragraphs)) {
                    this.description = _.join(this.wordVo.paragraphs, '|').substring(0, appConstant.seoDescriptionLengthLimit);
                } else {
                    this.description = this.wordVo.word;
                }

                // 设置关键字
                let keywords = '';
                if (!_.isEmpty(this.wordVo.links)) {
                    keywords = keywords + _.join(this.wordVo.links.map(it => it.value), ',') + ',';
                }
                if (!_.isEmpty(this.wordVo.categories)) {
                    keywords = keywords + _.join(this.wordVo.categories.map(it => it.value), ',') + ',';
                }
                if (!_.isEmpty(this.wordVo.externalLinks)) {
                    keywords = keywords + _.join(this.wordVo.externalLinks.map(it => it.value), ',') + ',';
                }
                if (!_.isEmpty(this.wordVo.sections)) {
                    keywords = keywords + _.join(this.wordVo.sections, ',');
                }
                this.keywords = keywords;
            });
        },

        editClick() {
            if (!this.loggedIn) {
                simpleError(this.$t('notice.notSignInError'));
                return;
            }
            if (this.adminAuth < appConstant.adminAuthEnum.adminAuth.type) {
                simpleError(this.$t('notice.notSignInError'));
                return;
            }
            this.$router.push({ path: '/word/edit/' + this.wordVo.id });
        },

        shareClick(event) {
            clipboard(appConstant.homeUrl + this.$route.path, event);
        }
    }
};
</script>

<style scoped>
    .imgWord {
        display: flex;
        max-width: 100%;
        cursor: pointer;
    }
    .divParagraph {
        text-indent: 2em;
    }
</style>
