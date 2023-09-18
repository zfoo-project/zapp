// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const RefreshChannelTimeRequest = function(allChannelIds, channelId, groupId, refreshTime) {
    this.allChannelIds = allChannelIds; // java.util.List<java.lang.Long>
    this.channelId = channelId; // long
    this.groupId = groupId; // long
    this.refreshTime = refreshTime; // long
};

RefreshChannelTimeRequest.prototype.protocolId = function() {
    return 1310;
};

RefreshChannelTimeRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLongArray(packet.allChannelIds);
    buffer.writeLong(packet.channelId);
    buffer.writeLong(packet.groupId);
    buffer.writeLong(packet.refreshTime);
};

RefreshChannelTimeRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new RefreshChannelTimeRequest();
    const list0 = buffer.readLongArray();
    packet.allChannelIds = list0;
    const result1 = buffer.readLong();
    packet.channelId = result1;
    const result2 = buffer.readLong();
    packet.groupId = result2;
    const result3 = buffer.readLong();
    packet.refreshTime = result3;
    return packet;
};

export default RefreshChannelTimeRequest;
