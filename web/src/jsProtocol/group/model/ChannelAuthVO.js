// @author jaysunxiao
// @version 1.0
// @since 2020-05-05 12:35
const ChannelAuthVO = function(channelAuth, id) {
    // 频道权限
    this.channelAuth = channelAuth; // int
    // 对应于GroupAuthPO里的id
    this.id = id; // long
};

ChannelAuthVO.prototype.protocolId = function() {
    return 18012;
};

ChannelAuthVO.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeInt(packet.channelAuth);
    buffer.writeLong(packet.id);
};

ChannelAuthVO.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new ChannelAuthVO();
    const result0 = buffer.readInt();
    packet.channelAuth = result0;
    const result1 = buffer.readLong();
    packet.id = result1;
    return packet;
};

export default ChannelAuthVO;
