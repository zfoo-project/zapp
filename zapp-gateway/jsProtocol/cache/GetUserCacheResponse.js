// @author jaysunxiao
// @version 1.0
// @since 2019-11-08 10:35
const GetUserCacheResponse = function(userCacheMap) {
    this.userCacheMap = userCacheMap; // java.util.Map<java.lang.Long, com.zfoo.app.zapp.common.protocol.cache.model.UserCache>
};

GetUserCacheResponse.prototype.protocolId = function() {
    return 3022;
};

GetUserCacheResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLongPacketMap(packet.userCacheMap, 3000);
};

GetUserCacheResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new GetUserCacheResponse();
    const map0 = buffer.readLongPacketMap(3000);
    packet.userCacheMap = map0;
    return packet;
};

export default GetUserCacheResponse;
