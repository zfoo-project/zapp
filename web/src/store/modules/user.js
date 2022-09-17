import i18n from '@/i18n/i18n.js';

import { set } from '@/util/vuexUtils.js';
import { getXToken } from '@/util/authUtils.js';
import { getUserProfileApi } from '@/apiHttp/loginApi.js';
import { connect } from '@/util/websocketUtils.js';
import { isBlank } from '@/util/stringUtils.js';
import { simpleSuccess } from '@/util/noticeUtils.js';
import { requestPermission } from '@/util/pushUtils.js';
import { settingConstant } from '@/constant/constant.js';

export default {
    namespaced: true,

    state: {
        loggedIn: false,
        wsLoggedIn: false,

        id: '0',
        adminAuth: 0,
        name: '',
        coin: '',
        signature: '',
        gender: 0,
        avatar: '',
        background: '',
        customTime: '0',
        custom: '',
        fanNum: 0,
        starNum: 0,
        free: '0',
        normal: '0',
        follows: [],
        stars: [],
        locations: [],
        persons: [],
        items: [],
        setting: {}
    },

    actions: {
        // 获取用户信息
        getUserInfo: async({ commit, state }, vuetify) => {
            if (state.loggedIn) {
                return;
            }
            if (isBlank(getXToken())) {
                return;
            }
            getUserProfileApi(getXToken()).then(response => {
                const data = response.data;

                // 用户信息
                if (!isBlank(data.avatar)) {
                    commit('setAvatar', data.avatar);
                }
                if (!isBlank(data.background)) {
                    commit('setBackground', data.background);
                }
                commit('setId', data.id);
                commit('setAdminAuth', data.adminAuth);
                commit('setName', data.name);
                commit('setCoin', data.coin);
                commit('setGender', data.gender);
                commit('setSignature', data.signature);
                commit('setCustomTime', data.customTime);
                commit('setCustom', data.custom);
                commit('setFanNum', data.fanNum);
                commit('setStarNum', data.starNum);
                commit('setFree', data.free);
                commit('setNormal', data.normal);
                commit('setFollows', data.follows);
                commit('setStars', data.stars);
                commit('setLocations', data.locations);
                commit('setPersons', data.persons);
                commit('setItems', data.items);
                commit('setSetting', data.setting);

                commit('setLoggedIn', true);

                setTimeout(() => {
                    // 用户自定义设置
                    vuetify.theme.dark = settingConstant.theme.dark === data.setting.theme;
                    i18n.locale = settingConstant.language.cn.type === data.setting.language ? settingConstant.language.cn.value : settingConstant.language.en.value;

                    // 连接websocket的网关
                    connect(process.env.VUE_APP_WEBSOCKET_URL, 'http登录成功连接ws');
                }, 500);

                // 获取弹出消息的权限
                requestPermission();
            });
        }

    },

    getters: {
        myProfile: (state) => {
            return {
                id: state.id,
                name: state.name,
                signature: state.signature,
                gender: state.gender,
                avatar: state.avatar,
                background: state.background,
                customTime: state.customTime,
                custom: state.custom,
                locations: state.locations,
                persons: state.persons,
                items: state.items
            };
        }
    },

    mutations: {
        setLoggedIn: set('loggedIn'),
        setWsLoggedIn: set('wsLoggedIn'),
        setToken: set('token'),
        setId: set('id'),
        setAdminAuth: set('adminAuth'),
        setName: set('name'),
        setCoin: set('coin'),
        setSignature: set('signature'),
        setAvatar: set('avatar'),
        setGender: set('gender'),
        setBackground: set('background'),
        setCustomTime: set('customTime'),
        setCustom: set('custom'),
        setFanNum: set('fanNum'),
        setStarNum: set('starNum'),
        setFree: set('free'),
        setNormal: set('normal'),
        setFollows: set('follows'),
        setStars: set('stars'),
        setLocations: set('locations'),
        setPersons: set('persons'),
        setItems: set('items'),
        setSetting: set('setting'),

        clearUserState(state, vuetify) {
            state.id = 0;
            state.name = '';
            state.signature = '';
            state.gender = 0;
            state.avatar = '';
            state.background = '';
            state.customTime = 0;
            state.custom = '';
            state.fanNum = 0;
            state.starNum = 0;
            state.free = 0;
            state.normal = 0;
            state.stars = [];
            state.locations = [];
            state.persons = [];
            state.items = [];
            state.setting = {};
            vuetify.theme.dark = false;
        },

        updateFollows(state, followUserId) {
            if (_.findIndex(state.follows, (it) => it === followUserId) >= 0) {
                const follows = Array.from(state.follows);
                _.remove(follows, (it) => it === followUserId);
                state.follows = follows;
                simpleSuccess(i18n.t('notice.unfollowUserMessage'));
            } else {
                state.follows.push(followUserId);
                simpleSuccess(i18n.t('notice.followUserMessage'));
            }
        },

        updateStars(state, tsId) {
            if (_.findIndex(state.stars, (it) => it === tsId) >= 0) {
                const stars = Array.from(state.stars);
                _.remove(stars, (it) => it === tsId);
                state.stars = stars;
                simpleSuccess(i18n.t('notice.unstarMessage'));
            } else {
                state.stars.push(tsId);
                simpleSuccess(i18n.t('notice.starMessage'));
            }
        },

        updateLocation(state, locationPair) {
            if (_.findIndex(state.locations, (it) => it.key === locationPair.key) >= 0) {
                const locations = Array.from(state.locations);
                _.remove(locations, (it) => it.key === locationPair.key);
                state.locations = locations;
                simpleSuccess(i18n.t('notice.unsubscribeMessage'));
            } else {
                state.locations.push(locationPair);
                simpleSuccess(i18n.t('notice.subscribeMessage'));
            }
        },
        updateItem(state, itemPair) {
            if (_.findIndex(state.items, (it) => it.key === itemPair.key) >= 0) {
                const items = Array.from(state.items);
                _.remove(items, (it) => it.key === itemPair.key);
                state.items = items;
                simpleSuccess(i18n.t('notice.unsubscribeMessage'));
            } else {
                state.items.push(itemPair);
                simpleSuccess(i18n.t('notice.subscribeMessage'));
            }
        },
        updatePerson(state, personPair) {
            if (_.findIndex(state.persons, (it) => it.key === personPair.key) >= 0) {
                const persons = Array.from(state.items);
                _.remove(persons, (it) => it.key === personPair.key);
                state.persons = persons;
                simpleSuccess(i18n.t('notice.unsubscribeMessage'));
            } else {
                state.persons.push(personPair);
                simpleSuccess(i18n.t('notice.subscribeMessage'));
            }
        }
    }

};
