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

package com.zfoo.app.zapp.user.coin.controller;

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.entity.user.UserEntity;
import com.zfoo.app.zapp.common.protocol.user.time.CoinConsumeAnswer;
import com.zfoo.app.zapp.common.protocol.user.time.CoinConsumeAsk;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.user.login.model.LoginEvent;
import com.zfoo.event.model.anno.EventReceiver;
import com.zfoo.net.NetContext;
import com.zfoo.net.router.receiver.PacketReceiver;
import com.zfoo.net.session.model.Session;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import com.zfoo.scheduler.util.TimeUtils;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-23 17:56
 */
@Component
public class CoinController {

    @EntityCachesInjection
    private IEntityCaches<Long, UserEntity> entityCaches;

    @EventReceiver
    public void onLoginEvent(LoginEvent event) {
        var userEntity = event.getUser();

        // 如果是首次登录，则奖励z币
        if (TimeUtils.isSameDay(TimeUtils.now(), userEntity.getLoginTime())) {
            return;
        }

        var coin = userEntity.getCoin();
        userEntity.setCoin(coin + AppConstant.LOGIN_COIN_REWARD);
        userEntity.setLoginTime(TimeUtils.now());
        entityCaches.update(userEntity);
    }

    @PacketReceiver
    public void atCoinConsumeAsk(Session session, CoinConsumeAsk ask) {
        var userId = ask.getUserId();
        var coinConsume = ask.getCoinConsume();

        var userEntity = entityCaches.load(userId);
        if (userEntity.id() == 0L) {
            NetContext.getRouter().send(session, CoinConsumeAnswer.valueOf(CodeEnum.USER_NOT_EXIST));
            return;
        }

        var coin = userEntity.getCoin();
        if (coin - coinConsume < AppConstant.CREDIT_COIN_NUM) {
            NetContext.getRouter().send(session, CoinConsumeAnswer.valueOf(CodeEnum.COIN_NOT_ENOUGH));
            return;
        }

        userEntity.setCoin(coin - coinConsume);
        entityCaches.update(userEntity);
        NetContext.getRouter().send(session, CoinConsumeAnswer.valueOf(CodeEnum.OK, userEntity.getCoin()));
    }

}
