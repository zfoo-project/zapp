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

package com.zfoo.app.zapp.common.protocol.cache.refresh;

import com.zfoo.protocol.IPacket;

import java.util.Set;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-25 18:15
 */
public class RefreshWordCacheAsk implements IPacket {

    public static final transient short PROTOCOL_ID = 3101;

    private Set<Long> words;

    public static RefreshWordCacheAsk valueOf(Set<Long> words) {
        var ask = new RefreshWordCacheAsk();
        ask.words = words;
        return ask;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }


    public Set<Long> getWords() {
        return words;
    }

    public void setWords(Set<Long> words) {
        this.words = words;
    }
}
