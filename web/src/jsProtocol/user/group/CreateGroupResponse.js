// 创建群组
//
// @author jaysunxiao
// @version 1.0
// @since 2020-04-22 13:20
const CreateGroupResponse = function(group) {
    this.group = group; // com.zfoo.app.zapp.common.protocol.group.model.GroupVO
};

CreateGroupResponse.prototype.protocolId = function() {
    return 1301;
};

CreateGroupResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writePacket(packet.group, 18000);
};

CreateGroupResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new CreateGroupResponse();
    const result0 = buffer.readPacket(18000);
    packet.group = result0;
    return packet;
};

export default CreateGroupResponse;
