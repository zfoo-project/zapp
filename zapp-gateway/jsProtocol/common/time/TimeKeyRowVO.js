// @author jaysunxiao
// @version 1.0
// @since 2020-02-22 17:44
const TimeKeyRowVO = function(a, aa, ab, ac, ad, ae, af, ag, ah, ai, aj, ak, al, am, an, ao, ap, aq, ar, as, at, au, av, aw, ax, ay, az, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z) {
    this.a = a; // java.lang.String
    this.aa = aa; // java.lang.String
    this.ab = ab; // java.lang.String
    this.ac = ac; // java.lang.String
    this.ad = ad; // java.lang.String
    this.ae = ae; // java.lang.String
    this.af = af; // java.lang.String
    this.ag = ag; // java.lang.String
    this.ah = ah; // java.lang.String
    this.ai = ai; // java.lang.String
    this.aj = aj; // java.lang.String
    this.ak = ak; // java.lang.String
    this.al = al; // java.lang.String
    this.am = am; // java.lang.String
    this.an = an; // java.lang.String
    this.ao = ao; // java.lang.String
    this.ap = ap; // java.lang.String
    this.aq = aq; // java.lang.String
    this.ar = ar; // java.lang.String
    this.as = as; // java.lang.String
    this.at = at; // java.lang.String
    this.au = au; // java.lang.String
    this.av = av; // java.lang.String
    this.aw = aw; // java.lang.String
    this.ax = ax; // java.lang.String
    this.ay = ay; // java.lang.String
    this.az = az; // java.lang.String
    this.b = b; // java.lang.String
    this.c = c; // java.lang.String
    this.d = d; // java.lang.String
    this.e = e; // java.lang.String
    this.f = f; // java.lang.String
    this.g = g; // java.lang.String
    this.h = h; // java.lang.String
    this.i = i; // java.lang.String
    this.j = j; // java.lang.String
    this.k = k; // java.lang.String
    this.l = l; // java.lang.String
    this.m = m; // java.lang.String
    this.n = n; // java.lang.String
    this.o = o; // java.lang.String
    this.p = p; // java.lang.String
    this.q = q; // java.lang.String
    this.r = r; // java.lang.String
    this.s = s; // java.lang.String
    this.t = t; // java.lang.String
    this.u = u; // java.lang.String
    this.v = v; // java.lang.String
    this.w = w; // java.lang.String
    this.x = x; // java.lang.String
    this.y = y; // java.lang.String
    this.z = z; // java.lang.String
};

TimeKeyRowVO.prototype.protocolId = function() {
    return 131;
};

TimeKeyRowVO.write = function(buffer, packet) {
    if (buffer.writePacketFlag(packet)) {
        return;
    }
    buffer.writeString(packet.a);
    buffer.writeString(packet.aa);
    buffer.writeString(packet.ab);
    buffer.writeString(packet.ac);
    buffer.writeString(packet.ad);
    buffer.writeString(packet.ae);
    buffer.writeString(packet.af);
    buffer.writeString(packet.ag);
    buffer.writeString(packet.ah);
    buffer.writeString(packet.ai);
    buffer.writeString(packet.aj);
    buffer.writeString(packet.ak);
    buffer.writeString(packet.al);
    buffer.writeString(packet.am);
    buffer.writeString(packet.an);
    buffer.writeString(packet.ao);
    buffer.writeString(packet.ap);
    buffer.writeString(packet.aq);
    buffer.writeString(packet.ar);
    buffer.writeString(packet.as);
    buffer.writeString(packet.at);
    buffer.writeString(packet.au);
    buffer.writeString(packet.av);
    buffer.writeString(packet.aw);
    buffer.writeString(packet.ax);
    buffer.writeString(packet.ay);
    buffer.writeString(packet.az);
    buffer.writeString(packet.b);
    buffer.writeString(packet.c);
    buffer.writeString(packet.d);
    buffer.writeString(packet.e);
    buffer.writeString(packet.f);
    buffer.writeString(packet.g);
    buffer.writeString(packet.h);
    buffer.writeString(packet.i);
    buffer.writeString(packet.j);
    buffer.writeString(packet.k);
    buffer.writeString(packet.l);
    buffer.writeString(packet.m);
    buffer.writeString(packet.n);
    buffer.writeString(packet.o);
    buffer.writeString(packet.p);
    buffer.writeString(packet.q);
    buffer.writeString(packet.r);
    buffer.writeString(packet.s);
    buffer.writeString(packet.t);
    buffer.writeString(packet.u);
    buffer.writeString(packet.v);
    buffer.writeString(packet.w);
    buffer.writeString(packet.x);
    buffer.writeString(packet.y);
    buffer.writeString(packet.z);
};

