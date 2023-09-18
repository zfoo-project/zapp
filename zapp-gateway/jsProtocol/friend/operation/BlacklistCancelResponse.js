// @author jaysunxiao
// @version 1.0
// @since 2019-11-13 18:41
const BlacklistCancelResponse = function(targetId) {
    this.targetId = targetId; // long
};

BlacklistCancelResponse.prototype.protocolId = function() {
    return 15111;
};

BlacklistCancelResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.targetId);
};

BlacklistCancelResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new BlacklistCancelResponse();
    const result0 = buffer.readLong();
    packet.targetId = result0;
    return packet;
};

export default BlacklistCancelResponse;
