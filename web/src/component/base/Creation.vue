<template>
    <v-container>
        <v-row dense>
            <v-col md="4" cols="12">
                <v-row dense>
                    <v-col md="8">
                        <license-selector ref="licenseSelector" />
                    </v-col>
                </v-row>
                <v-row dense>
                    <v-col md="4">
                        <time-slice-date-selector ref="tsStartDateSelector" :label="$t('ts.date.startDate')" />
                    </v-col>
                    <v-col md="4">
                        <time-slice-time-selector ref="tsStartTimeSelector" :label="$t('ts.date.startTme')" :init-time="'09:00:00'" />
                    </v-col>
                </v-row>
                <v-row dense>
                    <v-col md="4">
                        <time-slice-date-selector ref="tsEndDateSelector" :label="$t('ts.date.endDate')" />
                    </v-col>
                    <v-col md="4">
                        <time-slice-time-selector ref="tsEndTimeSelector" :label="$t('ts.date.endTme')" :init-time="'17:00:00'" />
                    </v-col>
                </v-row>

                <v-row dense>
                    <v-col md="8">
                        <location-selector ref="locationSelector" />
                    </v-col>
                </v-row>

                <v-row dense>
                    <v-col md="8">
                        <time-slice-person-selector ref="tsPersonSelector" />
                    </v-col>
                </v-row>
                <v-row dense>
                    <v-col md="8">
                        <time-slice-item-selector ref="tsItemSelector" />
                    </v-col>
                </v-row>
            </v-col>
            <v-col md="8" cols="12">

                <v-row>
                    <v-col>
                        <time-key v-if="showTimeKeyEdit" ref="timeKeyEdit" :time-key="key" />
                    </v-col>
                </v-row>

                <v-row>
                    <v-col>
                        <v-textarea
                            v-model="content"
                            :label="$t('ts.contentHint')"
                            type="text"
                            auto-grow
                            full-width
                            outlined
                            rows="8"
                        />
                    </v-col>
                </v-row>

                <v-row dense>
                    <v-col md="11" cols="9">
                        <v-file-input
                            id="imgInput"
                            ref="imgInput"
                            v-model="imgFiles"
                            style="display: none"
                            multiple
                            accept="image/*"
                            @change="imgFilesChange()"
                        />
                        <v-file-input
                            id="videoInput"
                            ref="videoInput"
                            v-model="videoFile"
                            style="display: none"
                            accept="video/*"
                            @change="videoFilesChange()"
                        />
                        <v-file-input
                            id="videoMultipleInput"
                            ref="videoMultipleInput"
                            v-model="videoMultipleFiles"
                            style="display: none"
                            multiple
                            accept="video/*"
                            @change="videoMultipleFilesChange()"
                        />
                        <v-file-input
                            id="videoPosterInput"
                            ref="videoPosterInput"
                            v-model="videoPosterFile"
                            style="display: none"
                            accept="image/*"
                            @change="videoPosterFileChange()"
                        />
                        <v-btn-toggle rounded>
                            <template v-for="(item) in timeSliceEle">
                                <v-tooltip v-if="item.value !== 2 || appConstant.createTimeMode.create.type === createTimeMode" :key="item.value" bottom>
                                    <template v-slot:activator="{ on }">
                                        <v-btn @click="trigger(item.value)" v-on="on">
                                            <v-icon>{{ item.icon }}</v-icon>
                                        </v-btn>
                                    </template>
                                    <span>{{ $t(item.tooltip) }}</span>
                                </v-tooltip>
                            </template>
                        </v-btn-toggle>
                    </v-col>
                    <v-col md="1" cols="3">
                        <v-btn v-if="appConstant.createTimeMode.create.type === createTimeMode" color="primary" class="my-1 py-1" @click="publish()">{{ $t('ts.publish') }}</v-btn>
                        <v-btn v-if="appConstant.createTimeMode.recommit.type === createTimeMode" color="primary" class="my-1 py-1" @click="publish()">{{ $t('ts.recommit') }}</v-btn>
                        <v-btn v-if="appConstant.createTimeMode.edit.type === createTimeMode" color="primary" class="my-1 py-1" @click="publish()">{{ $t('ts.editAndCommit') }}</v-btn>
                    </v-col>
                </v-row>

                <v-row dense>
                    <v-col>
                        <time-link-album :albums="albums" />
                    </v-col>
                </v-row>

                <v-row v-if="showTimeLinkAlbumEdit" dense>
                    <v-col cols="12">
                        <time-link-album :albums="timeLinkAlbumEditArray" :close-icon="true" :close-callback="timeLinkCloseFunction" />
                    </v-col>
                    <v-col md="6" sm="12">
                        <v-card>
                            <v-card-text>
                                <v-text-field v-model="timeLinkAlbumEditTitle" :label="$t('ts.timeLinkAlbum.linkGroupName')" prepend-icon="mdi-format-title" type="text" />
                                <v-text-field v-model="timeLinkAlbumEditValue" :label="$t('ts.timeLinkAlbum.linkGroupInput')" prepend-icon="mdi-timer-outline" type="text">
                                    <template v-slot:append-outer>
                                        <v-btn fab small color="primary" @click="addTimeLink()">
                                            <v-icon>mdi-plus</v-icon>
                                        </v-btn>
                                    </template>
                                </v-text-field>
                            </v-card-text>
                            <v-card-actions>
                                <v-spacer />
                                <v-btn color="primary" @click="cancelTimeLinkAlbum()">
                                    {{ $t('ts.timeLinkAlbum.cancelLinkAlbum') }}
                                </v-btn>
                                <v-btn color="primary" @click="saveTimeLinkAlbum()">
                                    {{ $t('ts.timeLinkAlbum.confirmLinkAlbum') }}
                                </v-btn>
                            </v-card-actions>
                        </v-card>
                    </v-col>
                </v-row>

                <v-row v-if="fileUploadShow" dense>
                    <v-col>
                        <v-progress-circular
                            :rotate="-90"
                            :size="100"
                            :width="15"
                            :value="fileUploadValue"
                            color="amber"
                        >
                            {{ fileUploadValue }}
                        </v-progress-circular>
                    </v-col>
                </v-row>

                <v-row dense>
                    <v-col>
                        <time-img-gallery :images="images" img-icon="mdi-close-circle-outline" :img-icon-function="imgIconClick" :img-tag="false" />
                    </v-col>
                </v-row>

                <v-row v-if="!_.isNil(video)" dense>
                    <v-col>
                        <time-video
                            id="myVideo"
                            :poster="video.poster"
                            :url="video.url"
                            :lazy-mode="false"
                            :loadedmetadata-callback="loadedmetadataCallback"
                        />
                    </v-col>
                </v-row>
                <template v-if="videoPostersShow">
                    <v-row dense>
                        <v-col>
                            {{ $t('ts.videoPosterSelector') }}
                            <v-btn icon color="primary" @click="uploadVideoPosterFile(-1)">
                                <v-icon>mdi-cloud-upload-outline</v-icon>
                            </v-btn>
                        </v-col>
                    </v-row>
                    <v-row dense>
                        <v-col>
                            <time-img-gallery
                                :images="videoPosters"
                                img-icon="mdi-check-circle-outline"
                                :img-icon-function="videoPosterClick"
                                :compress="false"
                                :img-tag="false"
                            />
                        </v-col>
                    </v-row>
                </template>

                <v-row v-if="!_.isEmpty(videoMultiple)" dense>
                    <v-col>
                        {{ $t('ts.videoMultipleUpload') }}
                    </v-col>
                </v-row>
                <template v-for="(videoMul, index) in videoMultiple">
                    <v-row v-if="!_.isNil(videoMul)" :key="index" dense>
                        <v-col>
                            <time-video
                                :id="'myVideo' + index"
                                :poster="videoMul.poster"
                                :url="videoMul.url"
                                :lazy-mode="false"
                                :loadedmetadata-callback="loadedmetadataCallbackVideoMul"
                            />
                        </v-col>
                    </v-row>
                    <v-row v-if="videoMul.videoPostersShow" :key="'upload' + index" dense>
                        <v-col>
                            {{ $t('ts.videoPosterSelector') }}
                            <v-btn icon color="primary" @click="uploadVideoPosterFile(index)">
                                <v-icon>mdi-cloud-upload-outline</v-icon>
                            </v-btn>
                        </v-col>
                    </v-row>
                    <v-row v-if="videoMul.videoPostersShow" :key="'poster' + index" dense>
                        <v-col>
                            <time-img-gallery
                                :images="videoMul.videoPosters"
                                img-icon="mdi-check-circle-outline"
                                :img-icon-function="videoMulPosterClick"
                                :compress="false"
                                :img-tag="false"
                            />
                        </v-col>
                    </v-row>
                </template>
            </v-col>
        </v-row>
        <v-row>
            <v-col cols="12">
                <app-footer />
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import { policyApi, uploadFileToOssApi } from '@/apiHttp/ossApi.js';
import { checkTimeLinksApi } from '@/apiHttp/timeApi.js';
import { simpleError } from '@/util/noticeUtils.js';
import { checkImgUrl } from '@/util/fileUtils.js';
import { appConstant, chipConstant } from '@/constant/constant.js';
import { mapState } from 'vuex';
import { isBlank } from '@/util/stringUtils';

