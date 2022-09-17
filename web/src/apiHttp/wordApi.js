import request from '@/util/requestUtils.js';

export function personHintApi(name) {
    return request({
        url: '/api/word/personHint',
        method: 'get',
        params: { name }
    });
}

export function wordHintApi(name) {
    return request({
        url: '/api/word/wordHint',
        method: 'get',
        params: { name }
    });
}

export function categoryHintApi(name) {
    return request({
        url: '/api/word/categoryHint',
        method: 'get',
        params: { name }
    });
}

export function wordInfoApi(words) {
    return request({
        url: '/api/word/info',
        method: 'get',
        params: {
            words: _.join(words, '-')
        }
    });
}

export function categoryInfoApi(categories) {
    return request({
        url: '/api/category/info',
        method: 'get',
        params: {
            categories: _.join(categories, '-')
        }
    });
}

export function wordDetailApi(word) {
    return request({
        url: '/api/word/detail',
        method: 'get',
        params: {
            word: word
        }
    });
}

export function editWordApi(id, word, person, background, paragraphs, sections, links, categories, externalLinks) {
    const data = {
        id,
        word,
        person,
        background,
        paragraphs,
        sections,
        links,
        categories,
        externalLinks
    };
    return request({
        url: '/api/word/edit',
        method: 'post',
        data
    });
}


export function editCategoryApi(id, name, background, parent, children, words) {
    const data = {
        id,
        name,
        background,
        parent,
        children,
        words
    };
    return request({
        url: '/api/category/edit',
        method: 'post',
        data
    });
}
