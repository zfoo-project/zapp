// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const JoinGroupResponse = function() {
};

JoinGroupResponse.prototype.protocolId = function() {
    return 18413;
};

JoinGroupResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
};

JoinGroupResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new JoinGroupResponse();
    return packet;
};

export default JoinGroupResponse;
