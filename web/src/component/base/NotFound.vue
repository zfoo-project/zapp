<template>
    <v-container fluid>
        <v-row align="center" justify="center">
            <v-col md="6">
                <v-img v-ripple :src="toHighImgUrl(notFoundObj.imgUrl)" />
            </v-col>
        </v-row>
        <v-row align="center" justify="center">
            <h1 class="display-1">{{ $t(notFoundObj.title) }}</h1>
        </v-row>
        <v-row align="center" justify="center">
            <p>{{ $t(notFoundObj.subTitle) }}</p>
        </v-row>
        <v-row align="center" justify="center">
            <v-btn :to="notFoundObj.to" outlined>
                {{ $t(notFoundObj.toText) }}
            </v-btn>
        </v-row>
    </v-container>
</template>


<script>
import { toHighImgUrl } from '@/util/fileUtils.js';

export default {
    name: 'NotFound',
    props: {
        notFoundObj: {
            type: Object,
            default: () => {}
        }
    },
    data() {
        return {
            toHighImgUrl
        };
    }
};
</script>
