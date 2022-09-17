<template>
    <v-combobox
        v-model="persons"
        :items="personTags"
        :search-input.sync="personHintValue"
        return-object
        hide-selected
        :label="$t(chipConstant.person.text)"
        :prepend-icon="chipConstant.person.icon"
        multiple
        persistent-hint
        small-chips
        clearable
    />
</template>

<script>
import { personHintApi } from '@/apiHttp/wordApi.js';
import { chipConstant } from '@/constant/constant.js';
import { isBlank } from '@/util/stringUtils.js';
export default {
    name: 'TimeSlicePersonSelector',

    data() {
        return {
            chipConstant,
            persons: [],
            personTags: [],
            personHintValue: ''
        };
    },

    watch: {
        personHintValue() {
            if (isBlank(this.personHintValue)) {
                return;
            }
            personHintApi(this.personHintValue).then(response => {
                const data = response.data;
                if (_.isEmpty(data)) {
                    return;
                }
                const tags = new Set();
                data.forEach(it => tags.add({
                    value: it.key,
                    text: it.value
                }));
                this.personTags = Array.from(tags);
            });
        },

        persons() {
            this.personHintValue = '';

            // 防止用户输入不存在的人
            const lastPerson = _.last(this.persons);
            if (_.isEmpty(lastPerson)) {
                return;
            }

            if (_.findIndex(this.personTags, (it) => _.isEqual(it.value, lastPerson.value)) < 0) {
                this.persons.pop();
            }
        }
    },

    methods: {
        setPersons(value) {
            this.persons = value;
        },

        setPersonTags(value) {
            this.personTags = value;
        },

        getPersons() {
            return this.persons;
        }
    }
};
</script>
