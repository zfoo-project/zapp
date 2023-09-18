// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const RefreshChannelTimeResponse = function(channelId, groupId, refreshTime) {
    this.channelId = channelId; // long
    this.groupId = groupId; // long
    this.refreshTime = refreshTime; // long
};

RefreshChannelTimeResponse.prototype.protocolId = function() {
    return 1311;
};

RefreshChannelTimeResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.channelId);
    buffer.writeLong(packet.groupId);
    buffer.writeLong(packet.refreshTime);
};

RefreshChannelTimeResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new RefreshChannelTimeResponse();
    const result0 = buffer.readLong();
    packet.channelId = result0;
    const result1 = buffer.readLong();
    packet.groupId = result1;
    const result2 = buffer.readLong();
    packet.refreshTime = result2;
    return packet;
};

export default RefreshChannelTimeResponse;
