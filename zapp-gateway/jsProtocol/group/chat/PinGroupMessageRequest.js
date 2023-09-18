// 群组聊天
//
// @author jaysunxiao
// @version 1.0
// @since 2020-04-21 18:20
const PinGroupMessageRequest = function(channelId, groupId, messageId) {
    this.channelId = channelId; // long
    this.groupId = groupId; // long
    this.messageId = messageId; // long
};

PinGroupMessageRequest.prototype.protocolId = function() {
    return 18109;
};

PinGroupMessageRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.channelId);
    buffer.writeLong(packet.groupId);
    buffer.writeLong(packet.messageId);
};

PinGroupMessageRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new PinGroupMessageRequest();
    const result0 = buffer.readLong();
    packet.channelId = result0;
    const result1 = buffer.readLong();
    packet.groupId = result1;
    const result2 = buffer.readLong();
    packet.messageId = result2;
    return packet;
};

export default PinGroupMessageRequest;
