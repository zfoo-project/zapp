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

package com.zfoo.app.zapp.common.protocol.group.member.model;

import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-03 21:09
 */
public class InviteCodeVO implements IPacket {

    public static final transient short PROTOCOL_ID = 18400;

    private String code;
    private int expireType;
    private long expireTime;
    private int countType;
    private int count;

    public static InviteCodeVO valueOf(String code, int expireType, long expireTime, int countType, int count) {
        var po = new InviteCodeVO();
        po.code = code;
        po.expireType = expireType;
        po.expireTime = expireTime;
        po.countType = countType;
        po.count = count;
        return po;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public void addCount() {
        this.count++;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getExpireType() {
        return expireType;
    }

    public void setExpireType(int expireType) {
        this.expireType = expireType;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public int getCountType() {
        return countType;
    }

    public void setCountType(int countType) {
        this.countType = countType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
