// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const CreateGroupAuthRequest = function(groupId, name) {
    this.groupId = groupId; // long
    // 身份名称
    this.name = name; // java.lang.String
};

CreateGroupAuthRequest.prototype.protocolId = function() {
    return 18500;
};

CreateGroupAuthRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.groupId);
    buffer.writeString(packet.name);
};

CreateGroupAuthRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new CreateGroupAuthRequest();
    const result0 = buffer.readLong();
    packet.groupId = result0;
    const result1 = buffer.readString();
    packet.name = result1;
    return packet;
};

export default CreateGroupAuthRequest;
