// @author jaysunxiao
// @version 3.0
const Error = function(errorCode, errorMessage, module) {
    this.errorCode = errorCode; // int
    this.errorMessage = errorMessage; // java.lang.String
    this.module = module; // int
};

Error.prototype.protocolId = function() {
    return 101;
};

Error.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeInt(packet.errorCode);
    buffer.writeString(packet.errorMessage);
    buffer.writeInt(packet.module);
};

Error.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new Error();
    const result0 = buffer.readInt();
    packet.errorCode = result0;
    const result1 = buffer.readString();
    packet.errorMessage = result1;
    const result2 = buffer.readInt();
    packet.module = result2;
    return packet;
};

export default Error;
