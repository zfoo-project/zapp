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

package com.zfoo.app.zapp.web.word.service;

import com.zfoo.net.packet.common.PairLS;
import com.zfoo.protocol.model.Pair;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-16 10:26
 */
public interface IWordService {

    /**
     * 相关的人的输入提示信息
     */
    List<PairLS> personHint(String name, int hintSize);

    /**
     * 相关的物的输入提示信息
     */
    List<PairLS> wordHint(String name, int hintSize);

    /**
     * 类别的输入提示信息
     */
    List<PairLS> categoryHint(String name, int hintSize);

    /**
     * 检查item的id，并返回存在的item
     */
    List<Pair<Long, String>> existWordList(List<Long> words);

    /**
     * 检查person的id，并返回存在的person
     */
    List<Pair<Long, String>> existPersonList(List<Long> persons);

    /**
     * 检查category的id，并返回存在的类别
     */
    List<Pair<Long, String>> existCategoryList(List<Long> categories);

}
