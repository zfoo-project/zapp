// @author jaysunxiao
// @version 3.0

@ProtobufClass
const Pong = function(time) {
    // 服务器当前的时间戳
    this.time = time; // long
};

Pong.prototype.protocolId = function() {
    return 104;
};

Pong.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.time);
};

Pong.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new Pong();
    const result0 = buffer.readLong();
    packet.time = result0;
    return packet;
};

export default Pong;
