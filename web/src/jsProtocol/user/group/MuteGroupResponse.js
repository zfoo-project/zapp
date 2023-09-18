// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const MuteGroupResponse = function(groupId, mute) {
    this.groupId = groupId; // long
    this.mute = mute; // boolean
};

MuteGroupResponse.prototype.protocolId = function() {
    return 1313;
};

MuteGroupResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.groupId);
    buffer.writeBoolean(packet.mute);
};

MuteGroupResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new MuteGroupResponse();
    const result0 = buffer.readLong();
    packet.groupId = result0;
    const result1 = buffer.readBoolean(); 
    packet.mute = result1;
    return packet;
};

export default MuteGroupResponse;
