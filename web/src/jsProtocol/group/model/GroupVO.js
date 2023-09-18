// @author jaysunxiao
// @version 1.0
// @since 2020-04-22 11:43
const GroupVO = function(adminId, avatar, background, channelBoxes, createTime, groupAuths, id, name) {
    this.adminId = adminId; // long
    this.avatar = avatar; // java.lang.String
    this.background = background; // java.lang.String
    this.channelBoxes = channelBoxes; // java.util.List<com.zfoo.app.zapp.common.protocol.group.model.ChannelBoxVO>
    this.createTime = createTime; // long
    this.groupAuths = groupAuths; // java.util.List<com.zfoo.app.zapp.common.protocol.group.model.GroupAuthVO>
    this.id = id; // long
    this.name = name; // java.lang.String
};

GroupVO.prototype.protocolId = function() {
    return 18000;
};

GroupVO.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.adminId);
    buffer.writeString(packet.avatar);
    buffer.writeString(packet.background);
    buffer.writePacketArray(packet.channelBoxes, 18011);
    buffer.writeLong(packet.createTime);
    buffer.writePacketArray(packet.groupAuths, 18001);
    buffer.writeLong(packet.id);
    buffer.writeString(packet.name);
};

GroupVO.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new GroupVO();
    const result0 = buffer.readLong();
    packet.adminId = result0;
    const result1 = buffer.readString();
    packet.avatar = result1;
    const result2 = buffer.readString();
    packet.background = result2;
    const list3 = buffer.readPacketArray(18011);
    packet.channelBoxes = list3;
    const result4 = buffer.readLong();
    packet.createTime = result4;
    const list5 = buffer.readPacketArray(18001);
    packet.groupAuths = list5;
    const result6 = buffer.readLong();
    packet.id = result6;
    const result7 = buffer.readString();
    packet.name = result7;
    return packet;
};

export default GroupVO;
