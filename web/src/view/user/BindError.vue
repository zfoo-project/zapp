<template>
    <v-container>
        <v-row>
            <v-col>
                <v-card>
                    <v-card-title>
                        {{ errorMessage }}
                    </v-card-title>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
export default {
    name: 'BindError',

    data() {
        return {
            errorMessage: ''
        };
    },

    created() {
        const platform = this.$route.params.platform;
        if (_.isEqual(platform, 'weibo')) {
            this.errorMessage = this.$t('user.weiBoBindError');
        } else if (_.isEqual(platform, 'weChat')) {
            this.errorMessage = this.$t('user.weChatBindError');
        }
    }
};
</script>
