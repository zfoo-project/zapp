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

package com.zfoo.app.zapp.push.sid.service;

import com.zfoo.net.session.model.Session;

import java.util.Map;
import java.util.Set;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-16 20:04
 */
public interface ISidSessionService {

    void synchronizeSid(Session session, String gatewayHostAndPort, Map<Long, Long> sidMap);

    void addSid(Session session, String gatewayHostAndPort, long sid, long uid);

    void removeSid(Session session, String gatewayHostAndPort, long sid, long uid);

    void removeSession(Session session);

    Map<Session, Set<Long>> getSessionByUid(long uid);

    Map<Session, Set<Long>> getSessionByUidSet(Set<Long> uidList);

}
