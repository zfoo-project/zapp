import request from '@/util/requestUtils.js';

export function loginByAccountApi(account, password, verifyKey, verifyCode) {
    const data = {
        account,
        password,
        verifyKey,
        verifyCode
    };
    return request({
        url: '/api/account/signIn',
        method: 'post',
        data
    });
}

export function loginByPhoneApi(phoneNumber, phoneCode) {
    const data = {
        phoneNumber,
        phoneCode
    };
    return request({
        url: '/api/phone/signIn',
        method: 'post',
        data
    });
}

export function checkPhoneCodeApi(phoneNumber, phoneCode) {
    const data = {
        phoneNumber,
        phoneCode
    };
    return request({
        url: '/api/check/phoneCode',
        method: 'post',
        data
    });
}

/**
 * 仅仅用在测试环境，用于免密，直接登录并且注册账号
 * @param account
 */
export function loginTestApi(account) {
    return request({
        url: '/api/signInTest',
        method: 'get',
        params: { account }
    });
}

export function captchaVerifyApi() {
    return request({
        url: '/api/verify/captcha',
        method: 'get'
    });
}

export function bindVerifyApi() {
    return request({
        url: '/api/verify/bind',
        method: 'get'
    });
}

export function unbindWeiBoApi() {
    return request({
        url: '/api/weiBo/unbind',
        method: 'delete'
    });
}

export function registerWeChatApi(verifyKey, phoneNumber, phoneCode) {
    const data = {
        verifyKey,
        phoneNumber,
        phoneCode
    };
    return request({
        url: '/api/weChat/register',
        method: 'post',
        data
    });
}

export function registerWeChatServiceApi(verifyKey, phoneNumber, phoneCode) {
    const data = {
        verifyKey,
        phoneNumber,
        phoneCode
    };
    return request({
        url: '/api/weChatService/register',
        method: 'post',
        data
    });
}

export function unbindWeChatApi() {
    return request({
        url: '/api/weChat/unbind',
        method: 'delete'
    });
}

export function registerWeiBoApi(verifyKey, phoneNumber, phoneCode) {
    const data = {
        verifyKey,
        phoneNumber,
        phoneCode
    };
    return request({
        url: '/api/weiBo/register',
        method: 'post',
        data
    });
}

export function phoneVerifyApi(phoneNumber, verifyKey, verifyCode) {
    const data = {
        phoneNumber,
        verifyKey,
        verifyCode
    };
    return request({
        url: '/api/verify/phone',
        method: 'post',
        data
    });
}

export function logoutApi() {
    return request({
        url: '/api/logout',
        method: 'post'
    });
}

export function getUserProfileApi(token) {
    return request({
        url: '/api/user/profile',
        method: 'get',
        params: { token }
    });
}
