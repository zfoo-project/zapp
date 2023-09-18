// 群组聊天
//
// @author jaysunxiao
// @version 1.0
// @since 2020-04-21 18:20
const DeleteGroupMessageRequest = function(channelId, groupId, messageId) {
    this.channelId = channelId; // long
    this.groupId = groupId; // long
    this.messageId = messageId; // long
};

DeleteGroupMessageRequest.prototype.protocolId = function() {
    return 18105;
};

DeleteGroupMessageRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.channelId);
    buffer.writeLong(packet.groupId);
    buffer.writeLong(packet.messageId);
};

DeleteGroupMessageRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new DeleteGroupMessageRequest();
    const result0 = buffer.readLong();
    packet.channelId = result0;
    const result1 = buffer.readLong();
    packet.groupId = result1;
    const result2 = buffer.readLong();
    packet.messageId = result2;
    return packet;
};

export default DeleteGroupMessageRequest;
