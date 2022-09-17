<template>
    <v-autocomplete
        v-model="selectedItem"
        :items="items"
        :search-input.sync="query"
        :loading="loading"
        :placeholder="placeholder"
        :item-text="getText"
        :item-value="getValue"
        :menu-props="menuProps"
        append-icon="search"
        dense
        hide-no-data
        hide-details
        single-line
        return-object
        @change="onChange()"
        @blur="onBlur()"
        @click="onClick()"
        @click:append="search()"
        @keyup.enter="search()"
    >
        <template v-slot:item="data">
            <template v-if="data.item.type === chipConstant.location.type">
                <v-icon :color="chipConstant.location.color" class="mr-2">{{ chipConstant.location.icon }}</v-icon>
                {{ data.item.name }}
            </template>
            <template v-else-if="data.item.type === chipConstant.person.type">
                <v-icon :color="chipConstant.person.color" class="mr-2">{{ chipConstant.person.icon }}</v-icon>
                {{ data.item.name }}
            </template>
            <template v-else-if="data.item.type === chipConstant.item.type">
                <v-icon :color="chipConstant.item.color" class="mr-2">{{ chipConstant.item.icon }}</v-icon>
                {{ data.item.name }}
            </template>
            <template v-else-if="data.item.type === chipConstant.hot.type">
                <v-icon :color="chipConstant.hot.color" class="mr-2">{{ chipConstant.hot.icon }}</v-icon>
                <span class="d-inline-block text-truncate" style="max-width: 30em;">
                    {{ data.item.name + ' ' + data.item.score / 10 + $t('word.hot') }}
                </span>
            </template>
            <template v-else>
                <v-icon :color="chipConstant.search.color" class="mr-2">{{ chipConstant.search.icon }}</v-icon>
                <span class="d-inline-block text-truncate" style="max-width: 30em;">
                    {{ data.item.name }}
                </span>
            </template>
        </template>
    </v-autocomplete>
</template>

<script>
import { chipConstant } from '@/constant/constant.js';
import { simpleError } from '@/util/noticeUtils.js';
import { isBlank } from '@/util/stringUtils.js';
import { searchHintApi, hotApi } from '@/apiHttp/homeApi.js';

export default {
    name: 'AppSearch',
    data: () => ({
        chipConstant,
        menuProps: {
            closeOnClick: false,
            closeOnContentClick: false,
            disableKeys: true,
            openOnClick: false,
            maxHeight: 500
        },
        query: '',
        placeholder: '',
        selectedItem: null,
        items: [],
        hotItems: [],
        loading: false,

        initFlag: false
    }),

    watch: {
        query() {
            if (isBlank(this.query)) {
                return;
            }
            const query = this.query;
            this.loading = true;
            searchHintApi(this.query).then(response => {
                const data = response.data;
                this.loading = false;

                if (_.isEmpty(data)) {
                    return;
                }
                const item = {
                    type: chipConstant.search.type,
                    id: 1,
                    name: query
                };

                this.items = _.concat([item], data);
            });
        },

        $route() {
            this.init();
        }
    },

    created() {
        this.init();
    },

    methods: {
        init() {
            if (_.startsWith(this.$route.path, '/search')) {
                const query = this.$route.query.query;
                if (!_.isEqual(this.selectedItem, query)) {
                    const item = {
                        type: chipConstant.search.type,
                        id: 1,
                        name: query
                    };
                    this.items = [item];
                    this.selectedItem = query;
                    this.placeholder = query;
                }
                this.initFlag = false;
            } else {
                if (!this.initFlag) {
                    hotApi().then(response => {
                        const data = response.data;
                        if (_.isEmpty(data)) {
                            return;
                        }
                        this.hotItems = data;
                        this.items = data;
                        this.placeholder = data[0].name;
                        this.initFlag = true;
                    });
                }
            }
        },

        onChange() {
            if (_.isNil(this.selectedItem)) {
                return;
            }
            this.query = this.selectedItem.name;
            this.search();
        },

        onBlur() {
            if (_.startsWith(this.$route.path, '/search')) {
                return;
            }

            this.query = '';
            this.selectedItem = null;
        },

        onClick() {
            if (_.startsWith(this.$route.path, '/search')) {
                this.$vuetify.goTo(0);
            }
        },

        getValue(item) {
            return item.type + '-' + item.id;
        },

        getText(item) {
            return item.name;
        },

        search() {
            if (isBlank(this.query)) {
                simpleError(this.$t('notice.searchEmptyError'));
                return;
            }
            const query = this.query;
            if (_.isEqual(this.$route.query.query, query)) {
                return;
            }
            this.items = [];
            this.selectedItem = {
                type: chipConstant.search.type,
                id: 1,
                name: query
            };
            this.items.push(this.selectedItem);
            this.$router.push({
                path: '/search',
                query: {
                    query: query
                }
            });
        }
    }

};
</script>
