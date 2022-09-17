<template>
    <v-row>
        <report-dialog ref="reportDialog" :option="currentOption" :callback="report" />

        <v-dialog v-model="deleteDialog" max-width="290">
            <v-card>
                <v-card-title class="headline">{{ $t('common.alert') }}</v-card-title>
                <v-card-text>
                    {{ $t('ts.deleteAlert') }}
                </v-card-text>

                <v-card-actions>
                    <v-spacer />
                    <v-btn color="primary" @click="deleteTsClick()">
                        {{ $t('common.confirm') }}
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <v-dialog v-model="imageDownloadDialog" max-width="500">
            <v-card>
                <v-card-title class="headline">{{ $t('ts.operation.imageDownloadDialogDesc') }}</v-card-title>
                <v-card-text>
                    <v-radio-group v-model="imageQuality">
                        <template v-slot:label>
                            <div><strong>{{ $t('ts.operation.imageDownloadQualityDesc') }}</strong></div>
                        </template>
                        <v-radio :label="$t(appConstant.imageQualityEnum.origin.description)" :value="appConstant.imageQualityEnum.origin.type" />
                        <v-radio :label="$t(appConstant.imageQualityEnum.high.description)" :value="appConstant.imageQualityEnum.high.type" />
                        <v-radio :label="$t(appConstant.imageQualityEnum.middle.description)" :value="appConstant.imageQualityEnum.middle.type" />
                        <v-radio :label="$t(appConstant.imageQualityEnum.low.description)" :value="appConstant.imageQualityEnum.low.type" />
                    </v-radio-group>
                </v-card-text>
                <v-card-text>
                    <div class="subtitle-1 font-weight-black">{{ $t('ts.operation.imageDownloadSelectDesc') }}</div>
                    <v-container>
                        <v-row>
                            <v-col v-for="(image, index) in timeSlice.images" :key="index" cols="4">
                                <v-checkbox v-model="selectedImages" :label="_.toString(index + 1)" :value="index" />
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card-text>

                <v-card-actions>
                    <v-spacer />
                    <v-btn color="primary" @click="doDownload()">
                        {{ $t('ts.download') }}
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <v-col>
            <v-btn-toggle>
                <v-tooltip bottom>
                    <template v-slot:activator="{ on }">
                        <v-btn @click="shareClick($event)" v-on="on">
                            <v-icon>mdi-share-variant</v-icon>
                        </v-btn>
                    </template>
                    <span>{{ $t('ts.share') }}</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <template v-slot:activator="{ on }">
                        <v-btn @click="loveClick()" v-on="on">
                            <v-icon>mdi-thumb-up-outline</v-icon>
                        </v-btn>
                    </template>
                    <span><span class="error--text">{{ loveNum }}</span>{{ $t('ts.love') }}</span>
                </v-tooltip>
                <v-tooltip v-if="starFlag" bottom>
                    <template v-slot:activator="{ on }">
                        <v-btn @click="starClick()" v-on="on">
                            <v-icon>mdi-star</v-icon>
                        </v-btn>
                    </template>
                    <span>{{ $t('ts.unstar') }}</span>
                </v-tooltip>
                <v-tooltip v-if="id !== timeSlice.userInfo.id && !starFlag" bottom>
                    <template v-slot:activator="{ on }">
                        <v-btn @click="starClick()" v-on="on">
                            <v-icon>mdi-star-outline</v-icon>
                        </v-btn>
                    </template>
                    <span>{{ $t('ts.star') }}</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <template v-slot:activator="{ on }">
                        <v-btn @click="chatClick()" v-on="on">
                            <v-icon>mdi-chat-outline</v-icon>
                        </v-btn>
                    </template>
                    <span>{{ $t('user.chat.info') }}</span>
                </v-tooltip>

                <v-tooltip bottom>
                    <template v-slot:activator="{ on }">
                        <v-btn @click="downloadClick()" v-on="on">
                            <v-icon v-if="downloading">mdi-loading mdi-spin</v-icon>
                            <v-icon v-else>mdi-arrow-down-bold-outline</v-icon>
                        </v-btn>
                    </template>
                    <span v-if="downloading">{{ $t('ts.downloading') }}</span>
                    <span v-else>{{ $t('ts.download') }}</span>
                </v-tooltip>

                <v-menu right offset-x>
                    <template v-slot:activator="{ on: menu }">
                        <v-tooltip bottom>
                            <template v-slot:activator="{ on: tooltip }">
                                <v-btn v-on="{ ...tooltip, ...menu }">
                                    <v-icon>mdi-menu</v-icon>
                                </v-btn>
                            </template>
                            <span>{{ $t('ts.more') }}</span>
                        </v-tooltip>
                    </template>
                    <v-list>
                        <v-list-item
                            v-for="(option, index) in moreOptions"
                            :key="'item' + index"
                            @click="optionClick(option.id)"
                        >
                            <v-list-item-title>{{ option.name }}</v-list-item-title>
                        </v-list-item>
                    </v-list>
                </v-menu>

                <v-menu v-if="loggedIn && id === timeSlice.userInfo.id" right offset-x>
                    <template v-slot:activator="{ on: menu }">
                        <v-tooltip bottom>
                            <template v-slot:activator="{ on: tooltip }">
                                <v-btn v-on="{ ...tooltip, ...menu }">
                                    <v-icon>mdi-pickaxe</v-icon>
                                </v-btn>
                            </template>
                            <span>{{ $t('common.edit') }}</span>
                        </v-tooltip>
                    </template>
                    <v-list>
                        <template v-if="loggedIn && id === timeSlice.userInfo.id">
                            <v-list-item @click="editTsClick()">
                                <v-list-item-title>{{ $t('ts.edit') }}</v-list-item-title>
                                <v-list-item-icon>
                                    <v-icon color="primary">mdi-vector-polyline-edit</v-icon>
                                </v-list-item-icon>
                            </v-list-item>
                            <v-list-item @click="deleteDialog = !deleteDialog">
                                <v-list-item-title>{{ $t('ts.delete') }}</v-list-item-title>
                                <v-list-item-icon>
                                    <v-icon color="error">mdi-delete-forever-outline</v-icon>
                                </v-list-item-icon>
                            </v-list-item>
                        </template>
                    </v-list>
                </v-menu>
            </v-btn-toggle>
        </v-col>
    </v-row>
