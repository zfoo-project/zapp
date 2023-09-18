// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const CreateChannelRequest = function(channelBoxName, channelName, groupId) {
    this.channelBoxName = channelBoxName; // java.lang.String
    this.channelName = channelName; // java.lang.String
    this.groupId = groupId; // long
};

CreateChannelRequest.prototype.protocolId = function() {
    return 18302;
};

CreateChannelRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.channelBoxName);
    buffer.writeString(packet.channelName);
    buffer.writeLong(packet.groupId);
};

CreateChannelRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new CreateChannelRequest();
    const result0 = buffer.readString();
    packet.channelBoxName = result0;
    const result1 = buffer.readString();
    packet.channelName = result1;
    const result2 = buffer.readLong();
    packet.groupId = result2;
    return packet;
};

export default CreateChannelRequest;
