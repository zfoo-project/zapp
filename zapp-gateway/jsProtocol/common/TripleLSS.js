// Long + String + String
//
// @author jaysunxiao
// @version 3.0
const TripleLSS = function(left, middle, right) {
    this.left = left; // long
    this.middle = middle; // java.lang.String
    this.right = right; // java.lang.String
};

TripleLSS.prototype.protocolId = function() {
    return 116;
};

TripleLSS.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.left);
    buffer.writeString(packet.middle);
    buffer.writeString(packet.right);
};

TripleLSS.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new TripleLSS();
    const result0 = buffer.readLong();
    packet.left = result0;
    const result1 = buffer.readString();
    packet.middle = result1;
    const result2 = buffer.readString();
    packet.right = result2;
    return packet;
};

export default TripleLSS;
