// @author jaysunxiao
// @version 1.0
// @since 2019-10-15 17:55
const WebsocketSignInResponse = function(applyFriends, blacklist, friendInfoMap, friends, groupMemberSimpleVOs, groups, userId) {
    this.applyFriends = applyFriends; // java.util.List<com.zfoo.app.zapp.common.protocol.friend.model.ApplyFriendVO>
    this.blacklist = blacklist; // java.util.List<com.zfoo.app.zapp.common.protocol.cache.model.UserCache>
    this.friendInfoMap = friendInfoMap; // java.util.Map<java.lang.Long, com.zfoo.app.zapp.common.protocol.friend.model.FriendInfoVO>
    this.friends = friends; // java.util.List<com.zfoo.app.zapp.common.protocol.cache.model.UserCache>
    // 用户所拥有的群组对应的权限信息
    this.groupMemberSimpleVOs = groupMemberSimpleVOs; // java.util.List<com.zfoo.app.zapp.common.protocol.group.model.GroupMemberSimpleVO>
    // 用户拥有的所有群组信息
    this.groups = groups; // java.util.List<com.zfoo.app.zapp.common.protocol.group.model.GroupVO>
    this.userId = userId; // long
};

WebsocketSignInResponse.prototype.protocolId = function() {
    return 1001;
};

WebsocketSignInResponse.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writePacketArray(packet.applyFriends, 15000);
    buffer.writePacketArray(packet.blacklist, 3000);
    buffer.writeLongPacketMap(packet.friendInfoMap, 15001);
    buffer.writePacketArray(packet.friends, 3000);
    buffer.writePacketArray(packet.groupMemberSimpleVOs, 18013);
    buffer.writePacketArray(packet.groups, 18000);
    buffer.writeLong(packet.userId);
};

WebsocketSignInResponse.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new WebsocketSignInResponse();
    const list0 = buffer.readPacketArray(15000);
    packet.applyFriends = list0;
    const list1 = buffer.readPacketArray(3000);
    packet.blacklist = list1;
    const map2 = buffer.readLongPacketMap(15001);
    packet.friendInfoMap = map2;
    const list3 = buffer.readPacketArray(3000);
    packet.friends = list3;
    const list4 = buffer.readPacketArray(18013);
    packet.groupMemberSimpleVOs = list4;
    const list5 = buffer.readPacketArray(18000);
    packet.groups = list5;
    const result6 = buffer.readLong();
    packet.userId = result6;
    return packet;
};

export default WebsocketSignInResponse;
