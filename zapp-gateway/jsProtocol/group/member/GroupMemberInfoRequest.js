// @author jaysunxiao
// @version 1.0
// @since 2020-05-08 14:20
const GroupMemberInfoRequest = function(groupId, members) {
    this.groupId = groupId; // long
    this.members = members; // java.util.List<java.lang.Long>
};

GroupMemberInfoRequest.prototype.protocolId = function() {
    return 18432;
};

GroupMemberInfoRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.groupId);
    buffer.writeLongArray(packet.members);
};

GroupMemberInfoRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new GroupMemberInfoRequest();
    const result0 = buffer.readLong();
    packet.groupId = result0;
    const list1 = buffer.readLongArray();
    packet.members = list1;
    return packet;
};

export default GroupMemberInfoRequest;
