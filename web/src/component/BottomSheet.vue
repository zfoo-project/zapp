<template>
    <div class="text-center">
        <v-bottom-sheet v-model="sheet" persistent>
            <v-sheet class="text-center" height="150px">
                <div class="text-right">
                    <v-btn icon color="error" large @click="sheet = !sheet">
                        <v-icon>
                            mdi-close-circle-outline
                        </v-icon>
                    </v-btn>
                </div>
                <v-list>
                    <v-list-item>
                        <v-list-item-title>{{ $t('bottomSheet.first') }}</v-list-item-title>
                    </v-list-item>
                    <v-list-item>
                        <v-list-item-title>
                            {{ $t('bottomSheet.second') }}
                            <img :src="require(`@/asset/icon/apple-action-icon.png`)" width="30" height="30">
                            {{ $t('bottomSheet.third') }}
                        </v-list-item-title>
                    </v-list-item>
                </v-list>
            </v-sheet>
        </v-bottom-sheet>
    </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
    name: 'BottomSheet',
    data: () => ({
        sheet: false
    }),

    computed: {
        ...mapState('app', ['ios', 'safari'])
    },

    created() {
        // 是否已PWA方式运行
        const isStandaloneMode = (window.matchMedia('(display-mode: standalone)').matches) || (window.navigator.standalone) || document.referrer.includes('android-app://');
        if (!isStandaloneMode) {
            setTimeout(() => {
                this.showBottomSheet();
            }, 30 * 1000);
            setTimeout(() => {
                this.showBottomSheet();
            }, 5 * 60 * 100);
            setTimeout(() => {
                this.showBottomSheet();
            }, 20 * 60 * 100);
        }
    },

    methods: {
        showBottomSheet() {
            // 只有iphone手机需要手动提示，Android会主动提示
            if (this.ios && this.safari) {
                this.sheet = true;
            }
        }
    }
};
</script>
