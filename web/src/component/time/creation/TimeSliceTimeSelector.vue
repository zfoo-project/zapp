<template>
    <v-menu
        ref="startMenu"
        v-model="timeMenu"
        :close-on-content-click="false"
        :return-value.sync="time"
        transition="scale-transition"
        offset-y
    >
        <template v-slot:activator="{ on }">
            <v-text-field
                v-model="time"
                :label="label"
                prepend-icon="access_time"
                readonly
                v-on="on"
            />
        </template>
        <v-time-picker
            v-if="timeMenu"
            v-model="time"
            format="24hr"
            use-seconds
            full-width
            @click:second="$refs.startMenu.save(time)"
        />
    </v-menu>
</template>

<script>
export default {
    name: 'TimeSliceTimeSelector',
    props: {
        label: {
            type: String,
            default: '',
            required: true
        },
        initTime: {
            type: String,
            default: '',
            required: true
        }
    },
    data() {
        return {
            timeMenu: false,
            time: this.initTime
        };
    },
    methods: {
        setTime(value) {
            this.time = value;
        },
        getTime() {
            return this.time;
        }
    }
};
</script>
