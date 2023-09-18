// @author jaysunxiao
// @version 1.0
// @since 2020-04-22 10:23
const GroupTimeVO = function(channelTimes, groupId, mute) {
    this.channelTimes = channelTimes; // java.util.List<com.zfoo.app.zapp.common.protocol.group.model.ChannelTimeVO>
    this.groupId = groupId; // long
    this.mute = mute; // boolean
};

GroupTimeVO.prototype.protocolId = function() {
    return 18014;
};

GroupTimeVO.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writePacketArray(packet.channelTimes, 18015);
    buffer.writeLong(packet.groupId);
    buffer.writeBoolean(packet.mute);
};

GroupTimeVO.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new GroupTimeVO();
    const list0 = buffer.readPacketArray(18015);
    packet.channelTimes = list0;
    const result1 = buffer.readLong();
    packet.groupId = result1;
    const result2 = buffer.readBoolean(); 
    packet.mute = result2;
    return packet;
};

export default GroupTimeVO;
