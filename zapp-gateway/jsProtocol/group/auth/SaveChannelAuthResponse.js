// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const SaveChannelAuthResponse = function() {
};

SaveChannelAuthResponse.prototype.protocolId = function() {
    return 18513;
};

SaveChannelAuthResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
};

SaveChannelAuthResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new SaveChannelAuthResponse();
    return packet;
};

export default SaveChannelAuthResponse;
