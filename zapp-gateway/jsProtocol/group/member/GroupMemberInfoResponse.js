// @author jaysunxiao
// @version 1.0
// @since 2020-05-08 14:20
const GroupMemberInfoResponse = function(groupId, members) {
    this.groupId = groupId; // long
    this.members = members; // java.util.List<com.zfoo.app.zapp.common.protocol.group.member.model.GroupMemberVO>
};

GroupMemberInfoResponse.prototype.protocolId = function() {
    return 18433;
};

GroupMemberInfoResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.groupId);
    buffer.writePacketArray(packet.members, 18401);
};

GroupMemberInfoResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new GroupMemberInfoResponse();
    const result0 = buffer.readLong();
    packet.groupId = result0;
    const list1 = buffer.readPacketArray(18401);
    packet.members = list1;
    return packet;
};

export default GroupMemberInfoResponse;
