import request from '@/util/requestUtils.js';

export function adminUserAuthApi(targetId, adminAuth) {
    const data = {
        targetId,
        adminAuth
    };
    return request({
        url: '/api/admin/user/auth',
        method: 'post',
        data
    });
}

export function adminTimeDeleteApi(timeSliceId) {
    const data = {
        timeSliceId
    };
    return request({
        url: '/api/admin/time/delete',
        method: 'post',
        data
    });
}

export function adminTimeReviewApi(timeSliceId) {
    const data = {
        timeSliceId
    };
    return request({
        url: '/api/admin/time/review',
        method: 'post',
        data
    });
}
