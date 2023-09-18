// @author jaysunxiao
// @version 1.0
// @since 2020-05-04 19:59
const GroupMemberSimpleVO = function(groupAuthIds, groupId, groupTime, memberId) {
    this.groupAuthIds = groupAuthIds; // java.util.List<java.lang.Long>
    this.groupId = groupId; // long
    this.groupTime = groupTime; // com.zfoo.app.zapp.common.protocol.group.model.GroupTimeVO
    this.memberId = memberId; // long
};

GroupMemberSimpleVO.prototype.protocolId = function() {
    return 18013;
};

GroupMemberSimpleVO.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLongArray(packet.groupAuthIds);
    buffer.writeLong(packet.groupId);
    buffer.writePacket(packet.groupTime, 18014);
    buffer.writeLong(packet.memberId);
};

GroupMemberSimpleVO.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new GroupMemberSimpleVO();
    const list0 = buffer.readLongArray();
    packet.groupAuthIds = list0;
    const result1 = buffer.readLong();
    packet.groupId = result1;
    const result2 = buffer.readPacket(18014);
    packet.groupTime = result2;
    const result3 = buffer.readLong();
    packet.memberId = result3;
    return packet;
};

export default GroupMemberSimpleVO;
