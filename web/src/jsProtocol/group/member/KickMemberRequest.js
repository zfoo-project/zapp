// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const KickMemberRequest = function(groupId, memberId) {
    this.groupId = groupId; // long
    this.memberId = memberId; // long
};

KickMemberRequest.prototype.protocolId = function() {
    return 18434;
};

KickMemberRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.groupId);
    buffer.writeLong(packet.memberId);
};

KickMemberRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new KickMemberRequest();
    const result0 = buffer.readLong();
    packet.groupId = result0;
    const result1 = buffer.readLong();
    packet.memberId = result1;
    return packet;
};

export default KickMemberRequest;
