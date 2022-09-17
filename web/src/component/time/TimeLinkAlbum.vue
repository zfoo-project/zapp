<template>
    <v-row>
        <v-col v-for="(timeLinkAlbum, index) in albums" :key="'col' + index" cols="12">
            <v-card>
                <v-card-title v-if="isBlank(timeLinkAlbum.album)">{{ $t('ts.timeLinkAlbum.defaultAlbumName') }}</v-card-title>
                <v-card-title v-else>{{ timeLinkAlbum.album }}</v-card-title>
                <v-card-text>
                    <v-chip
                        v-for="(link, linkIndex) in timeLinkAlbum.links"
                        :key="'chip' + linkIndex"
                        class="mt-1 mr-1"
                        color="light-green darken-2"
                        text-color="white"
                        small
                        ripple
                        :to="'/time/' + link"
                        :close="closeIcon"
                        @click:close="closeFunction(link)"
                    >
                        <v-icon small>mdi-vector-link</v-icon>
                        {{ link }}
                    </v-chip>
                </v-card-text>
            </v-card>
        </v-col>
    </v-row>
</template>

<script>
import { isBlank } from '@/util/stringUtils.js';

export default {
    name: 'TimeLinkAlbum',
    props: {
        albums: {
            type: Array,
            default: () => []
        },
        closeIcon: {
            type: Boolean,
            default: false,
            required: false
        },
        closeCallback: {
            type: Function,
            default: () => () => {},
            required: false
        }
    },
    data() {
        return {
            dialog: false,
            isBlank
        };
    },
    methods: {
        closeFunction(timeLink) {
            if (_.isEmpty(timeLink)) {
                return;
            }
            if (_.isNil(this.closeCallback)) {
                return;
            }
            this.closeCallback(timeLink);
        }
    }
};
</script>