</template>

<script>
import { download } from '@/util/fileUtils.js';
import clipboard from '@/util/clipboardUtils.js';
import reports from '@/i18n/cn/reports.js';
import { appConstant } from '@/constant/constant.js';
import { reportApi, deleteTimeSliceApi } from '@/apiHttp/timeApi.js';
import { loveApi, downloadApi } from '@/apiHttp/coinApi.js';
import { starTimeSliceApi } from '@/apiHttp/userApi.js';
import { simpleSuccess, simpleError } from '@/util/noticeUtils.js';
import { mapMutations, mapState } from 'vuex';
import { sendPacket } from '@/util/websocketUtils.js';

import JoinGroupByUserIdRequest from '@/jsProtocol/group/member/JoinGroupByUserIdRequest.js';

export default {
    name: 'TimeOperation',

    components: {
        ReportDialog: () => import('@/component/app/ReportDialog.vue')
    },

    props: {
        timeSlice: {
            type: Object,
            default: () => {},
            required: true
        }
    },

    data() {
        return {
            appConstant,

            deleteDialog: false,
            imageDownloadDialog: false,

            imageQuality: appConstant.imageQualityEnum.origin.type,
            selectedImages: [],

            loveNum: '',

            downloading: false,

            moreOptions: reports,
            currentOption: {}
        };
    },

    computed: {
        ...mapState('user', ['loggedIn', 'id', 'stars']),
        ...mapState('group', ['groups']),
        ...mapState('app', ['group']),

        starFlag: function() {
            const tsId = this.timeSlice.id;
            if (_.findIndex(this.stars, (it) => it === tsId) >= 0) {
                return true;
            } else {
                return false;
            }
        }
    },

    mounted() {
        this.init();
    },

    methods: {
        ...mapMutations('user', ['setCoin']),

        init() {
            this.loveNum = this.timeSlice.love;
            this.loading = false;
            this.files = null;
        },

        shareClick(event) {
            clipboard(appConstant.homeUrl + '/time/' + this.timeSlice.id, event);
        },

        loveClick() {
            if (!this.loggedIn) {
                simpleError(this.$t('notice.notSignInError'));
                return;
            }
            const id = this.timeSlice.id;
            if (id <= 0) {
                simpleError(this.$t('notice.timeSliceError'));
                return;
            }
            loveApi(id).then(response => {
                const data = response.data;
                this.loveNum = data.key;
                this.setCoin(data.value);
                simpleSuccess(this.$t('notice.loveSuccess'));
            });
        },

        starClick() {
            const id = this.timeSlice.id;
            if (id <= 0) {
                simpleError(this.$t('notice.timeSliceError'));
                return;
            }
            starTimeSliceApi(id).then(response => {
                this.$store.commit('user/updateStars', id);
            });
        },

        chatClick() {
            if (!this.loggedIn) {
                simpleError(this.$t('notice.notSignInError'));
                return;
            }

            this.group.setGroupDrawer(true);
            if (_.findIndex(this.groups, it => it.id === '-' + this.timeSlice.userInfo.id) >= 0) {
                return;
            }

            sendPacket(new JoinGroupByUserIdRequest(this.timeSlice.userInfo.id));
        },

        downloadClick() {
            if (!this.loggedIn) {
                simpleError(this.$t('notice.notSignInError'));
                return;
            }

            if (this.timeSlice.type === appConstant.licenseEnum.zfoo.type) {
                simpleError(this.$t('notice.zfooLicenseDownloadError'));
                return;
            }

            if (_.isEmpty(this.timeSlice.images) && _.isNil(this.timeSlice.video)) {
                simpleError(this.$t('notice.downloadEmptyError'));
                return;
            }

            const id = this.timeSlice.id;
            if (id <= 0) {
                simpleError(this.$t('notice.timeSliceError'));
                return;
            }

            if (this.downloading === true) {
                return;
            }

            if (!_.isEmpty(this.timeSlice.images)) {
                this.imageDownloadDialog = true;
                return;
            }

            this.doDownload();
        },

        doDownload() {
            if (!_.isEmpty(this.timeSlice.images) && _.isEmpty(this.selectedImages)) {
                simpleError(this.$t('notice.timeSliceImageEmptyError'));
                return;
            }

            const id = this.timeSlice.id;
            downloadApi(id, this.imageQuality, this.selectedImages).then(response => {
                const data = response.data;
                if (this.timeSlice.userInfo.id !== this.id) {
                    this.loveNum = data.love;
                    this.setCoin(data.coin);
                }

                const downloadUrls = data.downloadUrls;
                if (_.isEmpty(downloadUrls)) {
                    simpleError(this.$t('notice.timeSliceDownloadError'));
                    return;
                }

                this.downloading = true;
                Array.from(downloadUrls).forEach(it => download(it, null, null, null, () => {
                    this.downloading = false;
                    if (!_.isEmpty(this.timeSlice.images)) {
                        simpleSuccess(this.$t('notice.timeSliceImageDownloadSuccess'));
                    } else {
                        simpleSuccess(this.$t('notice.timeSliceVideoDownloadSuccess'));
                    }
                }));
                this.imageDownloadDialog = false;
            });
        },

        editTsClick() {
            this.$router.push({
                name: 'EditTime',
                params: {
                    editId: this.timeSlice.id,
                    timeSlice: this.timeSlice
                }
            });
        },

        deleteTsClick() {
            if (_.isNil(this.timeSlice)) {
                return;
            }
            deleteTimeSliceApi(Array.of(this.timeSlice.id)).then(response => {
                simpleSuccess(this.$t('notice.timeSliceDeleteSuccess'));

                const myProfile = this.$store.state.app.myProfile;
                if (_.isNil(myProfile)) {
                    return;
                }
                myProfile.deleteTimeSlice(this.timeSlice.id);

                this.deleteDialog = false;
            });
        },

        optionClick(optionId) {
            if (!this.loggedIn) {
                simpleError(this.$t('notice.notSignInError'));
                return;
            }

            this.currentOption = _.find(this.moreOptions, it => _.isEqual(it.id, optionId));
            this.$refs.reportDialog.showDialog(true);
        },

        report(optionType, selectedItems, reportContent, fileLinks) {
            const timeSliceId = this.timeSlice.id;

            if (timeSliceId <= 0) {
                simpleError(this.$t('notice.timeSliceError'));
                return;
            }

            reportApi(timeSliceId, optionType, selectedItems, reportContent, fileLinks).then(response => {
                this.$refs.reportDialog.showDialog(false);
                simpleSuccess(this.$t('notice.reportSuccess'));
            });
        }
    }
};
</script>
