export const appConstant = {
    appVersion: 'v1.0.0',
    appName: 'zfoo',
    // appName: 'aaa',
    appIcon: 'mdi-alpha-z-circle',

    homeUrl: process.env.VUE_APP_HTTP,
    unionUrl: 'https://union.zfoo.com',
    imgUrl: 'https://img.zfoo.com',
    videoUrl: 'https://video.zfoo.com',
    reportUrl: 'https://report.zfoo.com',

    testVideoUrl: 'https://report.zfoo.com/test/video/',

    unionAvatarUrl: 'https://union.zfoo.com/avatar',
    groupAvatarUrl: 'https://group.zfoo.com/avatar',

    weChatLoginUrl: 'https://open.weixin.qq.com/connect/qrconnect?appid=wxf97338646edfa7bb&redirect_uri=https%3a%2f%2fwww.zfoo.com%2fapi%2fweChat%2fsignIn&response_type=code&scope=snsapi_login',
    weChatServiceLoginUrl: 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxbbef399f260454ac&redirect_uri=http%3a%2f%2fwww.zfoo.com%2fapi%2fweChat%2fservice%2fsignIn&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect',
    weChatBindUrl: 'https://open.weixin.qq.com/connect/qrconnect?appid=wxf97338646edfa7bb&redirect_uri=https%3a%2f%2fwww.zfoo.com%2fapi%2fweChat%2fbind&response_type=code&scope=snsapi_login',
    weiBoLoginUrl: 'https://api.weibo.com/oauth2/authorize?client_id=1761789659&response_type=code&redirect_uri=https://www.zfoo.com/api/weiBo/signIn',
    weiBoBindUrl: 'https://api.weibo.com/oauth2/authorize?client_id=1761789659&response_type=code&redirect_uri=https://www.zfoo.com/api/weiBo/bind',

    defaultAvatar: 'https://union.zfoo.com/asset/default_avatar.gif',
    defaultBackground: 'https://union.zfoo.com/asset/default_user_background.jpg',
    defaultGroupBackground: 'https://union.zfoo.com/asset/default_group_background.jpg',
    defaultUrlEmpty: 'https://union.zfoo.com/asset/default_url_empty.jpg',
    defaultVideoPoster: 'https://union.zfoo.com/asset/default_video_poster.png',

    defaultLocation: 'https://union.zfoo.com/asset/default_location.jpg',
    defaultItem: 'https://union.zfoo.com/asset/default_item.jpg',
    defaultPerson: 'https://union.zfoo.com/asset/default_person.jpg',
    defaultWord: 'https://union.zfoo.com/asset/default_word.jpg',
    defaultCategory: 'https://union.zfoo.com/asset/default_category.jpg',


    licenseCcCondition: 'https://union.zfoo.com/asset/license_cc_condition.png',
    licenseCcItem: 'https://union.zfoo.com/asset/license_cc_item.png',

    calendar: {
        today: 'ts.calendar.today',
        month: 'ts.calendar.month',
        week: 'ts.calendar.week',
        day: 'ts.calendar.day',
        dimension: 'ts.calendar.dimension'
    },

    timeSliceLinks: {
        icon: 'mdi-vector-link',
        value: 'user.timeSliceLinks'
    },

    timeKeyLimit: 52,

    imgLimit: 9,
    dimensionLimit: 10,
    dimensionDefault: 5,

    userListPageSize: 10,

    userNameMinLength: 4,
    userNameMaxLength: 16,
    userSignatureMaxLength: 100,

    seoTitleLengthLimit: 28,
    seoDescriptionLengthLimit: 120,

    feed: {
        pageSize: 10,
        userPageSize: 10
    },

    otherOptionId: 900,

    createTimeMode: {
        create: {
            type: 0
        },
        recommit: {
            type: 1
        },
        edit: {
            type: 2
        }
    },

    adminAuthEnum: {
        noAuth: {
            type: 0,
            text: 'admin.adminAuthEnum.noAuth'
        },
        baseAuth: {
            type: 3,
            text: 'admin.adminAuthEnum.baseAuth'
        },
        middleAuth: {
            type: 6,
            text: 'admin.adminAuthEnum.middleAuth'
        },
        highAuth: {
            type: 9,
            text: 'admin.adminAuthEnum.highAuth'
        },
        specialAuth: {
            type: 12,
            text: 'admin.adminAuthEnum.specialAuth'
        },
        adminAuth: {
            type: 15,
            text: 'admin.adminAuthEnum.adminAuth'
        }
    },

    licenseEnum: {
        cc0: {
            type: 0,
            text: 'CC0',
            description: 'CC0（无条件开放所有权）'
        },
        cc1: {
            type: 1,
            text: 'CC1',
            description: 'CC1（有条件开放所有权）'
        },
        cc2: {
            type: 2,
            text: 'CC2',
            description: 'CC2（有条件开放所有权）'
        },
        cc6: {
            type: 6,
            text: 'CC6',
            description: 'CC6（保留所有权）'
        },
        cc7: {
            type: 7,
            text: 'CC7',
            description: 'CC7（保留所有权）'
        },
        cc8: {
            type: 8,
            text: 'CC8',
            description: 'CC8（保留所有权）'
        },
        cc9: {
            type: 9,
            text: 'CC9',
            description: 'CC9（保留所有权）'
        },
        zfoo: {
            type: 100,
            text: 'ZFOO',
            description: 'ZFOO协议（保留所有权）'
        }
    },

    imageQualityEnum: {
        origin: {
            type: 0,
            description: 'ts.operation.imageQualityOrigin'
        },
        high: {
            type: 2,
            description: 'ts.operation.imageQualityHigh'
        },
        middle: {
            type: 4,
            description: 'ts.operation.imageQualityMiddle'
        },
        low: {
            type: 6,
            description: 'ts.operation.imageQualityLow'
        }
    },

    ossPolicyEnum: {
        avatar: {
            type: 0,
            prefix: 'https://union.zfoo.com/avatar'
        },
        background: {
            type: 1,
            prefix: 'https://union.zfoo.com/background'
        },
        image: {
            type: 2,
            prefix: 'https://img.zfoo.com/img'
        },
        video: {
            type: 3,
            prefix: 'https://video.zfoo.com'
        },
        videoPoster: {
            type: 4,
            prefix: 'https://video.zfoo.com/poster'
        },
        report: {
            type: 10,
            prefix: 'https://report.zfoo.com'
        },
        wordBackground: {
            type: 15,
            prefix: 'https://word.zfoo.com/background'
        },
        chatImage: {
            type: 20,
            prefix: 'https://chat.zfoo.com/img'
        },
        chatVideo: {
            type: 21,
            prefix: 'https://chat.zfoo.com/video'
        },
        groupAvatar: {
            type: 30,
            prefix: 'https://group.zfoo.com/avatar'
        },
        groupBackground: {
            type: 31,
            prefix: 'https://group.zfoo.com/background'
        },
        groupImage: {
            type: 35,
            prefix: 'https://group.zfoo.com/img'
        },
        groupVideo: {
            type: 36,
            prefix: 'https://group.zfoo.com/video'
        }
    }
};

