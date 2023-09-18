// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const DeleteInviteGroupCodeRequest = function(inviteCode) {
    this.inviteCode = inviteCode; // java.lang.String
};

DeleteInviteGroupCodeRequest.prototype.protocolId = function() {
    return 18420;
};

DeleteInviteGroupCodeRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.inviteCode);
};

DeleteInviteGroupCodeRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new DeleteInviteGroupCodeRequest();
    const result0 = buffer.readString();
    packet.inviteCode = result0;
    return packet;
};

export default DeleteInviteGroupCodeRequest;
