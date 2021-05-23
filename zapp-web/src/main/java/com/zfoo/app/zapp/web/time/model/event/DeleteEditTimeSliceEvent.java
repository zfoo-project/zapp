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

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020 07.28 17:48
 */
public class DeleteEditTimeSliceEvent implements IEvent {

    /**
     * 编辑的时间片
     */
    private List<TimeSliceEntity> editTimeSlices;

    /**
     * 编辑的时间片对应的存在的时间片
     */
    private List<TimeSliceEntity> timeSlices;

    public static DeleteEditTimeSliceEvent value(List<TimeSliceEntity> editTimeSlices, List<TimeSliceEntity> timeSlices) {
        var event = new DeleteEditTimeSliceEvent();
        event.editTimeSlices = editTimeSlices;
        event.timeSlices = timeSlices;
        return event;
    }

    public List<TimeSliceEntity> getEditTimeSlices() {
        return editTimeSlices;
    }

    public List<TimeSliceEntity> getTimeSlices() {
        return timeSlices;
    }
}
