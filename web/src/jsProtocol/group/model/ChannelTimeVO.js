// @author jaysunxiao
// @version 1.0
// @since 2020-04-22 10:23
const ChannelTimeVO = function(channelId, mute, refreshTime) {
    this.channelId = channelId; // long
    this.mute = mute; // boolean
    this.refreshTime = refreshTime; // long
};

ChannelTimeVO.prototype.protocolId = function() {
    return 18015;
};

ChannelTimeVO.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.channelId);
    buffer.writeBoolean(packet.mute);
    buffer.writeLong(packet.refreshTime);
};

ChannelTimeVO.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new ChannelTimeVO();
    const result0 = buffer.readLong();
    packet.channelId = result0;
    const result1 = buffer.readBoolean(); 
    packet.mute = result1;
    const result2 = buffer.readLong();
    packet.refreshTime = result2;
    return packet;
};

export default ChannelTimeVO;
