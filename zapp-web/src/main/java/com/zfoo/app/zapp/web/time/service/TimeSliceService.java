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

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.constant.LocationConstant;
import com.zfoo.app.zapp.common.constant.OssPolicyConstant;
import com.zfoo.app.zapp.common.constant.ReportConstant;
import com.zfoo.app.zapp.common.entity.core.WordEntity;
import com.zfoo.app.zapp.common.entity.time.TimeSliceEntity;
import com.zfoo.app.zapp.common.entity.time.TsEditEntity;
import com.zfoo.app.zapp.common.entity.time.model.TimeKeyPO;
import com.zfoo.app.zapp.common.entity.time.model.TimeLinkAlbumPO;
import com.zfoo.app.zapp.common.entity.time.model.VideoLinkPO;
import com.zfoo.app.zapp.common.model.LicenseEnum;
import com.zfoo.app.zapp.common.model.OssPolicyEnum;
import com.zfoo.app.zapp.common.model.time.TimeSliceVO;
import com.zfoo.app.zapp.common.protocol.cache.GetUserCacheAnswer;
import com.zfoo.app.zapp.common.protocol.cache.GetUserCacheAsk;
import com.zfoo.app.zapp.common.protocol.cache.model.UserCache;
import com.zfoo.app.zapp.common.protocol.cache.refresh.RefreshUserTsCacheAsk;
import com.zfoo.app.zapp.common.protocol.user.time.DeleteTimeSliceAsk;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.web.time.model.event.DeleteTimeSliceEvent;
import com.zfoo.app.zapp.web.time.model.vo.CreateTimeSliceVO;
import com.zfoo.app.zapp.web.util.HttpUtils;
import com.zfoo.app.zapp.web.util.SliceUtils;
import com.zfoo.app.zapp.web.word.service.WordService;
import com.zfoo.event.manager.EventBus;
import com.zfoo.net.NetContext;
import com.zfoo.net.packet.common.PairLS;
import com.zfoo.net.util.SimpleCache;
import com.zfoo.orm.OrmContext;
import com.zfoo.orm.util.MongoIdUtils;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.model.Pair;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-01-28 13:56
 */
@Component
public class TimeSliceService implements ITimeSliceService {

    private static final Logger logger = LoggerFactory.getLogger(TimeSliceService.class);

    @Autowired
    private WordService wordService;

    public SimpleCache<Long, TimeSliceVO> timeSliceCaches = SimpleCache.build(
            10 * TimeUtils.MILLIS_PER_MINUTE, 5 * TimeUtils.MILLIS_PER_MINUTE, 1_0000
            , links -> OrmContext.getQuery().queryFieldIn("_id", links, TimeSliceEntity.class)
                    .stream()
                    .map(it -> {
                        var list = converter(List.of(it));
                        var vo = CollectionUtils.isNotEmpty(list)
                                ? list.get(0)
                                : TimeSliceVO.valueOf(UserCache.valueOf(0L, AppConstant.USER_UNKNOWN_NAME), it.getId(), AppConstant.TIME_SLICE_UNKNOWN_NAME);
                        return new Pair<>(it.getId(), vo);
                    })
                    .collect(Collectors.toList())
            , key -> TimeSliceVO.valueOf(UserCache.valueOf(0L, AppConstant.USER_UNKNOWN_NAME), key, AppConstant.TIME_SLICE_UNKNOWN_NAME));


    @PostConstruct
    public void init() throws IOException {
        LocationConstant.init();
        ReportConstant.init();
        OssPolicyConstant.init();
    }


    @Override
    public List<TimeSliceVO> converter(List<TimeSliceEntity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return Collections.EMPTY_LIST;
        }

        // 远程请求基本的用户信息
        var userIds = entityList.stream().map(it -> it.getUserId()).collect(Collectors.toSet());
        var ask = GetUserCacheAsk.valueOf(userIds);
        GetUserCacheAnswer answer = null;
        try {
            answer = NetContext.getConsumer().syncAsk(ask, GetUserCacheAnswer.class, null).packet();
        } catch (Exception e) {
            logger.error("获取用户信息异常", e);
            return Collections.EMPTY_LIST;
        }

        var userCacheMap = answer.getUserCacheMap();

        var result = new ArrayList<TimeSliceVO>();
        for (var entity : entityList) {
            var userCache = userCacheMap.get(entity.getUserId());
            if (userCache == null) {
                continue;
            }
            var id = entity.id();
            var love = entity.getLove();
            var type = entity.getType();
            var start = TimeUtils.timeToString(entity.getStart());
            var end = TimeUtils.timeToString(entity.getEnd());
            var locations = LocationConstant.converter(entity.getLocations());

            var persons = wordService.personCaches.batchGet(entity.getPersons()).entrySet()
                    .stream()
                    .map(it -> PairLS.valueOf(it.getKey(), it.getValue()))
                    .collect(Collectors.toList());

            var items = wordService.wordCaches.batchGet(entity.getItems()).entrySet()
                    .stream()
                    .map(it -> PairLS.valueOf(it.getKey(), it.getValue()))
                    .collect(Collectors.toList());

            var content = entity.getContent();
            var images = entity.getImages();
            var video = entity.getVideo();
            var key = entity.getKey();
            var albums = entity.getAlbums();

            var vo = TimeSliceVO.valueOf(userCache, id, love, type, start, end, locations, persons, items, content
                    , images, video, key, albums);

            result.add(vo);
        }

