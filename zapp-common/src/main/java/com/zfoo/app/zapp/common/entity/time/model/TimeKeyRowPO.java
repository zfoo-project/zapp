/*
 * Copyright (C) 2020 The zfoo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.zfoo.app.zapp.common.entity.time.model;

import com.zfoo.protocol.util.StringUtils;

/**
 * 键值对的行
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-22 17:44
 */
public class TimeKeyRowPO {

    /**
     * 第一列的内容，下面的依次类推，类似于excel
     */
    private String a;
    private String b;
    private String c;
    private String d;
    private String e;
    private String f;
    private String g;
    private String h;
    private String i;
    private String j;
    private String k;
    private String l;
    private String m;
    private String n;
    private String o;
    private String p;
    private String q;
    private String r;
    private String s;
    private String t;
    private String u;
    private String v;
    private String w;
    private String x;
    private String y;
    private String z;
    private String aa;
    private String ab;
    private String ac;
    private String ad;
    private String ae;
    private String af;
    private String ag;
    private String ah;
    private String ai;
    private String aj;
    private String ak;
    private String al;
    private String am;
    private String an;
    private String ao;
    private String ap;
    private String aq;
    private String ar;
    private String as;
    private String at;
    private String au;
    private String av;
    private String aw;
    private String ax;
    private String ay;
    private String az;

    public void trim() {
        this.a = StringUtils.trim(a);
        this.b = StringUtils.trim(b);
        this.c = StringUtils.trim(c);
        this.d = StringUtils.trim(d);
        this.e = StringUtils.trim(e);
        this.f = StringUtils.trim(f);
        this.g = StringUtils.trim(g);
        this.h = StringUtils.trim(h);
        this.i = StringUtils.trim(i);
        this.j = StringUtils.trim(j);
        this.k = StringUtils.trim(k);
        this.l = StringUtils.trim(l);
        this.m = StringUtils.trim(m);
        this.n = StringUtils.trim(n);
        this.o = StringUtils.trim(o);
        this.p = StringUtils.trim(p);
        this.q = StringUtils.trim(q);
        this.r = StringUtils.trim(r);
        this.s = StringUtils.trim(s);
        this.t = StringUtils.trim(t);
        this.u = StringUtils.trim(u);
        this.v = StringUtils.trim(v);
        this.w = StringUtils.trim(w);
        this.x = StringUtils.trim(x);
        this.y = StringUtils.trim(y);
        this.z = StringUtils.trim(z);
        this.aa = StringUtils.trim(aa);
        this.ab = StringUtils.trim(ab);
        this.ac = StringUtils.trim(ac);
        this.ad = StringUtils.trim(ad);
        this.ae = StringUtils.trim(ae);
        this.af = StringUtils.trim(af);
        this.ag = StringUtils.trim(ag);
        this.ah = StringUtils.trim(ah);
        this.ai = StringUtils.trim(ai);
        this.aj = StringUtils.trim(aj);
        this.ak = StringUtils.trim(ak);
        this.al = StringUtils.trim(al);
        this.am = StringUtils.trim(am);
        this.an = StringUtils.trim(an);
        this.ao = StringUtils.trim(ao);
        this.ap = StringUtils.trim(ap);
        this.aq = StringUtils.trim(aq);
        this.ar = StringUtils.trim(ar);
        this.as = StringUtils.trim(as);
        this.at = StringUtils.trim(at);
        this.au = StringUtils.trim(au);
        this.av = StringUtils.trim(av);
        this.aw = StringUtils.trim(aw);
        this.ax = StringUtils.trim(ax);
        this.ay = StringUtils.trim(ay);
        this.az = StringUtils.trim(az);
    }

