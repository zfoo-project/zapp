<template>
    <v-chip
        class="pl-0 mr-1"
        :color="chipType.color"
        text-color="white"
        x-small
        ripple
        :to="chipGoTo()"
        :title="name"
    >
        <v-avatar class="pl-3" left>
            <v-icon x-small>{{ chipType.icon }}</v-icon>
        </v-avatar>
        {{ name }}
    </v-chip>
</template>

<script>
import { appConstant, chipConstant } from '@/constant/constant.js';
export default {
    name: 'TimeChip',
    props: {
        id: {
            type: String,
            default: '',
            required: true
        },
        name: {
            type: String,
            default: '',
            required: true
        },
        chipType: {
            type: Object,
            default: () => {},
            required: true
        }
    },
    methods: {
        chipGoTo() {
            let goto = '';
            switch (this.chipType.type) {
            case chipConstant.license.type:
                if (_.isEqual(this.name, appConstant.licenseEnum.zfoo.text)) {
                    goto = '/terms';
                } else {
                    goto = '/license';
                }
                break;
            case chipConstant.time.type:
                goto = '/time/' + this.id;
                break;
            case chipConstant.location.type:
                goto = '/location/' + this.id;
                break;
            case chipConstant.person.type:
                goto = '/person/' + this.id;
                break;
            case chipConstant.item.type:
                goto = '/item/' + this.id;
                break;
            case chipConstant.word.type:
                goto = '/word/' + this.id;
                break;
            case chipConstant.category.type:
                goto = '/category/' + this.id;
                break;
            default:
            }
            return goto;
        }
    }
};
</script>
