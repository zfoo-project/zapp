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

package com.zfoo.app.zapp.common.protocol.cache.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.zfoo.net.packet.common.PairLS;
import com.zfoo.protocol.IPacket;

import java.util.List;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2020-08-13 16:04
 */
public class CategoryVO implements IPacket {

    public static final transient short PROTOCOL_ID = 3002;

    @JsonSerialize(using = ToStringSerializer.class)
    private long id;

    private String name;

    private String background;

    private List<PairLS> parent;

    private List<PairLS> children;

    private List<PairLS> words;

    public static CategoryVO valueOf(long id, String name) {
        var vo = new CategoryVO();
        vo.id = id;
        vo.name = name;
        return vo;
    }

    public static CategoryVO valueOf(long id, String name, String background, List<PairLS> parent, List<PairLS> children, List<PairLS> words) {
        var vo = new CategoryVO();
        vo.id = id;
        vo.name = name;
        vo.background = background;
        vo.parent = parent;
        vo.children = children;
        vo.words = words;
        return vo;
    }

    @Override
    public short protocolId() {
        return PROTOCOL_ID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public List<PairLS> getParent() {
        return parent;
    }

    public void setParent(List<PairLS> parent) {
        this.parent = parent;
    }

    public List<PairLS> getChildren() {
        return children;
    }

    public void setChildren(List<PairLS> children) {
        this.children = children;
    }

    public List<PairLS> getWords() {
        return words;
    }

    public void setWords(List<PairLS> words) {
        this.words = words;
    }
}
