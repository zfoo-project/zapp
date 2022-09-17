<template>
    <div v-scroll="onScroll" class="ma-0 pa-0">
        <v-container v-if="$vuetify.breakpoint.mdAndUp">
            <v-row>
                <v-col cols="10">
                    <v-timeline align-top dense>
                        <template v-for="(timeSlice, index) in timeSliceArray">
                            <v-timeline-item :key="'pc' + index">
                                <template v-slot:icon>
                                    <v-menu offset-y :close-on-content-click="false">
                                        <template v-slot:activator="{ on }">
                                            <v-avatar v-ripple v-on="on" @click="avatarClick(timeSlice.userInfo.id)">
                                                <v-img :src="simpleAvatar(timeSlice.userInfo)" />
                                            </v-avatar>
                                        </template>
                                        <time-avatar :user-profile="userProfile" />
                                    </v-menu>
                                </template>

                                <strong>
                                    <router-link :to="'/user/info/' + timeSlice.userInfo.id" :title="timeSlice.userInfo.name" style="text-decoration: none; color: inherit">
                                        {{ timeSlice.userInfo.name }}
                                    </router-link>
                                </strong>

                                <v-list-item-content>
                                    <v-list-item-subtitle>
                                        {{ timeSlice.userInfo.signature }}
                                    </v-list-item-subtitle>
                                </v-list-item-content>

                                <v-row>
                                    <v-col class="pb-0 mb-0">
                                        <time-chip :id="timeSlice.id" :name="toLicense(timeSlice.type)" :chip-type="chipConstant.license" />
                                        <time-chip :id="timeSlice.id" :name="timeSlice.start + ' / ' + timeSlice.end" :chip-type="chipConstant.time" />
                                    </v-col>
                                </v-row>

                                <v-row>
                                    <v-col class="pt-0 mt-0">
                                        <template v-for="(location) in timeSlice.locations">
                                            <time-chip :id="location.key" :key="'location' + location.key" :name="location.value" :chip-type="chipConstant.location" />
                                        </template>
                                        <template v-for="(person) in timeSlice.persons">
                                            <time-chip :id="person.key" :key="'person' + person.key" :name="person.value" :chip-type="chipConstant.person" />
                                        </template>
                                        <template v-for="(item) in timeSlice.items">
                                            <time-chip :id="item.key" :key="'item' + item.key" :name="item.value" :chip-type="chipConstant.item" />
                                        </template>
                                    </v-col>
                                </v-row>
                                <time-link-album :albums="timeSlice.albums" />
                                <v-row v-if="timeSlice.key">
                                    <v-col>
                                        <time-key :time-key="timeSlice.key" :edit-model="false" />
                                    </v-col>
                                </v-row>
                                <time-content v-if="!isBlank(timeSlice.content)" :id="timeSlice.id" :content="timeSlice.content" />
                                <time-img-gallery :images="timeSlice.images" :img-tag="true" :img-alt="timeSlice.content" />
                                <time-video
                                    v-if="timeSlice.video"
                                    :id="'myVideo_' + timeSlice.id + '_' + index"
                                    ref="myVideos"
                                    :poster="timeSlice.video.poster"
                                    :url="timeSlice.video.url"
                                    :play-callback="playCallback"
                                />
                                <time-operation v-if="operation" :time-slice="timeSlice" />
                            </v-timeline-item>
                        </template>
                    </v-timeline>
                </v-col>
            </v-row>
        </v-container>

        <v-container v-else>
            <template v-for="(timeSlice, index) in timeSliceArray">
                <v-container :key="'mobile' + index">
                    <v-row>
                        <v-list-item class="py-0 my-0 pl-3">
                            <v-list-item-avatar>
                                <v-menu offset-y :close-on-content-click="false">
                                    <template v-slot:activator="{ on }">
                                        <v-avatar v-ripple v-on="on" @click="avatarClick(timeSlice.userInfo.id)">
                                            <v-img :src="simpleAvatar(timeSlice.userInfo)" />
                                        </v-avatar>
                                    </template>
                                    <time-avatar :user-profile="userProfile" />
                                </v-menu>
                            </v-list-item-avatar>

                            <v-list-item-content>
                                <v-list-item-title>
                                    <router-link :to="'/user/info/' + timeSlice.userInfo.id" :title="timeSlice.userInfo.name" style="text-decoration: none; color: inherit">
                                        {{ timeSlice.userInfo.name }}
                                    </router-link>
                                </v-list-item-title>
                                <v-list-item-subtitle>
                                    {{ timeSlice.userInfo.signature }}
                                </v-list-item-subtitle>
                            </v-list-item-content>
                        </v-list-item>
                    </v-row>
                    <v-row>
                        <v-col class="py-0 my-0">
                            <time-chip :id="timeSlice.id" :name="toLicense(timeSlice.type)" :chip-type="chipConstant.license" />
                            <time-chip :id="timeSlice.id" :name="timeSlice.start + ' / ' + timeSlice.end" :chip-type="chipConstant.time" />
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col class="py-0 my-0">
                            <template v-for="(location) in timeSlice.locations">
                                <time-chip :id="location.key" :key="'location' + location.key" :name="location.value" :chip-type="chipConstant.location" />
                            </template>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col class="py-0 my-0">
                            <template v-for="(person) in timeSlice.persons">
                                <time-chip :id="person.key" :key="'person' + person.key" :name="person.value" :chip-type="chipConstant.person" />
                            </template>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col class="py-0 my-0">
                            <template v-for="(item) in timeSlice.items">
                                <time-chip :id="item.key" :key="'item' + item.key" :name="item.value" :chip-type="chipConstant.item" />
                            </template>
                        </v-col>
                    </v-row>

                    <time-link-album :albums="timeSlice.albums" />
                    <v-row v-if="timeSlice.key">
                        <v-col>
                            <time-key :time-key="timeSlice.key" :edit-model="false" />
                        </v-col>
                    </v-row>
                    <time-content v-if="!isBlank(timeSlice.content)" :id="timeSlice.id" :content="timeSlice.content" />
                    <time-img-gallery :images="timeSlice.images" :img-tag="true" :img-alt="timeSlice.content" />
                    <time-video
                        v-if="timeSlice.video"
                        :id="'myVideo' + index"
                        ref="myVideos"
                        :poster="timeSlice.video.poster"
                        :url="timeSlice.video.url"
                        :play-callback="playCallback"
                    />
                    <time-operation v-if="operation" :time-slice="timeSlice" />
                    <v-divider />
                </v-container>
            </template>
        </v-container>
        <v-footer v-if="noData" padless>
            <v-spacer />
            {{ $t('ts.bottomDisplay') }}
            <v-spacer />
        </v-footer>
        <v-progress-linear v-else-if="loading" color="amber" indeterminate />
        <v-divider v-else-if="divider" />
    </div>
