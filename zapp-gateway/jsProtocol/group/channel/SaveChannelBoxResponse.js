// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 15:33
const SaveChannelBoxResponse = function(groupVO) {
    this.groupVO = groupVO; // com.zfoo.app.zapp.common.protocol.group.model.GroupVO
};

SaveChannelBoxResponse.prototype.protocolId = function() {
    return 18311;
};

SaveChannelBoxResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writePacket(packet.groupVO, 18000);
};

SaveChannelBoxResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new SaveChannelBoxResponse();
    const result0 = buffer.readPacket(18000);
    packet.groupVO = result0;
    return packet;
};

export default SaveChannelBoxResponse;
