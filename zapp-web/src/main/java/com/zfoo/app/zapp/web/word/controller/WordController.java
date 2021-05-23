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

package com.zfoo.app.zapp.web.word.controller;

import com.zfoo.app.zapp.common.constant.AppConstant;
import com.zfoo.app.zapp.common.entity.core.CategoryEntity;
import com.zfoo.app.zapp.common.entity.core.PersonEntity;
import com.zfoo.app.zapp.common.entity.core.WordEntity;
import com.zfoo.app.zapp.common.entity.user.model.AdminAuthEnum;
import com.zfoo.app.zapp.common.model.OssPolicyEnum;
import com.zfoo.app.zapp.common.protocol.cache.GetCategoryCacheAnswer;
import com.zfoo.app.zapp.common.protocol.cache.GetCategoryCacheAsk;
import com.zfoo.app.zapp.common.protocol.cache.GetWordCacheAnswer;
import com.zfoo.app.zapp.common.protocol.cache.GetWordCacheAsk;
import com.zfoo.app.zapp.common.protocol.cache.refresh.RefreshCategoryCacheAsk;
import com.zfoo.app.zapp.common.protocol.cache.refresh.RefreshWordCacheAsk;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserAdminAuthAnswer;
import com.zfoo.app.zapp.common.protocol.user.info.GetUserAdminAuthAsk;
import com.zfoo.app.zapp.common.result.BaseResponse;
import com.zfoo.app.zapp.common.result.CodeEnum;
import com.zfoo.app.zapp.common.util.CommonUtils;
import com.zfoo.app.zapp.web.util.HttpUtils;
import com.zfoo.app.zapp.web.word.model.*;
import com.zfoo.app.zapp.web.word.service.IWordService;
import com.zfoo.event.manager.EventBus;
import com.zfoo.net.NetContext;
import com.zfoo.net.packet.common.PairString;
import com.zfoo.orm.OrmContext;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-13 15:57
 */
@Controller
public class WordController {

    @Autowired
    private IWordService wordService;

