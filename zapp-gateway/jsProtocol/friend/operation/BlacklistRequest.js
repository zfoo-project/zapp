// @author jaysunxiao
// @version 1.0
// @since 2019-11-14 10:54
const BlacklistRequest = function(targetId, userId) {
    // 需要加入黑名单的用户id
    this.targetId = targetId; // long
    // 用户的id
    this.userId = userId; // long
};

BlacklistRequest.prototype.protocolId = function() {
    return 15108;
};

BlacklistRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.targetId);
    buffer.writeLong(packet.userId);
};

BlacklistRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new BlacklistRequest();
    const result0 = buffer.readLong();
    packet.targetId = result0;
    const result1 = buffer.readLong();
    packet.userId = result1;
    return packet;
};

export default BlacklistRequest;
