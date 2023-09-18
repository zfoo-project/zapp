// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const DeleteGroupRequest = function(groupId) {
    this.groupId = groupId; // long
};

DeleteGroupRequest.prototype.protocolId = function() {
    return 18530;
};

DeleteGroupRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.groupId);
};

DeleteGroupRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new DeleteGroupRequest();
    const result0 = buffer.readLong();
    packet.groupId = result0;
    return packet;
};

export default DeleteGroupRequest;
