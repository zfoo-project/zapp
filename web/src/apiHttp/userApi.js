import request from '@/util/requestUtils.js';

export function searchUserApi(query) {
    return request({
        url: '/api/user/search',
        method: 'get',
        params: {
            query: query
        }
    });
}

export function getUserInfoApi(ids) {
    return request({
        url: '/api/user/info',
        method: 'get',
        params: {
            ids: _.join(ids, '-')
        }
    });
}

export function getUserCreationApi() {
    return request({
        url: '/api/user/creation',
        method: 'get'
    });
}

export function accountSecurityApi() {
    return request({
        url: '/api/user/accountSecurity',
        method: 'get'
    });
}

export function savePasswordApi(phoneNumber, phoneCode, newPassword) {
    const data = {
        phoneNumber,
        phoneCode,
        newPassword
    };
    return request({
        url: '/api/user/savePassword',
        method: 'post',
        data
    });
}

export function savePhoneNumberApi(phoneNumber, phoneCode) {
    const data = {
        phoneNumber,
        phoneCode
    };
    return request({
        url: '/api/user/savePhone',
        method: 'post',
        data
    });
}

export function saveAvatarApi(avatar) {
    const data = {
        avatar
    };
    return request({
        url: '/api/user/saveAvatar',
        method: 'post',
        data
    });
}

export function saveBackgroundApi(background) {
    const data = {
        background
    };
    return request({
        url: '/api/user/saveBackground',
        method: 'post',
        data
    });
}

export function saveBaseInfoApi(name, gender, signature) {
    const data = {
        name,
        gender,
        signature
    };
    return request({
        url: '/api/user/saveBaseInfo',
        method: 'post',
        data
    });
}

export function saveCustomStatusApi(customTime, custom) {
    const data = {
        customTime,
        custom
    };
    return request({
        url: '/api/user/saveCustomStatus',
        method: 'post',
        data
    });
}

export function saveSettingApi(theme, language) {
    const data = {
        theme,
        language
    };
    return request({
        url: '/api/user/saveSetting',
        method: 'post',
        data
    });
}

export function subscribeLocationApi(locationId) {
    const data = {
        locationId
    };
    return request({
        url: '/api/subscribe/location',
        method: 'post',
        data
    });
}

export function subscribeItemApi(itemId) {
    const data = {
        itemId
    };
    return request({
        url: '/api/subscribe/item',
        method: 'post',
        data
    });
}

export function subscribePersonApi(personId) {
    const data = {
        personId
    };
    return request({
        url: '/api/subscribe/person',
        method: 'post',
        data
    });
}

export function followUserApi(followUserId) {
    const data = {
        followUserId
    };
    return request({
        url: '/api/follow/user',
        method: 'post',
        data
    });
}

export function unfollowUserApi(followUserId) {
    const data = {
        followUserId
    };
    return request({
        url: '/api/unfollow/user',
        method: 'post',
        data
    });
}

export function starTimeSliceApi(tsId) {
    const data = {
        tsId
    };
    return request({
        url: '/api/time/star',
        method: 'post',
        data
    });
}

export function userStarListApi(page) {
    return request({
        url: '/api/user/star/list',
        method: 'get',
        params: {
            page: page
        }
    });
}

export function userFollowListApi(id, page) {
    return request({
        url: '/api/user/follow/list',
        method: 'get',
        params: {
            id: id,
            page: page
        }
    });
}

export function userProfileFollowApi(page) {
    return request({
        url: '/api/user/profile/follow',
        method: 'get',
        params: {
            page: page
        }
    });
}

export function userProfileFanApi(page) {
    return request({
        url: '/api/user/profile/fan',
        method: 'get',
        params: {
            page: page
        }
    });
}

export function userFanListApi(id, page) {
    return request({
        url: '/api/user/fan/list',
        method: 'get',
        params: {
            id: id,
            page: page
        }
    });
}
