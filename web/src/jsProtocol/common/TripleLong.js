// @author jaysunxiao
// @version 3.0
const TripleLong = function(left, middle, right) {
    this.left = left; // long
    this.middle = middle; // long
    this.right = right; // long
};

TripleLong.prototype.protocolId = function() {
    return 114;
};

TripleLong.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeLong(packet.left);
    buffer.writeLong(packet.middle);
    buffer.writeLong(packet.right);
};

TripleLong.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new TripleLong();
    const result0 = buffer.readLong();
    packet.left = result0;
    const result1 = buffer.readLong();
    packet.middle = result1;
    const result2 = buffer.readLong();
    packet.right = result2;
    return packet;
};

export default TripleLong;
