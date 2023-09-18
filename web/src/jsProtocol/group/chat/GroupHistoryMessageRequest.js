// 好友聊天
//
// @author jaysunxiao
// @version 1.0
// @since 2020-05-07 10:54
const GroupHistoryMessageRequest = function(channelId, groupId, lastMessageId) {
    this.channelId = channelId; // long
    this.groupId = groupId; // long
    // 最老消息的id
    this.lastMessageId = lastMessageId; // long
};

GroupHistoryMessageRequest.prototype.protocolId = function() {
    return 18102;
};

GroupHistoryMessageRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.channelId);
    buffer.writeLong(packet.groupId);
    buffer.writeLong(packet.lastMessageId);
};

GroupHistoryMessageRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new GroupHistoryMessageRequest();
    const result0 = buffer.readLong();
    packet.channelId = result0;
    const result1 = buffer.readLong();
    packet.groupId = result1;
    const result2 = buffer.readLong();
    packet.lastMessageId = result2;
    return packet;
};

export default GroupHistoryMessageRequest;
