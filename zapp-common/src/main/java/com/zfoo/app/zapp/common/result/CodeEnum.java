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

package com.zfoo.app.zapp.common.result;

/**
 * HTTP状态码规范，英文名称，code，message都符合标准规范
 * 1xx：相关信息
 * 2xx：操作成功
 * 3xx：重定向
 * 4xx：客户端错误
 * 5xx：服务器错误
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-03-21 16:30
 */
public enum CodeEnum {

    /**
     * 通用错误码
     */
    FAIL_TWO(-2, "请求失败-2"),
    FAIL_ONE(-1, "请求失败-1"),
    FAIL(0, "请求失败"),
    OK(1, "请求成功"),
    OK_QUIETLY(2, "请求成功，但是不会在客户端上显示提示"),

    PARAMETER_ERROR(10, "请求参数非法"),
    PARAMETER_ERROR_ONE(11, "请求参数非常非法"),
    PARAMETER_ERROR_TWO(12, "请求参数极度非法"),
    PARAMETER_ERROR_THREE(13, "请求参数变态非法"),
    PARAMETER_ERROR_FOUR(14, "请求参数无法形容非法"),
    PARAMETER_EMPTY(15, "空的请求参数"),
    PARAMETER_EMPTY_ONE(16, "非常空的请求参数"),
    PARAMETER_EMPTY_TWO(17, "极度空的请求参数"),
    PARAMETER_NOT_MATCH(20, "参数无法匹配"),
    PARAMETER_URL_ERROR(25, "url不合法"),

    PARAMETER_WORD_FILTER_ERROR(30, "检测到敏感字符"),

    /**
     * 500-600为服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器遇到了一个未曾预料的状况，导致了它无法完成对请求的处理"),
    NOT_IMPLEMENTED(501, "服务器无法识别请求的方法，并且无法支持其对任何资源的请求"),
    BAD_GATEWAY(502, "作为网关或者代理工作的服务器尝试执行请求时，从上游服务器接收到无效的响应"),
    SERVICE_UNAVAILABLE(503, "临时的服务器维护或者过载，服务器当前无法处理请求"),
    GATEWAY_TIMEOUT(504, "网关或者代理工作的服务器尝试执行请求时"),
    HTTP_VERSION_NOT_SUPPORTED(505, "服务器不支持，或者拒绝支持在请求中使用的 HTTP 版本"),
    VARIANT_ALSO_NEGOTIATES(506, "服务器存在内部配置错误：透明内容协商协议"),
    INSUFFICIENT_STORAGE(507, "服务器无法存储完成请求所必须的内容"),
    BANDWIDTH_LIMIT_EXCEEDED(509, "服务器达到带宽限制，这不是一个官方的状态码，但是仍被广泛使用"),
    NOT_EXTENDED(510, "获取资源所需要的策略并没有被满足"),


    /**
     * 1000-1100为登录相关
     */
    SIGN_IN_FAIL(1000, "账号密码错误"),
    SIGN_IN_FIRST(1001, "请先登录"),
    SIGN_IN_VERIFY_CODE_ERROR(1002, "验证码不正确"),
    SIGN_IN_VERIFY_CODE_NOT_EXIST(1003, "图片验证码过期"),
    SIGN_IN_VERIFY_CODE_CALCULATE_ERROR(1004, "验证码计算错误"),
    SIGN_IN_PHONE_NUMBER_ERROR(1005, "手机号码错误"),
    SIGN_IN_PHONE_NUMBER_LIMIT_ERROR(1006, "验证码发送数量超过限制"),
    SIGN_IN_PHONE_CODE_NOT_EXIST(1007, "手机验证码过期"),
    SIGN_IN_PHONE_CODE_ERROR(1008, "手机验证码不正确"),
    SIGN_IN_REGISTER_CODE_NOT_EXIST(1009, "验证过期，请重新验证"),

    USER_NOT_EXIST(1010, "用户不存在"),
    COIN_NOT_ENOUGH(1011, "z币不足"),
    STAR_MYSELF_ERROR(1012, "用户无法关注自己本身"),

    USER_AVATAR_PATH_ERROR(1020, "用户上传头像路径错误"),
    USER_BACKGROUND_PATH_ERROR(1021, "用户上传背景路径错误"),
    USER_BIND_WEIBO_ERROR(1022, "该微博已经绑定到了其它用户"),
    USER_BIND_PHONE_ERROR(1023, "该手机号已经绑定到了其它用户"),
    USER_ALREADY_BIND_WEIBO_ERROR(1024, "该账号已经绑定了微博"),
    USER_BIND_WEIBO_EMPTY_ERROR(1025, "该账户没有绑定任何微博"),
    USER_BIND_WECHAT_ERROR(1026, "该微信已经绑定到了其它用户"),
    USER_ALREADY_BIND_WECHAT_ERROR(1027, "该账号已经绑定了微博"),
    USER_BIND_WECHAT_EMPTY_ERROR(1028, "该账户没有绑定任何微信"),


