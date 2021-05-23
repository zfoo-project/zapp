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

package com.zfoo.app.zapp.chat.friend.service;

import com.zfoo.app.zapp.common.entity.friend.ApplicantEntity;
import com.zfoo.app.zapp.common.entity.friend.FriendEntity;
import com.zfoo.orm.model.anno.EntityCachesInjection;
import com.zfoo.orm.model.cache.IEntityCaches;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.util.security.IdUtils;
import org.springframework.stereotype.Component;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-11-15 16:21
 */
@Component
public class FriendService implements IFriendService {

    @EntityCachesInjection
    private IEntityCaches<String, ApplicantEntity> applicantEntityCaches;

    @EntityCachesInjection
    private IEntityCaches<String, FriendEntity> friendEntityCaches;

    @Override
    public boolean blacklisted(long userId, long targetId) {
        var id = IdUtils.generateStringId(userId, targetId);
        var friendEntity = friendEntityCaches.load(id);
        if (StringUtils.isBlank(friendEntity.getId())) {
            return false;
        }
        return friendEntity.blacklisted(userId);
    }


    @Override
    public boolean connected(long userId, long targetId) {
        var id = IdUtils.generateStringId(userId, targetId);
        var friendEntity = friendEntityCaches.load(id);
        if (StringUtils.isBlank(friendEntity.getId())) {
            return false;
        }
        if (friendEntity.isConnected()) {
            return true;
        }
        return false;
    }

}
