// @author jaysunxiao
// @version 1.0
// @since 2020-05-08 14:20
const GroupMemberListResponse = function(groupId, members, page) {
    this.groupId = groupId; // long
    this.members = members; // java.util.List<com.zfoo.app.zapp.common.protocol.group.member.model.GroupMemberVO>
    this.page = page; // int
};

GroupMemberListResponse.prototype.protocolId = function() {
    return 18431;
};

GroupMemberListResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.groupId);
    buffer.writePacketArray(packet.members, 18401);
    buffer.writeInt(packet.page);
};

GroupMemberListResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new GroupMemberListResponse();
    const result0 = buffer.readLong();
    packet.groupId = result0;
    const list1 = buffer.readPacketArray(18401);
    packet.members = list1;
    const result2 = buffer.readInt();
    packet.page = result2;
    return packet;
};

export default GroupMemberListResponse;
