// @author jaysunxiao
// @version 3.0
const TripleString = function(left, middle, right) {
    this.left = left; // java.lang.String
    this.middle = middle; // java.lang.String
    this.right = right; // java.lang.String
};

TripleString.prototype.protocolId = function() {
    return 115;
};

TripleString.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.left);
    buffer.writeString(packet.middle);
    buffer.writeString(packet.right);
};

TripleString.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new TripleString();
    const result0 = buffer.readString();
    packet.left = result0;
    const result1 = buffer.readString();
    packet.middle = result1;
    const result2 = buffer.readString();
    packet.right = result2;
    return packet;
};

export default TripleString;
