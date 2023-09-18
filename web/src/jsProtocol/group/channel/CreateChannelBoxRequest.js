// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 15:33
const CreateChannelBoxRequest = function(channelBoxName, groupId) {
    this.channelBoxName = channelBoxName; // java.lang.String
    this.groupId = groupId; // long
};

CreateChannelBoxRequest.prototype.protocolId = function() {
    return 18300;
};

CreateChannelBoxRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.channelBoxName);
    buffer.writeLong(packet.groupId);
};

CreateChannelBoxRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new CreateChannelBoxRequest();
    const result0 = buffer.readString();
    packet.channelBoxName = result0;
    const result1 = buffer.readLong();
    packet.groupId = result1;
    return packet;
};

export default CreateChannelBoxRequest;
