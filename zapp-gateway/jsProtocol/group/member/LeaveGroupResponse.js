// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const LeaveGroupResponse = function(groupId, groupName) {
    this.groupId = groupId; // long
    this.groupName = groupName; // java.lang.String
};

LeaveGroupResponse.prototype.protocolId = function() {
    return 18423;
};

LeaveGroupResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.groupId);
    buffer.writeString(packet.groupName);
};

LeaveGroupResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new LeaveGroupResponse();
    const result0 = buffer.readLong();
    packet.groupId = result0;
    const result1 = buffer.readString();
    packet.groupName = result1;
    return packet;
};

export default LeaveGroupResponse;
