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

package com.zfoo.app.zapp.web.time.service;

import com.zfoo.app.zapp.common.entity.time.TimeSliceEntity;
import com.zfoo.app.zapp.common.entity.time.model.TimeKeyPO;
import com.zfoo.app.zapp.common.entity.time.model.TimeLinkAlbumPO;
import com.zfoo.app.zapp.common.entity.time.model.VideoLinkPO;
import com.zfoo.app.zapp.common.model.time.TimeSliceVO;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.web.time.model.vo.CreateTimeSliceVO;

import java.text.ParseException;
import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-01-28 13:56
 */
public interface ITimeSliceService {

    /**
     * 将时间片的entity转换为vo视图对象，因为会远程请求用户信息，没有用户信息的时间片将会被忽略
     */
    List<TimeSliceVO> converter(List<TimeSliceEntity> entityList);

    /**
     * 获取存在的时间片
     */
    List<TimeSliceVO> existTimeSliceList(List<Long> linkList);

    /**
     * 检查时间片的id，并返回不存在的时间片
     */
    List<Long> notExistTimeLinks(List<Long> timeLinks);

    void deleteTimeSlice(long timeSliceId);

    /**
     * 检查视频是否合法
     */
    boolean checkVideoLink(VideoLinkPO video);

    /**
     * 检查键值对是否合法，主要判断行和列是否匹配
     */
    boolean checkTimeKey(TimeKeyPO key);

    boolean checkTimeLinkAlbums(List<TimeLinkAlbumPO> albums);

    /**
     * 检查时间片的链接是否合法，主要判断时间片是否存在
     */
    BaseResponse checkCreateTimeSliceVO(CreateTimeSliceVO vo) throws ParseException;

    /**
     * 将不存在的物品转换为有id的物品
     */
    List<Long> toItems(List<Long> items, List<String> notExistItems);
}
