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
 * 审核拒绝编辑的时间片的请求
 *
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-01-27 21:22
 */
public class RejectTimeSliceEditRequest {

    /**
     * 时间片id
     */
    private long tsEditId;

    /**
     * 拒绝原因类型
     */
    private int optionType;

    /**
     * 拒绝的条目
     */
    private List<Integer> selectedItems;

    /**
     * 拒绝的内容
     */
    private String content;

    public long getTsEditId() {
        return tsEditId;
    }

    public void setTsEditId(long tsEditId) {
        this.tsEditId = tsEditId;
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
}
