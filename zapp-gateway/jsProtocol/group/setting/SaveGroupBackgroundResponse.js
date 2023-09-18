// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const SaveGroupBackgroundResponse = function(background, groupId) {
    this.background = background; // java.lang.String
    this.groupId = groupId; // long
};

SaveGroupBackgroundResponse.prototype.protocolId = function() {
    return 18203;
};

SaveGroupBackgroundResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.background);
    buffer.writeLong(packet.groupId);
};

SaveGroupBackgroundResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new SaveGroupBackgroundResponse();
    const result0 = buffer.readString();
    packet.background = result0;
    const result1 = buffer.readLong();
    packet.groupId = result1;
    return packet;
};

export default SaveGroupBackgroundResponse;
