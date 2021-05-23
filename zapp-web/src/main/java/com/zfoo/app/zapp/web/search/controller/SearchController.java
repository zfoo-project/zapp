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

package com.zfoo.app.zapp.web.search.controller;

import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.constant.LocationConstant;
import com.zfoo.app.zapp.common.entity.search.SearchEntity;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.web.search.model.SearchHintVO;
import com.zfoo.app.zapp.web.search.model.SearchHotVO;
import com.zfoo.app.zapp.web.search.service.ISearchService;
import com.zfoo.app.zapp.web.time.service.TimeSliceService;
import com.zfoo.app.zapp.web.word.service.IWordService;
import com.zfoo.event.model.event.AppStartEvent;
import com.zfoo.net.util.SingleCache;
import com.zfoo.orm.OrmContext;
import com.zfoo.protocol.util.StringUtils;
import com.zfoo.scheduler.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-22 21:28
 */
@Controller
public class SearchController implements ApplicationListener<AppStartEvent> {

    @Autowired
    private IWordService wordService;

    @Autowired
    private TimeSliceService timeSliceService;

    @Autowired
    private ISearchService searchService;

    private SingleCache<List<SearchHotVO>> hotSearchCache;

    /**
     * 搜索内容
     * 通过关键字搜索相关内容
     *
     * @param query 搜索关键字
     * @param page  页数，默认是第1页
     * @return 对象数组
     * @real_return {@link java.util.List<com.zfoo.app.zapp.common.model.time.TimeSliceVO>}
     */
    @GetMapping("/api/search")
    @ResponseBody
    public BaseResponse search(HttpServletRequest request, @RequestParam("query") String query, @RequestParam("page") int page) {
        if (StringUtils.isBlank(query)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }
        var list = searchService.search(query, page);
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, list);
    }


    /**
     * 搜索的提示
     * 通过关键字提示相关内容
     *
     * @param query 搜索关键字
     * @return 对象数组
     * @real_return {@link java.util.List<com.zfoo.app.zapp.web.search.model.SearchHintVO>}
     */
    @GetMapping("/api/search/hint")
    @ResponseBody
    public BaseResponse searchHint(HttpServletRequest request, @RequestParam("query") String query) {
        if (StringUtils.isBlank(query)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY);
        }

        var locationHintList = LocationConstant.locationIdMap.entrySet()
                .stream()
                .filter(it -> it.getValue().startsWith(query))
                .limit(AppConstant.SEARCH_HINT_SIZE)
                .map(it -> SearchHintVO.valueOf(AppConstant.CHIP_LOCATION_TYPE, it.getKey(), it.getValue()))
                .collect(Collectors.toList());

        var personHintList = wordService.personHint(query, AppConstant.SEARCH_HINT_SIZE)
                .stream()
                .map(it -> SearchHintVO.valueOf(AppConstant.CHIP_PERSON_TYPE, it.getKey(), it.getValue()))
                .collect(Collectors.toList());

        var itemHintList = wordService.wordHint(query, AppConstant.SEARCH_HINT_SIZE)
                .stream()
                .map(it -> SearchHintVO.valueOf(AppConstant.CHIP_ITEM_TYPE, it.getKey(), it.getValue()))
                .collect(Collectors.toList());

        var result = new ArrayList<SearchHintVO>();
        result.addAll(locationHintList);
        result.addAll(personHintList);
        result.addAll(itemHintList);

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, result);
    }


    /**
     * 热搜
     * 热搜的内容
     *
     * @return 热搜条目
     * @real_return {@link java.util.List<com.zfoo.app.zapp.web.search.model.SearchHintVO>}
     */
    @GetMapping("/api/hot")
    @ResponseBody
    public BaseResponse hot(HttpServletRequest request) {
        var list = hotSearchCache.get();
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, list);
    }

    @Override
    public void onApplicationEvent(AppStartEvent appStartEvent) {
        hotSearchCache = SingleCache.build(10 * TimeUtils.MILLIS_PER_MINUTE, new Supplier<List<SearchHotVO>>() {
            @Override
            public List<SearchHotVO> get() {
                var hotSearchList = new ArrayList<SearchEntity>();
                OrmContext.getOrmManager()
                        .getCollection(SearchEntity.class)
                        .find()
                        .sort(Sorts.descending("score"))
                        .projection(Projections.include("_id", "score"))
                        .limit(10)
                        .forEach(it -> hotSearchList.add(it));

                var result = new ArrayList<SearchHotVO>();
                for (var i = 0; i < hotSearchList.size(); i++) {
                    var entity = hotSearchList.get(i);
                    result.add(SearchHotVO.valueOf(AppConstant.CHIP_HOT_TYPE, i + 1, entity.getId(), entity.getScore()));
                }
                return result;
            }
        });
    }
}
