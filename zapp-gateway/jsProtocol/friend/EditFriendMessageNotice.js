// @author jaysunxiao
// @version 1.0
// @since 2019-11-14 10:54
const EditFriendMessageNotice = function(chatMessage, messageId, uidA, uidB) {
    this.chatMessage = chatMessage; // java.lang.String
    this.messageId = messageId; // long
    this.uidA = uidA; // long
    this.uidB = uidB; // long
};

EditFriendMessageNotice.prototype.protocolId = function() {
    return 16004;
};

EditFriendMessageNotice.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.chatMessage);
    buffer.writeLong(packet.messageId);
    buffer.writeLong(packet.uidA);
    buffer.writeLong(packet.uidB);
};

EditFriendMessageNotice.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new EditFriendMessageNotice();
    const result0 = buffer.readString();
    packet.chatMessage = result0;
    const result1 = buffer.readLong();
    packet.messageId = result1;
    const result2 = buffer.readLong();
    packet.uidA = result2;
    const result3 = buffer.readLong();
    packet.uidB = result3;
    return packet;
};

export default EditFriendMessageNotice;
