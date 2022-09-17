import request from '@/util/requestUtils.js';

export function locationFeedApi(location, page) {
    return request({
        url: '/api/location/feed',
        method: 'get',
        params: {
            location: location,
            page: page
        }
    });
}

export function locationInfoApi(locations) {
    return request({
        url: '/api/location/info',
        method: 'get',
        params: {
            locations: _.join(locations, '-')
        }
    });
}

export function itemFeedApi(item, page) {
    return request({
        url: '/api/item/feed',
        method: 'get',
        params: {
            item: item,
            page: page
        }
    });
}

export function itemInfoApi(items) {
    return request({
        url: '/api/item/info',
        method: 'get',
        params: {
            items: _.join(items, '-')
        }
    });
}

export function personFeedApi(person, page) {
    return request({
        url: '/api/person/feed',
        method: 'get',
        params: {
            person: person,
            page: page
        }
    });
}

export function personInfoApi(persons) {
    return request({
        url: '/api/person/info',
        method: 'get',
        params: {
            persons: _.join(persons, '-')
        }
    });
}
