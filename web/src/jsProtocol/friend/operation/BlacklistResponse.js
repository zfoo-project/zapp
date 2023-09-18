// @author jaysunxiao
// @version 1.0
// @since 2019-11-13 18:41
const BlacklistResponse = function(userCache) {
    this.userCache = userCache; // com.zfoo.app.zapp.common.protocol.cache.model.UserCache
};

BlacklistResponse.prototype.protocolId = function() {
    return 15109;
};

BlacklistResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writePacket(packet.userCache, 3000);
};

BlacklistResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new BlacklistResponse();
    const result0 = buffer.readPacket(3000);
    packet.userCache = result0;
    return packet;
};

export default BlacklistResponse;
