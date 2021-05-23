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

import com.zfoo.net.session.model.AttributeType;
import com.zfoo.net.session.model.Session;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.collection.ConcurrentHashSet;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-16 20:05
 */
@Component
public class SidSessionService implements ISidSessionService {

    /**
     * 考虑到多端登录，一个网关session可能对应多个uid，一个uid可能对于多个sid
     */
    private ConcurrentHashMap<Session, Map<Long, Set<Long>>> uidSessionMap = new ConcurrentHashMap<>();


    @Override
    public void synchronizeSid(Session session, String gatewayHostAndPort, Map<Long, Long> sidMap) {
        var uidMap = uidSessionMap.computeIfAbsent(session, it -> {
            session.putAttribute(AttributeType.GATEWAY_HOST_AND_PORT, gatewayHostAndPort);
            return new ConcurrentHashMap<>();
        });

        // 移除过期的网关session
        var sessionSet = uidSessionMap.keySet().stream()
                .filter(it -> it.getAttribute(AttributeType.GATEWAY_HOST_AND_PORT).equals(gatewayHostAndPort))
                .collect(Collectors.toSet());
        sessionSet.stream()
                .filter(it -> !session.equals(it))
                .forEach(it -> uidSessionMap.remove(it));

        sidMap.forEach((key, value) -> addSid(session, gatewayHostAndPort, key, value));

        // 移除过期的uid和sid
        var minSidOptional = sidMap.keySet().stream().min((a, b) -> Long.compare(a, b));
        if (minSidOptional.isPresent()) {
            var minSid = minSidOptional.get();
            var needRemovedList = new ArrayList<Long>();
            for (var entry : uidMap.entrySet()) {
                var uid = entry.getKey();
                var sidSet = entry.getValue();
                var removed = sidSet.removeIf(it -> it < minSid);
                if (removed && CollectionUtils.isEmpty(sidSet)) {
                    needRemovedList.add(uid);
                }
            }

            for (var uid : needRemovedList) {
                uidMap.remove(uid);
            }
        }
    }

    @Override
    public void addSid(Session session, String gatewayHostAndPort, long sid, long uid) {
        var uidMap = uidSessionMap.computeIfAbsent(session, it -> {
            session.putAttribute(AttributeType.GATEWAY_HOST_AND_PORT, gatewayHostAndPort);
            return new ConcurrentHashMap<>();
        });
        var sidSet = uidMap.computeIfAbsent(uid, it -> new ConcurrentHashSet<>(1));
        sidSet.add(sid);
    }

    @Override
    public void removeSid(Session session, String gatewayHostAndPort, long sid, long uid) {
        var uidMap = uidSessionMap.computeIfAbsent(session, it -> {
            session.putAttribute(AttributeType.GATEWAY_HOST_AND_PORT, gatewayHostAndPort);
            return new ConcurrentHashMap<>();
        });

        if (CollectionUtils.isEmpty(uidMap)) {
            return;
        }


        var sidSet = uidMap.get(uid);
        if (CollectionUtils.isEmpty(sidSet)) {
            return;
        }

        sidSet.remove(sid);

        if (CollectionUtils.isEmpty(sidSet)) {
            uidMap.remove(uid);
        }
    }

    @Override
    public void removeSession(Session session) {
        uidSessionMap.remove(session);
    }

    @Override
    public Map<Session, Set<Long>> getSessionByUid(long uid) {
        var map = new HashMap<Session, Set<Long>>(1);
        for (var entry : uidSessionMap.entrySet()) {
            var set = entry.getValue().get(uid);
            if (CollectionUtils.isNotEmpty(set)) {
                map.put(entry.getKey(), set);
            }
        }
        return map;
    }

    @Override
    public Map<Session, Set<Long>> getSessionByUidSet(Set<Long> uidList) {
        if (CollectionUtils.isEmpty(uidList)) {
            return Collections.emptyMap();
        }

        var map = new HashMap<Session, Set<Long>>(1);

        for (var entry : uidSessionMap.entrySet()) {
            uidList.stream().forEach(uid -> {
                var set = entry.getValue().get(uid);
                if (CollectionUtils.isNotEmpty(set)) {
                    map.compute(entry.getKey(), (session, sidSet) -> {
                        if (CollectionUtils.isEmpty(sidSet)) {
                            return new HashSet<>(set);
                        } else {
                            sidSet.addAll(set);
                            return sidSet;
                        }
                    });
                }
            });
        }
        return map;
    }

}
