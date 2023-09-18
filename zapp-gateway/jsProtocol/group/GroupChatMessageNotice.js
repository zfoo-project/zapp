// @author jaysunxiao
// @version 1.0
// @since 2020-05-07 10:49
const GroupChatMessageNotice = function(channelId, chatMessage, groupId) {
    this.channelId = channelId; // long
    this.chatMessage = chatMessage; // com.zfoo.app.zapp.common.protocol.common.ChatMessage
    this.groupId = groupId; // long
};

GroupChatMessageNotice.prototype.protocolId = function() {
    return 19006;
};

GroupChatMessageNotice.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.channelId);
    buffer.writePacket(packet.chatMessage, 120);
    buffer.writeLong(packet.groupId);
};

GroupChatMessageNotice.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new GroupChatMessageNotice();
    const result0 = buffer.readLong();
    packet.channelId = result0;
    const result1 = buffer.readPacket(120);
    packet.chatMessage = result1;
    const result2 = buffer.readLong();
    packet.groupId = result2;
    return packet;
};

export default GroupChatMessageNotice;