export default {
    name: 'Creation',
    components: {
        LicenseSelector: () => import('@/component/time/creation/LicenseSelector.vue'),
        TimeSliceDateSelector: () => import('@/component/time/creation/TimeSliceDateSelector.vue'),
        TimeSliceTimeSelector: () => import('@/component/time/creation/TimeSliceTimeSelector.vue'),
        TimeSlicePersonSelector: () => import('@/component/time/creation/TimeSlicePersonSelector.vue'),
        TimeSliceItemSelector: () => import('@/component/time/creation/TimeSliceItemSelector.vue'),
        TimeLinkAlbum: () => import('@/component/time/TimeLinkAlbum.vue'),
        TimeImgGallery: () => import('@/component/time/TimeImgGallery.vue'),
        TimeVideo: () => import('@/component/time/TimeVideo.vue'),
        TimeKey: () => import('@/component/time/TimeKey.vue'),
        LocationSelector: () => import('@/component/base/LocationSelector.vue'),
        AppFooter: () => import('@/component/base/AppFooter.vue')
    },

    props: {
        createTimeMode: {
            type: Number,
            default: 0,
            required: false
        },
        callback: {
            type: Function,
            default: () => () => {},
            required: false
        },
        createCallback: {
            type: Function,
            default: null,
            required: false
        }
    },

    data() {
        return {
            appConstant,
            chipConstant,

            content: '',

            // 时间片链接专辑相关
            albums: [],
            showTimeLinkAlbumEdit: false,
            timeLinkAlbumEditTitle: '',
            timeLinkAlbumEditValue: '',
            timeLinkAlbumEditArray: [],
            timeSliceEle: [
                {
                    icon: 'mdi-image-outline',
                    value: 0,
                    tooltip: 'ts.timeSliceEle.image'
                },
                {
                    icon: 'mdi-video-outline',
                    value: 1,
                    tooltip: 'ts.timeSliceEle.video'
                },
                {
                    icon: 'mdi-video-plus-outline',
                    value: 2,
                    tooltip: 'ts.timeSliceEle.videoAlbum'
                },
                {
                    icon: 'mdi-key-outline',
                    value: 3,
                    tooltip: 'ts.timeSliceEle.key'
                },
                {
                    icon: 'mdi-vector-link',
                    value: 4,
                    tooltip: 'ts.timeSliceEle.link'
                }
            ],

            // 上传进度
            fileUploadShow: false,
            fileUploadValue: 0,

            // 图片相关
            imgFiles: null,
            images: [],
            imgOssPolicy: {
                expire: 0
            },

            // 单个视频相关
            videoFile: null,
            video: null,
            videoPosters: [],
            videoPostersShow: false,

            // 视频专辑
            videoMultipleFiles: null,
            videoMultiple: [],

            //
            videoOssPolicy: {
                expire: 0
            },

            // 视频封面
            videoPosterFile: null,
            videoPosterIndex: -1,
            videoPosterOssPolicy: {
                expire: 0
            },

            // 键值对相关
            showTimeKeyEdit: false,
            key: null
        };
    },

    computed: {
        ...mapState('user', ['loggedIn'])
    },

    watch: {
        timeLinkAlbumEditTitle() {
            const newTimeSliceArray = Array.from(this.timeLinkAlbumEditArray);
            newTimeSliceArray[0].album = this.timeLinkAlbumEditTitle;
            this.timeLinkAlbumEditArray = newTimeSliceArray;
        }
    },

    mounted() {
        this.initCreation();

        this.initDefaultTimeLink();
    },

    methods: {
        initCreation() {
            setTimeout(() => {
                const timeSlice = this.$route.params.timeSlice;
                if (_.isNil(timeSlice)) {
                    return;
                }
                if (_.isEmpty(timeSlice)) {
                    return;
                }
                if (_.isNil(this.$refs.licenseSelector) || _.isNil(this.$refs.tsStartDateSelector) || _.isNil(this.$refs.tsStartTimeSelector) ||
                _.isNil(this.$refs.tsEndDateSelector) || _.isNil(this.$refs.tsEndTimeSelector) || _.isNil(this.$refs.locationSelector) ||
                _.isNil(this.$refs.tsPersonSelector) || _.isNil(this.$refs.tsItemSelector) || _.isNil(this.$refs.tsItemSelector)) {
                    this.initCreation();
                    return;
                }
                this.$refs.licenseSelector.setLicenseType(timeSlice.type);

                let timeSplits = timeSlice.start.split(' ');
                this.$refs.tsStartDateSelector.setDate(timeSplits[0]);
                this.$refs.tsStartTimeSelector.setTime(timeSplits[1]);

                timeSplits = timeSlice.end.split(' ');
                this.$refs.tsEndDateSelector.setDate(timeSplits[0]);
                this.$refs.tsEndTimeSelector.setTime(timeSplits[1]);

                this.$refs.locationSelector.setLocations(timeSlice.locations);

                if (!_.isEmpty(timeSlice.persons)) {
                    const persons = timeSlice.persons.map((it) => {
                        return {
                            value: it.key,
                            text: it.value
                        };
                    });
                    this.$refs.tsPersonSelector.setPersons(Array.from(persons));
                    this.$refs.tsPersonSelector.setPersonTags(Array.from(persons));
                }

                if (!_.isEmpty(timeSlice.items)) {
                    const items = timeSlice.items.map((it) => {
                        return {
                            value: it.key,
                            text: it.value
                        };
                    });
                    this.$refs.tsItemSelector.setItems(Array.from(items));
                    this.$refs.tsItemSelector.setItemTags(Array.from(items));
                }

                if (!isBlank(timeSlice.content)) {
                    this.content = timeSlice.content;
                }

                if (!_.isEmpty(timeSlice.images)) {
                    this.images = timeSlice.images;
                }
                if (!_.isEmpty(timeSlice.video)) {
                    this.video = timeSlice.video;
                }
                if (!_.isEmpty(timeSlice.key)) {
                    this.key = timeSlice.key;
                    this.showTimeKeyEdit = true;
                }
                if (!_.isEmpty(timeSlice.albums)) {
                    this.albums = timeSlice.albums;
                }
            }, 500);
        },

        initDefaultTimeLink() {
            const groupName = this.$t('ts.timeLinkAlbum.linkGroupInputHint');
            this.timeLinkAlbumEditArray = [
                {
                    album: groupName,
                    links: []
                }
            ];
            this.timeLinkAlbumEditTitle = '';
            this.timeLinkAlbumEditValue = '';
        },

        imgIconClick(imgLink) {
            const newImgLinks = Array.from(this.images);
            _.remove(newImgLinks, (it) => _.isEqual(it, imgLink));
            this.images = newImgLinks;
        },

        videoPosterClick(poster) {
            this.$set(this.video, 'poster', poster);
            this.videoPostersShow = false;
        },

        videoMulPosterClick(poster) {
            const videoMul = _.find(this.videoMultiple, it => _.startsWith(poster, it.url));
            videoMul.poster = poster;
            videoMul.videoPostersShow = false;

            const index = _.findIndex(this.videoMultiple, it => _.startsWith(poster, it.url));
            this.$set(this.videoMultiple, index, videoMul);
        },

        videoMulPosterOutClick(poster, index) {
            const videoMul = this.videoMultiple[index];
            videoMul.poster = poster;
            videoMul.videoPostersShow = false;
            this.$set(this.videoMultiple, index, videoMul);
        },

        loadedmetadataCallback(myPlayer) {
            // 获取视频总的时间，单位秒，向下取整
            const duration = (_.isNaN(myPlayer.duration()) || _.isNil(myPlayer.duration()))
                ? 9 : _.floor(myPlayer.duration());

            const array = [];
            // 将视频平均分为9个时间段，然后截帧
            for (let i = 1; i <= 9; i++) {
                const timestamp = _.floor(duration / i * 1000);
                const snapshot = this.video.url + '?x-oss-process=video/snapshot,t_' + timestamp + ',f_jpg,m_fast';
                array.push(snapshot);
            }
            _.reverse(array);

            const firstPoster = array[0];
            checkImgUrl(firstPoster).then((resolve) => {
                this.$set(this.video, 'poster', firstPoster);
                this.videoPosters = array;
                this.videoPostersShow = true;
            },
            (reject) => {
                // do nothing
            });
        },

        loadedmetadataCallbackVideoMul(myPlayer) {
            // 获取视频总的时间，单位秒，向下取整
            const duration = (_.isNaN(myPlayer.duration()) || _.isNil(myPlayer.duration()))
                ? 9 : _.floor(myPlayer.duration());

            const videoMul = _.find(this.videoMultiple, it => _.isEqual(it.url, myPlayer.src()));
            const array = [];
            // 将视频平均分为9个时间段，然后截帧
            for (let i = 1; i <= 9; i++) {
                const timestamp = _.floor(duration / i * 1000);
                const snapshot = videoMul.url + '?x-oss-process=video/snapshot,t_' + timestamp + ',f_jpg,m_fast';
                array.push(snapshot);
            }
            _.reverse(array);

            const firstPoster = array[0];
            checkImgUrl(firstPoster).then((resolve) => {
                if (_.isNil(videoMul)) {
                    simpleError(this.$t('notice.uploadVideoMulError'));
                    return;
                }
                videoMul.poster = firstPoster;
                videoMul.videoPosters = array;
                videoMul.videoPostersShow = true;

                const index = _.findIndex(this.videoMultiple, it => _.isEqual(it.url, myPlayer.src()));
                this.$set(this.videoMultiple, index, videoMul);
            },
            (reject) => {
                // do nothing
            });
        },

        imgFilesChange() {
            if (_.isEmpty(this.imgFiles)) {
                return;
            }

            if (this.images.length + this.imgFiles.length > appConstant.imgLimit) {
                this.$refs.imgInput.clearableCallback();
                simpleError(this.$t('notice.imgLimitError'));
                return;
            }

            this.video = null;
            this.videoMultiple = [];

            // 图片可能会批量上传，这里设置5秒的容错时间
            policyApi(appConstant.ossPolicyEnum.image.type).then(response => {
                const data = response.data;
                this.imgOssPolicy = data;

                const imgUploadFiles = Array.from(this.imgFiles);
                this.uploadImages(imgUploadFiles, data);
                this.$refs.imgInput.clearableCallback();
            });
        },

        uploadImages(imgUploadFiles, ossPolicy) {
            if (_.isEmpty(imgUploadFiles)) {
                return;
            }
            const file = imgUploadFiles.shift();

            this.fileUploadShow = true;
            this.fileUploadValue = 0;
            const upload = uploadFileToOssApi(file, ossPolicy, (fileName, complete) => {
                this.fileUploadValue = complete;
            });

            const key = upload.key;
            upload.callback.then(response => {
                this.images.push(this.imgOssPolicy.host + '/' + key);
                this.videoPostersShow = false;

                this.fileUploadShow = false;
                this.fileUploadValue = 0;

                this.uploadImages(imgUploadFiles, ossPolicy);
            }).catch(error => {
                console.log(error);
                simpleError(this.$t('notice.uploadImageSizeLimitError'));
            });
        },

        videoFilesChange() {
            if (_.isNil(this.videoFile)) {
                return;
            }
            this.images = [];
            this.videoMultiple = [];
            const file = this.videoFile;

            policyApi(appConstant.ossPolicyEnum.video.type).then(response => {
                const data = response.data;
                this.videoOssPolicy = data;

                this.fileUploadShow = true;
                this.fileUploadValue = 0;
                const upload = uploadFileToOssApi(file, this.videoOssPolicy, (fileName, complete) => {
                    this.fileUploadValue = complete;
                });

                const key = upload.key;
                upload.callback.then(response => {
                    this.video = {
                        url: this.videoOssPolicy.host + '/' + key
                    };
                    this.fileUploadShow = false;
                    this.fileUploadValue = 0;
                }).catch(error => {
                    console.log(error);
                    simpleError(this.$t('notice.uploadVideoSizeLimitError'));
                });

                this.$refs.videoInput.clearableCallback();
            });
        },

        videoMultipleFilesChange() {
            if (_.isEmpty(this.videoMultipleFiles)) {
                return;
            }
            this.images = [];
            this.video = null;

            policyApi(appConstant.ossPolicyEnum.video.type).then(response => {
                const data = response.data;
                this.videoOssPolicy = data;

                const videoUploadFiles = Array.from(this.videoMultipleFiles);
                this.uploadVideoMultiple(videoUploadFiles, data);
                this.$refs.videoMultipleInput.clearableCallback();
            });
        },

        uploadVideoMultiple(videoUploadFiles, ossPolicy) {
            if (_.isEmpty(videoUploadFiles)) {
                return;
            }
            const file = videoUploadFiles.shift();

            this.fileUploadShow = true;
            this.fileUploadValue = 0;
            const upload = uploadFileToOssApi(file, ossPolicy, (fileName, complete) => {
                this.fileUploadValue = complete;
            });

            const key = upload.key;
            upload.callback.then(response => {
                this.videoMultiple.push({
                    url: this.videoOssPolicy.host + '/' + key
                });
                this.videoPostersShow = false;

                this.fileUploadShow = false;
                this.fileUploadValue = 0;

                this.uploadVideoMultiple(videoUploadFiles, ossPolicy);
            }).catch(error => {
                console.log(error);
                simpleError(this.$t('notice.uploadVideoSizeLimitError'));
            });
        },

        uploadVideoPosterFile(index) {
            this.videoPosterIndex = index;
            this.$refs.videoPosterInput.$refs.input.click();
        },

        videoPosterFileChange() {
            if (_.isNil(this.videoPosterFile)) {
                return;
            }
            const file = this.videoPosterFile;

            policyApi(appConstant.ossPolicyEnum.videoPoster.type).then(response => {
                const data = response.data;
                this.videoPosterOssPolicy = data;

                const upload = uploadFileToOssApi(file, this.videoPosterOssPolicy);
                const key = upload.key;
                upload.callback.then(response => {
                    if (this.videoPosterIndex < 0) {
                        this.videoPosterClick(this.videoPosterOssPolicy.host + '/' + key);
                    } else {
                        this.videoMulPosterOutClick(this.videoPosterOssPolicy.host + '/' + key, this.videoPosterIndex);
                    }
                }).catch(error => {
                    console.log(error);
                    simpleError(this.$t('notice.uploadImageSizeLimitError'));
                });

                this.$refs.videoPosterInput.clearableCallback();
            });
        },

        trigger(val) {
            switch (val) {
            case 0:
                // 上传的图片不能大于9张
                if (this.images.length > appConstant.imgLimit) {
                    simpleError(this.$t('notice.imgLimitError'));
                    return;
                }
                this.$refs.imgInput.$refs.input.click();
                break;
            case 1:
                this.$refs.videoInput.$refs.input.click();
                break;
            case 2:
                this.$refs.videoMultipleInput.$refs.input.click();
                break;
            case 3:
                this.showTimeKeyEdit = !this.showTimeKeyEdit;
                break;
            case 4:
                this.showTimeLinkAlbumEdit = !this.showTimeLinkAlbumEdit;
                if (!this.showTimeLinkAlbumEdit) {
                    this.initDefaultTimeLink();
                }
                break;
            }
        },

        timeLinkCloseFunction(timeLink) {
            if (_.isEmpty(timeLink)) {
                return;
            }
            if (_.isEmpty(this.timeLinkAlbumEditArray)) {
                return;
            }
            const newTimeSliceArray = Array.from(this.timeLinkAlbumEditArray);
            _.remove(newTimeSliceArray[0].links, (it) => _.isEqual(timeLink, it));
            this.timeLinkAlbumEditArray = newTimeSliceArray;
        },

        addTimeLink() {
            if (_.isEmpty(this.timeLinkAlbumEditValue)) {
                return;
            }
            const newTimeSliceArray = Array.from(this.timeLinkAlbumEditArray);
            if (_.isEmpty(newTimeSliceArray)) {
                newTimeSliceArray.push({
                    links: []
                });
            }

            if (_.findIndex(newTimeSliceArray[0].links, (it) => _.isEqual(it, this.timeLinkAlbumEditValue)) >= 0) {
                simpleError(this.$t('notice.duplicateTsError'));
                return;
            }
            newTimeSliceArray[0].links.push(this.timeLinkAlbumEditValue);
            this.timeLinkAlbumEditArray = newTimeSliceArray;
            this.timeLinkAlbumEditValue = '';
        },

        cancelTimeLinkAlbum() {
            this.initDefaultTimeLink();
            this.showTimeLinkAlbumEdit = false;
        },

        saveTimeLinkAlbum() {
            if (_.isEmpty(this.timeLinkAlbumEditArray)) {
                return;
            }

            const timeLinkAlbum = this.timeLinkAlbumEditArray[0];
            if (_.isEmpty(timeLinkAlbum)) {
                return;
            }

            if (_.isEmpty(timeLinkAlbum.album) || _.isEmpty(this.timeLinkAlbumEditTitle)) {
                simpleError(this.$t('notice.linkAlbumNameEmptyError'));
                return;
            }

            if (_.isEmpty(timeLinkAlbum.links)) {
                simpleError(this.$t('notice.linkEmptyError'));
                return;
            }

            checkTimeLinksApi(timeLinkAlbum.links).then(response => {
                this.albums.push(timeLinkAlbum);
                this.initDefaultTimeLink();
                this.showTimeLinkAlbumEdit = false;
            });
        },

        publish() {
            if (!this.loggedIn) {
                simpleError(this.$t('notice.notSignInError'));
                return;
            }

            if (this.showTimeLinkAlbumEdit) {
                simpleError(this.$t('notice.linkAlbumEditError'));
                return;
            }

            const type = this.$refs.licenseSelector.getLicenseType();
            const start = this.$refs.tsStartDateSelector.getDate() + ' ' + this.$refs.tsStartTimeSelector.getTime();
            const end = this.$refs.tsEndDateSelector.getDate() + ' ' + this.$refs.tsEndTimeSelector.getTime();
            const locations = this.$refs.locationSelector.toSimpleLocations().map(it => it.id);
            const persons = this.$refs.tsPersonSelector.getPersons().map(it => it.value);
            const items = this.$refs.tsItemSelector.getItems().filter(it => _.isInteger(_.toNumber(it.value))).map(it => it.value);
            const notExistItems = this.$refs.tsItemSelector.getItems().filter(it => !_.isInteger(_.toNumber(it.value))).map(it => it);
            const content = this.content;
            const images = this.images;
            let video = this.video;
            const key = _.isEmpty(this.$refs.timeKeyEdit) ? null : this.$refs.timeKeyEdit.toTimeKey();
            const albums = this.albums;


            if (new Date(start).getTime() > new Date(end).getTime()) {
                simpleError(this.$t('notice.tsStartTimeAfterEndTimeError'));
                return;
            }

            if (!_.isEmpty(key)) {
                const headers = key.headers;
                const rows = key.rows;
                if (_.isEmpty(headers)) {
                    simpleError(this.$t('notice.timeKeyColEmptyError'));
                    return;
                }
                if (_.isEmpty(rows)) {
                    simpleError(this.$t('notice.timeKeyRowEmptyError'));
                    return;
                }
            }

            if (!_.isEmpty(video)) {
                if (_.isEmpty(video.url)) {
                    video = null;
                }
            }

            if (!_.isNil(this.createCallback)) {
                if (_.isEmpty(this.videoMultiple)) {
                    this.createCallback(Array.of({
                        type: type,
                        start: start,
                        end: end,
                        locations: locations,
                        persons: persons,
                        items: items,
                        notExistItems: notExistItems,
                        content: content,
                        images: images,
                        video: video,
                        key: key,
                        albums: albums
                    }));
                } else {
                    this.createCallback(this.videoMultiple.map(it => {
                        return {
                            type: type,
                            start: start,
                            end: end,
                            locations: locations,
                            persons: persons,
                            items: items,
                            notExistItems: notExistItems,
                            content: content,
                            images: images,
                            video: {
                                poster: it.poster,
                                url: it.url
                            },
                            key: key,
                            albums: albums
                        };
                    }));
                }
            } else {
                this.callback(type, start, end, locations, persons, items, notExistItems, content, images, video, key, albums);
            }
        }
    }
};
</script>
