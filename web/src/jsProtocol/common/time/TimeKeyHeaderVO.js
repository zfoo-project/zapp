// @author jaysunxiao
// @version 1.0
// @since 2020-02-22 17:41
const TimeKeyHeaderVO = function(text, value) {
    this.text = text; // java.lang.String
    this.value = value; // java.lang.String
};

TimeKeyHeaderVO.prototype.protocolId = function() {
    return 130;
};

TimeKeyHeaderVO.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.text);
    buffer.writeString(packet.value);
};

TimeKeyHeaderVO.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new TimeKeyHeaderVO();
    const result0 = buffer.readString();
    packet.text = result0;
    const result1 = buffer.readString();
    packet.value = result1;
    return packet;
};

export default TimeKeyHeaderVO;
