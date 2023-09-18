// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 15:33
const DeleteChannelRequest = function(channelId, groupId) {
    this.channelId = channelId; // long
    this.groupId = groupId; // long
};

DeleteChannelRequest.prototype.protocolId = function() {
    return 18306;
};

DeleteChannelRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.channelId);
    buffer.writeLong(packet.groupId);
};

DeleteChannelRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new DeleteChannelRequest();
    const result0 = buffer.readLong();
    packet.channelId = result0;
    const result1 = buffer.readLong();
    packet.groupId = result1;
    return packet;
};

export default DeleteChannelRequest;
