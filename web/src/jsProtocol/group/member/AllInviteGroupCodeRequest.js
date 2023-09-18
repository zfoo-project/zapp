// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const AllInviteGroupCodeRequest = function(groupId) {
    this.groupId = groupId; // long
};

AllInviteGroupCodeRequest.prototype.protocolId = function() {
    return 18424;
};

AllInviteGroupCodeRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.groupId);
};

AllInviteGroupCodeRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new AllInviteGroupCodeRequest();
    const result0 = buffer.readLong();
    packet.groupId = result0;
    return packet;
};

export default AllInviteGroupCodeRequest;
