// @author jaysunxiao
// @version 1.0
// @since 2019-11-13 18:41
const RejectFriendResponse = function(friendId) {
    this.friendId = friendId; // long
};

RejectFriendResponse.prototype.protocolId = function() {
    return 15103;
};

RejectFriendResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.friendId);
};

RejectFriendResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new RejectFriendResponse();
    const result0 = buffer.readLong();
    packet.friendId = result0;
    return packet;
};

export default RejectFriendResponse;
