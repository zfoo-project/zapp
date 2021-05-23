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
import com.zfoo.app.zapp.common.protocol.user.time.CoinConsumeAnswer;
import com.zfoo.app.zapp.common.protocol.user.time.CoinConsumeAsk;
import com.zfoo.app.zapp.common.protocol.user.time.LoveStatisticsAsk;
import com.zfoo.app.zapp.common.protocol.user.time.LoveTimeSliceAsk;
import com.zfoo.app.zapp.web.time.service.ITimeSliceService;
import com.zfoo.net.NetContext;
import com.zfoo.protocol.model.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-23 18:57
 */
@Component
public class CoinService implements ICoinService {

    @Autowired
    private ITimeSliceService timeSliceService;

    @Override
    public Triple<Integer, Long, Long> loveConsume(long userId, long coinConsume, TimeSliceVO cacheTimeSliceVO) throws Exception {
        var timeSliceId = cacheTimeSliceVO.getId();
        // 先消耗
        var answer = NetContext.getConsumer().syncAsk(CoinConsumeAsk.valueOf(userId, coinConsume), CoinConsumeAnswer.class, userId).packet();
        if (!answer.getMessage().success()) {
            return new Triple<>(answer.getMessage().getCode(), 0L, 0L);
        }

        // 更新缓存
        cacheTimeSliceVO.setLove(cacheTimeSliceVO.getLove() + coinConsume);

        // 更新feed趋势
        NetContext.getConsumer().send(LoveTimeSliceAsk.valueOf(timeSliceId, coinConsume), timeSliceId);

        // 更新时间片所有者的喜爱数据
        var targetId = cacheTimeSliceVO.getUserInfo().getId();
        NetContext.getConsumer().send(LoveStatisticsAsk.valueOf(targetId, timeSliceId, cacheTimeSliceVO.getType(), coinConsume), targetId);

        return new Triple<>(null, cacheTimeSliceVO.getLove(), answer.getCoin());
    }


}
