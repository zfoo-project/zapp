// @author jaysunxiao
// @version 1.0
// @since 2020-04-22 11:43
const ChannelVO = function(channelAuths, id, name, refreshTime) {
    this.channelAuths = channelAuths; // java.util.List<com.zfoo.app.zapp.common.protocol.group.model.ChannelAuthVO>
    this.id = id; // long
    this.name = name; // java.lang.String
    this.refreshTime = refreshTime; // long
};

ChannelVO.prototype.protocolId = function() {
    return 18010;
};

ChannelVO.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writePacketArray(packet.channelAuths, 18012);
    buffer.writeLong(packet.id);
    buffer.writeString(packet.name);
    buffer.writeLong(packet.refreshTime);
};

ChannelVO.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new ChannelVO();
    const list0 = buffer.readPacketArray(18012);
    packet.channelAuths = list0;
    const result1 = buffer.readLong();
    packet.id = result1;
    const result2 = buffer.readString();
    packet.name = result2;
    const result3 = buffer.readLong();
    packet.refreshTime = result3;
    return packet;
};

export default ChannelVO;
