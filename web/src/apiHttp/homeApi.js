import request from '@/util/requestUtils.js';

export function homeFeedApi(feed, page) {
    return request({
        url: '/api/home/feed',
        method: 'get',
        params: {
            feed: feed,
            page: page
        }
    });
}

export function subscribeFeedApi(page) {
    return request({
        url: '/api/subscribe/feed',
        method: 'get',
        params: {
            page: page
        }
    });
}

export function searchApi(query, page) {
    return request({
        url: '/api/search',
        method: 'get',
        params: {
            query: query,
            page: page
        }
    });
}

export function searchHintApi(query) {
    return request({
        url: '/api/search/hint',
        method: 'get',
        params: {
            query: query
        }
    });
}

export function hotApi() {
    return request({
        url: '/api/hot',
        method: 'get'
    });
}
