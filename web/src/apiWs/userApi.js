import store from '@/store/store.js';
import { packetReceiver } from '@/util/websocketUtils.js';

import GetUserCacheResponse from '@/jsProtocol/cache/GetUserCacheResponse.js';

packetReceiver(GetUserCacheResponse, (packet) => {
    const map = packet.userCacheMap;
    for (const value of map.values()) {
        store.commit('app/setProfile', value);
    }
});
