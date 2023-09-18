// 好友聊天
//
// @author jaysunxiao
// @version 1.0
// @since 2019-11-14 10:54
const ReadFriendMessageRequest = function(friendId, userId) {
    this.friendId = friendId; // long
    this.userId = userId; // long
};

ReadFriendMessageRequest.prototype.protocolId = function() {
    return 15202;
};

ReadFriendMessageRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.friendId);
    buffer.writeLong(packet.userId);
};

ReadFriendMessageRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new ReadFriendMessageRequest();
    const result0 = buffer.readLong();
    packet.friendId = result0;
    const result1 = buffer.readLong();
    packet.userId = result1;
    return packet;
};

export default ReadFriendMessageRequest;
