<template>
    <v-row>
        <v-col cols="10">
            <v-text-field
                v-model="groupAuthColorValue"
                :label="$t('group.auth.groupAuthColor')"
                type="text"
                disabled
                prepend-icon="mdi-draw"
            />
        </v-col>
        <v-col cols="2" class="mt-4">
            <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                    <v-icon v-on="on">mdi-map-marker-question-outline</v-icon>
                </template>
                <p class="my-0 py-0">{{ $t('group.operation.groupAuth.colorDesc0') }}</p>
                <p class="my-0 py-0">{{ $t('group.operation.groupAuth.colorDesc1') }}</p>
                <p class="my-0 py-0">{{ $t('group.operation.groupAuth.colorDesc2') }}</p>
            </v-tooltip>
        </v-col>
        <v-col cols="12">
            <v-color-picker v-model="groupAuthColorValue" :swatches="swatches" show-swatches />
        </v-col>
    </v-row>
</template>

<script>
import { isBlank } from '@/util/stringUtils.js';
export default {
    name: 'GroupColorSelector',

    props: {
        groupAuthId: {
            type: String,
            default: '',
            required: true
        },
        groupAuthColor: {
            type: String,
            default: '#000000',
            required: true
        }
    },

    data() {
        return {
            groupAuthColorValue: '',
            defaultColor: '#000000',
            swatches: [
                ['#FF0000', '#AA0000', '#550000'],
                ['#FFFF00', '#AAAA00', '#555500'],
                ['#00FF00', '#00AA00', '#005500'],
                ['#00FFFF', '#00AAAA', '#005555'],
                ['#0000FF', '#0000AA', '#000055']
            ]
        };
    },

    watch: {
        groupAuthColor() {
            this.init();
        }
    },

    created() {
        this.init();
    },

    methods: {
        init() {
            if (isBlank(this.groupAuthColor)) {
                this.groupAuthColorValue = this.defaultColor;
            } else {
                this.groupAuthColorValue = this.groupAuthColor;
            }
        },

        getGroupAuthColorValue() {
            if (this.groupAuthColorValue === this.defaultColor) {
                return '';
            }
            return this.groupAuthColorValue;
        }
    }
};
</script>
