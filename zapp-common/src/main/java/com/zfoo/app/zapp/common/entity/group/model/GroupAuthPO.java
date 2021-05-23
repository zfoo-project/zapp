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

package com.zfoo.app.zapp.common.entity.group.model;

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.protocol.group.model.GroupAuthVO;
import com.zfoo.protocol.util.StringUtils;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 身份
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-04 19:59
 */
public class GroupAuthPO {

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

    /**
     * 所属这个身分组的成员id
     */
    private Set<Long> members = new CopyOnWriteArraySet<>();

    public static GroupAuthPO valueOf(long id, String name, int groupAuth, String color, Set<Long> members) {
        var po = new GroupAuthPO();
        po.id = id;
        po.name = name;
        po.groupAuth = groupAuth;
        po.color = color;
        po.members = members;
        return po;
    }

    public static GroupAuthPO defaultGroupAuthPO() {
        return GroupAuthPO.valueOf(AppConstant.GROUP_AUTH_DEFAULT_ID, AppConstant.GROUP_AUTH_DEFAULT_NAME, GroupAuthEnum.BASE_AUTH.getType()
                , StringUtils.EMPTY, new CopyOnWriteArraySet<>());
    }

    public GroupAuthVO toGroupAuthVO() {
        return GroupAuthVO.valueOf(id, name, groupAuth, color);
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

    public Set<Long> getMembers() {
        return members;
    }

    public void setMembers(Set<Long> members) {
        this.members = members;
    }
}
