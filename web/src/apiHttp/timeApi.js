import request from '@/util/requestUtils.js';

export function userTsApi(id, page) {
    return request({
        url: '/api/time/user',
        method: 'get',
        params: {
            id,
            page
        }
    });
}

export function checkTimeLinksApi(timeLinks) {
    return request({
        url: '/api/time/creation/checkTimeLinks',
        method: 'get',
        params: {
            timeLinks: _.join(timeLinks, '-')
        }
    });
}

export function timeSliceInfoApi(timeLinks) {
    return request({
        url: '/api/time/info',
        method: 'get',
        params: {
            timeLinks: _.join(timeLinks, '-')
        }
    });
}

export function singleTimeSliceApi(timeSliceId, dimension) {
    return request({
        url: '/api/singleTimeSlice/info',
        method: 'get',
        params: {
            id: timeSliceId,
            dimension: dimension
        }
    });
}

export function reportApi(timeSliceId, optionType, selectedItems, content, fileLinks) {
    const data = {
        timeSliceId,
        optionType,
        selectedItems,
        content,
        fileLinks
    };
    return request({
        url: '/api/time/report',
        method: 'post',
        data
    });
}

export function createTimeSliceApi(createList) {
    const data = {
        createList
    };
    return request({
        url: '/api/time/creation',
        method: 'post',
        data
    });
}

export function recommitTimeSliceApi(rejectId, reviewId, type, start, end, locations, persons, items, notExistItems, content
    , images, video, key, albums) {
    const data = {
        rejectId: rejectId,
        reviewId: reviewId,
        vo: {
            type,
            start,
            end,
            locations,
            persons,
            items,
            notExistItems,
            content,
            images,
            video,
            key,
            albums
        }
    };
    return request({
        url: '/api/time/recommit',
        method: 'post',
        data
    });
}

export function editTimeSliceApi(editId, type, start, end, locations, persons, items, notExistItems, content
    , images, video, key, albums) {
    const data = {
        editId: editId,
        vo: {
            type,
            start,
            end,
            locations,
            persons,
            items,
            notExistItems,
            content,
            images,
            video,
            key,
            albums
        }
    };
    return request({
        url: '/api/time/edit',
        method: 'post',
        data
    });
}

export function passTimeSliceReviewApi(tsReviewId, recommend) {
    const data = {
        tsReviewId: tsReviewId,
        recommend
    };
    return request({
        url: '/api/time/review/pass',
        method: 'post',
        data
    });
}

export function rejectTimeSliceReviewApi(tsReviewId, optionType, selectedItems, content) {
    const data = {
        tsReviewId: tsReviewId,
        optionType,
        selectedItems,
        content
    };
    return request({
        url: '/api/time/review/reject',
        method: 'post',
        data
    });
}

export function passTimeSliceEditApi(tsEditId, recommend) {
    const data = {
        tsEditId: tsEditId,
        recommend
    };
    return request({
        url: '/api/time/edit/pass',
        method: 'post',
        data
    });
}

export function rejectTimeSliceEditApi(tsEditId, optionType, selectedItems, content) {
    const data = {
        tsEditId: tsEditId,
        optionType,
        selectedItems,
        content
    };
    return request({
        url: '/api/time/edit/reject',
        method: 'post',
        data
    });
}

export function deleteTimeSliceReviewApi(reviewIds) {
    return request({
        url: '/api/time/review/delete',
        method: 'delete',
        params: {
            reviewIds: _.join(reviewIds, '-')
        }
    });
}

export function deleteTimeSliceReviewRejectApi(reviewIds) {
    return request({
        url: '/api/time/review/reject/delete',
        method: 'delete',
        params: {
            reviewIds: _.join(reviewIds, '-')
        }
    });
}

export function deleteTimeSliceEditApi(editIds) {
    return request({
        url: '/api/time/edit/delete',
        method: 'delete',
        params: {
            editIds: _.join(editIds, '-')
        }
    });
}

export function deleteTimeSliceEditRejectApi(editIds) {
    return request({
        url: '/api/time/edit/reject/delete',
        method: 'delete',
        params: {
            editIds: _.join(editIds, '-')
        }
    });
}

export function reviewTsApi(page) {
    return request({
        url: '/api/time/review/ts',
        method: 'get',
        params: {
            page
        }
    });
}

export function deleteTimeSliceApi(timeLinks) {
    return request({
        url: '/api/time/delete',
        method: 'delete',
        params: {
            timeLinks: _.join(timeLinks, '-')
        }
    });
}
