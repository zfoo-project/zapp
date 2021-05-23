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

import com.zfoo.app.zapp.common.entity.common.MessagePO;
import com.zfoo.app.zapp.common.entity.group.GroupEntity;
import com.zfoo.app.zapp.common.protocol.common.ChatMessage;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-07 15:15
 */
public interface IGroupService {

    List<ChatMessage> toChatMessages(List<MessagePO> messages);

    void removeMemberFromGroup(GroupEntity groupEntity, long memberId);

}
