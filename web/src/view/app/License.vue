<template>
    <v-container>
        <v-row>
            <v-col>
                <div class="display-1 text-center">
                    {{ $t('license.title') }}
                </div>
            </v-col>
        </v-row>
        <v-row>
            <v-col>
                <p>
                    {{ $t('license.header1') }}
                    <a href="https://creativecommons.org/licenses" target="_blank" style="text-decoration:none;">
                        {{ $t('license.header2') }}
                    </a>
                </p>
                <p>{{ $t('license.header3') }}</p>
            </v-col>
        </v-row>
        <v-row>
            <v-col>
                <div class="title text-center">
                    {{ $t('license.titlePart1') }}
                </div>
                <p>
                    {{ $t('license.part11') }}
                    <a href="https://www.seagate.com/cn/zh/our-story/data-age-2025/" target="_blank" style="text-decoration:none;">
                        {{ $t('license.part12') }}
                    </a>
                    {{ $t('license.part13') }}
                    <a href="https://www.seagate.com/files/www-content/our-story/trends/files/idc-seagate-dataage-whitepaper.pdf" target="_blank" style="text-decoration:none;">
                        {{ $t('license.part14') }}
                    </a>
                    {{ $t('license.part15') }}
                </p>
                <p>{{ $t('license.part16') }}</p>
                <p>{{ $t('license.part17') }}</p>
                <p>{{ $t('license.part18') }}</p>
                <p>{{ $t('license.part19') }}</p>
                <p>
                    {{ $t('license.part110') }}
                    <a href="https://baike.baidu.com/item/%E8%A7%86%E8%A7%89%E4%B8%AD%E5%9B%BD%E7%89%88%E6%9D%83%E9%97%A8/23408851" target="_blank" style="text-decoration:none;">
                        {{ $t('license.part111') }}
                    </a>
                    <a href="https://zhuanlan.zhihu.com/p/75276181" target="_blank" style="text-decoration:none;">
                        {{ $t('license.part112') }}
                    </a>
                    {{ $t('license.part113') }}
                </p>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12">
                <div class="title text-center">
                    {{ $t('license.titlePart2') }}
                </div>
                <p>{{ $t('license.part21') }}</p>
            </v-col>
            <v-col md="6" offset-md="2">
                <v-img :src="toHighImgUrl(appConstant.licenseCcCondition)" contain />
            </v-col>
        </v-row>
        <v-row>
            <v-col>
                <p>{{ $t('license.part22') }}</p>
                <p>{{ $t('license.part23') }}</p>
                <p>{{ $t('license.part24') }}</p>
                <p>{{ $t('license.part25') }}</p>
                <p>{{ $t('license.part26') }}</p>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12">
                <div class="title text-center">
                    {{ $t('license.titlePart3') }}
                </div>
                <p>{{ $t('license.part31') }}</p>
            </v-col>
            <v-col md="6" offset-md="2">
                <v-img :src="toHighImgUrl(appConstant.licenseCcItem)" contain />
            </v-col>
        </v-row>
        <v-row>
            <v-col>
                <p>{{ $t('license.part32') }}</p>
                <p>{{ $t('license.part33') }}</p>
                <p>{{ $t('license.part34') }}</p>
                <p>{{ $t('license.part35') }}</p>
                <p>{{ $t('license.part36') }}</p>
                <p>{{ $t('license.part37') }}</p>
                <p>{{ $t('license.part38') }}</p>
                <p>{{ $t('license.part39') }}</p>
                <p>{{ $t('license.part310') }}</p>
            </v-col>
        </v-row>
        <v-row>
            <v-col>
                <div class="title text-center">
                    {{ $t('license.titlePart4') }}
                </div>
                <p>{{ $t('license.part41') }}</p>
                <p>{{ $t('license.part42') }}</p>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import { appConstant } from '@/constant/constant.js';
import { toHighImgUrl } from '@/util/fileUtils.js';
export default {
    name: 'License',

    data() {
        return {
            appConstant,
            toHighImgUrl
        };
    }

};
</script>

<style scoped>
    p {
        text-indent: 2em;
    }
    ol {
        list-style-type: disc;
    }
</style>
