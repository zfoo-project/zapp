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
import com.zfoo.net.packet.common.PairLS;
import com.zfoo.protocol.collection.CollectionUtils;
import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.protocol.util.IOUtils;
import com.zfoo.protocol.util.JsonUtils;
import com.zfoo.protocol.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-03-02 17:43
 */
public abstract class LocationConstant {

    public static Map<Long, String> locationIdMap;
    public static Map<String, Long> locationNameMap;

    public static void init() throws IOException {
        parseLocations();
    }


    private static void parseLocations() throws IOException {
        var inputStream = ClassUtils.getFileFromClassPath("locations.json");
        var bytes = IOUtils.toByteArray(inputStream);
        var str = StringUtils.bytesToString(bytes);
        var elementList = JsonUtils.string2Array(str, Element.class);
        var idMap = new HashMap<Long, String>();
        var nameMap = new HashMap<String, Long>();

        // 广度优先搜索，遍历所有地点的id
        var queue = new LinkedList<>(List.of(elementList));
        while (!queue.isEmpty()) {
            var item = queue.poll();
            idMap.put(item.getId(), item.getName());
            nameMap.put(item.getName(), item.getId());
            if (CollectionUtils.isNotEmpty(item.getChildren())) {
                for (var child : item.getChildren()) {
                    queue.offer(child);
                }
            }
        }

        locationIdMap = Collections.unmodifiableMap(idMap);
        locationNameMap = Collections.unmodifiableMap(nameMap);

        IOUtils.closeIO(inputStream);
    }


    /**
     * 检查位置是否合法
     */
    public static boolean checkLocations(List<Long> locations) {
        if (CollectionUtils.isEmpty(locations)) {
            return true;
        }
        for (var locationId : locations) {
            if (!LocationConstant.locationIdMap.containsKey(locationId)) {
                return false;
            }
        }
        return true;
    }


    public static List<PairLS> converter(List<Long> locations) {
        if (CollectionUtils.isEmpty(locations)) {
            return Collections.EMPTY_LIST;
        }
        return locations
                .stream()
                .map(it -> PairLS.valueOf(it, LocationConstant.locationIdMap.get(it)))
                .collect(Collectors.toList());
    }
}
