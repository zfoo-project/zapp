// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const SaveChannelAuthRequest = function(channelAuths, channelId, groupId) {
    this.channelAuths = channelAuths; // java.util.List<com.zfoo.app.zapp.common.protocol.group.model.ChannelAuthVO>
    this.channelId = channelId; // long
    this.groupId = groupId; // long
};

SaveChannelAuthRequest.prototype.protocolId = function() {
    return 18512;
};

SaveChannelAuthRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writePacketArray(packet.channelAuths, 18012);
    buffer.writeLong(packet.channelId);
    buffer.writeLong(packet.groupId);
};

SaveChannelAuthRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new SaveChannelAuthRequest();
    const list0 = buffer.readPacketArray(18012);
    packet.channelAuths = list0;
    const result1 = buffer.readLong();
    packet.channelId = result1;
    const result2 = buffer.readLong();
    packet.groupId = result2;
    return packet;
};

export default SaveChannelAuthRequest;
