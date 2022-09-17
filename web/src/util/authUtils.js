import Cookies from 'js-cookie';

const TokenKey = 'zapp';

export function getXToken() {
    return Cookies.get(TokenKey);
}

export function setXToken(token) {
    return Cookies.set(TokenKey, token, { expires: 7 });
}

export function removeXToken() {
    return Cookies.remove(TokenKey);
}

export function getTokenKey() {
    return TokenKey;
}
