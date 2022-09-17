<template>
    <v-row :class="galleryRowClass">
        <v-dialog
            v-model="dialog"
            :scrollable="scrollable"
            :max-width="dialogMaxWidth"
        >
            <v-img
                v-touch="{
                    left: () => swipe('left'),
                    right: () => swipe('right'),
                    up: () => swipe('up'),
                    down: () => swipe('down')
                }"
                v-ripple
                :src="compress ? toHighImgUrl(dialogImgSrc) : dialogImgSrc"
                :lazy-src="compress ? toLowImgUrl(dialogImgSrc) : dialogImgSrc"
                contain
                @click="swipe('left')"
            >
                <template v-slot:placeholder>
                    <v-row
                        class="fill-height ma-0"
                        align="center"
                        justify="center"
                    >
                        <v-progress-circular indeterminate />
                    </v-row>
                </template>
            </v-img>
        </v-dialog>
        <template v-for="(imgLink, imgIndex) in images">
            <v-col :key="imgIndex" cols="4" :class="galleryClass">
                <div v-if="imgTag && $vuetify.breakpoint.mdAndUp" v-ripple>
                    <img
                        ref="img"
                        :src="compress ? toLowImgUrl(imgLink) : imgLink"
                        :alt="imgAlt"
                        class="imgGallery"
                        @click="readImg(imgLink, imgIndex)"
                    >
                </div>
                <v-img
                    v-else
                    ref="img"
                    v-ripple
                    :src="compress ? toLowImgUrl(imgLink) : imgLink"
                    aspect-ratio="1"
                    class="grey lighten-2 text-right"
                    @click="readImg(imgLink, imgIndex)"
                >
                    <template v-slot:placeholder>
                        <v-row class="fill-height ma-0" align="center" justify="center">
                            <v-progress-circular indeterminate color="grey lighten-5" />
                        </v-row>
                    </template>
                    <v-btn v-if="imgIcon" icon @click.stop="imgIconClick(imgLink)">
                        <v-icon>{{ imgIcon }}</v-icon>
                    </v-btn>
                </v-img>
            </v-col>
        </template>
    </v-row>
</template>

<script>
import { toLowImgUrl, toHighImgUrl } from '@/util/fileUtils.js';
import { simpleError } from '@/util/noticeUtils.js';

export default {
    name: 'TimeImgGallery',
    props: {
        images: {
            type: Array,
            default: () => []
        },
        imgIcon: {
            type: String,
            default: '',
            required: false
        },
        imgIconFunction: {
            type: Function,
            default: () => () => {},
            required: false
        },
        compress: {
            type: Boolean,
            default: true,
            required: false
        },
        imgTag: {
            type: Boolean,
            default: true,
            required: false
        },
        imgAlt: {
            type: String,
            default: '',
            required: false
        }
    },
    data() {
        return {
            toLowImgUrl,
            toHighImgUrl,
            galleryRowClass: this.$vuetify.breakpoint.mdAndUp ? 'pl-3 pr-1' : 'pl-3 pr-2',
            galleryClass: this.$vuetify.breakpoint.mdAndUp ? 'pl-0 pt-0 pb-2 pr-2' : 'pl-0 pt-0 pb-1 pr-1',
            dialog: false,
            dialogImgSrc: '',
            scrollable: false,
            dialogMaxWidth: this.$vuetify.breakpoint.mdAndUp ? this.$vuetify.breakpoint.width * 0.6 : this.$vuetify.breakpoint.width
        };
    },
    methods: {
        readImg(imgLink, imgIndex) {
            // console.log(this.$refs.img[imgIndex]);
            const image = this.imgTag ? this.$refs.img[imgIndex] : this.$refs.img[imgIndex].image;
            const width = image.width;
            const height = image.height;
            if (height / width > 2) {
                this.scrollable = false;
            } else {
                this.scrollable = true;
            }

            this.dialog = true;
            this.dialogImgSrc = imgLink;
        },
        imgIconClick(imgLink) {
            this.imgIconFunction(imgLink);
        },
        swipe(direction) {
            if (_.isEqual('up', direction) || _.isEqual('down', direction)) {
                return;
            }

            const currentImageIndex = _.findIndex(this.images, it => it === this.dialogImgSrc);
            if (_.isEqual('right', direction)) {
                const previousIndex = (currentImageIndex === 0) ? -1 : currentImageIndex - 1;
                if (previousIndex < 0) {
                    simpleError(this.$t('notice.timeSliceImageFirstError'));
                    return;
                }
                this.dialogImgSrc = this.images[previousIndex];
            } else if (_.isEqual('left', direction)) {
                const nextIndex = (currentImageIndex >= this.images.length - 1) ? -1 : currentImageIndex + 1;
                if (nextIndex < 0) {
                    simpleError(this.$t('notice.timeSliceImageLastError'));
                    return;
                }
                this.dialogImgSrc = this.images[nextIndex];
            }
        }
    }
};
</script>

<style scoped>
    .imgGallery {
        display: flex;
        max-width: 100%;
        cursor:pointer;
    }
</style>
