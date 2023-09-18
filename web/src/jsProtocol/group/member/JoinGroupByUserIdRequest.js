// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const JoinGroupByUserIdRequest = function(userId) {
    this.userId = userId; // long
};

JoinGroupByUserIdRequest.prototype.protocolId = function() {
    return 18414;
};

JoinGroupByUserIdRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.userId);
};

JoinGroupByUserIdRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new JoinGroupByUserIdRequest();
    const result0 = buffer.readLong();
    packet.userId = result0;
    return packet;
};

export default JoinGroupByUserIdRequest;
