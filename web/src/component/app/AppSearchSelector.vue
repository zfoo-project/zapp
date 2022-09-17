<template>
    <v-container v-show="show" :class="$vuetify.breakpoint.mdAndUp ? 'pt-9' : 'pt-6'">
        <v-row>
            <v-col class="py-0 mt-1">
                <v-chip
                    v-for="(location, index) in locations"
                    :key="'location' + index"
                    :color="chipConstant.location.color"
                    class="mr-1"
                    text-color="white"
                    ripple
                    small
                    close
                    @click:close="remove(location)"
                >
                    <v-avatar left>
                        <v-icon>{{ chipConstant.location.icon }}</v-icon>
                    </v-avatar>
                    {{ location.value }}
                </v-chip>
            </v-col>
        </v-row>
        <v-row>
            <v-col class="py-0 mt-1">
                <v-chip
                    v-for="(person, index) in persons"
                    :key="'person' + index"
                    :color="chipConstant.person.color"
                    class="mr-1"
                    text-color="white"
                    ripple
                    small
                    close
                    @click:close="remove(person)"
                >
                    <v-avatar left>
                        <v-icon>{{ chipConstant.person.icon }}</v-icon>
                    </v-avatar>
                    {{ person.value }}
                </v-chip>
            </v-col>
        </v-row>
        <v-row>
            <v-col class="py-0 mt-1">
                <v-chip
                    v-for="(item, index) in items"
                    :key="'item' + index"
                    :color="chipConstant.item.color"
                    class="mr-1"
                    text-color="white"
                    ripple
                    small
                    close
                    @click:close="remove(item)"
                >
                    <v-avatar left>
                        <v-icon>{{ chipConstant.item.icon }}</v-icon>
                    </v-avatar>
                    {{ item.value }}
                </v-chip>
            </v-col>
        </v-row>
    </v-container>
</template>
<script>
import { chipConstant } from '@/constant/constant.js';
export default {
    name: 'AppSearchSelector',
    data() {
        return {
            chipConstant,
            show: false,
            locations: [],
            persons: [],
            items: []
        };
    },
    watch: {
        $route() {
            if (_.startsWith(this.$route.path, '/search')) {
                this.show = true;
            } else {
                this.show = false;
            }
        }
    },
    methods: {
        updateSelectedItem(item) {
            switch (item.type) {
            case chipConstant.time.type:
                break;
            case chipConstant.location.type:
                this.locations.push(item);
                break;
            case chipConstant.person.type:
                this.locations.push(item);
                break;
            case chipConstant.item.type:
                this.locations.push(item);
                break;
            case chipConstant.hot.type:
                break;
            case chipConstant.search.type:
                break;
            default:
            }
            this.show = true;
        },

        remove(item) {
            this.$store.state.app.appSearch.remove(item);
        }
    }
};
</script>
