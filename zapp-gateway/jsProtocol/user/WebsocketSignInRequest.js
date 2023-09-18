// @author jaysunxiao
// @version 1.0
// @since 2019-10-15 17:55
const WebsocketSignInRequest = function(token) {
    this.token = token; // java.lang.String
};

WebsocketSignInRequest.prototype.protocolId = function() {
    return 1000;
};

WebsocketSignInRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.token);
};

WebsocketSignInRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new WebsocketSignInRequest();
    const result0 = buffer.readString();
    packet.token = result0;
    return packet;
};

export default WebsocketSignInRequest;