export const groupConstant = {
    inviteUrl: 'https://www.zfoo.com/invite/',
    invitePath: '/invite/',
    newGroupAuthName: 'group.auth.newGroupAuthName',
    newGroupAuthColor: '',
    groupAuthDefaultId: '1',

    friendRecentChatMessageTime: 259200000,
    channelMaxMessageNum: 100,

    channel: {
        main: 'main'
    },

    messageEnum: {
        text: {
            type: 0,
            icon: 'mdi-message-text-outline'
        },
        audio: {
            type: 1
        },
        image: {
            type: 2,
            icon: 'mdi-message-image-outline'
        },
        video: {
            type: 3,
            icon: 'mdi-message-video'
        },
        file: {
            type: 4,
            icon: 'mdi-file-question-outline'
        },
        emotion: {
            type: 7,
            icon: 'mdi-emoticon-cool-outline'
        },
        gif: {
            type: 8,
            icon: 'mdi-gif'
        },
        location: {
            type: 10
        },
        userProfile: {
            type: 20
        }
    },

    textMessageEnum: {
        text: {
            type: 0
        },
        space: {
            type: 1
        },
        ls: {
            type: 2
        },
        emotion: {
            type: 3
        },
        http: {
            type: 4
        }
    },

    inviteExpireEnum: {
        expireUnlimited: {
            type: 0,
            value: 0,
            text: 'group.inviteExpire.expireUnlimited'
        },
        expireHalfHour: {
            type: 1,
            value: 1,
            text: 'group.inviteExpire.expireHalfHour'
        },
        expireOneHour: {
            type: 2,
            value: 5,
            text: 'group.inviteExpire.expireOneHour'
        },
        expireSixHour: {
            type: 3,
            value: 10,
            text: 'group.inviteExpire.expireSixHour'
        },
        expireTwelveHour: {
            type: 4,
            value: 25,
            text: 'group.inviteExpire.expireTwelveHour'
        },
        expireOneDay: {
            type: 5,
            value: 50,
            text: 'group.inviteExpire.expireOneDay'
        }
    },
    inviteCountEnum: {
        countUnlimited: {
            type: 0,
            value: 0,
            text: 'group.inviteCount.countUnlimited'
        },
        count1: {
            type: 1,
            value: 1,
            text: 'group.inviteCount.count1'
        },
        count5: {
            type: 2,
            value: 5,
            text: 'group.inviteCount.count5'
        },
        count10: {
            type: 3,
            value: 10,
            text: 'group.inviteCount.count10'
        },
        count25: {
            type: 4,
            value: 25,
            text: 'group.inviteCount.count25'
        },
        count50: {
            type: 5,
            value: 50,
            text: 'group.inviteCount.count50'
        },
        count100: {
            type: 6,
            value: 100,
            text: 'group.inviteCount.count100'
        }
    },

    groupAuthEnum: {
        noAuth: {
            type: 0,
            text: 'group.auth.noAuth'
        },
        baseAuth: {
            type: 3,
            text: 'group.auth.baseAuth'
        },
        middleAuth: {
            type: 6,
            text: 'group.auth.middleAuth'
        },
        highAuth: {
            type: 9,
            text: 'group.auth.highAuth'
        },
        specialAuth: {
            type: 12,
            text: 'group.auth.specialAuth'
        },
        adminAuth: {
            type: 15,
            text: 'group.auth.adminAuth'
        }
    },

    channelAuthEnum: {
        noAuth: {
            type: 0,
            text: 'group.auth.noAuth'
        },
        baseAuth: {
            type: 3,
            text: 'group.auth.baseAuth'
        },
        middleAuth: {
            type: 6,
            text: 'group.auth.middleAuth'
        },
        highAuth: {
            type: 9,
            text: 'group.auth.highAuth'
        },
        specialAuth: {
            type: 12,
            text: 'group.auth.specialAuth'
        },
        adminAuth: {
            type: 15,
            text: 'group.auth.adminAuth'
        }
    }
};

