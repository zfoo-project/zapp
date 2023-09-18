// @author jaysunxiao
// @version 1.0
// @since 2019-12-11 10:05
const FriendInfoVO = function(friendId, readTime, refreshTime, tag) {
    this.friendId = friendId; // long
    this.readTime = readTime; // long
    this.refreshTime = refreshTime; // long
    this.tag = tag; // java.lang.String
};

FriendInfoVO.prototype.protocolId = function() {
    return 15001;
};

FriendInfoVO.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.friendId);
    buffer.writeLong(packet.readTime);
    buffer.writeLong(packet.refreshTime);
    buffer.writeString(packet.tag);
};

FriendInfoVO.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new FriendInfoVO();
    const result0 = buffer.readLong();
    packet.friendId = result0;
    const result1 = buffer.readLong();
    packet.readTime = result1;
    const result2 = buffer.readLong();
    packet.refreshTime = result2;
    const result3 = buffer.readString();
    packet.tag = result3;
    return packet;
};

export default FriendInfoVO;
