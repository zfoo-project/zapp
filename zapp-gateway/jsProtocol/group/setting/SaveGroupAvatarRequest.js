// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const SaveGroupAvatarRequest = function(avatar, groupId) {
    this.avatar = avatar; // java.lang.String
    this.groupId = groupId; // long
};

SaveGroupAvatarRequest.prototype.protocolId = function() {
    return 18200;
};

SaveGroupAvatarRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.avatar);
    buffer.writeLong(packet.groupId);
};

SaveGroupAvatarRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new SaveGroupAvatarRequest();
    const result0 = buffer.readString();
    packet.avatar = result0;
    const result1 = buffer.readLong();
    packet.groupId = result1;
    return packet;
};

export default SaveGroupAvatarRequest;
