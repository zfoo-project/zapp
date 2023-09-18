// @author jaysunxiao
// @version 1.0
// @since 2020-05-04 19:59
const GroupAuthVO = function(color, groupAuth, id, name) {
    // 颜色
    this.color = color; // java.lang.String
    // 权限类型
    this.groupAuth = groupAuth; // int
    this.id = id; // long
    // 身份名称
    this.name = name; // java.lang.String
};

GroupAuthVO.prototype.protocolId = function() {
    return 18001;
};

GroupAuthVO.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.color);
    buffer.writeInt(packet.groupAuth);
    buffer.writeLong(packet.id);
    buffer.writeString(packet.name);
};

GroupAuthVO.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new GroupAuthVO();
    const result0 = buffer.readString();
    packet.color = result0;
    const result1 = buffer.readInt();
    packet.groupAuth = result1;
    const result2 = buffer.readLong();
    packet.id = result2;
    const result3 = buffer.readString();
    packet.name = result3;
    return packet;
};

export default GroupAuthVO;
