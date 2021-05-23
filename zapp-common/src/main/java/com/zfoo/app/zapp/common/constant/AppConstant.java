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

package com.zfoo.app.zapp.common.constant;

import com.zfoo.scheduler.util.TimeUtils;

import java.util.regex.Pattern;

public class AppConstant {

    public static final String ZFOO_COOKIE_TOKEN = "zapp";
    public static final int ZFOO_COOKIE_TOKEN_EXPIRE_TIME = (int) (TimeUtils.MILLIS_PER_DAY / 1000 * 7);

    public static final byte ZAPP_PUSH_MODULE_ID = (byte) 90;

    /**
     * -------------------------------------------- 词条分类相关 --------------------------------------------
     */
    public static final String USER_UNKNOWN_NAME = "用户未知，可能已经注销";
    public static final String WORD_UNKNOWN_NAME = "未知词条";
    public static final String CATEGORY_UNKNOWN_NAME = "未知分类";
    public static final String TIME_SLICE_UNKNOWN_NAME = "未知分类";

    /**
     * -------------------------------------------- 用户相关 --------------------------------------------
     */
    public static final int PHONE_NUMBER_LENGTH = 11;
    // 用户设置密码的最小长度
    public static final int PASSWORD_MIN_LENGTH = 8;
    // 用户设置密码的最大长度
    public static final int PASSWORD_MAX_LENGTH = 16;
    // 用户名称的最小长度
    public static final int USER_NAME_MIN_LENGTH = 4;
    // 用户名称的最大长度
    public static final int USER_NAME_MAX_LENGTH = 16;
    // 用户签名的最大长度
    public static final int USER_SIGNATURE_MAX_LENGTH = 100;
    // 判断是否为中文人名字符
    public static final Pattern CN_PATTERN = Pattern.compile("[\u4E00-\u9FA5|\\·]");

    // 每日用户登录奖励的z币
    public static final long LOGIN_COIN_REWARD = 100;
    // 点赞消耗的z币数量
    public static final int LOVE_COIN_CONSUME = 1;
    // 下载图片消耗的z币数量
    public static final int DOWNLOAD_IMAGE_COIN_CONSUME = 2;
    // 下载视频消耗的z币数量
    public static final int DOWNLOAD_VIDEO_COIN_CONSUME = 10;
    // z币的信用度
    public static final long CREDIT_COIN_NUM = -10000;

    /**
     * -------------------------------------------- Feed相关 --------------------------------------------
     */
    public static final int FEED_PAGE_SIZE = 10;
    public static final int USER_TS_PAGE_SIZE = 10;

    public static final int LOCATION_TOP_LINK_SIZE = 100;
    public static final int LOCATION_TREND_LINK_SIZE = 100;
    public static final int LOCATION_NEW_LINK_SIZE = 100;
    public static final int LOCATION_RECOMMEND_LINK_SIZE = 100;

    public static final int PERSON_TOP_LINK_SIZE = 100;
    public static final int PERSON_TREND_LINK_SIZE = 100;
    public static final int PERSON_NEW_LINK_SIZE = 100;
    public static final int PERSON_RECOMMEND_LINK_SIZE = 100;

    public static final int ITEM_TOP_LINK_SIZE = 100;
    public static final int ITEM_TREND_LINK_SIZE = 100;
    public static final int ITEM_NEW_LINK_SIZE = 100;
    public static final int ITEM_RECOMMEND_LINK_SIZE = 100;

    public static final int COMMON_FEED_PAGE_TOP_LINK_SIZE = 3;
    public static final int COMMON_FEED_PAGE_TREND_LINK_SIZE = 3;
    public static final int COMMON_FEED_PAGE_NEW_LINK_SIZE = 2;
    public static final int COMMON_FEED_PAGE_RECOMMEND_LINK_SIZE = 2;


    // 后期这个值可以设置大一点
    // public static final int HOME_FEED_SIZE = 1000;
    public static final int HOME_FEED_SIZE = 100;

    public static final int HOME_NEW_LINK_SIZE = 100;
    public static final int HOME_RECOMMEND_LINK_SIZE = 100;
    public static final int HOME_TOP_LINK_SIZE = 100;
    public static final int HOME_TREND_LINK_SIZE = 100;

    // 首页推荐每种类型的推荐数量
    public static final int HOME_FEED_PAGE_TOP_LINK_SIZE = 3;
    public static final int HOME_FEED_PAGE_TREND_LINK_SIZE = 3;
    public static final int HOME_FEED_PAGE_NEW_LINK_SIZE = 2;
    public static final int HOME_FEED_PAGE_RECOMMEND_LINK_SIZE = 2;