</template>

<script>
import { mapState } from 'vuex';
import { appConstant, chipConstant } from '@/constant/constant.js';
import { getUserInfoApi } from '@/apiHttp/userApi.js';
import { isBlank } from '@/util/stringUtils.js';
import { toSimpleAvatarUrl } from '@/util/fileUtils.js';
export default {
    name: 'TimeSlice',
    components: {
        TimeChip: () => import('@/component/time/TimeChip.vue'),
        TimeAvatar: () => import('@/component/time/TimeAvatar.vue'),
        TimeKey: () => import('@/component/time/TimeKey.vue'),
        TimeContent: () => import('@/component/time/TimeContent.vue'),
        TimeLinkAlbum: () => import('@/component/time/TimeLinkAlbum.vue'),
        TimeImgGallery: () => import('@/component/time/TimeImgGallery.vue'),
        TimeVideo: () => import('@/component/time/TimeVideo.vue'),
        TimeOperation: () => import('@/component/time/TimeOperation.vue')
    },
    props: {
        timeSliceArray: {
            type: Array,
            default: () => []
        },
        bottomCallback: {
            type: Function,
            default: null,
            required: false
        },
        divider: {
            type: Boolean,
            default: true,
            required: false
        },
        operation: {
            type: Boolean,
            default: true,
            required: false
        }
    },
    data() {
        return {
            chipConstant,
            isBlank,
            loading: false,
            noData: false,
            userProfile: {}
        };
    },

    computed: {
        ...mapState('user', ['loggedIn', 'id', 'avatar'])
    },

    methods: {
        playCallback(myPlayer) {
            this.$refs.myVideos.forEach(it => {
                const player = it.player;
                if (_.isNil(player)) {
                    return;
                }
                if (!_.isEqual(myPlayer.id(), it.player.id())) {
                    it.player.pause();
                }
            });
        },

        toLicense(type) {
            const license = _.values(appConstant.licenseEnum).find(it => it.type === type);
            return _.isNil(license) ? appConstant.licenseEnum.cc0.text : license.text;
        },

        endLoading() {
            this.loading = false;
        },

        setNoData() {
            this.noData = true;
        },

        onScroll(e) {
            if (_.isNil(this.bottomCallback)) {
                return;
            }
            // 如果v-scroll没有绑定任何对象，默认绑定window
            const doc = e.target;

            // tolerance调成0 : 10，在pc环境有概率不触发刷新操作
            // const tolerance = this.$vuetify.breakpoint.mdAndUp ? 0 : 10;
            const tolerance = this.$vuetify.breakpoint.mdAndUp ? 5 : 10;

            // scrollTop 滚动条滚动时，距离顶部的距离
            const scrollTop = doc.documentElement.scrollTop || doc.body.scrollTop;
            // windowHeight 可视区的高度
            const windowHeight = doc.documentElement.clientHeight || doc.body.clientHeight;
            // scrollHeight 滚动条的总高度
            const scrollHeight = doc.documentElement.scrollHeight || doc.body.scrollHeight;
            // 滚动条到底部的条件
            if (scrollTop + windowHeight + tolerance >= scrollHeight && this.loading === false && this.noData === false) {
                // 加载数据
                this.loading = true;
                this.bottomCallback(this);
                // console.log('onScroll:' + (scrollTop + windowHeight) + '->' + scrollHeight);
            }
        },

        avatarClick(id) {
            if (!_.isNil(this.userProfile) && id === this.userProfile.id) {
                return;
            }
            if (this.loggedIn) {
                if (this.id === id) {
                    this.userProfile = this.$store.getters['user/myProfile'];
                    return;
                }
            }
            getUserInfoApi(Array.of(id)).then(response => {
                const data = response.data;
                const findResult = _.find(data, (it) => id === it.id);
                if (_.isNil(findResult)) {
                    return;
                }
                this.userProfile = findResult;
            });
        },

        simpleAvatar(userInfo) {
            if (this.id === userInfo.id) {
                return toSimpleAvatarUrl(this.avatar);
            } else {
                return toSimpleAvatarUrl(userInfo.avatar);
            }
        }
    }
};
</script>
