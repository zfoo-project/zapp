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

import com.zfoo.app.zapp.common.model.Element;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.protocol.util.IOUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-02 17:43
 */
public abstract class ReportConstant {

    public static Map<Integer, String> reportMap;

    public static void init() throws IOException {
        parseReports();
    }


    private static void parseReports() throws IOException {
        var inputStream = ClassUtils.getFileFromClassPath("reports.json");
        var bytes = IOUtils.toByteArray(inputStream);
        var str = StringUtils.bytesToString(bytes);
        var elementList = JsonUtils.string2Array(str, Element.class);
        var map = new HashMap<Integer, String>();

        // 广度优先搜索，遍历所有地点的id
        var queue = new LinkedList<>(List.of(elementList));
        while (!queue.isEmpty()) {
            var item = queue.poll();
            map.put((int) item.getId(), item.getName());
            if (CollectionUtils.isNotEmpty(item.getChildren())) {
                for (var child : item.getChildren()) {
                    queue.offer(child);
                }
            }
        }

        reportMap = Collections.unmodifiableMap(map);

        IOUtils.closeIO(inputStream);
    }
}
