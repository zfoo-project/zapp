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

package com.zfoo.app.zapp.common.constant;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-11-09 17:45
 */
public enum ZappDeployEnum {


    /**
     * 开发环境
     */
    dev,

    /**
     * 正式环境
     */
    pro,

    ;

    private static final String PROFILE = "spring.profiles.active";

    public static void initDevDeploy() {
        if (System.getProperty(PROFILE) == null) {
            //设置系统变量
            System.setProperty(PROFILE, ZappDeployEnum.dev.name());
        }
    }

    public static ZappDeployEnum getDeployEnum() {
        return ZappDeployEnum.valueOf(System.getProperty(PROFILE));
    }

}
