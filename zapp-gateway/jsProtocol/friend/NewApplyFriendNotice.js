// @author jaysunxiao
// @version 1.0
// @since 2019-11-18 10:46
const NewApplyFriendNotice = function(applyFriendVO) {
    this.applyFriendVO = applyFriendVO; // com.zfoo.app.zapp.common.protocol.friend.model.ApplyFriendVO
};

NewApplyFriendNotice.prototype.protocolId = function() {
    return 16001;
};

NewApplyFriendNotice.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writePacket(packet.applyFriendVO, 15000);
};

NewApplyFriendNotice.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new NewApplyFriendNotice();
    const result0 = buffer.readPacket(15000);
    packet.applyFriendVO = result0;
    return packet;
};

export default NewApplyFriendNotice;
