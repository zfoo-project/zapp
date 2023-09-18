// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const LeaveGroupRequest = function(groupId) {
    this.groupId = groupId; // long
};

LeaveGroupRequest.prototype.protocolId = function() {
    return 18422;
};

LeaveGroupRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.groupId);
};

LeaveGroupRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new LeaveGroupRequest();
    const result0 = buffer.readLong();
    packet.groupId = result0;
    return packet;
};

export default LeaveGroupRequest;
