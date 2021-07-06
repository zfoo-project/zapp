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

package com.zfoo.record.search.lucene.query.index;

import com.zfoo.protocol.util.FileUtils;
import com.zfoo.protocol.util.IOUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Ignore
public class IndexTest {

    private static final News news1 = new News(1, "安倍晋三本周会晤特朗普 将强调日本对美国益处",
            "日本首相安倍晋三计划2月10日在华盛顿与美国总统特朗普举行会晤时提出加大日本在美国投资的设想", 100);
    private static final News news2 = new News(2, "北大迎4380名新生 农村学生700多人近年最多",
            "昨天，北京大学迎来4380名来自全国各地及数十个国家的本科新生。其中，农村学生共700余名，为近年最多...", 200);
    private static final News news3 = new News(3, "特朗普宣誓(Donald Trump)就任美国第45任总统",
            "当地时间1月20日，唐纳德·特朗普在美国国会宣誓就职，正式成为美国第45任总统。", 300);

    private Document newsToDocument(News news, FieldType idFieldType, FieldType titleFieldType, FieldType contentFieldType) {
        Document doc = new Document();
        doc.add(new Field("id", String.valueOf(news.getId()), idFieldType));
        doc.add(new Field("title", news.getTitle(), titleFieldType));
        doc.add(new Field("content", news.getContent(), contentFieldType));
        doc.add(new IntPoint("reply", news.getReply()));
        doc.add(new StoredField("reply_display", news.getReply()));
        return doc;
    }

    @Test
    public void createIndexTest() throws IOException {
        // 开始时间
        Date start = new Date();
        System.out.println("**********开始创建索引**********");

        // 索引目录
        Path indexPath = Paths.get(FileUtils.getProAbsPath() + File.separator + "lucene-data");
        Directory dir = FSDirectory.open(indexPath);

        // 索引配置
        Analyzer analyzer = new SmartChineseAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(OpenMode.CREATE);

        // 写索引的工具
        IndexWriter inWriter = new IndexWriter(dir, iwc);

        // 写入字段配置
        FieldType idType = new FieldType();
        idType.setIndexOptions(IndexOptions.DOCS);
        idType.setStored(true);

        FieldType titleType = new FieldType();
        titleType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        titleType.setStored(true);
        titleType.setTokenized(true);

        FieldType contentType = new FieldType();
        contentType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        contentType.setStored(true); // 默认false，设置true为存储字段
        contentType.setTokenized(true); // 使用分词器对字段进行词条化
        contentType.setStoreTermVectors(true); // 保存词向量
        contentType.setStoreTermVectorPositions(true); // 保存词向量中的位移信息
        contentType.setStoreTermVectorOffsets(true); // 保存词向量中的偏移信息

        inWriter.addDocument(newsToDocument(news1, idType, titleType, contentType));
        inWriter.addDocument(newsToDocument(news2, idType, titleType, contentType));
        inWriter.addDocument(newsToDocument(news3, idType, titleType, contentType));

        inWriter.commit();

        IOUtils.closeIO(inWriter, dir);

        Date end = new Date();
        System.out.println("索引文档用时:" + (end.getTime() - start.getTime()) + " milliseconds");
        System.out.println("**********索引创建完成**********");
    }


    @Test
    public void deleteIndexTest() throws IOException {
        Path indexPath = Paths.get(FileUtils.getProAbsPath() + File.separator + "lucene-data");
        Directory directory = FSDirectory.open(indexPath);

        Analyzer analyzer = new SmartChineseAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

        IndexWriter indexWriter = new IndexWriter(directory, iwc);

        // 删除title中含有“美国”的文档
        indexWriter.deleteDocuments(new Term("title", "美国"));

        indexWriter.commit();
        indexWriter.close();
        System.out.println("删除完成!");
    }


    @Test
    public void updateTest() throws IOException {
        Path indexPath = Paths.get(FileUtils.getProAbsPath() + File.separator + "lucene-data");
        Directory directory = FSDirectory.open(indexPath);

        Analyzer analyzer = new SmartChineseAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

        IndexWriter indexWriter = new IndexWriter(directory, iwc);

        // 写入字段配置，跟新的IndexOptions必须和以前的一样
        FieldType idType = new FieldType();
        idType.setIndexOptions(IndexOptions.DOCS);
        idType.setStored(true);

        FieldType titleType = new FieldType();
        titleType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        titleType.setStored(true);
        titleType.setTokenized(true);

        FieldType contentType = new FieldType();
        contentType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        contentType.setStored(true);
        contentType.setTokenized(true);
        contentType.setStoreTermVectors(true);
        contentType.setStoreTermVectorPositions(true);
        contentType.setStoreTermVectorOffsets(true);

        indexWriter.updateDocument(new Term("title", "北大"), newsToDocument(news1, idType, titleType, contentType));

        indexWriter.commit();
        indexWriter.close();

        System.out.println("更新完成!");
    }

}
