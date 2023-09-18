// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const DeleteGroupNotice = function(groupId, groupName) {
    this.groupId = groupId; // long
    this.groupName = groupName; // java.lang.String
};

DeleteGroupNotice.prototype.protocolId = function() {
    return 19000;
};

DeleteGroupNotice.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.groupId);
    buffer.writeString(packet.groupName);
};

DeleteGroupNotice.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new DeleteGroupNotice();
    const result0 = buffer.readLong();
    packet.groupId = result0;
    const result1 = buffer.readString();
    packet.groupName = result1;
    return packet;
};

export default DeleteGroupNotice;
