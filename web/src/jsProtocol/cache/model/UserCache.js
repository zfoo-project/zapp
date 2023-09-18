// @author jaysunxiao
// @version 1.0
// @since 2019-11-08 11:00
const UserCache = function(avatar, background, coin, custom, customTime, fanNum, followNum, free, gender, id, items, locations, name, normal, persons, signature, starNum) {
    this.avatar = avatar; // java.lang.String
    this.background = background; // java.lang.String
    this.coin = coin; // long
    this.custom = custom; // java.lang.String
    this.customTime = customTime; // long
    this.fanNum = fanNum; // int
    this.followNum = followNum; // int
    this.free = free; // long
    this.gender = gender; // int
    this.id = id; // long
    this.items = items; // java.util.List<com.zfoo.net.packet.common.PairLS>
    this.locations = locations; // java.util.List<com.zfoo.net.packet.common.PairLS>
    this.name = name; // java.lang.String
    this.normal = normal; // long
    this.persons = persons; // java.util.List<com.zfoo.net.packet.common.PairLS>
    this.signature = signature; // java.lang.String
    this.starNum = starNum; // int
};

UserCache.prototype.protocolId = function() {
    return 3000;
};

UserCache.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.avatar);
    buffer.writeString(packet.background);
    buffer.writeLong(packet.coin);
    buffer.writeString(packet.custom);
    buffer.writeLong(packet.customTime);
    buffer.writeInt(packet.fanNum);
    buffer.writeInt(packet.followNum);
    buffer.writeLong(packet.free);
    buffer.writeInt(packet.gender);
    buffer.writeLong(packet.id);
    buffer.writePacketArray(packet.items, 113);
    buffer.writePacketArray(packet.locations, 113);
    buffer.writeString(packet.name);
    buffer.writeLong(packet.normal);
    buffer.writePacketArray(packet.persons, 113);
    buffer.writeString(packet.signature);
    buffer.writeInt(packet.starNum);
};

UserCache.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new UserCache();
    const result0 = buffer.readString();
    packet.avatar = result0;
    const result1 = buffer.readString();
    packet.background = result1;
    const result2 = buffer.readLong();
    packet.coin = result2;
    const result3 = buffer.readString();
    packet.custom = result3;
    const result4 = buffer.readLong();
    packet.customTime = result4;
    const result5 = buffer.readInt();
    packet.fanNum = result5;
    const result6 = buffer.readInt();
    packet.followNum = result6;
    const result7 = buffer.readLong();
    packet.free = result7;
    const result8 = buffer.readInt();
    packet.gender = result8;
    const result9 = buffer.readLong();
    packet.id = result9;
    const list10 = buffer.readPacketArray(113);
    packet.items = list10;
    const list11 = buffer.readPacketArray(113);
    packet.locations = list11;
    const result12 = buffer.readString();
    packet.name = result12;
    const result13 = buffer.readLong();
    packet.normal = result13;
    const list14 = buffer.readPacketArray(113);
    packet.persons = list14;
    const result15 = buffer.readString();
    packet.signature = result15;
    const result16 = buffer.readInt();
    packet.starNum = result16;
    return packet;
};

export default UserCache;
