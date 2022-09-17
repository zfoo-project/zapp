<template>
    <v-tooltip bottom>
        <template v-slot:activator="{ on }">
            <v-icon v-on="on">{{ icon }}</v-icon>
        </template>
        <span>{{ tooltip }}</span>
    </v-tooltip>
</template>

<script>
export default {

    name: 'IconWithTooltip',

    props: {
        icon: {
            type: String,
            default: '',
            required: true
        },
        tooltip: {
            type: String,
            default: '',
            required: true
        }
    }
};
</script>
