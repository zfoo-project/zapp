// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const DeleteGroupAuthRequest = function(groupAuthId, groupId) {
    this.groupAuthId = groupAuthId; // long
    this.groupId = groupId; // long
};

DeleteGroupAuthRequest.prototype.protocolId = function() {
    return 18501;
};

DeleteGroupAuthRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.groupAuthId);
    buffer.writeLong(packet.groupId);
};

DeleteGroupAuthRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new DeleteGroupAuthRequest();
    const result0 = buffer.readLong();
    packet.groupAuthId = result0;
    const result1 = buffer.readLong();
    packet.groupId = result1;
    return packet;
};

export default DeleteGroupAuthRequest;
