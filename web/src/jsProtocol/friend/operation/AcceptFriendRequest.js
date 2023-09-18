// 同意好友申请
//
// @author jaysunxiao
// @version 1.0
// @since 2019-11-14 10:54
const AcceptFriendRequest = function(friendId, userId) {
    // 申请人用户的id
    this.friendId = friendId; // long
    this.userId = userId; // long
};

AcceptFriendRequest.prototype.protocolId = function() {
    return 15104;
};

AcceptFriendRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.friendId);
    buffer.writeLong(packet.userId);
};

AcceptFriendRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new AcceptFriendRequest();
    const result0 = buffer.readLong();
    packet.friendId = result0;
    const result1 = buffer.readLong();
    packet.userId = result1;
    return packet;
};

export default AcceptFriendRequest;
