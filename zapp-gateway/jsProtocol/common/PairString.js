// @author jaysunxiao
// @version 3.0
const PairString = function(key, value) {
    this.key = key; // java.lang.String
    this.value = value; // java.lang.String
};

PairString.prototype.protocolId = function() {
    return 112;
};

PairString.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.key);
    buffer.writeString(packet.value);
};

PairString.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new PairString();
    const result0 = buffer.readString();
    packet.key = result0;
    const result1 = buffer.readString();
    packet.value = result1;
    return packet;
};

export default PairString;
