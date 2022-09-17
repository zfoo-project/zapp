<template>
    <div>
        <not-found v-if="_.isNil(categoryVo)" :not-found-obj="category404" />
        <div v-else>
            <v-container>
                <v-row>
                    <v-col>
                        <v-card>
                            <v-list-item>
                                <v-list-item-icon>
                                    <v-icon>mdi-lightbulb-group</v-icon>
                                </v-list-item-icon>
                                <v-list-item-content>
                                    <v-list-item-title class="headline">{{ categoryVo.name }}</v-list-item-title>
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
                                :alt="categoryVo.word"
                                class="imgCategory"
                            >
                        </div>
                    </v-col>
                </v-row>

                <v-row>
                    <v-col>
                        <div class="text-h6 font-weight-bold">{{ $t('word.parent') }}</div>
                        <template v-for="(category) in categoryVo.parent">
                            <time-chip :id="category.key" :key="'parent' + category.key" :name="category.value" :chip-type="chipConstant.category" />
                        </template>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col>
                        <div class="text-h6 font-weight-bold">{{ $t('word.children') }}</div>
                        <template v-for="(category) in categoryVo.children">
                            <time-chip :id="category.key" :key="'children' + category.key" :name="category.value" :chip-type="chipConstant.category" />
                        </template>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col>
                        <div class="text-h6 font-weight-bold">{{ $t('word.childWord') }}</div>
                        <template v-for="(word) in categoryVo.words">
                            <time-chip :id="word.key" :key="'word' + word.key" :name="word.value" :chip-type="chipConstant.word" />
                        </template>
                    </v-col>
                </v-row>
            </v-container>
        </div>
    </div>
</template>

<script>
import { appConstant, chipConstant, notFound } from '@/constant/constant.js';
import { categoryInfoApi } from '@/apiHttp/wordApi.js';
import { isBlank } from '@/util/stringUtils.js';
import { simpleError } from '@/util/noticeUtils.js';
import clipboard from '@/util/clipboardUtils.js';
import { toHighImgUrl } from '@/util/fileUtils.js';
import { mapState } from 'vuex';

export default {
    name: 'Category',

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
            background: appConstant.defaultCategory,
            category404: notFound.category404,
            categoryVo: null
        };
    },

    computed: {
        ...mapState('user', ['loggedIn', 'adminAuth'])
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
            const categoryId = this.$route.params.id;
            if (isBlank(categoryId)) {
                return;
            }

            categoryInfoApi(Array.of(categoryId)).then(response => {
                const data = response.data;
                const category = _.find(data, (it) => categoryId === it.id);
                if (_.isNil(category)) {
                    return;
                }
                this.categoryVo = category;
                if (!isBlank(this.categoryVo.background)) {
                    this.background = this.categoryVo.background;
                }

                // 设置标题
                this.title = this.categoryVo.name + ' - ' + this.$t('appUrl.category');

                // 设置描述
                if (!_.isEmpty(this.categoryVo.words)) {
                    this.description = _.join(this.categoryVo.words.map(it => it.value), ',').substring(0, appConstant.seoDescriptionLengthLimit);
                } else {
                    this.description = this.categoryVo.name;
                }

                // 设置关键字
                let keywords = '';
                if (!_.isEmpty(this.categoryVo.parent)) {
                    keywords = keywords + _.join(this.categoryVo.parent.map(it => it.value), ',') + ',';
                }
                if (!_.isEmpty(this.categoryVo.children)) {
                    keywords = keywords + _.join(this.categoryVo.children.map(it => it.value), ',');
                }
                this.keywords = keywords;
            });
        },

        shareClick(event) {
            clipboard(appConstant.homeUrl + this.$route.path, event);
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
            this.$router.push({ path: '/category/edit/' + this.categoryVo.id });
        }
    }
};
</script>

<style scoped>
    .imgCategory {
        display: flex;
        max-width: 100%;
        cursor: pointer;
    }
</style>
