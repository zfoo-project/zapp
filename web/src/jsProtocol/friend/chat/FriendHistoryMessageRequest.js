// 好友聊天
//
// @author jaysunxiao
// @version 1.0
// @since 2019-11-14 10:54
const FriendHistoryMessageRequest = function(friendId, lastMessageId, userId) {
    this.friendId = friendId; // long
    // 最老消息的id
    this.lastMessageId = lastMessageId; // long
    this.userId = userId; // long
};

FriendHistoryMessageRequest.prototype.protocolId = function() {
    return 15204;
};

FriendHistoryMessageRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.friendId);
    buffer.writeLong(packet.lastMessageId);
    buffer.writeLong(packet.userId);
};

FriendHistoryMessageRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new FriendHistoryMessageRequest();
    const result0 = buffer.readLong();
    packet.friendId = result0;
    const result1 = buffer.readLong();
    packet.lastMessageId = result1;
    const result2 = buffer.readLong();
    packet.userId = result2;
    return packet;
};

export default FriendHistoryMessageRequest;
