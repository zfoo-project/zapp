<template>
    <v-row>
        <v-col>
            <v-card>
                <v-card-title size="10">
                    <div class="body-1" v-html="computedContent" />
                </v-card-title>
            </v-card>
        </v-col>
    </v-row>
</template>

<script>
import { contentToDisplay } from '@/util/stringUtils.js';
export default {
    name: 'TimeContent',
    props: {
        id: {
            type: String,
            default: ''
        },
        content: {
            type: String,
            default: ''
        }
    },
    computed: {
        computedContent: function() {
            return contentToDisplay(this.content);
        }
    }
};
</script>