    /**
     * 用户输人名称的提示
     *
     * @param name 名称输入
     * @return 对象数组
     * @real_return {@link java.util.List<  com.zfoo.net.packet.common.PairLS>}
     */
    @GetMapping(value = "/api/word/personHint")
    @ResponseBody
    public BaseResponse personHint(@RequestParam("name") String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        var personHints = wordService.personHint(name, AppConstant.PERSON_HINT_SIZE);
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, personHints);
    }

    /**
     * 用户创输入关键词的word提示
     *
     * @param name 名称输入
     * @return 对象数组
     * @real_return {@link java.util.List<  com.zfoo.net.packet.common.PairLS>}
     */
    @GetMapping(value = "/api/word/wordHint")
    @ResponseBody
    public BaseResponse wordHint(@RequestParam("name") String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        var wordHints = wordService.wordHint(name, AppConstant.WORD_HINT_SIZE);
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, wordHints);
    }

    /**
     * 用户创输入关键词的category提示
     *
     * @param name 名称输入
     * @return 对象数组
     * @real_return {@link java.util.List<  com.zfoo.net.packet.common.PairLS>}
     */
    @GetMapping(value = "/api/word/categoryHint")
    @ResponseBody
    public BaseResponse categoryHint(@RequestParam("name") String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        var categoryHints = wordService.categoryHint(name, AppConstant.CATEGORY_HINT_SIZE);
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, categoryHints);
    }


    /**
     * 词条信息
     *
     * @param words 词条信息的id，用-连接，如1-2-3
     * @return 对象数组
     * @real_return {@link java.util.List<com.zfoo.app.zapp.common.protocol.cache.model.WordVO>}
     */
    @GetMapping(value = "/api/word/info")
    @ResponseBody
    public BaseResponse wordInfo(HttpServletRequest request, @RequestParam("words") String words) throws Exception {
        var wordSet = HttpUtils.dashParamToSet(words);
        if (CollectionUtils.isEmpty(wordSet)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var firstWord = wordSet.stream().findFirst().get();
        var answer = NetContext.getConsumer().syncAsk(GetWordCacheAsk.valueOf(wordSet), GetWordCacheAnswer.class, firstWord).packet();
        var wordMap = answer.getWordMap();

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, wordMap.entrySet().stream().map(it -> it.getValue()).collect(Collectors.toList()));
    }


    @GetMapping(value = "/api/category/info")
    @ResponseBody
    public BaseResponse categoryInfo(HttpServletRequest request, @RequestParam("categories") String categories) throws Exception {
        var categorySet = HttpUtils.dashParamToSet(categories);
        if (CollectionUtils.isEmpty(categorySet)) {
            return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, Collections.EMPTY_LIST);
        }

        var firstCategory = categorySet.stream().findFirst().get();
        var answer = NetContext.getConsumer().syncAsk(GetCategoryCacheAsk.valueOf(categorySet), GetCategoryCacheAnswer.class, firstCategory).packet();
        var wordMap = answer.getCategoryMap();

        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, wordMap.entrySet().stream().map(it -> it.getValue()).collect(Collectors.toList()));
    }


    /**
     * 词条详细信息
     * 这个接口获取到的的person是直接从数据库中读的，所以永远是最新的
     *
     * @param word 词条信息的id
     * @return 对象数组
     * @real_return {@link com.zfoo.app.zapp.web.word.model.WordDetailResponse}
     */
    @GetMapping(value = "/api/word/detail")
    @ResponseBody
    public BaseResponse wordDetail(HttpServletRequest request, @RequestParam("word") String word) throws Exception {
        var wordId = Long.parseLong(word);
        var answer = NetContext.getConsumer().syncAsk(GetWordCacheAsk.valueOf(Set.of(wordId)), GetWordCacheAnswer.class, wordId).packet();
        var wordMap = answer.getWordMap();
        var person = OrmContext.getAccessor().load(wordId, PersonEntity.class) != null;
        var response = WordDetailResponse.valueOf(person, wordMap.get(wordId));
        return BaseResponse.valueOf(CodeEnum.OK_QUIETLY, response);
    }

    /**
     * 词条修改
     * 用户可以提交词条的修改信息
     *
     * @param cm 修改词条的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/word/edit", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse editWord(HttpServletRequest request, @RequestBody EditWordRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }


        var id = cm.getId();
        var word = cm.getWord();
        var background = cm.getBackground();
        var paragraphs = cm.getParagraphs();
        var sections = cm.getSections();
        var links = cm.getLinks();
        var categories = cm.getCategories();
        var externalLinks = cm.getExternalLinks();

        if (!CommonUtils.isWordIdInRange(List.of(id))) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }
        if (StringUtils.isBlank(word)) {
            return BaseResponse.valueOf(CodeEnum.WORD_EMPTY_ERROR);
        }
        if (!StringUtils.isBlank(background)) {
            if (!HttpUtils.isHttpUrl(background) || !background.startsWith(OssPolicyEnum.WORD_BACKGROUND.getUrl())) {
                return BaseResponse.valueOf(CodeEnum.PARAMETER_URL_ERROR);
            }
        }
        if (CollectionUtils.isEmpty(paragraphs) || paragraphs.stream().anyMatch(it -> StringUtils.isBlank(it))) {
            return BaseResponse.valueOf(CodeEnum.WORD_PARAGRAPH_EMPTY_ERROR);
        }
        if (CollectionUtils.isEmpty(sections) || sections.stream().anyMatch(it -> StringUtils.isBlank(it))) {
            return BaseResponse.valueOf(CodeEnum.WORD_SECTION_EMPTY_ERROR);
        }
        if (CollectionUtils.isNotEmpty(links) && wordService.existWordList(links).size() != links.size()) {
            return BaseResponse.valueOf(CodeEnum.WORD_NOT_EXIST_ERROR);
        }
        if (CollectionUtils.isNotEmpty(categories) && wordService.existCategoryList(categories).size() != categories.size()) {
            return BaseResponse.valueOf(CodeEnum.WORD_NOT_EXIST_ERROR);
        }
        if (CollectionUtils.isNotEmpty(externalLinks)) {
            if (externalLinks.stream().anyMatch(it -> !HttpUtils.isHttpUrl(it.getKey()) || StringUtils.isBlank(it.getValue()))) {
                return BaseResponse.valueOf(CodeEnum.WORD_EXTERNAL_LINK_ERROR);
            }
        }

        var wordEntity = WordEntity.valueOf(id, word.trim());
        wordEntity.setBackground(StringUtils.trim(background));
        if (CollectionUtils.isNotEmpty(paragraphs)) {
            wordEntity.setParagraphs(paragraphs.stream().map(it -> it.trim()).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(sections)) {
            wordEntity.setSections(sections.stream().map(it -> it.trim()).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(links)) {
            wordEntity.setLinks(new HashSet<>(links));
        }
        if (CollectionUtils.isNotEmpty(categories)) {
            wordEntity.setCategories(new HashSet<>(categories));
        }
        if (CollectionUtils.isNotEmpty(externalLinks)) {
            wordEntity.setExternalLinks(externalLinks.stream().map(it -> PairString.valueOf(it.getKey().trim(), it.getValue().trim())).collect(Collectors.toList()));
        }
        // 如果是管理员用户则直接可以修改，不需要审核
        var answer = NetContext.getConsumer().syncAsk(GetUserAdminAuthAsk.valueOf(userId), GetUserAdminAuthAnswer.class, userId).packet();
        if (!AdminAuthEnum.getAuthEnumByType(answer.getAdminAuth()).hasAuth(AdminAuthEnum.ADMIN_AUTH)) {
            return BaseResponse.valueOf(CodeEnum.WORD_AUTH_ERROR);
        }

        OrmContext.getAccessor().update(wordEntity);
        if (cm.isPerson()) {
            var personEntity = OrmContext.getAccessor().load(id, PersonEntity.class);
            if (personEntity == null) {
                OrmContext.getAccessor().insert(PersonEntity.valueOf(id, word));
            } else {
                OrmContext.getAccessor().update(PersonEntity.valueOf(id, word));
            }
        }
        NetContext.getConsumer().send(RefreshWordCacheAsk.valueOf(Set.of(id)), id);
        EventBus.asyncSubmit(EditWordEvent.valueOf(wordEntity));
        return BaseResponse.valueOf(CodeEnum.OK);
    }


    /**
     * 类别修改
     * 用户可以提交类别的修改信息
     *
     * @param cm 修改类别的请求
     * @return 一个空的对象
     */
    @PostMapping(value = "/api/category/edit", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResponse editCategory(HttpServletRequest request, @RequestBody EditCategoryRequest cm) throws Exception {
        var userId = HttpUtils.getUid(request);
        if (!CommonUtils.isUserIdInRange(List.of(userId))) {
            return BaseResponse.valueOf(CodeEnum.SIGN_IN_FIRST);
        }


        var id = cm.getId();
        var name = cm.getName();
        var background = cm.getBackground();
        var parent = cm.getParent();
        var children = cm.getChildren();
        var words = cm.getWords();

        if (!CommonUtils.isWordIdInRange(List.of(id))) {
            return BaseResponse.valueOf(CodeEnum.PARAMETER_ERROR_ONE);
        }
        if (StringUtils.isBlank(name)) {
            return BaseResponse.valueOf(CodeEnum.WORD_EMPTY_ERROR);
        }
        if (!StringUtils.isBlank(background)) {
            if (!HttpUtils.isHttpUrl(background) || !background.startsWith(OssPolicyEnum.WORD_BACKGROUND.getUrl())) {
                return BaseResponse.valueOf(CodeEnum.PARAMETER_URL_ERROR);
            }
        }
        if (CollectionUtils.isNotEmpty(parent) && wordService.existWordList(parent).size() != parent.size()) {
            return BaseResponse.valueOf(CodeEnum.WORD_NOT_EXIST_ERROR);
        }
        if (CollectionUtils.isNotEmpty(children) && wordService.existWordList(children).size() != parent.size()) {
            return BaseResponse.valueOf(CodeEnum.WORD_NOT_EXIST_ERROR);
        }
        if (CollectionUtils.isNotEmpty(words) && wordService.existCategoryList(words).size() != words.size()) {
            return BaseResponse.valueOf(CodeEnum.WORD_NOT_EXIST_ERROR);
        }

        var categoryEntity = CategoryEntity.valueOf(id, name.trim());
        categoryEntity.setBackground(StringUtils.trim(background));
        if (CollectionUtils.isNotEmpty(parent)) {
            categoryEntity.setParent(new HashSet<>(parent));
        }
        if (CollectionUtils.isNotEmpty(children)) {
            categoryEntity.setChildren(new HashSet<>(children));
        }
        if (CollectionUtils.isNotEmpty(words)) {
            categoryEntity.setWords(new HashSet<>(words));
        }

        // 如果是管理员用户则直接可以修改，不需要审核
        var answer = NetContext.getConsumer().syncAsk(GetUserAdminAuthAsk.valueOf(userId), GetUserAdminAuthAnswer.class, userId).packet();
        if (!AdminAuthEnum.getAuthEnumByType(answer.getAdminAuth()).hasAuth(AdminAuthEnum.ADMIN_AUTH)) {
            return BaseResponse.valueOf(CodeEnum.WORD_CATEGORY_AUTH_ERROR);
        }

        OrmContext.getAccessor().update(categoryEntity);
        NetContext.getConsumer().send(RefreshCategoryCacheAsk.valueOf(Set.of(id)), id);
        EventBus.asyncSubmit(EditCategoryEvent.valueOf(categoryEntity));
        return BaseResponse.valueOf(CodeEnum.OK);
    }

}
