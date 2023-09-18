// @author jaysunxiao
// @version 1.0
// @since 2020-08-05 12:35
const GroupMemberVO = function(groupAuthIds, userCache) {
    // 用户在群里的身份
    this.groupAuthIds = groupAuthIds; // java.util.List<java.lang.Long>
    // 用户的信息
    this.userCache = userCache; // com.zfoo.app.zapp.common.protocol.cache.model.UserCache
};

GroupMemberVO.prototype.protocolId = function() {
    return 18401;
};

GroupMemberVO.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLongArray(packet.groupAuthIds);
    buffer.writePacket(packet.userCache, 3000);
};

GroupMemberVO.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new GroupMemberVO();
    const list0 = buffer.readLongArray();
    packet.groupAuthIds = list0;
    const result1 = buffer.readPacket(3000);
    packet.userCache = result1;
    return packet;
};

export default GroupMemberVO;
