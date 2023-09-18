// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const AllInviteGroupCodeResponse = function(inviteCodes) {
    this.inviteCodes = inviteCodes; // java.util.List<com.zfoo.app.zapp.common.protocol.group.member.model.InviteCodeVO>
};

AllInviteGroupCodeResponse.prototype.protocolId = function() {
    return 18425;
};

AllInviteGroupCodeResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writePacketArray(packet.inviteCodes, 18400);
};

AllInviteGroupCodeResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new AllInviteGroupCodeResponse();
    const list0 = buffer.readPacketArray(18400);
    packet.inviteCodes = list0;
    return packet;
};

export default AllInviteGroupCodeResponse;
