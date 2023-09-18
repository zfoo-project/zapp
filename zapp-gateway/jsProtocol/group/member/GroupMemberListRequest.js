// @author jaysunxiao
// @version 1.0
// @since 2020-05-08 14:20
const GroupMemberListRequest = function(groupId, page) {
    this.groupId = groupId; // long
    this.page = page; // int
};

GroupMemberListRequest.prototype.protocolId = function() {
    return 18430;
};

GroupMemberListRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.groupId);
    buffer.writeInt(packet.page);
};

GroupMemberListRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new GroupMemberListRequest();
    const result0 = buffer.readLong();
    packet.groupId = result0;
    const result1 = buffer.readInt();
    packet.page = result1;
    return packet;
};

export default GroupMemberListRequest;
