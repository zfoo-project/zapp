// @author jaysunxiao
// @version 1.0
// @since 2019-11-14 10:54
const DeleteFriendMessageRequest = function(friendId, messageId, userId) {
    this.friendId = friendId; // long
    this.messageId = messageId; // long
    this.userId = userId; // long
};

DeleteFriendMessageRequest.prototype.protocolId = function() {
    return 15206;
};

DeleteFriendMessageRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.friendId);
    buffer.writeLong(packet.messageId);
    buffer.writeLong(packet.userId);
};

DeleteFriendMessageRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new DeleteFriendMessageRequest();
    const result0 = buffer.readLong();
    packet.friendId = result0;
    const result1 = buffer.readLong();
    packet.messageId = result1;
    const result2 = buffer.readLong();
    packet.userId = result2;
    return packet;
};

export default DeleteFriendMessageRequest;
