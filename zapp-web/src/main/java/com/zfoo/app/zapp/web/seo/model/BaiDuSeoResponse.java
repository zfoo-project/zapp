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

package com.zfoo.app.zapp.web.seo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-07-27 11:27
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaiDuSeoResponse {

    /**
     * 成功推送的url条数
     */
    @JsonProperty("success")
    private int success;

    /**
     * 当天剩余的可推送url条数
     */
    @JsonProperty("remain")
    private int remain;

    /**
     * 由于不是本站url而未处理的url列表
     */
    @JsonProperty("not_same_site")
    private List<String> notSameSite;

    /**
     * 不合法的url列表
     */
    @JsonProperty("not_valid")
    private List<String> notValid;

    /**
     * 错误码，与状态码相同
     */
    @JsonProperty("error")
    private int error;

    /**
     * 错误描述
     */
    @JsonProperty("message")
    private String message;


    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getRemain() {
        return remain;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }

    public List<String> getNotSameSite() {
        return notSameSite;
    }

    public void setNotSameSite(List<String> notSameSite) {
        this.notSameSite = notSameSite;
    }

    public List<String> getNotValid() {
        return notValid;
    }

    public void setNotValid(List<String> notValid) {
        this.notValid = notValid;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
