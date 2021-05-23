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

package com.zfoo.app.zapp.push.sid.controller;

import com.zfoo.app.zapp.push.sid.service.ISidSessionService;
import com.zfoo.event.model.anno.EventReceiver;
import com.zfoo.net.core.tcp.model.ServerSessionInactiveEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-16 21:27
 */
@Component
public class SidController {

    @Autowired
    private ISidSessionService sidSessionManager;

    @EventReceiver
    public void onServerSessionInactiveEvent(ServerSessionInactiveEvent event) {
        sidSessionManager.removeSession(event.getSession());
    }

}
