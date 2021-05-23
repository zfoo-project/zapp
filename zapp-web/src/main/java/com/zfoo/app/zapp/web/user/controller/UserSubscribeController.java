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

package com.zfoo.app.zapp.web.user.controller;

import com.zfoo.app.zapp.common.constant.LocationConstant;
import com.zfoo.app.zapp.common.protocol.user.fan.SubscribeItemAsk;
import com.zfoo.app.zapp.common.protocol.user.fan.SubscribeLocationAsk;
import com.zfoo.app.zapp.common.protocol.user.fan.SubscribePersonAsk;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.web.user.model.subscribe.SubscribeItemRequest;
import com.zfoo.app.zapp.web.user.model.subscribe.SubscribeLocationRequest;
import com.zfoo.app.zapp.web.user.model.subscribe.SubscribePersonRequest;
import com.zfoo.app.zapp.web.util.HttpUtils;
import com.zfoo.app.zapp.web.word.service.IWordService;
import com.zfoo.net.NetContext;
import com.zfoo.net.packet.common.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-10-04 14:21
 */
@Controller
public class UserSubscribeController {

    @Autowired
    private IWordService wordService;

    /**
     * 关注相关的物
     *
     * @param cm 关注相关的物的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/subscribe/item", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse starItem(HttpServletRequest request, @RequestBody SubscribeItemRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        var itemId = cm.getItemId();
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }

        if (!CommonUtils.isWordIdInRange(List.of(itemId))) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }

        if (wordService.existWordList(List.of(itemId)).isEmpty()) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_TWO);
        }
        NetContext.getConsumer().syncAsk(SubscribeItemAsk.valueOf(userId, itemId), Message.class, userId);
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }

    /**
     * 关注相关的地点
     *
     * @param cm 关注相关的物的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/subscribe/location", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse starLocation(HttpServletRequest request, @RequestBody SubscribeLocationRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        var locationId = cm.getLocationId();
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        if (!LocationConstant.checkLocations(List.of(locationId))) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }

        NetContext.getConsumer().syncAsk(SubscribeLocationAsk.valueOf(userId, locationId), Message.class, userId);
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }

    /**
     * 关注相关的人
     *
     * @param cm 关注相关的人的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/subscribe/person", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse starPerson(HttpServletRequest request, @RequestBody SubscribePersonRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        var personId = cm.getPersonId();
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR);
        }

        if (!CommonUtils.isWordIdInRange(List.of(personId))) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }

        if (wordService.existPersonList(List.of(personId)).isEmpty()) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_TWO);
        }
        NetContext.getConsumer().syncAsk(SubscribePersonAsk.valueOf(userId, personId), Message.class, userId);
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
    }

}
