// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const ChangeGroupAdminRequest = function(adminId, groupId) {
    this.adminId = adminId; // long
    this.groupId = groupId; // long
};

ChangeGroupAdminRequest.prototype.protocolId = function() {
    return 18535;
};

ChangeGroupAdminRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.adminId);
    buffer.writeLong(packet.groupId);
};

ChangeGroupAdminRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new ChangeGroupAdminRequest();
    const result0 = buffer.readLong();
    packet.adminId = result0;
    const result1 = buffer.readLong();
    packet.groupId = result1;
    return packet;
};

export default ChangeGroupAdminRequest;