TimeKeyRowVO.read = function(buffer) {
    if (!buffer.readBoolean()) {
        return null;
    }
    const packet = new TimeKeyRowVO();
    const result0 = buffer.readString();
    packet.a = result0;
    const result1 = buffer.readString();
    packet.aa = result1;
    const result2 = buffer.readString();
    packet.ab = result2;
    const result3 = buffer.readString();
    packet.ac = result3;
    const result4 = buffer.readString();
    packet.ad = result4;
    const result5 = buffer.readString();
    packet.ae = result5;
    const result6 = buffer.readString();
    packet.af = result6;
    const result7 = buffer.readString();
    packet.ag = result7;
    const result8 = buffer.readString();
    packet.ah = result8;
    const result9 = buffer.readString();
    packet.ai = result9;
    const result10 = buffer.readString();
    packet.aj = result10;
    const result11 = buffer.readString();
    packet.ak = result11;
    const result12 = buffer.readString();
    packet.al = result12;
    const result13 = buffer.readString();
    packet.am = result13;
    const result14 = buffer.readString();
    packet.an = result14;
    const result15 = buffer.readString();
    packet.ao = result15;
    const result16 = buffer.readString();
    packet.ap = result16;
    const result17 = buffer.readString();
    packet.aq = result17;
    const result18 = buffer.readString();
    packet.ar = result18;
    const result19 = buffer.readString();
    packet.as = result19;
    const result20 = buffer.readString();
    packet.at = result20;
    const result21 = buffer.readString();
    packet.au = result21;
    const result22 = buffer.readString();
    packet.av = result22;
    const result23 = buffer.readString();
    packet.aw = result23;
    const result24 = buffer.readString();
    packet.ax = result24;
    const result25 = buffer.readString();
    packet.ay = result25;
    const result26 = buffer.readString();
    packet.az = result26;
    const result27 = buffer.readString();
    packet.b = result27;
    const result28 = buffer.readString();
    packet.c = result28;
    const result29 = buffer.readString();
    packet.d = result29;
    const result30 = buffer.readString();
    packet.e = result30;
    const result31 = buffer.readString();
    packet.f = result31;
    const result32 = buffer.readString();
    packet.g = result32;
    const result33 = buffer.readString();
    packet.h = result33;
    const result34 = buffer.readString();
    packet.i = result34;
    const result35 = buffer.readString();
    packet.j = result35;
    const result36 = buffer.readString();
    packet.k = result36;
    const result37 = buffer.readString();
    packet.l = result37;
    const result38 = buffer.readString();
    packet.m = result38;
    const result39 = buffer.readString();
    packet.n = result39;
    const result40 = buffer.readString();
    packet.o = result40;
    const result41 = buffer.readString();
    packet.p = result41;
    const result42 = buffer.readString();
    packet.q = result42;
    const result43 = buffer.readString();
    packet.r = result43;
    const result44 = buffer.readString();
    packet.s = result44;
    const result45 = buffer.readString();
    packet.t = result45;
    const result46 = buffer.readString();
    packet.u = result46;
    const result47 = buffer.readString();
    packet.v = result47;
    const result48 = buffer.readString();
    packet.w = result48;
    const result49 = buffer.readString();
    packet.x = result49;
    const result50 = buffer.readString();
    packet.y = result50;
    const result51 = buffer.readString();
    packet.z = result51;
    return packet;
};

export default TimeKeyRowVO;
