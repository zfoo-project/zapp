// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const SaveChannelBoxRequest = function(groupId, newChannelBoxName, oldChannelBoxName) {
    this.groupId = groupId; // long
    this.newChannelBoxName = newChannelBoxName; // java.lang.String
    this.oldChannelBoxName = oldChannelBoxName; // java.lang.String
};

SaveChannelBoxRequest.prototype.protocolId = function() {
    return 18310;
};

SaveChannelBoxRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.groupId);
    buffer.writeString(packet.newChannelBoxName);
    buffer.writeString(packet.oldChannelBoxName);
};

SaveChannelBoxRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new SaveChannelBoxRequest();
    const result0 = buffer.readLong();
    packet.groupId = result0;
    const result1 = buffer.readString();
    packet.newChannelBoxName = result1;
    const result2 = buffer.readString();
    packet.oldChannelBoxName = result2;
    return packet;
};

export default SaveChannelBoxRequest;
