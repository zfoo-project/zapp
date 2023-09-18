// 通用的返回，既可以用在远程调用，又可以嵌套在其它协议里
//
// @author jaysunxiao
// @version 3.0
const Message = function(code, message, module) {
    // 1是成功，其它的均视为失败的请求
    this.code = code; // int
    this.message = message; // java.lang.String
    this.module = module; // byte
};

Message.prototype.protocolId = function() {
    return 100;
};

Message.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeInt(packet.code);
    buffer.writeString(packet.message);
    buffer.writeByte(packet.module);
};

Message.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new Message();
    const result0 = buffer.readInt();
    packet.code = result0;
    const result1 = buffer.readString();
    packet.message = result1;
    const result2 = buffer.readByte();
    packet.module = result2;
    return packet;
};

export default Message;
