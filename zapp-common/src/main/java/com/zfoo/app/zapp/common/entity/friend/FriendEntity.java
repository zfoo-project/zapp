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

package com.zfoo.app.zapp.common.entity.friend;

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.entity.common.MessagePO;
import com.zfoo.orm.model.anno.EntityCache;
import com.zfoo.orm.model.anno.Id;
import com.zfoo.orm.model.anno.Persister;
import com.zfoo.orm.model.entity.IEntity;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.util.security.IdUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-17 16:22
 */
@EntityCache(cacheStrategy = "tenThousand", persister = @Persister("time30s"))
public class FriendEntity implements IEntity<String> {

    @Id
    private String id;

    private long vs;

    private long uidA;

    private long uidB;

    /**
     * A是否对B设置了黑名单
     */
    private boolean blA2B;

    /**
     * B是否对A设置了黑名单
     */
    private boolean blB2A;

    /**
     * A给B设置的备注
     */
    private String uidA2BTag;

    /**
     * B给A设置的备注
     */
    private String uidB2ATag;

    /**
     * A读B消息的时间
     */
    private long uidA2BReadTime;

    /**
     * B读A消息的时间
     */
    private long uidB2AReadTime;

    /**
     * 因为可能a把b删除了，所以可能为false
     */
    private boolean connected;

    private long refreshTime;

    private long inc;

    private List<MessagePO> messages = new LinkedList<>();

    public static FriendEntity valueOf(long aUserId, long bUserId) {
        var entity = new FriendEntity();
        entity.id = IdUtils.generateStringId(aUserId, bUserId);
        entity.uidA = Math.min(aUserId, bUserId);
        entity.uidB = Math.max(aUserId, bUserId);
        entity.connected = false;
        return entity;
    }

    public void addMessage(MessagePO messagePO) {
        messagePO.setId(++inc);
        messages.add(messagePO);

        if (messages.size() > AppConstant.FRIEND_CHAT_MESSAGE_MAX_SIZE) {
            messages.remove(0);
        }
    }

    /**
     * userId是否将对方设置为了黑名单
     */
    public boolean blacklisted(long userId) {
        if (uidA == userId) {
            return blA2B;
        } else if (uidB == userId) {
            return blB2A;
        } else {
            throw new RuntimeException(StringUtils.format("好友的实体类[uidA:{}][uidB:{}]匹配不到[userId:{}]", userId, uidA, uidB));
        }
    }

    public void makeBlacklist(long userId) {
        if (uidA == userId) {
            blA2B = true;
        } else if (uidB == userId) {
            blB2A = true;
        } else {
            throw new RuntimeException(StringUtils.format("好友的实体类[uidA:{}][uidB:{}]匹配不到[userId:{}]", userId, uidA, uidB));
        }
    }

    public void cancelBlacklist(long userId) {
        if (uidA == userId) {
            blA2B = false;
        } else if (uidB == userId) {
            blB2A = false;
        } else {
            throw new RuntimeException(StringUtils.format("好友的实体类[uidA:{}][uidB:{}]匹配不到[userId:{}]", userId, uidA, uidB));
        }
    }

    public long friendId(long userId) {
        if (uidA == userId) {
            return uidB;
        } else if (uidB == userId) {
            return uidA;
        } else {
            throw new RuntimeException(StringUtils.format("好友的实体类[uidA:{}][uidB:{}]匹配不到[userId:{}]", userId, uidA, uidB));
        }
    }

    public void markTag(long userId, String tag) {
        if (uidA == userId) {
            uidA2BTag = tag;
        } else if (uidB == userId) {
            uidB2ATag = tag;
        } else {
            throw new RuntimeException(StringUtils.format("好友的实体类[uidA:{}][uidB:{}]匹配不到[userId:{}]", userId, uidA, uidB));
        }
    }

    public String tag(long userId) {
        if (uidA == userId) {
            return uidA2BTag;
        } else if (uidB == userId) {
            return uidB2ATag;
        } else {
            throw new RuntimeException(StringUtils.format("好友的实体类[uidA:{}][uidB:{}]匹配不到[userId:{}]", userId, uidA, uidB));
        }
    }

    public void readTime(long userId, long time) {
        if (uidA == userId) {
            uidA2BReadTime = time;
        } else if (uidB == userId) {
            uidB2AReadTime = time;
        } else {
            throw new RuntimeException(StringUtils.format("好友的实体类[uidA:{}][uidB:{}]匹配不到[userId:{}]", userId, uidA, uidB));
        }
    }

    public long readTimeOfUserId(long userId) {
        if (uidA == userId) {
            return uidA2BReadTime;
        } else if (uidB == userId) {
            return uidB2AReadTime;
        } else {
            throw new RuntimeException(StringUtils.format("好友的实体类[uidA:{}][uidB:{}]匹配不到[userId:{}]", userId, uidA, uidB));
        }
    }

    @Override
    public String id() {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getVs() {
        return vs;
    }

    public void setVs(long vs) {
        this.vs = vs;
    }

    public long getUidA() {
        return uidA;
    }

    public void setUidA(long uidA) {
        this.uidA = uidA;
    }

    public long getUidB() {
        return uidB;
    }

    public void setUidB(long uidB) {
        this.uidB = uidB;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public long getInc() {
        return inc;
    }

    public void setInc(long inc) {
        this.inc = inc;
    }

    public List<MessagePO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessagePO> messages) {
        this.messages = messages;
    }

    public boolean isBlA2B() {
        return blA2B;
    }

    public void setBlA2B(boolean blA2B) {
        this.blA2B = blA2B;
    }

    public boolean isBlB2A() {
        return blB2A;
    }

    public void setBlB2A(boolean blB2A) {
        this.blB2A = blB2A;
    }

    public String getUidA2BTag() {
        return uidA2BTag;
    }

    public void setUidA2BTag(String uidA2BTag) {
        this.uidA2BTag = uidA2BTag;
    }

    public String getUidB2ATag() {
        return uidB2ATag;
    }

    public void setUidB2ATag(String uidB2ATag) {
        this.uidB2ATag = uidB2ATag;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public long getUidA2BReadTime() {
        return uidA2BReadTime;
    }

    public void setUidA2BReadTime(long uidA2BReadTime) {
        this.uidA2BReadTime = uidA2BReadTime;
    }

    public long getUidB2AReadTime() {
        return uidB2AReadTime;
    }

    public void setUidB2AReadTime(long uidB2AReadTime) {
        this.uidB2AReadTime = uidB2AReadTime;
    }
}
