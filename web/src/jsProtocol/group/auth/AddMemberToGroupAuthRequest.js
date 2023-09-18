// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const AddMemberToGroupAuthRequest = function(groupAuthId, groupId, memberId) {
    this.groupAuthId = groupAuthId; // long
    this.groupId = groupId; // long
    this.memberId = memberId; // long
};

AddMemberToGroupAuthRequest.prototype.protocolId = function() {
    return 18520;
};

AddMemberToGroupAuthRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.groupAuthId);
    buffer.writeLong(packet.groupId);
    buffer.writeLong(packet.memberId);
};

AddMemberToGroupAuthRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new AddMemberToGroupAuthRequest();
    const result0 = buffer.readLong();
    packet.groupAuthId = result0;
    const result1 = buffer.readLong();
    packet.groupId = result1;
    const result2 = buffer.readLong();
    packet.memberId = result2;
    return packet;
};

export default AddMemberToGroupAuthRequest;
