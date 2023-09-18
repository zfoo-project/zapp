// @author jaysunxiao
// @version 1.0
// @since 2020-04-22 11:43
const ChannelBoxVO = function(channels, name) {
    this.channels = channels; // java.util.List<com.zfoo.app.zapp.common.protocol.group.model.ChannelVO>
    this.name = name; // java.lang.String
};

ChannelBoxVO.prototype.protocolId = function() {
    return 18011;
};

ChannelBoxVO.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writePacketArray(packet.channels, 18010);
    buffer.writeString(packet.name);
};

ChannelBoxVO.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new ChannelBoxVO();
    const list0 = buffer.readPacketArray(18010);
    packet.channels = list0;
    const result1 = buffer.readString();
    packet.name = result1;
    return packet;
};

export default ChannelBoxVO;
