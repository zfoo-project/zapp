// @author jaysunxiao
// @version 3.0
const PairLong = function(key, value) {
    this.key = key; // long
    this.value = value; // long
};

PairLong.prototype.protocolId = function() {
    return 111;
};

PairLong.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.key);
    buffer.writeLong(packet.value);
};

PairLong.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new PairLong();
    const result0 = buffer.readLong();
    packet.key = result0;
    const result1 = buffer.readLong();
    packet.value = result1;
    return packet;
};

export default PairLong;
