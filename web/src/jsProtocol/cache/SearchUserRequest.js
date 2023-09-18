// @author jaysunxiao
// @version 1.0
// @since 2020-04-04 21:44
const SearchUserRequest = function(query) {
    this.query = query; // java.lang.String
};

SearchUserRequest.prototype.protocolId = function() {
    return 3030;
};

SearchUserRequest.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.query);
};

SearchUserRequest.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new SearchUserRequest();
    const result0 = buffer.readString();
    packet.query = result0;
    return packet;
};

export default SearchUserRequest;
