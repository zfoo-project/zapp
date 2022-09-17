<template>
    <div>
        <template v-if="lazyMode && !init">
            <v-row>
                <v-col :md="playerMdCols">
                    <v-img
                        v-ripple
                        :src="toVideoPoster(poster)"
                        :lazy-src="toVideoPoster(poster)"
                        contain
                        style="cursor:pointer;"
                        @click="doInitPlayer()"
                        @load="perfectImage()"
                    >
                        <template v-slot:placeholder>
                            <v-row class="fill-height ma-0" align="center" justify="center">
                                <v-progress-circular indeterminate color="grey lighten-5" />
                            </v-row>
                        </template>
                        <v-row class="fill-height ma-0" align="center" justify="center">
                            <v-btn large depressed style="background-color: rgba(43, 51, 63, 0.5);" @click.stop="doInitPlayer()">
                                <v-icon large color="white">mdi-play</v-icon>
                            </v-btn>
                        </v-row>
                    </v-img>
                </v-col>
            </v-row>
        </template>
        <template v-else>
            <v-row
                v-touch="{
                    left: () => swipe('left'),
                    right: () => swipe('right'),
                    up: () => swipe('up'),
                    down: () => swipe('down')
                }"
            >
                <v-col :md="playerMdCols">
                    <video
                        :id="id"
                        :poster="toVideoPoster(poster)"
                        class="video-js vjs-big-play-centered vjs-fluid"
                        x5-video-player-type="h5-page"
                        playsinline
                        webkit-playsinline
                    >
                        <source :src="url" type="video/mp4">
                    </video>
                </v-col>
            </v-row>
        </template>
    </div>
</template>

<script>
import { toVideoPoster } from '@/util/fileUtils.js';
export default {
    name: 'TimeVideo',
    props: {
        id: {
            type: String,
            default: '',
            required: true
        },
        lazyMode: {
            type: Boolean,
            default: true,
            required: false
        },
        poster: {
            type: String,
            default: '',
            required: false
        },
        url: {
            type: String,
            default: '',
            required: true
        },
        cols: {
            type: Number,
            default: -1,
            required: false
        },
        playCallback: {
            type: Function,
            default: () => () => {
            },
            required: false
        },
        pauseCallback: {
            type: Function,
            default: () => () => {
            },
            required: false
        },
        loadedmetadataCallback: {
            type: Function,
            default: () => () => {
            },
            required: false
        }
    },
    data() {
        return {
            player: null,
            playerMdCols: 8,
            init: false,
            toVideoPoster
        };
    },
    watch: {
        poster: function() {
            if (_.isNil(this.player)) {
                return;
            }
            this.player.poster(toVideoPoster(this.poster));
        },
        url: function() {
            if (_.isNil(this.player)) {
                return;
            }
            this.player.src({
                type: 'video/mp4',
                src: this.url
            });
            this.player.load(this.url);
        }
    },

    mounted() {
        if (this.lazyMode) {
            return;
        }
        this.initPlayer();
    },

    beforeDestroy() {
        if (!_.isNil(this.player)) {
            this.player.dispose();
            this.player = null;
        }
    },
    methods: {
        initPlayer() {
            this.init = true;

            if (this.cols > 0) {
                this.playerMdCols = this.cols;
            }
            const _this = this;
            const flag = this.$vuetify.breakpoint.mdAndUp;
            const option = flag
                ? [{
                    name: 'currentTimeDisplay'
                },
                {
                    name: 'progressControl'
                },
                {
                    name: 'durationDisplay'
                }]
                // 下面是手机选项设置
                : [{
                    name: 'playToggle'
                },
                {
                    name: 'progressControl'
                },
                {
                    name: 'fullscreenToggle'
                }];
            // 初始化视频方法 循环列表获取每个视频的id
            const myPlayer = this.$video(this.id, {
                // 设置播放器语言
                language: 'zh-CN',
                // 自动播放属性,muted:静音播放
                autoplay: true,
                // 建议浏览器是否应在<video>加载元素后立即开始下载视频数据。
                preload: 'auto',
                // 设置视频播放器的显示宽度（以像素为单位）
                // width: '800px',
                // 设置视频播放器的显示高度（以像素为单位）
                // height: '400px',
                // 封面图
                // poster: this.poster,
                // 是否显示底部控制栏
                controls: true,
                controlBar: {
                    children: option
                }
            });

            myPlayer.on('pause', function() {
                // console.log('pause---视频暂停----');
                _this.pauseCallback(myPlayer);
            });
            myPlayer.on('play', function() {
                // console.log('play---视频播放----');
                _this.playCallback(myPlayer);
            });
            myPlayer.on('loadedmetadata', function() {
                // console.log('loadedmetadata---视频源数据加载完成----');
                // console.log('视频高度：' + myPlayer.videoHeight());
                // console.log('视频宽度' + myPlayer.videoWidth());
                // console.log('时长' + myPlayer.duration());
                if (myPlayer.videoWidth() <= myPlayer.videoHeight()) {
                    // 如果设置了cols，则优先使用cols
                    if (_this.cols <= 0) {
                        _this.playerMdCols = 4;
                    }
                    // 如果是12的话，可能是在聊天中，聊天如果是手机视频12会导致超过屏幕，这里做一下修正
                    if (_this.cols === 12) {
                        _this.playerMdCols = 9;
                    }
                }
                _this.loadedmetadataCallback(myPlayer);
                setTimeout(() => {
                    myPlayer.play();
                }, 100);
            });
            myPlayer.on('userinactive', function() {
                // 当控制条隐藏时触发这个事件
            });
            myPlayer.on('useractive', function() {
                // 当控制条显示时触发这个事件
            });
            this.player = myPlayer;
        },

        doInitPlayer() {
            this.init = true;
            this.$nextTick(() => {
                this.initPlayer();
            });
        },

        swipe(direction) {
            // console.log(direction);
            if (_.isEqual('right', direction)) {
                this.player.currentTime(this.player.currentTime() + 5);
            } else if (_.isEqual('left', direction)) {
                this.player.currentTime(this.player.currentTime() - 5);
            }
        },

        perfectImage() {
            const _this = this;
            new Promise(function(resolve, reject) {
                const imgObj = new Image();
                imgObj.onload = function() {
                    resolve('load image success');
                    if (imgObj.width < imgObj.height) {
                        // 如果设置了cols，则优先使用cols
                        if (_this.cols <= 0) {
                            _this.playerMdCols = 4;
                        }
                        // 如果是12的话，可能是在聊天中，聊天如果是手机视频12会导致超过屏幕，这里做一下修正
                        if (_this.cols === 12) {
                            _this.playerMdCols = 9;
                        }
                    }
                };
                imgObj.src = toVideoPoster(_this.poster);
            });
        }
    }
};
</script>
