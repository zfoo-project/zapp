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

package com.zfoo.app.zapp.common.util;

import com.zfoo.app.zapp.common.entity.core.WordEntity;
import com.zfoo.app.zapp.common.entity.group.ChannelEntity;
import com.zfoo.app.zapp.common.entity.group.GroupEntity;
import com.zfoo.app.zapp.common.entity.time.TimeSliceEntity;
import com.zfoo.app.zapp.common.entity.user.UserEntity;
import com.zfoo.net.util.SingleCache;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.scheduler.util.TimeUtils;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-15 15:54
 */
public abstract class CommonUtils {

    /**
     * 获取用户最大id，每10秒钟刷新一次
     */
    private static SingleCache<Long> maxUserIdCache = SingleCache.build(10 * TimeUtils.MILLIS_PER_SECOND, new Supplier<Long>() {
        @Override
        public Long get() {
            return MongoIdUtils.getMaxIdFromMongoDefault(UserEntity.class);
        }
    });

    /**
     * 获取时间片最大id，每10秒钟刷新一次
     */
    private static SingleCache<Long> maxTimeSliceIdCache = SingleCache.build(10 * TimeUtils.MILLIS_PER_SECOND, new Supplier<Long>() {
        @Override
        public Long get() {
            return MongoIdUtils.getMaxIdFromMongoDefault(TimeSliceEntity.class);
        }
    });

    /**
     * 获取group最大id，每1分钟刷新一次
     */
    private static SingleCache<Long> maxGroupIdCache = SingleCache.build(10 * TimeUtils.MILLIS_PER_SECOND, new Supplier<Long>() {
        @Override
        public Long get() {
            return MongoIdUtils.getMaxIdFromMongoDefault(GroupEntity.class);
        }
    });

    /**
     * 获取channel最大id，每1分钟刷新一次
     */
    private static SingleCache<Long> maxChannelIdCache = SingleCache.build(10 * TimeUtils.MILLIS_PER_SECOND, new Supplier<Long>() {
        @Override
        public Long get() {
            return MongoIdUtils.getMaxIdFromMongoDefault(ChannelEntity.class);
        }
    });

    /**
     * 获取word最大id，每1分钟刷新一次
     */
    private static SingleCache<Long> maxWordIdCache = SingleCache.build(10 * TimeUtils.MILLIS_PER_SECOND, new Supplier<Long>() {
        @Override
        public Long get() {
            return MongoIdUtils.getMaxIdFromMongoDefault(WordEntity.class);
        }
    });


    public static boolean isUserIdInRange(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return true;
        }

        for (var id : userIds) {
            if (id <= 0 || id > maxUserIdCache.get()) {
                return false;
            }
        }
        return true;
    }


    public static boolean isTsIdInRange(List<Long> tsLinks) {
        if (CollectionUtils.isEmpty(tsLinks)) {
            return true;
        }

        for (var id : tsLinks) {
            if (id <= 0 || id > maxTimeSliceIdCache.get()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isGroupIdInRange(List<Long> groupIds) {
        if (CollectionUtils.isEmpty(groupIds)) {
            return true;
        }

        for (var id : groupIds) {
            if (id > maxGroupIdCache.get()) {
                return false;
            }
        }
        return true;
    }


    public static boolean isChannelIdInRange(List<Long> channelIds) {
        if (CollectionUtils.isEmpty(channelIds)) {
            return true;
        }

        for (var id : channelIds) {
            if (id <= 0 || id > maxChannelIdCache.get()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isWordIdInRange(List<Long> wordIds) {
        if (CollectionUtils.isEmpty(wordIds)) {
            return true;
        }

        for (var id : wordIds) {
            if (id <= 0 || id > maxWordIdCache.get()) {
                return false;
            }
        }
        return true;
    }
}
