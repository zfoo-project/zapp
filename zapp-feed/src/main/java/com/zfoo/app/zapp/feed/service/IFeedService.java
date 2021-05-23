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

package com.zfoo.app.zapp.feed.service;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-16 13:58
 */
public interface IFeedService {

    void createTs(long tsId, boolean recommend);

    void loveTs(long tsId, long love, long score);

    void createTsWithLocation(long locationId, long tsId, boolean recommend);

    void createTsWithItem(long itemId, long tsId, boolean recommend);

    void createTsWithPerson(long personId, long tsId, boolean recommend);

    void loveTsWithLocation(long locationId, long tsId, long love, long score);

    void loveTsWithItem(long itemId, long tsId, long love, long score);

    void loveTsWithPerson(long personId, long tsId, long love, long score);


}
