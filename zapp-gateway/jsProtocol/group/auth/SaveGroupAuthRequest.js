// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const SaveGroupAuthRequest = function(groupAuths, groupId) {
    this.groupAuths = groupAuths; // java.util.List<com.zfoo.app.zapp.common.protocol.group.model.GroupAuthVO>
    this.groupId = groupId; // long
};

SaveGroupAuthRequest.prototype.protocolId = function() {
    return 18502;
};

SaveGroupAuthRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writePacketArray(packet.groupAuths, 18001);
    buffer.writeLong(packet.groupId);
};

SaveGroupAuthRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new SaveGroupAuthRequest();
    const list0 = buffer.readPacketArray(18001);
    packet.groupAuths = list0;
    const result1 = buffer.readLong();
    packet.groupId = result1;
    return packet;
};

export default SaveGroupAuthRequest;
