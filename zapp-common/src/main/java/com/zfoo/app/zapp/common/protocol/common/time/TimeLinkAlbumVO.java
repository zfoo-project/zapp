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

package com.zfoo.app.zapp.common.protocol.common.time;

import com.zfoo.app.zapp.common.entity.time.model.TimeLinkAlbumPO;
import com.zfoo.protocol.IPacket;
import com.zfoo.protocol.collection.CollectionUtils;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-25 13:12
 */
public class TimeLinkAlbumVO implements IPacket {

    public static final transient short PROTOCOL_ID = 133;

    private String album;
    private List<Long> links;

    public static TimeLinkAlbumVO valueOf(TimeLinkAlbumPO po) {
        var vo = new TimeLinkAlbumVO();
        vo.album = po.getAlbum();
        vo.links = po.getLinks();
        return vo;
    }

    @Nullable
    public static List<TimeLinkAlbumVO> posToVos(List<TimeLinkAlbumPO> pos) {
        if (CollectionUtils.isEmpty(pos)) {
            return null;
        }
        return pos.stream().map(it -> valueOf(it)).collect(Collectors.toList());
    }

    @Nullable
    public static List<TimeLinkAlbumPO> vosToPos(@Nullable List<TimeLinkAlbumVO> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return null;
        }
        return vos.stream().map(it -> TimeLinkAlbumPO.valueOf(it.getAlbum(), it.getLinks())).collect(Collectors.toList());
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public List<Long> getLinks() {
        return links;
    }

    public void setLinks(List<Long> links) {
        this.links = links;
    }

}
