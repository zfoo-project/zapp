// 好友聊天
//
// @author jaysunxiao
// @version 1.0
// @since 2019-11-14 10:54
const ReadFriendMessageResponse = function(friendId, readTime, refreshTime) {
    this.friendId = friendId; // long
    this.readTime = readTime; // long
    this.refreshTime = refreshTime; // long
};

ReadFriendMessageResponse.prototype.protocolId = function() {
    return 15203;
};

ReadFriendMessageResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.friendId);
    buffer.writeLong(packet.readTime);
    buffer.writeLong(packet.refreshTime);
};

ReadFriendMessageResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new ReadFriendMessageResponse();
    const result0 = buffer.readLong();
    packet.friendId = result0;
    const result1 = buffer.readLong();
    packet.readTime = result1;
    const result2 = buffer.readLong();
    packet.refreshTime = result2;
    return packet;
};

export default ReadFriendMessageResponse;
