// @author jaysunxiao
// @version 1.0
// @since 2019-11-18 10:46
const NewFriendNotice = function(userCacheA, userCacheB) {
    this.userCacheA = userCacheA; // com.zfoo.app.zapp.common.protocol.cache.model.UserCache
    this.userCacheB = userCacheB; // com.zfoo.app.zapp.common.protocol.cache.model.UserCache
};

NewFriendNotice.prototype.protocolId = function() {
    return 16002;
};

NewFriendNotice.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writePacket(packet.userCacheA, 3000);
    buffer.writePacket(packet.userCacheB, 3000);
};

NewFriendNotice.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new NewFriendNotice();
    const result0 = buffer.readPacket(3000);
    packet.userCacheA = result0;
    const result1 = buffer.readPacket(3000);
    packet.userCacheB = result1;
    return packet;
};

export default NewFriendNotice;
