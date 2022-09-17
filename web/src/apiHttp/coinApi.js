import request from '@/util/requestUtils.js';

export function loveApi(id) {
    const data = {
        id
    };
    return request({
        url: '/api/time/love',
        method: 'post',
        data
    });
}

export function downloadApi(id, imageQuality, selectedImages) {
    const data = {
        id,
        imageQuality,
        selectedImages
    };
    return request({
        url: '/api/time/download',
        method: 'post',
        data
    });
}
