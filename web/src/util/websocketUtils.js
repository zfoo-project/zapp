import ByteBuffer from '@/jsProtocol/buffer/ByteBuffer';
import ProtocolManager from '@/jsProtocol/ProtocolManager.js';
import WebsocketSignInRequest from '@/jsProtocol/user/WebsocketSignInRequest.js';
import Error from '@/jsProtocol/common/Error.js';
import Message from '@/jsProtocol/common/Message.js';
import Ping from '@/jsProtocol/common/Ping';
import Pong from '@/jsProtocol/common/Pong';
import i18n from '@/i18n/i18n.js';
import { getXToken } from '@/util/authUtils.js';
import { simpleSuccess, simpleError } from '@/util/noticeUtils.js';
import { isBlank } from '@/util/stringUtils.js';

let websocket = null;
let wsUrl = null;
let serverTime = new Date().getTime();

// 每30秒发送一次心跳包
setInterval(function() {
    // 如果没有登录，则不需要发送心跳包
    if (isBlank(getXToken())) {
        return;
    }

    if (isBlank(wsUrl)) {
        return;
    }

    // 如果服务器长时间没有回应，则重新连接
    if (new Date().getTime() - serverTime >= 3 * 60 * 1000) {
        connect(wsUrl, '服务器长时间没有相应，进行重连尝试');
        return;
    }

    sendPacket(new Ping());
}, 30000);

// readyState的状态码定义
// 0 (CONNECTING)，正在链接中
// 1 (OPEN)，已经链接并且可以通讯
// 2 (CLOSING)，连接正在关闭
// 3 (CLOSED)，连接已关闭或者没有链接成功
export function connect(url, desc) {
    console.log('start connect websocket: ' + desc);

    closeWebsocket();

    // const ws = new WebSocket('ws://127.0.0.1:9000/websocket');
    const ws = new WebSocket(url);
    websocket = ws;
    wsUrl = url;

    ws.binaryType = 'arraybuffer';

    ws.onopen = function() {
        console.log('websocket open success');

        // websocket连接成功过后，先发送ping同步服务器时间，再发送登录请求
        sendPacket(new Ping());

        const request = new WebsocketSignInRequest(getXToken());
        sendPacket(request);

        simpleSuccess(i18n.t('notice.websocketConnectSuccess'));
        serverTime = new Date().getTime();
    };


    ws.onmessage = function(event) {
        const data = event.data;

        const byteBuffer = new ByteBuffer();
        byteBuffer.writeBytes(data);
        byteBuffer.setReadOffset(4);
        const packet = ProtocolManager.read(byteBuffer);
        byteBuffer.readBoolean();
        console.log('Websocket收到:', packet);
        if (packet.protocolId() === Pong.prototype.protocolId()) {
            serverTime = _.toNumber(packet.time);
        } else if (packet.protocolId() === Message.prototype.protocolId()) {
            if (packet.code === 1) {
                simpleSuccess(i18n.t('code_enum_' + packet.code));
            } else if (packet.code === 2) {
                // do noting
            } else {
                simpleError(i18n.t('code_enum_' + packet.code));
            }
        } else if (packet.protocolId() === Error.prototype.protocolId()) {
            simpleError(i18n.t('code_enum_' + packet.errorCode));
        } else {
            ProtocolManager.getProtocol(packet.protocolId()).receiver(packet);
        }
    };

    ws.onerror = function(event) {
        console.log('websocket error');
        console.log(event);
    };

    ws.onclose = function(event) {
        console.log('websocket close');
        console.log(event);
    };
}

export function closeWebsocket() {
    if (_.isNil(websocket)) {
        return;
    }
    if (websocket.readyState === 0 || websocket.readyState === 1) {
        websocket.close();
        return;
    }
    websocket = null;
}

export function sendPacket(packet) {
    console.log('Websocket发送:', packet);

    if (isBlank(getXToken())) {
        simpleError(i18n.t('notice.notSignInError'));
        return;
    }

    if (isBlank(wsUrl)) {
        simpleError(i18n.t('notice.websocketConnectError'));
        return;
    }

    if (_.isNil(websocket)) {
        simpleError(i18n.t('notice.websocketReconnect'));
        connect(wsUrl, '发送消息的时候ws是空的，重连ws');
        return;
    }

    switch (websocket.readyState) {
    case 0:
        simpleSuccess(i18n.t('notice.websocketConnecting'));
        break;
    case 1:
        // eslint-disable-next-line no-case-declarations
        const byteBuffer = new ByteBuffer();
        byteBuffer.setWriteOffset(4);
        ProtocolManager.write(byteBuffer, packet);
        byteBuffer.writeBoolean(false);
        // eslint-disable-next-line no-case-declarations
        const writeOffset = byteBuffer.writeOffset;
        byteBuffer.setWriteOffset(0);
        byteBuffer.writeRawInt(writeOffset - 4);
        byteBuffer.setWriteOffset(writeOffset);
        websocket.send(byteBuffer.buffer);
        break;
    case 2:
        simpleError(i18n.t('notice.websocketConnecting'));
        connect(wsUrl, '发送消息的时候ws正在关闭，进行重连尝试');
        break;
    case 3:
        simpleError(i18n.t('notice.websocketConnecting'));
        connect(wsUrl, '发送消息的时候ws连接关闭，进行重连尝试');
        break;
    default:
        simpleError(i18n.t('notice.websocketStateError'));
    }
}


export function packetReceiver(protocol, receiverCallback) {
    if (_.isNil(protocol.receiver)) {
        protocol.receiver = receiverCallback;
    }
}

export function getServerTime() {
    return serverTime;
}
