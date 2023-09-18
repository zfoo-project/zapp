// @author jaysunxiao
// @version 1.0
// @since 2020-02-18 12:06
const VideoLinkVO = function(poster, url) {
    this.poster = poster; // java.lang.String
    this.url = url; // java.lang.String
};

VideoLinkVO.prototype.protocolId = function() {
    return 134;
};

VideoLinkVO.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.poster);
    buffer.writeString(packet.url);
};

VideoLinkVO.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new VideoLinkVO();
    const result0 = buffer.readString();
    packet.poster = result0;
    const result1 = buffer.readString();
    packet.url = result1;
    return packet;
};

export default VideoLinkVO;
