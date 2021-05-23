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

package com.zfoo.app.zapp.web.admin.model;

/**
 * 修改用户的权限的请求
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-31 20:28
 */
public class AdminUserAuthRequest {

    /**
     * 目标用户的id
     */
    private long targetId;

    /**
     * 修改到的权限
     */
    private byte adminAuth;

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public byte getAdminAuth() {
        return adminAuth;
    }

    public void setAdminAuth(byte adminAuth) {
        this.adminAuth = adminAuth;
    }

}
