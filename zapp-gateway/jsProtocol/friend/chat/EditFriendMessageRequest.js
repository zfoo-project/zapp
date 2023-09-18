// @author jaysunxiao
// @version 1.0
// @since 2019-11-14 10:54
const EditFriendMessageRequest = function(chatMessage, friendId, messageId, userId) {
    this.chatMessage = chatMessage; // java.lang.String
    this.friendId = friendId; // long
    this.messageId = messageId; // long
    this.userId = userId; // long
};

EditFriendMessageRequest.prototype.protocolId = function() {
    return 15208;
};

EditFriendMessageRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.chatMessage);
    buffer.writeLong(packet.friendId);
    buffer.writeLong(packet.messageId);
    buffer.writeLong(packet.userId);
};

EditFriendMessageRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new EditFriendMessageRequest();
    const result0 = buffer.readString();
    packet.chatMessage = result0;
    const result1 = buffer.readLong();
    packet.friendId = result1;
    const result2 = buffer.readLong();
    packet.messageId = result2;
    const result3 = buffer.readLong();
    packet.userId = result3;
    return packet;
};

export default EditFriendMessageRequest;
