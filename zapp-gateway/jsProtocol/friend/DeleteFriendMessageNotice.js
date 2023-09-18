// @author jaysunxiao
// @version 1.0
// @since 2019-11-14 10:54
const DeleteFriendMessageNotice = function(messageId, uidA, uidB) {
    this.messageId = messageId; // long
    this.uidA = uidA; // long
    this.uidB = uidB; // long
};

DeleteFriendMessageNotice.prototype.protocolId = function() {
    return 16003;
};

DeleteFriendMessageNotice.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.messageId);
    buffer.writeLong(packet.uidA);
    buffer.writeLong(packet.uidB);
};

DeleteFriendMessageNotice.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new DeleteFriendMessageNotice();
    const result0 = buffer.readLong();
    packet.messageId = result0;
    const result1 = buffer.readLong();
    packet.uidA = result1;
    const result2 = buffer.readLong();
    packet.uidB = result2;
    return packet;
};

export default DeleteFriendMessageNotice;
