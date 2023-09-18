// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const SaveChannelRequest = function(channelId, channelName, groupId) {
    this.channelId = channelId; // long
    this.channelName = channelName; // java.lang.String
    this.groupId = groupId; // long
};

SaveChannelRequest.prototype.protocolId = function() {
    return 18308;
};

SaveChannelRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.channelId);
    buffer.writeString(packet.channelName);
    buffer.writeLong(packet.groupId);
};

SaveChannelRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new SaveChannelRequest();
    const result0 = buffer.readLong();
    packet.channelId = result0;
    const result1 = buffer.readString();
    packet.channelName = result1;
    const result2 = buffer.readLong();
    packet.groupId = result2;
    return packet;
};

export default SaveChannelRequest;
