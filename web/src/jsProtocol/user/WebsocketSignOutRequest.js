// @author jaysunxiao
// @version 1.0
// @since 2019-10-15 17:55
const WebsocketSignOutRequest = function() {
};

WebsocketSignOutRequest.prototype.protocolId = function() {
    return 1002;
};

WebsocketSignOutRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
};

WebsocketSignOutRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new WebsocketSignOutRequest();
    return packet;
};

export default WebsocketSignOutRequest;