    /**
     * 1100-1300为好友相关
     */
    FRIEND_ALREADY_ADDED(1100, "已经添加过该好友"),
    FRIEND_NOT_ADDED(1101, "没有添加该好友，请添加"),
    FRIEND_ALREADY_DELETE(1102, "好友已经删除，无需重复删除"),
    FRIEND_IS_EMPTY(1103, "好友列表为空"),
    FRIEND_NOT_EXIST(1104, "好友不存在"),
    FRIEND_NOT_SELF(1105, "不能添加自己为好友"),
    FRIEND_REJECT_MESSAGE(1106, "对方拒绝了你的消息"),
    FRIEND_BLACKLIST_MESSAGE(1107, "你已经屏蔽了对方，请解除屏蔽关系"),
    FRIEND_LIMIT(1108, "好友已经达到200上限"),
    BLACKLIST_LIMIT(1109, "黑名单已经达到200上限"),
    FRIEND_ALREADY_APPLY(1110, "等待对方通过中，请勿重复申请"),

    /**
     * 1300-1600为群组相关
     */
    GROUP_NOT_EXIST(1300, "群组不存在"),
    GROUP_AUTH_ERROR(1301, "权限错误"),
    GROUP_CHANNEL_BOX_EXIST(1302, "群组类别已经存在"),
    GROUP_CHANNEL_BOX_NOT_EXIST(1303, "群组类别不存在"),
    GROUP_CHANNEL_EXIST(1304, "频道已经存在"),
    GROUP_CHANNEL_NOT_EXIST(1305, "频道不存在"),
    GROUP_INVITE_CODE_EXPIRE(1306, "邀请码过期"),
    GROUP_NOT_JOIN_ERROR(1307, "没有加入群组"),
    GROUP_NOT_INCLUDE_CHANNEL(1308, "群组中没有包含频道"),
    GROUP_AUTH_NAME_CAN_NOT_DEFAULT(1309, "群组的权限名称不能是默认身份"),
    GROUP_ADMIN_CAN_NOT_LEAVE_GROUP(1310, "管理员不能离开群组，考虑解散群组或者转移群组"),
    GROUP_NOT_HAVE_MEMBER(1311, "没有该成员"),
    GROUP_JOIN_ALREADY(1312, "已经加入过该群组"),
    GROUP_DELETE_SELF_GROUP_ERROR(1313, "不能删除自己的公共群组"),
    GROUP_CHANGE_ADMIN_SELF_GROUP_ERROR(1314, "不能转移自己的公共群组"),
    GROUP_CHANNEL_PING_SIZE_ERROR(1315, "pin消息最多只能有50条"),
    GROUP_LEAVE_ZFOO_ERROR(1316, "不能退出官方群组，可选择静音"),
    GROUP_MAX_CHANNEL_LIMIT(1317, "每个群组最多只能建立100个频道"),
    GROUP_NAME_SELF_CHANGE_ERROR(1318, "不能修改自己的群组名称"),
    GROUP_LEAVE_SELF_GROUP_ERROR(1319, "不能离开自己的公共群组"),

    /**
     * 2000-3000为时间片相关
     */
    TIME_SLICE_NOT_EXIST(2000, "时间片不存在"),
    PERSON_NOT_EXIST(2001, "相关的人不存在"),
    ITEM_NOT_EXIST(2002, "相关的物不存在"),
    LOCATION_NOT_EXIST(2003, "地点不存在"),
    PERSON_DUPLICATE(2004, "相关的人重复"),
    ITEM_DUPLICATE(2005, "相关的物重复"),
    LOCATION_DUPLICATE(2006, "地点重复"),
    IMG_LIMIT_ERROR(2010, "超过图片最大上限"),
    URL_ERROR(2011, "url地址不合法"),
    VIDEO_URL_ERROR(2012, "视频地址不合法"),
    TS_KEY_ERROR(2013, "键值对不合法"),
    TS_LINK_ALBUM_ERROR(2014, "链接组不合法"),
    IMG_AND_VIDEO_ERROR(2015, "不能同时上传图片和视频"),
    IMG_PATH_ERROR(2016, "图片的路径错误"),
    REPORT_PATH_ERROR(2020, "举报文件的路径错误"),
    TIME_SLICE_TYPE_ERROR(2021, "时间片类型错误"),
    TIME_SLICE_TIME_ERROR(2022, "时间片时间错误"),
    TIME_SLICE_RESOURCE_EMPTY_ERROR(2023, "没有资源可供下载"),

    /**
     * 3000-4000为词条相关
     */
    WORD_PARAGRAPH_EMPTY_ERROR(3000, "词条段落不能为空"),
    WORD_SECTION_EMPTY_ERROR(3001, "词条的部分说明不能为空"),
    WORD_NOT_EXIST_ERROR(3002, "词条不存在"),
    WORD_EXTERNAL_LINK_ERROR(3003, "外部链接的名称不能为空或者外部链接不合法"),
    WORD_EMPTY_ERROR(3004, "词条名称不能为空"),
    WORD_AUTH_ERROR(3005, "您现在无权限修改词条，可申请权限"),
    WORD_CATEGORY_AUTH_ERROR(3006, "您现在无权限修改类别，可申请权限");

    private int code;
    private String message;

    CodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
