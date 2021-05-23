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

package com.zfoo.app.zapp.web.coin.service;

import com.zfoo.app.zapp.common.model.time.TimeSliceVO;
import com.zfoo.protocol.model.Triple;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-23 18:57
 */
public interface ICoinService {

    /**
     * 喜爱时间片的消耗
     *
     * @param userId           用户id
     * @param coinConsume      消耗的z币
     * @param cacheTimeSliceVO 时间片
     * @return 第一个参数代表错误码，第二个参数代表时间片love数量，第三个参数代表剩余z币数量
     */
    Triple<Integer, Long, Long> loveConsume(long userId, long coinConsume, TimeSliceVO cacheTimeSliceVO) throws Exception;

}
