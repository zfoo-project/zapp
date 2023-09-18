// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 15:33
const CreateChannelResponse = function(groupVO) {
    this.groupVO = groupVO; // com.zfoo.app.zapp.common.protocol.group.model.GroupVO
};

CreateChannelResponse.prototype.protocolId = function() {
    return 18303;
};

CreateChannelResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writePacket(packet.groupVO, 18000);
};

CreateChannelResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new CreateChannelResponse();
    const result0 = buffer.readPacket(18000);
    packet.groupVO = result0;
    return packet;
};

export default CreateChannelResponse;