export const genderItems = [
    {
        value: 0,
        icon: 'mdi-gender-female',
        color: 'error',
        text: 'user.genderType.female'
    },
    {
        value: 1,
        icon: 'mdi-gender-male',
        color: 'success',
        text: 'user.genderType.male'
    },
    {
        value: 2,
        icon: 'mdi-gender-male-female',
        color: 'amber',
        text: 'user.genderType.maleFemale'
    },
    {
        value: 3,
        icon: 'mdi-gender-non-binary',
        color: 'white',
        text: 'user.genderType.nonBinary'
    },
    {
        value: 4,
        icon: 'mdi-gender-transgender',
        color: 'grey',
        text: 'user.genderType.transgender'
    }
];

export const settingConstant = {
    theme: {
        dark: 0,
        light: 1
    },
    language: {
        cn: {
            type: 0,
            value: 'cn'
        },
        en: {
            type: 1,
            value: 'en'
        }
    }
};


export const chipConstant = {
    license: {
        type: 0,
        icon: 'mdi-license',
        color: 'indigo'
    },
    time: {
        type: 1,
        icon: 'mdi-clock-out',
        color: 'indigo'
    },
    location: {
        type: 2,
        icon: 'mdi-map-marker-outline',
        color: 'yellow darken-4',
        text: 'user.locations'
    },
    person: {
        type: 3,
        icon: 'mdi-vector-point',
        color: 'blue-grey',
        text: 'user.persons'
    },
    item: {
        type: 4,
        icon: 'mdi-tree-outline',
        color: 'teal',
        text: 'user.items'
    },
    hot: {
        type: 5,
        icon: 'trending_up',
        color: 'red lighten-1'
    },
    search: {
        type: 10,
        icon: 'mdi-cloud-search-outline',
        color: 'success'
    },
    word: {
        type: 15,
        icon: 'mdi-lightbulb-on-outline',
        color: 'cyan darken-2'
    },
    category: {
        type: 16,
        icon: 'mdi-umbrella-closed-outline',
        color: 'indigo lighten-1'
    },
    childCategory: {
        type: 17,
        icon: 'mdi-umbrella-closed-outline',
        color: 'indigo lighten-1'
    },
    externalLink: {
        type: 20,
        icon: 'mdi-web',
        color: 'blue darken-2'
    }
};

