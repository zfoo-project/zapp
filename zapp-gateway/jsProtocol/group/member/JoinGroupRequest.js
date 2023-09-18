// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const JoinGroupRequest = function(inviteCode) {
    this.inviteCode = inviteCode; // java.lang.String
};

JoinGroupRequest.prototype.protocolId = function() {
    return 18412;
};

JoinGroupRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.inviteCode);
};

JoinGroupRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new JoinGroupRequest();
    const result0 = buffer.readString();
    packet.inviteCode = result0;
    return packet;
};

export default JoinGroupRequest;
