// @author jaysunxiao
// @version 3.0

const Heartbeat = function() {
};

Heartbeat.prototype.protocolId = function() {
    return 102;
};

Heartbeat.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
};

Heartbeat.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new Heartbeat();
    return packet;
};

export default Heartbeat;
