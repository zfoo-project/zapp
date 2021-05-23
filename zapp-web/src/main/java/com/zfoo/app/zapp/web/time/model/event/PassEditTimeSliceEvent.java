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

package com.zfoo.app.zapp.web.time.model.event;

import com.zfoo.app.zapp.common.entity.time.TimeSliceEntity;
import com.zfoo.event.model.event.IEvent;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020 05.23 17:48
 */
public class PassEditTimeSliceEvent implements IEvent {

    private TimeSliceEntity oldEntity;
    private TimeSliceEntity newEntity;

    private boolean recommend;


    public static PassEditTimeSliceEvent valueOf(TimeSliceEntity oldEntity, TimeSliceEntity newEntity, boolean recommend) {
        var event = new PassEditTimeSliceEvent();
        event.oldEntity = oldEntity;
        event.newEntity = newEntity;
        event.recommend = recommend;
        return event;
    }

    public TimeSliceEntity getOldEntity() {
        return oldEntity;
    }

    public TimeSliceEntity getNewEntity() {
        return newEntity;
    }

    public boolean isRecommend() {
        return recommend;
    }
}
