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

package com.zfoo.app.zapp.common.entity.group;

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.entity.common.MessagePO;
import com.zfoo.orm.model.anno.EntityCache;
import com.zfoo.orm.model.anno.Id;
import com.zfoo.orm.model.anno.Persister;
import com.zfoo.orm.model.entity.IEntity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-04-21 18:13
 */
@EntityCache(cacheStrategy = "tenThousand", persister = @Persister("time30s"))
public class ChannelEntity implements IEntity<Long> {

    @Id
    private long id;

    private long vs;

    private long inc;

    private List<MessagePO> messages = new CopyOnWriteArrayList<>();
    private List<MessagePO> pins = new CopyOnWriteArrayList<>();


    public void addMessage(MessagePO messagePO) {
        messagePO.setId(++inc);
        messages.add(messagePO);

        if (messages.size() > AppConstant.GROUP_CHAT_MESSAGE_MAX_SIZE) {
            messages.remove(0);
        }
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public long gvs() {
        return vs;
    }

    @Override
    public void svs(long vs) {
        this.vs = vs;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVs() {
        return vs;
    }

    public void setVs(long vs) {
        this.vs = vs;
    }

    public List<MessagePO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessagePO> messages) {
        this.messages = messages;
    }

    public long getInc() {
        return inc;
    }

    public void setInc(long inc) {
        this.inc = inc;
    }

    public List<MessagePO> getPins() {
        return pins;
    }

    public void setPins(List<MessagePO> pins) {
        this.pins = pins;
    }
}
