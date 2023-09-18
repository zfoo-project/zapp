// 群组聊天
//
// @author jaysunxiao
// @version 1.0
// @since 2020-04-21 18:20
const EditGroupMessageRequest = function(channelId, chatMessage, groupId, messageId) {
    this.channelId = channelId; // long
    this.chatMessage = chatMessage; // java.lang.String
    this.groupId = groupId; // long
    this.messageId = messageId; // long
};

EditGroupMessageRequest.prototype.protocolId = function() {
    return 18107;
};

EditGroupMessageRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.channelId);
    buffer.writeString(packet.chatMessage);
    buffer.writeLong(packet.groupId);
    buffer.writeLong(packet.messageId);
};

EditGroupMessageRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new EditGroupMessageRequest();
    const result0 = buffer.readLong();
    packet.channelId = result0;
    const result1 = buffer.readString();
    packet.chatMessage = result1;
    const result2 = buffer.readLong();
    packet.groupId = result2;
    const result3 = buffer.readLong();
    packet.messageId = result3;
    return packet;
};

export default EditGroupMessageRequest;
