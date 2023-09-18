// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const SaveGroupSettingResponse = function(groupId, name) {
    this.groupId = groupId; // long
    this.name = name; // java.lang.String
};

SaveGroupSettingResponse.prototype.protocolId = function() {
    return 18205;
};

SaveGroupSettingResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.groupId);
    buffer.writeString(packet.name);
};

SaveGroupSettingResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new SaveGroupSettingResponse();
    const result0 = buffer.readLong();
    packet.groupId = result0;
    const result1 = buffer.readString();
    packet.name = result1;
    return packet;
};

export default SaveGroupSettingResponse;