    /**
     * 使无用列后的字段为空
     *
     * @param col 不包括当前col列
     */
    public void minifyRow(int col) {
        var colIndex = 0;
        if (colIndex >= col) {
            a = null;
        }
        colIndex++;
        if (colIndex >= col) {
            b = null;
        }
        colIndex++;
        if (colIndex >= col) {
            c = null;
        }
        colIndex++;
        if (colIndex >= col) {
            d = null;
        }
        colIndex++;
        if (colIndex >= col) {
            e = null;
        }
        colIndex++;
        if (colIndex >= col) {
            f = null;
        }
        colIndex++;
        if (colIndex >= col) {
            g = null;
        }
        colIndex++;
        if (colIndex >= col) {
            h = null;
        }
        colIndex++;
        if (colIndex >= col) {
            i = null;
        }
        colIndex++;
        if (colIndex >= col) {
            j = null;
        }
        colIndex++;
        if (colIndex >= col) {
            k = null;
        }
        colIndex++;
        if (colIndex >= col) {
            l = null;
        }
        colIndex++;
        if (colIndex >= col) {
            m = null;
        }
        colIndex++;
        if (colIndex >= col) {
            n = null;
        }
        colIndex++;
        if (colIndex >= col) {
            o = null;
        }
        colIndex++;
        if (colIndex >= col) {
            p = null;
        }
        colIndex++;
        if (colIndex >= col) {
            q = null;
        }
        colIndex++;
        if (colIndex >= col) {
            r = null;
        }
        colIndex++;
        if (colIndex >= col) {
            s = null;
        }
        colIndex++;
        if (colIndex >= col) {
            t = null;
        }
        colIndex++;
        if (colIndex >= col) {
            u = null;
        }
        colIndex++;
        if (colIndex >= col) {
            v = null;
        }
        colIndex++;
        if (colIndex >= col) {
            w = null;
        }
        colIndex++;
        if (colIndex >= col) {
            x = null;
        }
        colIndex++;
        if (colIndex >= col) {
            y = null;
        }
        colIndex++;
        if (colIndex >= col) {
            z = null;
        }
        colIndex++;
        if (colIndex >= col) {
            aa = null;
        }
        colIndex++;
        if (colIndex >= col) {
            ab = null;
        }
        colIndex++;
        if (colIndex >= col) {
            ac = null;
        }
        colIndex++;
        if (colIndex >= col) {
            ad = null;
        }
        colIndex++;
        if (colIndex >= col) {
            ae = null;
        }
        colIndex++;
        if (colIndex >= col) {
            af = null;
        }
        colIndex++;
        if (colIndex >= col) {
            ag = null;
        }
        colIndex++;
        if (colIndex >= col) {
            ah = null;
        }
        colIndex++;
        if (colIndex >= col) {
            ai = null;
        }
        colIndex++;
        if (colIndex >= col) {
            aj = null;
        }
        colIndex++;
        if (colIndex >= col) {
            ak = null;
        }
        colIndex++;
        if (colIndex >= col) {
            al = null;
        }
        colIndex++;
        if (colIndex >= col) {
            am = null;
        }
        colIndex++;
        if (colIndex >= col) {
            an = null;
        }
        colIndex++;
        if (colIndex >= col) {
            ao = null;
        }
        colIndex++;
        if (colIndex >= col) {
            ap = null;
        }
        colIndex++;
        if (colIndex >= col) {
            aq = null;
        }
        colIndex++;
        if (colIndex >= col) {
            ar = null;
        }
        colIndex++;
        if (colIndex >= col) {
            as = null;
        }
        colIndex++;
        if (colIndex >= col) {
            at = null;
        }
        colIndex++;
        if (colIndex >= col) {
            au = null;
        }
        colIndex++;
        if (colIndex >= col) {
            av = null;
        }
        colIndex++;
        if (colIndex >= col) {
            aw = null;
        }
        colIndex++;
        if (colIndex >= col) {
            ax = null;
        }
        colIndex++;
        if (colIndex >= col) {
            ay = null;
        }
        colIndex++;
        if (colIndex >= col) {
            az = null;
        }
    }

