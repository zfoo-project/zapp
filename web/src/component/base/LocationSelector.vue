<template>
    <v-combobox
        v-model="selectedLocations"
        return-object
        hide-selected
        :label="this.$t(chipConstant.location.text)"
        :prepend-icon="chipConstant.location.icon"
        multiple
        persistent-hint
        small-chips
        clearable
    >
        <template v-slot:no-data>
            <v-treeview
                v-model="locations"
                dense
                selectable
                hoverable
                open-on-click
                return-object
                selected-color="primary"
                :items="cnLocations"
            />
        </template>
    </v-combobox>
</template>

<script>
import cnLocations from '@/i18n/cn/locations.js';
import { chipConstant } from '@/constant/constant.js';

export default {
    name: 'LocationSelector',
    data() {
        return {
            chipConstant,
            cnLocations,
            locations: []
        };
    },

    computed: {
        selectedLocations: {
            get() {
                return this.toSimpleLocations().map(it => it.name);
            },
            set(val) {
                this.locations = [];
            }
        }
    },

    methods: {
        setLocations(locations) {
            this.locations = locations.map(it => {
                return {
                    id: it.key,
                    name: it.value
                };
            });
        },

        toSimpleLocations() {
            if (_.isEmpty(this.locations)) {
                return [];
            }
            const result = [];
            // 省的Map
            const middleMap = new Map();
            // 区的Map
            const rightMap = new Map();
            for (const location of this.locations) {
                const idStr = _.toString(location.id);

                // 长度为3代表一个国家
                if (_.isEqual(idStr.length, 3)) {
                    result.push(location);
                    continue;
                }

                // 长度为5代表一个省
                if (_.isEqual(idStr.length, 5)) {
                    const keyMiddle = Number.parseInt(idStr.substring(0, 3));
                    let list = middleMap.get(keyMiddle);
                    if (_.isEmpty(list)) {
                        list = [];
                        middleMap.set(keyMiddle, list);
                    }
                    list.push(location);
                    continue;
                }

                // 长度为9代表一个区
                if (_.isEqual(idStr.length, 7)) {
                    const keyRight = Number.parseInt(idStr.substring(0, 5));
                    let list = rightMap.get(keyRight);
                    if (_.isEmpty(list)) {
                        list = [];
                        rightMap.set(keyRight, list);
                    }
                    list.push(location);
                    continue;
                }
            }

            // 查看省份是否包括所有的区，如果包括全部的区则以省份做单位
            const middleLocation = [];
            for (const location of cnLocations) {
                if (!_.isEmpty(location.children)) {
                    for (const child of location.children) {
                        if (!_.isEmpty(child.children)) {
                            middleLocation.push(child);
                        }
                    }
                }
            }

            for (const [key, value] of rightMap) {
                const location = _.find(middleLocation, (it) => it.id === key);
                const flag = _.every(location.children, (it) => _.findIndex(value, (it0) => it.id === it0.id) >= 0);
                if (flag) {
                    const key = Number.parseInt(_.toString(location.id).substring(0, 3));
                    let list = middleMap.get(key);
                    if (_.isEmpty(list)) {
                        list = [];
                        middleMap.set(key, list);
                    }
                    list.push(location);
                } else {
                    value.forEach((it) => result.push(it));
                }
            }

            // 查看国家是否包括所有的省份，如果包括全部的省份则以国家做单位
            for (const [key, value] of middleMap) {
                const location = _.find(cnLocations, (it) => it.id === key);
                const flag = _.every(location.children, (it) => _.findIndex(value, (it0) => it.id === it0.id) >= 0);
                if (flag) {
                    result.push(location);
                } else {
                    value.forEach((it) => result.push(it));
                }
            }
            return result;
        }
    }
};
</script>
