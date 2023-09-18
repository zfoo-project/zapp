// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const CreateInviteGroupCodeRequest = function(countType, expireType, groupId) {
    // 邀请码可以邀请多少个人
    this.countType = countType; // int
    // 邀请码的过期时间
    this.expireType = expireType; // int
    this.groupId = groupId; // long
};

CreateInviteGroupCodeRequest.prototype.protocolId = function() {
    return 18410;
};

CreateInviteGroupCodeRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeInt(packet.countType);
    buffer.writeInt(packet.expireType);
    buffer.writeLong(packet.groupId);
};

CreateInviteGroupCodeRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new CreateInviteGroupCodeRequest();
    const result0 = buffer.readInt();
    packet.countType = result0;
    const result1 = buffer.readInt();
    packet.expireType = result1;
    const result2 = buffer.readLong();
    packet.groupId = result2;
    return packet;
};

export default CreateInviteGroupCodeRequest;
