// 创建群组
//
// @author jaysunxiao
// @version 1.0
// @since 2020-04-22 13:20
const CreateGroupRequest = function(groupName) {
    this.groupName = groupName; // java.lang.String
};

CreateGroupRequest.prototype.protocolId = function() {
    return 1300;
};

CreateGroupRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.groupName);
};

CreateGroupRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new CreateGroupRequest();
    const result0 = buffer.readString();
    packet.groupName = result0;
    return packet;
};

export default CreateGroupRequest;
