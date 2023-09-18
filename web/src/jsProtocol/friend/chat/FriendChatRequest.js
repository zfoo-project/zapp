// 好友聊天
//
// @author jaysunxiao
// @version 1.0
// @since 2019-11-14 10:54
const FriendChatRequest = function(chatMessage, friendId, type, userId) {
    this.chatMessage = chatMessage; // java.lang.String
    this.friendId = friendId; // long
    this.type = type; // byte
    this.userId = userId; // long
};

FriendChatRequest.prototype.protocolId = function() {
    return 15200;
};

FriendChatRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.chatMessage);
    buffer.writeLong(packet.friendId);
    buffer.writeByte(packet.type);
    buffer.writeLong(packet.userId);
};

FriendChatRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new FriendChatRequest();
    const result0 = buffer.readString();
    packet.chatMessage = result0;
    const result1 = buffer.readLong();
    packet.friendId = result1;
    const result2 = buffer.readByte();
    packet.type = result2;
    const result3 = buffer.readLong();
    packet.userId = result3;
    return packet;
};

export default FriendChatRequest;
