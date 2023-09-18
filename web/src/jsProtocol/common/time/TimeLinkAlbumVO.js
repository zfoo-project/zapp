// @author jaysunxiao
// @version 1.0
// @since 2020-02-25 13:12
const TimeLinkAlbumVO = function(album, links) {
    this.album = album; // java.lang.String
    this.links = links; // java.util.List<java.lang.Long>
};

TimeLinkAlbumVO.prototype.protocolId = function() {
    return 133;
};

TimeLinkAlbumVO.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.album);
    buffer.writeLongArray(packet.links);
};

TimeLinkAlbumVO.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new TimeLinkAlbumVO();
    const result0 = buffer.readString();
    packet.album = result0;
    const list1 = buffer.readLongArray();
    packet.links = list1;
    return packet;
};

export default TimeLinkAlbumVO;
