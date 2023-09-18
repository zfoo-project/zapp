// @author jaysunxiao
// @version 1.0
// @since 2019-11-08 10:35
const GetUserCacheRequest = function(userIds) {
    this.userIds = userIds; // java.util.Set<java.lang.Long>
};

GetUserCacheRequest.prototype.protocolId = function() {
    return 3021;
};

GetUserCacheRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLongArray(packet.userIds);
};

GetUserCacheRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new GetUserCacheRequest();
    const set0 = buffer.readLongArray();
    packet.userIds = set0;
    return packet;
};

export default GetUserCacheRequest;
