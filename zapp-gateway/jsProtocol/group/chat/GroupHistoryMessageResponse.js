// 好友聊天
//
// @author jaysunxiao
// @version 1.0
// @since 2019-11-14 10:54
const GroupHistoryMessageResponse = function(channelId, chatMessages, groupId) {
    this.channelId = channelId; // long
    this.chatMessages = chatMessages; // java.util.List<com.zfoo.app.zapp.common.protocol.common.ChatMessage>
    this.groupId = groupId; // long
};

GroupHistoryMessageResponse.prototype.protocolId = function() {
    return 18103;
};

GroupHistoryMessageResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.channelId);
    buffer.writePacketArray(packet.chatMessages, 120);
    buffer.writeLong(packet.groupId);
};

GroupHistoryMessageResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new GroupHistoryMessageResponse();
    const result0 = buffer.readLong();
    packet.channelId = result0;
    const list1 = buffer.readPacketArray(120);
    packet.chatMessages = list1;
    const result2 = buffer.readLong();
    packet.groupId = result2;
    return packet;
};

export default GroupHistoryMessageResponse;