    /**
     * 判断所有的列的内容是否都为空，只要有一个不为空就返回true
     */
    public boolean notAllBlank() {
        if (!StringUtils.isBlank(a)) {
            return true;
        }
        if (!StringUtils.isBlank(b)) {
            return true;
        }
        if (!StringUtils.isBlank(c)) {
            return true;
        }
        if (!StringUtils.isBlank(d)) {
            return true;
        }
        if (!StringUtils.isBlank(e)) {
            return true;
        }
        if (!StringUtils.isBlank(f)) {
            return true;
        }
        if (!StringUtils.isBlank(g)) {
            return true;
        }
        if (!StringUtils.isBlank(h)) {
            return true;
        }
        if (!StringUtils.isBlank(i)) {
            return true;
        }
        if (!StringUtils.isBlank(j)) {
            return true;
        }
        if (!StringUtils.isBlank(k)) {
            return true;
        }
        if (!StringUtils.isBlank(l)) {
            return true;
        }
        if (!StringUtils.isBlank(m)) {
            return true;
        }
        if (!StringUtils.isBlank(n)) {
            return true;
        }
        if (!StringUtils.isBlank(o)) {
            return true;
        }
        if (!StringUtils.isBlank(p)) {
            return true;
        }
        if (!StringUtils.isBlank(q)) {
            return true;
        }
        if (!StringUtils.isBlank(r)) {
            return true;
        }
        if (!StringUtils.isBlank(s)) {
            return true;
        }
        if (!StringUtils.isBlank(t)) {
            return true;
        }
        if (!StringUtils.isBlank(u)) {
            return true;
        }
        if (!StringUtils.isBlank(v)) {
            return true;
        }
        if (!StringUtils.isBlank(w)) {
            return true;
        }
        if (!StringUtils.isBlank(x)) {
            return true;
        }
        if (!StringUtils.isBlank(y)) {
            return true;
        }
        if (!StringUtils.isBlank(z)) {
            return true;
        }
        if (!StringUtils.isBlank(aa)) {
            return true;
        }
        if (!StringUtils.isBlank(ab)) {
            return true;
        }
        if (!StringUtils.isBlank(ac)) {
            return true;
        }
        if (!StringUtils.isBlank(ad)) {
            return true;
        }
        if (!StringUtils.isBlank(ae)) {
            return true;
        }
        if (!StringUtils.isBlank(af)) {
            return true;
        }
        if (!StringUtils.isBlank(ag)) {
            return true;
        }
        if (!StringUtils.isBlank(ah)) {
            return true;
        }
        if (!StringUtils.isBlank(ai)) {
            return true;
        }
        if (!StringUtils.isBlank(aj)) {
            return true;
        }
        if (!StringUtils.isBlank(ak)) {
            return true;
        }
        if (!StringUtils.isBlank(al)) {
            return true;
        }
        if (!StringUtils.isBlank(am)) {
            return true;
        }
        if (!StringUtils.isBlank(an)) {
            return true;
        }
        if (!StringUtils.isBlank(ao)) {
            return true;
        }
        if (!StringUtils.isBlank(ap)) {
            return true;
        }
        if (!StringUtils.isBlank(aq)) {
            return true;
        }
        if (!StringUtils.isBlank(ar)) {
            return true;
        }
        if (!StringUtils.isBlank(as)) {
            return true;
        }
        if (!StringUtils.isBlank(at)) {
            return true;
        }
        if (!StringUtils.isBlank(au)) {
            return true;
        }
        if (!StringUtils.isBlank(av)) {
            return true;
        }
        if (!StringUtils.isBlank(aw)) {
            return true;
        }
        if (!StringUtils.isBlank(ax)) {
            return true;
        }
        if (!StringUtils.isBlank(ay)) {
            return true;
        }
        if (!StringUtils.isBlank(az)) {
            return true;
        }
        return false;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public String getJ() {
        return j;
    }

    public void setJ(String j) {
        this.j = j;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getW() {
        return w;
    }

    public void setW(String w) {
        this.w = w;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getZ() {
        return z;
    }

    public void setZ(String z) {
        this.z = z;
    }

    public String getAa() {
        return aa;
    }

    public void setAa(String aa) {
        this.aa = aa;
    }

    public String getAb() {
        return ab;
    }

    public void setAb(String ab) {
        this.ab = ab;
    }

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getAe() {
        return ae;
    }

    public void setAe(String ae) {
        this.ae = ae;
    }

    public String getAf() {
        return af;
    }

    public void setAf(String af) {
        this.af = af;
    }

    public String getAg() {
        return ag;
    }

    public void setAg(String ag) {
        this.ag = ag;
    }

    public String getAh() {
        return ah;
    }

    public void setAh(String ah) {
        this.ah = ah;
    }

    public String getAi() {
        return ai;
    }

    public void setAi(String ai) {
        this.ai = ai;
    }

    public String getAj() {
        return aj;
    }

    public void setAj(String aj) {
        this.aj = aj;
    }

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getAl() {
        return al;
    }

    public void setAl(String al) {
        this.al = al;
    }

    public String getAm() {
        return am;
    }

    public void setAm(String am) {
        this.am = am;
    }

    public String getAn() {
        return an;
    }

    public void setAn(String an) {
        this.an = an;
    }

    public String getAo() {
        return ao;
    }

    public void setAo(String ao) {
        this.ao = ao;
    }

    public String getAp() {
        return ap;
    }

    public void setAp(String ap) {
        this.ap = ap;
    }

    public String getAq() {
        return aq;
    }

    public void setAq(String aq) {
        this.aq = aq;
    }

    public String getAr() {
        return ar;
    }

    public void setAr(String ar) {
        this.ar = ar;
    }

    public String getAs() {
        return as;
    }

    public void setAs(String as) {
        this.as = as;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public String getAu() {
        return au;
    }

    public void setAu(String au) {
        this.au = au;
    }

    public String getAv() {
        return av;
    }

    public void setAv(String av) {
        this.av = av;
    }

    public String getAw() {
        return aw;
    }

    public void setAw(String aw) {
        this.aw = aw;
    }

    public String getAx() {
        return ax;
    }

    public void setAx(String ax) {
        this.ax = ax;
    }

    public String getAy() {
        return ay;
    }

    public void setAy(String ay) {
        this.ay = ay;
    }

    public String getAz() {
        return az;
    }

    public void setAz(String az) {
        this.az = az;
    }
}
