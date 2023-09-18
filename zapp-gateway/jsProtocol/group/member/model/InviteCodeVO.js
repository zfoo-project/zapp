// @author jaysunxiao
// @version 1.0
// @since 2020-05-03 21:09
const InviteCodeVO = function(code, count, countType, expireTime, expireType) {
    this.code = code; // java.lang.String
    this.count = count; // int
    this.countType = countType; // int
    this.expireTime = expireTime; // long
    this.expireType = expireType; // int
};

InviteCodeVO.prototype.protocolId = function() {
    return 18400;
};

InviteCodeVO.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.code);
    buffer.writeInt(packet.count);
    buffer.writeInt(packet.countType);
    buffer.writeLong(packet.expireTime);
    buffer.writeInt(packet.expireType);
};

InviteCodeVO.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new InviteCodeVO();
    const result0 = buffer.readString();
    packet.code = result0;
    const result1 = buffer.readInt();
    packet.count = result1;
    const result2 = buffer.readInt();
    packet.countType = result2;
    const result3 = buffer.readLong();
    packet.expireTime = result3;
    const result4 = buffer.readInt();
    packet.expireType = result4;
    return packet;
};

export default InviteCodeVO;
