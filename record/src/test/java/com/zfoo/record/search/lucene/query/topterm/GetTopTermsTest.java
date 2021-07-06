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

package com.zfoo.record.search.lucene.query.topterm;

import com.zfoo.protocol.util.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

@Ignore
public class GetTopTermsTest {

    private static final Path indexPath = Paths.get(FileUtils.getProAbsPath() + File.separator + "lucene-data");
    private static final Analyzer analyzer = new SmartChineseAnalyzer();

    private static Directory dir = null;
    private static IndexReader reader = null;
    private static IndexSearcher searcher = null;

    static {
        try {
            dir = FSDirectory.open(indexPath);
            reader = DirectoryReader.open(dir);
            searcher = new IndexSearcher(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() throws IOException {
        // 因为只索引了一个文档，所以DocID为0，通过getTermVector获取content字段的词项
        Terms terms = reader.getTermVector(0, "content");

        // 遍历词项
        TermsEnum termIterator = terms.iterator();
        BytesRef thisTerm;
        Map<String, Integer> map = new HashMap<>();
        while ((thisTerm = termIterator.next()) != null) {
            // 词项
            String termText = thisTerm.utf8ToString();
            // 频率
            int freq = (int) termIterator.totalTermFreq();
            map.put(termText, freq);
        }

        // 按value排序
        List<Entry<String, Integer>> sortedList = new ArrayList<>(map.entrySet());
        Collections.sort(sortedList, (o1, o2) -> (o2.getValue() - o1.getValue()));

        for (int i = 0; i < sortedList.size(); i++) {
            System.out.println(sortedList.get(i).getKey() + ":" + sortedList.get(i).getValue());
        }
    }


}
