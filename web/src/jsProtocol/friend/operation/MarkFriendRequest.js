// @author jaysunxiao
// @version 1.0
// @since 2019-11-13 18:41
const MarkFriendRequest = function(friendId, tag, userId) {
    // 好友id
    this.friendId = friendId; // long
    this.tag = tag; // java.lang.String
    this.userId = userId; // long
};

MarkFriendRequest.prototype.protocolId = function() {
    return 15112;
};

MarkFriendRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.friendId);
    buffer.writeString(packet.tag);
    buffer.writeLong(packet.userId);
};

MarkFriendRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new MarkFriendRequest();
    const result0 = buffer.readLong();
    packet.friendId = result0;
    const result1 = buffer.readString();
    packet.tag = result1;
    const result2 = buffer.readLong();
    packet.userId = result2;
    return packet;
};

export default MarkFriendRequest;
