// Long + String
//
// @author jaysunxiao
// @version 3.0
const PairLS = function(key, value) {
    this.key = key; // long
    this.value = value; // java.lang.String
};

PairLS.prototype.protocolId = function() {
    return 113;
};

PairLS.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.key);
    buffer.writeString(packet.value);
};

PairLS.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new PairLS();
    const result0 = buffer.readLong();
    packet.key = result0;
    const result1 = buffer.readString();
    packet.value = result1;
    return packet;
};

export default PairLS;
