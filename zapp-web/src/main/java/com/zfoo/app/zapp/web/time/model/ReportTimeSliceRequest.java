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

package com.zfoo.app.zapp.web.time.model;


import java.util.List;

/**
 * 举报时间片的请求
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-01-27 21:22
 */
public class ReportTimeSliceRequest {

    /**
     * 时间片的id
     */
    private long timeSliceId;

    /**
     * 选项类型
     */
    private int optionType;

    /**
     * 选择的条目
     */
    private List<Integer> selectedItems;

    /**
     * 举报的内容
     */
    private String content;

    /**
     * 举报的附加文件
     */
    private List<String> fileLinks;

    public long getTimeSliceId() {
        return timeSliceId;
    }

    public void setTimeSliceId(long timeSliceId) {
        this.timeSliceId = timeSliceId;
    }

    public int getOptionType() {
        return optionType;
    }

    public void setOptionType(int optionType) {
        this.optionType = optionType;
    }

    public List<Integer> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<Integer> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getFileLinks() {
        return fileLinks;
    }

    public void setFileLinks(List<String> fileLinks) {
        this.fileLinks = fileLinks;
    }
}
