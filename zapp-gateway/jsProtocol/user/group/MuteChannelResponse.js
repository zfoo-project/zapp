// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const MuteChannelResponse = function(channelId, groupId, mute, refreshTime) {
    this.channelId = channelId; // long
    this.groupId = groupId; // long
    this.mute = mute; // boolean
    this.refreshTime = refreshTime; // long
};

MuteChannelResponse.prototype.protocolId = function() {
    return 1315;
};

MuteChannelResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.channelId);
    buffer.writeLong(packet.groupId);
    buffer.writeBoolean(packet.mute);
    buffer.writeLong(packet.refreshTime);
};

MuteChannelResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new MuteChannelResponse();
    const result0 = buffer.readLong();
    packet.channelId = result0;
    const result1 = buffer.readLong();
    packet.groupId = result1;
    const result2 = buffer.readBoolean(); 
    packet.mute = result2;
    const result3 = buffer.readLong();
    packet.refreshTime = result3;
    return packet;
};

export default MuteChannelResponse;
