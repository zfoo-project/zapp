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

package com.zfoo.app.zapp.common.protocol.user.info;

import com.zfoo.protocol.IPacket;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-10-04 11:11
 */
public class GetUserProfileFollowAnswer implements IPacket {

    public static final transient short PROTOCOL_ID = 1041;

    private List<Long> follows;

    public static GetUserProfileFollowAnswer valueOf(List<Long> follows) {
        var answer = new GetUserProfileFollowAnswer();
        answer.follows = follows;
        return answer;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public List<Long> getFollows() {
        return follows;
    }

    public void setFollows(List<Long> follows) {
        this.follows = follows;
    }
}
