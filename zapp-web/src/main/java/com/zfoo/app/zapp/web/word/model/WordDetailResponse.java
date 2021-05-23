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

package com.zfoo.app.zapp.web.word.model;

import com.zfoo.app.zapp.common.protocol.cache.model.WordVO;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-29 17:32
 */
public class WordDetailResponse {

    /**
     * 是否是人物
     */
    private boolean person;

    private WordVO wordVO;

    public static WordDetailResponse valueOf(boolean person, WordVO wordVO) {
        var response = new WordDetailResponse();
        response.person = person;
        response.wordVO = wordVO;
        return response;
    }

    public boolean isPerson() {
        return person;
    }

    public void setPerson(boolean person) {
        this.person = person;
    }

    public WordVO getWordVO() {
        return wordVO;
    }

    public void setWordVO(WordVO wordVO) {
        this.wordVO = wordVO;
    }
}
