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

package com.zfoo.app.zapp.web.user.model;

import com.zfoo.app.zapp.web.time.model.vo.TimeSliceRejectVO;
import com.zfoo.app.zapp.web.time.model.vo.TimeSliceReviewVO;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-05-22 15:28
 */
public class GetUserCreationResponse {

    private List<TimeSliceReviewVO> tsReviews;

    private List<TimeSliceReviewVO> tsEdits;

    private List<TimeSliceRejectVO> tsReviewRejects;

    private List<TimeSliceRejectVO> tsEditRejects;

    public static GetUserCreationResponse valueOf(List<TimeSliceReviewVO> tsReviews, List<TimeSliceReviewVO> tsEdits, List<TimeSliceRejectVO> tsReviewRejects, List<TimeSliceRejectVO> tsEditRejects) {
        var response = new GetUserCreationResponse();
        response.tsReviews = tsReviews;
        response.tsEdits = tsEdits;
        response.tsReviewRejects = tsReviewRejects;
        response.tsEditRejects = tsEditRejects;
        return response;
    }

    public List<TimeSliceReviewVO> getTsReviews() {
        return tsReviews;
    }

    public void setTsReviews(List<TimeSliceReviewVO> tsReviews) {
        this.tsReviews = tsReviews;
    }

    public List<TimeSliceReviewVO> getTsEdits() {
        return tsEdits;
    }

    public void setTsEdits(List<TimeSliceReviewVO> tsEdits) {
        this.tsEdits = tsEdits;
    }

    public List<TimeSliceRejectVO> getTsReviewRejects() {
        return tsReviewRejects;
    }

    public void setTsReviewRejects(List<TimeSliceRejectVO> tsReviewRejects) {
        this.tsReviewRejects = tsReviewRejects;
    }

    public List<TimeSliceRejectVO> getTsEditRejects() {
        return tsEditRejects;
    }

    public void setTsEditRejects(List<TimeSliceRejectVO> tsEditRejects) {
        this.tsEditRejects = tsEditRejects;
    }
}
