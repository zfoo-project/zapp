// @author jaysunxiao
// @version 1.0
// @since 2019-11-13 18:41
const DeleteFriendResponse = function(friendId) {
    this.friendId = friendId; // long
};

DeleteFriendResponse.prototype.protocolId = function() {
    return 15107;
};

DeleteFriendResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.friendId);
};

DeleteFriendResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new DeleteFriendResponse();
    const result0 = buffer.readLong();
    packet.friendId = result0;
    return packet;
};

export default DeleteFriendResponse;
