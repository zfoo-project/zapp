// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const RefreshGroupNameRequest = function(groupId) {
    this.groupId = groupId; // long
};

RefreshGroupNameRequest.prototype.protocolId = function() {
    return 18206;
};

RefreshGroupNameRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.groupId);
};

RefreshGroupNameRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new RefreshGroupNameRequest();
    const result0 = buffer.readLong();
    packet.groupId = result0;
    return packet;
};

export default RefreshGroupNameRequest;
