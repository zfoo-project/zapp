// @author jaysunxiao
// @version 1.0
// @since 2019-11-13 18:41
const ApplyFriendRequest = function(friendId, userId) {
    this.friendId = friendId; // long
    this.userId = userId; // long
};

ApplyFriendRequest.prototype.protocolId = function() {
    return 15100;
};

ApplyFriendRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.friendId);
    buffer.writeLong(packet.userId);
};

ApplyFriendRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new ApplyFriendRequest();
    const result0 = buffer.readLong();
    packet.friendId = result0;
    const result1 = buffer.readLong();
    packet.userId = result1;
    return packet;
};

export default ApplyFriendRequest;
