// @author jaysunxiao
// @version 3.0

const Ping = function() {
};

Ping.prototype.protocolId = function() {
    return 103;
};

Ping.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
};

Ping.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new Ping();
    return packet;
};

export default Ping;
