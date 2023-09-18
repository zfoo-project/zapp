// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const MemberGroupAuthIdUpdateNotice = function(groupAuthIds, groupId, memberId) {
    this.groupAuthIds = groupAuthIds; // java.util.List<java.lang.Long>
    this.groupId = groupId; // long
    this.memberId = memberId; // long
};

MemberGroupAuthIdUpdateNotice.prototype.protocolId = function() {
    return 19002;
};

MemberGroupAuthIdUpdateNotice.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLongArray(packet.groupAuthIds);
    buffer.writeLong(packet.groupId);
    buffer.writeLong(packet.memberId);
};

MemberGroupAuthIdUpdateNotice.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new MemberGroupAuthIdUpdateNotice();
    const list0 = buffer.readLongArray();
    packet.groupAuthIds = list0;
    const result1 = buffer.readLong();
    packet.groupId = result1;
    const result2 = buffer.readLong();
    packet.memberId = result2;
    return packet;
};

export default MemberGroupAuthIdUpdateNotice;
