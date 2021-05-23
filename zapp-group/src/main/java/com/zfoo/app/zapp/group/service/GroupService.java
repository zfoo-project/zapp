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

package com.zfoo.app.zapp.group.service;

import com.zfoo.app.zapp.common.constant.OssPolicyConstant;
import com.zfoo.app.zapp.common.entity.common.MessagePO;
import com.zfoo.app.zapp.common.entity.group.GroupEntity;
import com.zfoo.app.zapp.common.protocol.cache.GetUserCacheAnswer;
import com.zfoo.app.zapp.common.protocol.cache.GetUserCacheAsk;
import com.zfoo.app.zapp.common.protocol.common.ChatMessage;
import com.zfoo.net.NetContext;
import com.zfoo.net.util.SimpleCache;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-07 15:15
 */
@Component
public class GroupService implements IGroupService {

    private SimpleCache<Long, Pair<String, String>> userAvatarAndNameCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_0000
            , ids -> {
                var ask = GetUserCacheAsk.valueOf(new HashSet<>(ids));
                try {
                    var answer = NetContext.getConsumer().syncAsk(ask, GetUserCacheAnswer.class, ids.get(0)).packet();
                    return answer.getUserCacheMap()
                            .entrySet()
                            .stream()
                            .map(it -> new Pair<Long, Pair<String, String>>(it.getKey(), new Pair<>(it.getValue().getAvatar(), it.getValue().getName())))
                            .collect(Collectors.toList());
                } catch (Exception e) {
                    return Collections.EMPTY_LIST;
                }
            }
            , key -> new Pair<String, String>(StringUtils.EMPTY, StringUtils.EMPTY));

    @PostConstruct
    public void init() throws IOException {
        OssPolicyConstant.init();
    }

    @Override
    public List<ChatMessage> toChatMessages(List<MessagePO> messages) {
        if (CollectionUtils.isEmpty(messages)) {
            return Collections.EMPTY_LIST;
        }
        var idSet = new HashSet<Long>();
        messages.stream().forEach(it -> idSet.add(it.getSendId()));
        var avatarAndNameMap = userAvatarAndNameCaches.batchGet(idSet);
        return messages.stream().map(messagePO -> {
            var pair = avatarAndNameMap.get(messagePO.getSendId());
            var avatar = pair == null ? StringUtils.EMPTY : pair.getKey();
            var name = pair == null ? StringUtils.EMPTY : pair.getValue();
            return ChatMessage.valueOf(messagePO.getId(), messagePO.getType(), messagePO.getSendId(), avatar, name
                    , messagePO.getMessage(), messagePO.isRead(), messagePO.getTimestamp());
        }).collect(Collectors.toList());
    }

    @Override
    public void removeMemberFromGroup(GroupEntity groupEntity, long memberId) {
        groupEntity.getPeople().remove(memberId);
        groupEntity.getGroupAuths().forEach(it -> it.getMembers().remove(memberId));
    }

}