export const emotions = {
    color: 'yellow darken-3',
    textColor: 'yellow--text text--darken-3',
    normal: [
        [
            'mdi-emoticon-happy-outline',
            'mdi-emoticon-angry-outline',
            'mdi-emoticon-confused-outline',
            'mdi-emoticon-cool-outline',
            'mdi-emoticon-cry-outline',
            'mdi-emoticon-dead-outline',
            'mdi-emoticon-devil-outline',
            'mdi-emoticon-excited-outline',
            'mdi-emoticon-frown-outline',
            'mdi-emoticon-kiss-outline'
        ],
        [
            'mdi-emoticon-lol-outline',
            'mdi-emoticon-neutral-outline',
            'mdi-emoticon-outline',
            'mdi-emoticon-sad-outline',
            'mdi-emoticon-tongue-outline',
            'mdi-emoticon-wink-outline',
            'mdi-emoticon-poop-outline'
        ],
        [
            'mdi-alien-outline',
            'mdi-baby-face-outline',
            'mdi-ninja',
            'mdi-pirate',
            'mdi-skull-outline',
            'mdi-skull-crossbones',
            'mdi-robber',
            'mdi-robot',
            'mdi-space-invaders',
            'mdi-ghost'
        ],
        [
            'mdi-alert-octagram-outline',
            'mdi-rocket',
            'mdi-pistol',
            'mdi-hair-dryer-outline',
            'mdi-feather',
            'mdi-heart-circle-outline',
            'mdi-heart-outline',
            'mdi-help-circle-outline',
            'mdi-music',
            'mdi-key-outline'
        ],
        [
            'mdi-weather-cloudy',
            'mdi-weather-cloudy-alert',
            'mdi-weather-cloudy-arrow-right',
            'mdi-weather-hail',
            'mdi-weather-fog',
            'mdi-weather-lightning',
            'mdi-weather-lightning-rainy',
            'mdi-weather-night',
            'mdi-weather-night-partly-cloudy',
            'mdi-weather-partly-cloudy'
        ],
        [
            'mdi-weather-partly-lightning',
            'mdi-weather-partly-rainy',
            'mdi-weather-partly-snowy',
            'mdi-weather-partly-snowy-rainy',
            'mdi-weather-pouring',
            'mdi-weather-rainy',
            'mdi-weather-snowy',
            'mdi-weather-snowy-heavy',
            'mdi-weather-snowy-rainy',
            'mdi-weather-sunny'
        ],
        [
            'mdi-weather-sunset',
            'mdi-weather-windy',
            'mdi-weather-windy-variant',
            'mdi-waze',
            'mdi-umbrella-outline',
            'mdi-sleep'
        ],
        [
            'mdi-run-fast',
            'mdi-bike-fast',
            'mdi-car-side',
            'mdi-fire-truck',
            'mdi-golf-cart',
            'mdi-train-car',
            'mdi-tractor',
            'mdi-truck-fast-outline',
            'mdi-sail-boat'
        ],
        [
            'mdi-cow',
            'mdi-dog-service',
            'mdi-duck',
            'mdi-elephant',
            'mdi-jellyfish-outline',
            'mdi-fishbowl-outline',
            'mdi-owl',
            'mdi-spider',
            'mdi-pig',
            'mdi-panda'
        ],
        [
            'mdi-sheep'
        ],
        [
            'mdi-corn',
            'mdi-fruit-pineapple',
            'mdi-flower-tulip-outline',
            'mdi-fruit-grapes-outline',
            'mdi-fruit-cherries',
            'mdi-leaf',
            'mdi-fruit-watermelon',
            'mdi-tree-outline'
        ],
        [
            'mdi-hand-okay',
            'mdi-hand-peace',
            'mdi-hand-peace-variant',
            'mdi-hand-pointing-down',
            'mdi-hand-pointing-left',
            'mdi-hand-pointing-right',
            'mdi-hand-pointing-up'
        ]
    ]
};

