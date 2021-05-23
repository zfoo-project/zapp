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

package com.zfoo.app.zapp.web.oss;

import com.zfoo.app.zapp.common.constant.OssPolicyConstant;
import com.zfoo.app.zapp.common.constant.ZappDeployEnum;
import com.zfoo.app.zapp.common.model.OssPolicyEnum;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-06-07 14:58
 */
public class OssPolicyConstantTest {

    static {
        ZappDeployEnum.initDevDeploy();
    }

    @Test
    public void test() throws IOException {
        OssPolicyConstant.init();

        for (var ossPolicyEnum : OssPolicyEnum.values()) {
            var url = ossPolicyEnum.getUrl();
            Assert.assertTrue(ossPolicyEnum.toUrlAndDir(1).startsWith(url));
            Assert.assertFalse(ossPolicyEnum.toUrlAndDir(1).contains("{}"));
        }
    }

}
