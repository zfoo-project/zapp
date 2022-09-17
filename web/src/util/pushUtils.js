import Push from 'push.js';
import { isBlank } from '@/util/stringUtils.js';
import { toSimpleAvatarUrl } from '@/util/fileUtils.js';

export function requestPermission() {
    Push.Permission.request();
}

export function desktopPush(title, body, icon) {
    if (!Push.Permission.has()) {
        return;
    }
    Push.create(title, {
        body: body,
        requireInteraction: true,
        vibrate: true,
        icon: isBlank(icon) ? require(`@/asset/logo/zfoo_small.jpg`) : toSimpleAvatarUrl(icon),
        timeout: 3000
    });
}
