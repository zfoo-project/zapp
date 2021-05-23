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

package com.zfoo.app.zapp.common.protocol.group.model;

import com.zfoo.protocol.IPacket;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-04 19:59
 */
public class GroupAuthVO implements IPacket {

    public static final transient short PROTOCOL_ID = 18001;

    private long id;

    /**
     * 身份名称
     */
    private String name;

    /**
     * 权限类型
     */
    private int groupAuth;

    /**
     * 颜色
     */
    private String color;

    public static GroupAuthVO valueOf(long id, String name, int groupAuth, String color) {
        var vo = new GroupAuthVO();
        vo.id = id;
        vo.name = name;
        vo.groupAuth = groupAuth;
        vo.color = color;
        return vo;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroupAuth() {
        return groupAuth;
    }

    public void setGroupAuth(int groupAuth) {
        this.groupAuth = groupAuth;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}
