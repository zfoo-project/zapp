// @author jaysunxiao
// @version 1.0
// @since 2020-04-11 11:49
const FriendMessageNotice = function(chatMessage, uidA, uidB) {
    this.chatMessage = chatMessage; // com.zfoo.app.zapp.common.protocol.common.ChatMessage
    this.uidA = uidA; // long
    this.uidB = uidB; // long
};

FriendMessageNotice.prototype.protocolId = function() {
    return 16000;
};

FriendMessageNotice.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writePacket(packet.chatMessage, 120);
    buffer.writeLong(packet.uidA);
    buffer.writeLong(packet.uidB);
};

FriendMessageNotice.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new FriendMessageNotice();
    const result0 = buffer.readPacket(120);
    packet.chatMessage = result0;
    const result1 = buffer.readLong();
    packet.uidA = result1;
    const result2 = buffer.readLong();
    packet.uidB = result2;
    return packet;
};

export default FriendMessageNotice;
