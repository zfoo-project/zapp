// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const ChangeGroupAdminResponse = function(adminId, groupId) {
    this.adminId = adminId; // long
    this.groupId = groupId; // long
};

ChangeGroupAdminResponse.prototype.protocolId = function() {
    return 18536;
};

ChangeGroupAdminResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.adminId);
    buffer.writeLong(packet.groupId);
};

ChangeGroupAdminResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new ChangeGroupAdminResponse();
    const result0 = buffer.readLong();
    packet.adminId = result0;
    const result1 = buffer.readLong();
    packet.groupId = result1;
    return packet;
};

export default ChangeGroupAdminResponse;
