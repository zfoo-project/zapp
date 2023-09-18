// @author jaysunxiao
// @version 1.0
// @since 2020-04-23 14:20
const MuteChannelRequest = function(allChannelIds, channelId, groupId, mute) {
    this.allChannelIds = allChannelIds; // java.util.List<java.lang.Long>
    this.channelId = channelId; // long
    this.groupId = groupId; // long
    this.mute = mute; // boolean
};

MuteChannelRequest.prototype.protocolId = function() {
    return 1314;
};

MuteChannelRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLongArray(packet.allChannelIds);
    buffer.writeLong(packet.channelId);
    buffer.writeLong(packet.groupId);
    buffer.writeBoolean(packet.mute);
};

MuteChannelRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new MuteChannelRequest();
    const list0 = buffer.readLongArray();
    packet.allChannelIds = list0;
    const result1 = buffer.readLong();
    packet.channelId = result1;
    const result2 = buffer.readLong();
    packet.groupId = result2;
    const result3 = buffer.readBoolean(); 
    packet.mute = result3;
    return packet;
};

export default MuteChannelRequest;