        return result;
    }

    @Override
    public List<TimeSliceVO> existTimeSliceList(List<Long> linkList) {
        var timeSliceList = timeSliceCaches.batchGet(linkList)
                .entrySet()
                .stream()
                .map(it -> it.getValue())
                .filter(it -> it.getUserInfo().getId() != 0L)
                .collect(Collectors.toList());
        return timeSliceList;
    }

    @Override
    public List<Long> notExistTimeLinks(List<Long> timeLinks) {
        return timeSliceCaches.batchGet(timeLinks)
                .entrySet()
                .stream()
                .filter(it -> it.getValue().getUserInfo().getId() == 0L)
                .map(it -> it.getKey())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTimeSlice(long timeSliceId) {
        var timeSliceList = existTimeSliceList(List.of(timeSliceId));
        if (CollectionUtils.isEmpty(timeSliceList)) {
            return;
        }
        var timeSliceVO = timeSliceList.stream().findFirst().get();
        var userId = timeSliceVO.getUserInfo().getId();

        NetContext.getConsumer().send(DeleteTimeSliceAsk.valueOf(timeSliceId), timeSliceId);

        // 然后刷新用户的时间片缓存
        NetContext.getConsumer().send(RefreshUserTsCacheAsk.valueOf(Set.of(userId)), userId);

        // 时间片删除事件
        EventBus.syncSubmit(DeleteTimeSliceEvent.value(List.of(timeSliceVO)));

        // 删除编辑的时间片
        OrmContext.getAccessor().delete(timeSliceId, TsEditEntity.class);

        // 删除时间片的缓存
        timeSliceCaches.invalidate(timeSliceId);
    }

    @Override
    public boolean checkVideoLink(VideoLinkPO video) {
        if (video == null) {
            return true;
        }
        if (StringUtils.isBlank(video.getPoster()) || StringUtils.isBlank(video.getUrl())) {
            return false;
        }
        if (!video.getPoster().startsWith(OssPolicyEnum.VIDEO_POSTER.getUrl()) || !video.getUrl().startsWith(OssPolicyEnum.VIDEO.getUrl())) {
            return false;
        }

        return HttpUtils.isHttpUrl(video.getPoster()) && HttpUtils.isHttpUrl(video.getUrl());
    }

    @Override
    public boolean checkTimeKey(TimeKeyPO key) {
        if (key == null) {
            return true;
        }

        var headers = key.getHeaders();
        var rows = key.getRows();
        if (CollectionUtils.isEmpty(headers)) {
            return false;
        }
        if (CollectionUtils.isEmpty(rows)) {
            return false;
        }
        var maxIndexNum = headers.size();
        for (var header : headers) {
            if (StringUtils.isBlank(header.getText())) {
                return false;
            }
            if (StringUtils.isBlank(header.getValue())) {
                return false;
            }
            if (SliceUtils.fromNumberSystem26(header.getValue()) > maxIndexNum) {
                return false;
            }
        }

        for (var row : rows) {
            row.minifyRow(maxIndexNum);
            if (!row.notAllBlank()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkTimeLinkAlbums(List<TimeLinkAlbumPO> albums) {
        if (CollectionUtils.isEmpty(albums)) {
            return true;
        }

        var linkList = new ArrayList<Long>();
        for (var timeLinkAlbum : albums) {
            if (StringUtils.isBlank(timeLinkAlbum.getAlbum())) {
                return false;
            }
            if (CollectionUtils.isEmpty(timeLinkAlbum.getLinks())) {
                return false;
            }
            linkList.addAll(timeLinkAlbum.getLinks());
        }

        return notExistTimeLinks(linkList).size() == 0;
    }

    @Override
    public BaseResponse checkCreateTimeSliceVO(CreateTimeSliceVO vo) throws ParseException {
        var type = vo.getType();
        var start = vo.getStart();
        var end = vo.getEnd();
        var locations = vo.getLocations();
        var persons = vo.getPersons();
        var items = vo.getItems();
        var content = vo.getContent();
        var images = vo.getImages();
        var video = vo.getVideo();
        var key = vo.getKey();
        var albums = vo.getAlbums();

        // 验证客户端的各个参数
        if (LicenseEnum.getLicenseEnumByType(type) == null) {
            return BaseResponse.valueOf(CodeEnum.TIME_SLICE_TYPE_ERROR);
        }

        var startDateTime = TimeUtils.stringToDate(start);
        var endDateTime = TimeUtils.stringToDate(end);

        // 开始时间必须小于或者等于结束时间
        if (startDateTime.getTime() > endDateTime.getTime()) {
            return BaseResponse.valueOf(CodeEnum.TIME_SLICE_TIME_ERROR);
        }

        // 检查地点，相关的人，相关的物是否存在
        if (!LocationConstant.checkLocations(locations)) {
            return BaseResponse.valueOf(CodeEnum.LOCATION_NOT_EXIST);
        }
        if (CollectionUtils.isNotEmpty(persons) && wordService.existPersonList(persons).size() != persons.size()) {
            return BaseResponse.valueOf(CodeEnum.PERSON_NOT_EXIST);
        }
        if (CollectionUtils.isNotEmpty(items) && wordService.existWordList(items).size() != items.size()) {
            return BaseResponse.valueOf(CodeEnum.ITEM_NOT_EXIST);
        }

        // 检查地点，相关的人，相关的物是否重复
        if (CollectionUtils.isNotEmpty(locations)) {
            var size = LocationConstant.converter(locations)
                    .stream()
                    .map(it -> it.getValue().trim())
                    .collect(Collectors.toSet())
                    .size();
            if (size != locations.size()) {
                return BaseResponse.valueOf(CodeEnum.LOCATION_DUPLICATE);
            }
        }
        if (CollectionUtils.isNotEmpty(persons)) {
            var size = wordService.existPersonList(persons)
                    .stream()
                    .map(it -> it.getValue().trim())
                    .collect(Collectors.toSet())
                    .size();
            if (size != persons.size()) {
                return BaseResponse.valueOf(CodeEnum.PERSON_DUPLICATE);
            }
        }
        if (CollectionUtils.isNotEmpty(items)) {
            var existItemSet = wordService.existWordList(items)
                    .stream()
                    .map(it -> it.getValue().trim())
                    .collect(Collectors.toSet());
            if (existItemSet.size() != items.size()) {
                return BaseResponse.valueOf(CodeEnum.ITEM_DUPLICATE);
            }

            var notExistItemSet = vo.getNotExistItems();
            if (CollectionUtils.isNotEmpty(notExistItemSet)) {
                var itemSet = new HashSet<String>();
                itemSet.addAll(existItemSet);
                itemSet.addAll(notExistItemSet.stream().map(it -> it.trim()).collect(Collectors.toSet()));
                if ((existItemSet.size() + notExistItemSet.size()) != itemSet.size()) {
                    return BaseResponse.valueOf(CodeEnum.ITEM_DUPLICATE);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(images)) {
            if (images.size() > AppConstant.IMG_LIMIT) {
                return BaseResponse.valueOf(CodeEnum.IMG_LIMIT_ERROR);
            } else if (!HttpUtils.isHttpUrls(images)) {
                return BaseResponse.valueOf(CodeEnum.URL_ERROR);
            } else if (images.stream().anyMatch(it -> !it.startsWith(OssPolicyEnum.IMAGE.getUrl()))) {
                return BaseResponse.valueOf(CodeEnum.IMG_PATH_ERROR);
            }
        }

        if (!checkVideoLink(video)) {
            return BaseResponse.valueOf(CodeEnum.VIDEO_URL_ERROR);
        }

        if (!checkTimeKey(key)) {
            return BaseResponse.valueOf(CodeEnum.TS_KEY_ERROR);
        }

        if (!checkTimeLinkAlbums(albums)) {
            return BaseResponse.valueOf(CodeEnum.TS_LINK_ALBUM_ERROR);
        }

        if (CollectionUtils.isNotEmpty(images) && video != null) {
            return BaseResponse.valueOf(CodeEnum.IMG_AND_VIDEO_ERROR);
        }

        return null;
    }

    @Override
    public List<Long> toItems(List<Long> items, List<String> notExistItems) {
        var itemIdSet = new HashSet<Long>();

        if (CollectionUtils.isNotEmpty(items)) {
            itemIdSet.addAll(items);
        }

        if (CollectionUtils.isEmpty(notExistItems)) {
            return new ArrayList<>(itemIdSet);
        }

        notExistItems = notExistItems.stream().map(it -> it.trim()).collect(Collectors.toList());

        var list = OrmContext.getQuery().queryFieldIn("word", notExistItems, WordEntity.class);
        itemIdSet.addAll(list.stream().map(it -> it.getId()).collect(Collectors.toList()));

        var needInsertItems = notExistItems.stream()
                .filter(it -> list.stream().noneMatch(word -> it.equals(word.getWord().trim())))
                .map(it -> {
                    var id = MongoIdUtils.getIncrementIdFromMongoDefault(WordEntity.class);
                    itemIdSet.add(id);
                    return WordEntity.valueOf(id, it.trim());
                })
                .collect(Collectors.toList());

        OrmContext.getAccessor().batchInsert(needInsertItems);
        return new ArrayList<>(itemIdSet);
    }

}
