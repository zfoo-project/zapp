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

import com.zfoo.app.zapp.common.entity.time.model.VideoLinkPO;
import com.zfoo.protocol.IPacket;
import org.springframework.lang.Nullable;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-02-18 12:06
 */
public class VideoLinkVO implements IPacket {

    public static final transient short PROTOCOL_ID = 134;

    private String poster;
    private String url;

    @Nullable
    public static VideoLinkVO valueOf(VideoLinkPO po) {
        if (po == null) {
            return null;
        }
        var vo = new VideoLinkVO();
        vo.poster = po.getPoster();
        vo.url = po.getUrl();
        return vo;
    }

    public VideoLinkPO toVideoLinkPO() {
        return VideoLinkPO.valueOf(this.poster, this.url);
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
