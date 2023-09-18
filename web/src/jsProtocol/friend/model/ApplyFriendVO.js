// @author jaysunxiao
// @version 1.0
// @since 2019-12-11 10:05
const ApplyFriendVO = function(friendCache, friendId, status, timestamp) {
    this.friendCache = friendCache; // com.zfoo.app.zapp.common.protocol.cache.model.UserCache
    this.friendId = friendId; // long
    this.status = status; // int
    this.timestamp = timestamp; // long
};

ApplyFriendVO.prototype.protocolId = function() {
    return 15000;
};

ApplyFriendVO.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writePacket(packet.friendCache, 3000);
    buffer.writeLong(packet.friendId);
    buffer.writeInt(packet.status);
    buffer.writeLong(packet.timestamp);
};

ApplyFriendVO.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new ApplyFriendVO();
    const result0 = buffer.readPacket(3000);
    packet.friendCache = result0;
    const result1 = buffer.readLong();
    packet.friendId = result1;
    const result2 = buffer.readInt();
    packet.status = result2;
    const result3 = buffer.readLong();
    packet.timestamp = result3;
    return packet;
};

export default ApplyFriendVO;
