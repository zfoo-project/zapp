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

package com.zfoo.app.zapp.common.protocol.cache;

import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-22 10:24
 */
public class WordFilterAsk implements IPacket {

    public static final transient short PROTOCOL_ID = 3200;

    /**
     * 需要被过滤的关键词
     */
    private String word;

    public static WordFilterAsk valueOf(String word) {
        var ask = new WordFilterAsk();
        ask.word = word;
        return ask;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
