<template>
    <v-list-item @click="userClick(userCache)">
        <v-list-item-avatar>
            <v-img :src="toSimpleAvatarUrl(userCache.avatar)" />
        </v-list-item-avatar>

        <v-list-item-content>
            <v-list-item-title>
                {{ toTagOrName(userCache) }}
                <v-icon dense :color="genderItems[userCache.gender].color">{{ genderItems[userCache.gender].icon }}</v-icon>
            </v-list-item-title>
            <v-list-item-subtitle>
                {{ userCache.signature }}
            </v-list-item-subtitle>
            <v-list-item-subtitle v-if="new Date().getTime() <= userCache.customTime">
                <v-icon color="amber">mdi-emoticon-outline</v-icon>
                {{ userCache.custom }}
            </v-list-item-subtitle>
        </v-list-item-content>

        <template v-if="!_.isEmpty(userActions)">
            <v-list-item-action v-for="(action, index) in userActions" :key="index">
                <v-btn fab icon :color="action.color" @click.stop="action.callback(userCache)">
                    <v-icon>{{ action.icon }}</v-icon>
                </v-btn>
            </v-list-item-action>
        </template>
    </v-list-item>
</template>

<script>
import { genderItems } from '@/constant/constant.js';
import { toSimpleAvatarUrl } from '@/util/fileUtils.js';
import { toTagOrName } from '@/util/stringUtils.js';
export default {
    name: 'UserList',
    props: {
        userCache: {
            type: Object,
            default: () => {}
        },

        clickCallback: {
            type: Function,
            default: null,
            required: false
        },

        userActions: {
            type: Array,
            default: () => [],
            required: false
        }
    },

    data() {
        return {
            genderItems,
            toSimpleAvatarUrl,
            toTagOrName
        };
    },


    methods: {
        userClick(userCache) {
            if (_.isNil(this.clickCallback)) {
                return;
            }
            this.clickCallback(userCache);
        }
    }
};

</script>
