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

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-09 21:40
 */
public enum OperationEnum {

    // 群组操作相关
    //-----------------------------------------------------------------------
    /**
     * 0.群转让
     */
    TRANSFER_GROUP,

    /**
     * 0.群解散
     */
    DELETE_GROUP,

    /**
     * 0.群组命名
     */
    CHANGE_GROUP_NAME,

    /**
     * 0.群头像修改
     */
    CHANGE_GROUP_AVATAR,

    /**
     * 0.群背景修改
     */
    CHANGE_GROUP_BACKGROUND,

    /**
     * 1.创建群组身份
     */
    CREATE_GROUP_AUTH,

    /**
     * 1.删除群组身份
     */
    DELETE_GROUP_AUTH,

    /**
     * 1.修改身份名称，权限，颜色
     */
    CHANGE_GROUP_AUTH,

    /**
     * 1.在身份中增加成员
     */
    ADD_MEMBER_TO_GROUP_AUTH,

    /**
     * 1,在身份中移除成员
     */
    REMOVE_MEMBER_FROM_GROUP_AUTH,

    /**
     * 1.修改频道身份权限
     */
    CHANGE_CHANNEL_AUTH,

    /**
     * 2.增加频道
     */
    CREATE_CHANNEL,

    /**
     * 2.删除频道
     */
    DELETE_CHANNEL,

    /**
     * 2.修改频道的名称
     */
    CHANGE_CHANNEL_NAME,

    /**
     * 2.增加频道的类别
     */
    CREATE_CHANNEL_BOX,

    /**
     * 2.删除频道的类别
     */
    DELETE_CHANNEL_BOX,

    /**
     * 2.修改频道类别的名称
     */
    CHANGE_CHANNEL_BOX_NAME,


    /**
     * 3.剔除群组成员
     */
    KICK_GROUP_MEMBER,

    /**
     * 3.封锁群组成员
     */
    BAN_GROUP_MEMBER,

    /**
     * 4.修改群成员的昵称
     */
    RENAME_GROUP_MEMBER,

    /**
     * 5.创建邀请链接
     */
    CREATE_INVITE_CODE,

    /**
     * 5.删除邀请链接
     */
    DELETE_INVITE_CODE,

    /**
     * 5.访问邀请链接
     */
    ACCESS_INVITE_CODE,

    // 群组操作相关
    //-----------------------------------------------------------------------
    /**
     * 1.在频道删除消息
     */
    CHANNEL_DELETE_MESSAGE,

    /**
     * 1.在频道编辑消息
     */
    CHANNEL_EDIT_MESSAGE,

    /**
     * 1.在频道PIN消息
     */
    CHANNEL_PIN_MESSAGE,

    /**
     * 2.发送消息的时候附带附件
     */
    CHANNEL_SEND_MESSAGE_WITH_ATTACHMENT,

    /**
     * 3.发送普通消息（文字，图片，视频）
     */
    CHANNEL_SEND_NORMAL_MESSAGE,

    /**
     * 4.查看消息
     */
    CHANNEL_ACCESS_MESSAGE,

    ;

}
