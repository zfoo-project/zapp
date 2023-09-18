// @author jaysunxiao
// @version 1.0
// @since 2019-11-17 11:43
const ChatMessage = function(avatar, id, message, name, read, sendId, timestamp, type) {
    this.avatar = avatar; // java.lang.String
    this.id = id; // long
    // 消息内容
    this.message = message; // java.lang.String
    this.name = name; // java.lang.String
    this.read = read; // boolean
    // 发送者的id
    this.sendId = sendId; // long
    // 发送的时间戳
    this.timestamp = timestamp; // long
    this.type = type; // byte
};

ChatMessage.prototype.protocolId = function() {
    return 120;
};

ChatMessage.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.avatar);
    buffer.writeLong(packet.id);
    buffer.writeString(packet.message);
    buffer.writeString(packet.name);
    buffer.writeBoolean(packet.read);
    buffer.writeLong(packet.sendId);
    buffer.writeLong(packet.timestamp);
    buffer.writeByte(packet.type);
};

ChatMessage.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new ChatMessage();
    const result0 = buffer.readString();
    packet.avatar = result0;
    const result1 = buffer.readLong();
    packet.id = result1;
    const result2 = buffer.readString();
    packet.message = result2;
    const result3 = buffer.readString();
    packet.name = result3;
    const result4 = buffer.readBoolean(); 
    packet.read = result4;
    const result5 = buffer.readLong();
    packet.sendId = result5;
    const result6 = buffer.readLong();
    packet.timestamp = result6;
    const result7 = buffer.readByte();
    packet.type = result7;
    return packet;
};

export default ChatMessage;
