<template>
    <v-menu
        v-model="startDateMenu"
        :close-on-content-click="false"
        transition="scale-transition"
        offset-y
    >
        <template v-slot:activator="{ on }">
            <v-text-field
                v-model="startDate"
                :label="label"
                prepend-icon="event"
                readonly
                v-on="on"
            />
        </template>
        <v-date-picker
            v-model="startDate"
            min="0001-01-01"
            max="2222-12-31"
            first-day-of-week="1"
            locale="zh-cn"
            :day-format="(date) => new Date(date).getDate()"
            @input="startDateMenu = false"
        />
    </v-menu>
</template>

<script>
export default {
    name: 'TimeSliceDateSelector',
    props: {
        label: {
            type: String,
            default: '',
            required: true
        }
    },
    data() {
        return {
            startDateMenu: false,
            startDate: new Date().toISOString().substr(0, 10)
        };
    },
    methods: {
        setDate(value) {
            this.startDate = value;
        },

        getDate() {
            return this.startDate;
        }
    }
};
</script>