    // star收藏推荐每种类型的推荐数量
    public static final int STAR_FEED_PAGE_NEW_LINK_SIZE = 7;
    public static final int STAR_FEED_PAGE_RECOMMEND_LINK_SIZE = 3;

    public static final int STAR_FEED_PAGE_TOTAL_SIZE = 300;
    public static final int STAR_FEED_PAGE_LOCATION_LINK_SIZE = 2;
    public static final int STAR_FEED_PAGE_PERSON_LINK_SIZE = 4;
    public static final int STAR_FEED_PAGE_ITEM_LINK_SIZE = 4;

    /**
     * -------------------------------------------- 时间片相关 --------------------------------------------
     */
    public static final int DIMENSION_LIMIT = 10;

    public static final int IMG_LIMIT = 9;

    public static final int TS_REVIEW_PER_PAGE = 17;

    public static final int TS_ITEM_DESCRIPTION_LIMIT = 200;

    public static final int TS_PERSON_DESCRIPTION_LIMIT = 200;

    // chip
    public static final int CHIP_LOCATION_TYPE = 2;
    public static final int CHIP_PERSON_TYPE = 3;
    public static final int CHIP_ITEM_TYPE = 4;
    public static final int CHIP_HOT_TYPE = 5;
    public static final int CHIP_SEARCH_TYPE = 10;
    public static final int CHIP_WORD_TYPE = 15;
    public static final int CHIP_CATEGORY_TYPE = 16;
    public static final int CHIP_LINK_TYPE = 20;

    /**
     * -------------------------------------------- 用户相关 --------------------------------------------
     */
    public static final long USER_CUSTOM_STATUS_MAX_TIME = 10 * TimeUtils.MILLIS_PER_DAY;

    public static final int USER_FOLLOW_MAX_SIZE = 1000;

    public static final int USER_FAN_MAX_SIZE = 1000;

    public static final int USER_LIST_PAGE_SIZE = 10;

    /**
     * -------------------------------------------- 搜索相关 --------------------------------------------
     */
    public static final int PERSON_HINT_SIZE = 10;
    public static final int WORD_HINT_SIZE = 10;
    public static final int CATEGORY_HINT_SIZE = 10;

    public static final int SEARCH_HINT_SIZE = 3;

    public static final int SEARCH_PAGE_SIZE = 10;
    public static final int SEARCH_Analysis_STEP_SIZE = 1;

    // 搜搜结果关键词最多数量
    public static final int SEARCH_RESULT_LIMIT_SIZE = 1000;
    public static final int SEARCH_CONTENT_PAGE_SIZE = 3;
    public static final int SEARCH_LOCATION_PAGE_SIZE = 3;
    public static final int SEARCH_PERSON_PAGE_SIZE = 2;
    public static final int SEARCH_ITEM_PAGE_SIZE = 2;


    /**
     * -------------------------------------------- 聊天相关 --------------------------------------------
     */
    // 添加好友过后的第一条发送消息
    public static final String FRIEND_FIRST_MESSAGE = "hi";
    // 好友的上限
    public static final int FRIEND_LIMIT = 200;
    // 黑名单上限
    public static final int BLACKLIST_LIMIT = 200;
    // 好友申请列表的显示上限，之推给客户端最新的数量的好友申请
    public static final int FRIEND_APPLY_LIMIT = 200;
    // 好友聊天保存聊天数据的最大数量
    public static final int FRIEND_CHAT_MESSAGE_MAX_SIZE = 200;
    // 好友历史记录的消息数量
    public static final int FRIEND_HISTORY_MESSAGE_PER_REQUEST_SIZE = 20;


    /**
     * -------------------------------------------- 群组相关 --------------------------------------------
     */
    public static final String GROUP_DEFAULT_GROUP_NAME_SUFFIX = "的群组";
    public static final String GROUP_DEFAULT_PARENT_CHANNEL_CN_NAME = "默认分类";
    public static final String GROUP_DEFAULT_CHANNEL_CN_NAME = "常规";
    public static final String GROUP_COMMON_CHANNEL_CN_NAME = "公共频道";
    public static final String GROUP_MAIN_CHANNEL_BOX = "main";
    public static final long GROUP_AUTH_DEFAULT_ID = 1;
    public static final String GROUP_AUTH_DEFAULT_NAME = "默认身份";
    public static final int GROUP_AUTH_COLOR_LENGTH = 7;

    // 群组聊天保存聊天数据的最大数量
    public static final int GROUP_CHAT_MESSAGE_MAX_SIZE = 500;
    // 群组聊天的Pin消息数据的最大数量
    public static final int GROUP_CHAT_PIN_MESSAGE_MAX_SIZE = 50;

    public static final int GROUP_CHANNEL_MAX_SIZE = 100;

    // 浊浮官方群账号
    public static final long ZFOO_GROUP_ID = -1418;

}
