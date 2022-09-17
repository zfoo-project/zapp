<template>
    <div>
        <template v-for="(display, index) in contentToDisplayList(content)">
            <span v-if="groupConstant.textMessageEnum.text.type === display.type" :key="'text' + index" v-text="display.value" />
            <span v-else-if="groupConstant.textMessageEnum.space.type === display.type" :key="'space' + index" v-html="display.value" />
            <br v-else-if="groupConstant.textMessageEnum.ls.type === display.type" :key="'ls' + index">
            <v-icon v-else-if="groupConstant.textMessageEnum.emotion.type === display.type" :key="'emotion' + index" :color="emotions.color">
                {{ display.value }}
            </v-icon>
            <a
                v-else-if="groupConstant.textMessageEnum.http.type === display.type"
                :key="'http' + index"
                style="word-break: break-word;"
                class="body-1 blue--text"
                :href="display.value"
                target="_blank"
            >
                {{ display.value }}
            </a>
        </template>
    </div>
</template>

<script>
import { contentToDisplayList } from '@/util/stringUtils.js';
import { groupConstant, emotions } from '@/constant/constant.js';
export default {
    name: 'TimeContent',
    props: {
        content: {
            type: String,
            default: ''
        }
    },

    data() {
        return {
            contentToDisplayList,
            groupConstant,
            emotions
        };
    }
};
</script>
