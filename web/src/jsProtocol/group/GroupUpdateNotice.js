// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const GroupUpdateNotice = function(groupVO) {
    this.groupVO = groupVO; // com.zfoo.app.zapp.common.protocol.group.model.GroupVO
};

GroupUpdateNotice.prototype.protocolId = function() {
    return 19001;
};

GroupUpdateNotice.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writePacket(packet.groupVO, 18000);
};

GroupUpdateNotice.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new GroupUpdateNotice();
    const result0 = buffer.readPacket(18000);
    packet.groupVO = result0;
    return packet;
};

export default GroupUpdateNotice;
