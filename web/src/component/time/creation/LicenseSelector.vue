<template>
    <v-select
        v-model="licenseType"
        :items="licenses"
        :label="$t('user.license')"
        item-value="type"
        item-text="description"
        prepend-icon="mdi-license"
        append-outer-icon="mdi-map-marker-question-outline"
        @click:append-outer="goToLicense()"
    />
</template>

<script>
import { appConstant } from '@/constant/constant.js';
export default {
    name: 'Licenseselector',
    data() {
        return {
            licenses: [
                appConstant.licenseEnum.zfoo,
                appConstant.licenseEnum.cc0,
                appConstant.licenseEnum.cc1,
                appConstant.licenseEnum.cc2,
                appConstant.licenseEnum.cc6,
                appConstant.licenseEnum.cc7,
                appConstant.licenseEnum.cc8,
                appConstant.licenseEnum.cc9
            ],

            licenseType: 0
        };
    },
    methods: {
        setLicenseType(value) {
            this.licenseType = value;
        },

        getLicenseType() {
            return this.licenseType;
        },

        goToLicense() {
            this.$router.push({ path: '/license' });
        }
    }
};
</script>
