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

package com.zfoo.app.zapp.common.protocol.feed.item;

import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-16 13:36
 */
public class LoveTsWithItemAsk implements IPacket {

    public static final transient short PROTOCOL_ID = 4011;

    private long itemId;

    private long tsId;

    private long love;

    private long score;


    public static LoveTsWithItemAsk valueOf(long itemId, long tsId, long love, long score) {
        var ask = new LoveTsWithItemAsk();
        ask.itemId = itemId;
        ask.tsId = tsId;
        ask.love = love;
        ask.score = score;
        return ask;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getTsId() {
        return tsId;
    }

    public void setTsId(long tsId) {
        this.tsId = tsId;
    }

    public long getLove() {
        return love;
    }

    public void setLove(long love) {
        this.love = love;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