export const notFound = {
    page404: {
        imgUrl: 'https://union.zfoo.com/asset/notFound/page404.jpg',
        title: 'notFound.page404.title',
        subTitle: 'notFound.page404.subTitle',
        toText: 'notFound.page404.toText',
        to: '/'
    },
    singleTime404: {
        imgUrl: 'https://union.zfoo.com/asset/notFound/singleTime404.jpg',
        title: 'notFound.singleTime404.title',
        subTitle: 'notFound.singleTime404.subTitle',
        toText: 'notFound.singleTime404.toText',
        to: '/'
    },
    location404: {
        imgUrl: 'https://union.zfoo.com/asset/notFound/location404.jpg',
        title: 'notFound.location404.title',
        subTitle: 'notFound.location404.subTitle',
        toText: 'notFound.location404.toText',
        to: '/'
    },
    item404: {
        imgUrl: 'https://union.zfoo.com/asset/notFound/item404.jpg',
        title: 'notFound.item404.title',
        subTitle: 'notFound.item404.subTitle',
        toText: 'notFound.item404.toText',
        to: '/'
    },
    person404: {
        imgUrl: 'https://union.zfoo.com/asset/notFound/person404.jpg',
        title: 'notFound.person404.title',
        subTitle: 'notFound.person404.subTitle',
        toText: 'notFound.person404.toText',
        to: '/'
    },
    word404: {
        imgUrl: 'https://union.zfoo.com/asset/notFound/word404.jpg',
        title: 'notFound.word404.title',
        subTitle: 'notFound.word404.subTitle',
        toText: 'notFound.word404.toText',
        to: '/'
    },
    category404: {
        imgUrl: 'https://union.zfoo.com/asset/notFound/category404.jpg',
        title: 'notFound.category404.title',
        subTitle: 'notFound.category404.subTitle',
        toText: 'notFound.category404.toText',
        to: '/'
    },
    user404: {
        imgUrl: 'https://union.zfoo.com/asset/notFound/user404.jpg',
        title: 'notFound.user404.title',
        subTitle: 'notFound.user404.subTitle',
        toText: 'notFound.user404.toText',
        to: '/'
    },
    search404: {
        imgUrl: 'https://union.zfoo.com/asset/notFound/search404.jpg',
        title: 'notFound.search404.title',
        subTitle: 'notFound.search404.subTitle',
        toText: 'notFound.search404.toText',
        to: '/'
    }
};
