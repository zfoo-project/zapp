// 好友聊天
//
// @author jaysunxiao
// @version 1.0
// @since 2019-11-14 10:54
const FriendHistoryMessageResponse = function(chatMessages, uidA, uidB) {
    this.chatMessages = chatMessages; // java.util.List<com.zfoo.app.zapp.common.protocol.common.ChatMessage>
    this.uidA = uidA; // long
    this.uidB = uidB; // long
};

FriendHistoryMessageResponse.prototype.protocolId = function() {
    return 15205;
};

FriendHistoryMessageResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writePacketArray(packet.chatMessages, 120);
    buffer.writeLong(packet.uidA);
    buffer.writeLong(packet.uidB);
};

FriendHistoryMessageResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new FriendHistoryMessageResponse();
    const list0 = buffer.readPacketArray(120);
    packet.chatMessages = list0;
    const result1 = buffer.readLong();
    packet.uidA = result1;
    const result2 = buffer.readLong();
    packet.uidB = result2;
    return packet;
};

export default FriendHistoryMessageResponse;
