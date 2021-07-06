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
import com.zfoo.protocol.util.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Ignore
public class QueryTest {

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

    private void searchDocs(IndexSearcher searcher, Query query) throws IOException {
        // 返回前10条
        TopDocs tds = searcher.search(query, 10);
        for (ScoreDoc sd : tds.scoreDocs) {
            Document doc = searcher.doc(sd.doc);
            System.out.println(StringUtils.MULTIPLE_HYPHENS);
            System.out.println("DocID:" + sd.doc);
            System.out.println("id:" + doc.get("id"));
            System.out.println("title:" + doc.get("title"));
            System.out.println("content:" + doc.get("content"));
            System.out.println("文档评分:" + sd.score);
        }
    }


    @Test
    public void simpleQueryTest() throws IOException, ParseException {
        QueryParser parser = new QueryParser("title", analyzer);
        parser.setDefaultOperator(Operator.AND);

        // 查询语句
        Query query = parser.parse("农村学生");
        System.out.println("Query:" + query.toString());

        searchDocs(searcher, query);

        IOUtils.closeIO(dir, reader);
    }

    // 多域搜索
    @Test
    public void multiQueryTest() throws IOException, ParseException {
        String[] fields = {"title", "content"};

        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer);
        Query multiFieldQuery = parser.parse("日本");
        System.out.println(multiFieldQuery.toString());

        searchDocs(searcher, multiFieldQuery);

        IOUtils.closeIO(dir, reader);
    }

    // 词项搜索
    @Test
    public void termQueryTest() throws IOException {
        Term term = new Term("title", "美国");
        Query termQuery = new TermQuery(term);
        System.out.println("Query:" + termQuery);

        searchDocs(searcher, termQuery);

        IOUtils.closeIO(dir, reader);
    }

    // 布尔搜索
    @Test
    public void boolQueryTest() throws IOException {
        Query query1 = new TermQuery(new Term("title", "美国"));
        Query query2 = new TermQuery(new Term("content", "日本"));
        BooleanClause bc1 = new BooleanClause(query1, BooleanClause.Occur.MUST);
        BooleanClause bc2 = new BooleanClause(query2, BooleanClause.Occur.MUST_NOT);
        BooleanQuery boolQuery = new BooleanQuery.Builder().add(bc1).add(bc2).build();
        System.out.println("Query:" + boolQuery);

        searchDocs(searcher, boolQuery);

        IOUtils.closeIO(dir, reader);
    }

    // 范围搜索
    @Test
    public void rangeQueryTest() throws IOException {
        Query rangeQuery = IntPoint.newRangeQuery("reply", 150, 250);
        System.out.println("Query:" + rangeQuery);

        searchDocs(searcher, rangeQuery);

        IOUtils.closeIO(dir, reader);
    }


    // 前缀搜索
    @Test
    public void prefixQueryTest() throws IOException {
        Term term = new Term("title", "学");
        Query prefixQuery = new PrefixQuery(term);
        System.out.println("Query:" + prefixQuery);

        searchDocs(searcher, prefixQuery);

        IOUtils.closeIO(dir, reader);
    }

    // 多关键字搜索
    @Test
    public void phraseQueryTest() throws IOException {
        // 特|朗|普|宣誓|donald|trump|就任|美国|第|45|任|总统|
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        builder.add(new Term("title", "美国"), 8);
        builder.add(new Term("title", "总统"), 12);
        PhraseQuery phraseQuery = builder.build();
        System.out.println("Query:" + phraseQuery);

        searchDocs(searcher, phraseQuery);

        IOUtils.closeIO(dir, reader);
    }


    // 模糊搜索
    @Test
    public void fuzzyQueryTest() throws IOException {
        Term term = new Term("title", "Trmp");
        FuzzyQuery fuzzyQuery = new FuzzyQuery(term);
        System.out.println("Query:" + fuzzyQuery);

        searchDocs(searcher, fuzzyQuery);

        IOUtils.closeIO(dir, reader);
    }

    // 通配符搜索
    @Test
    public void wildcardQueryTest() throws IOException {
        Term term = new Term("title", "学*");
        Query wildcardQuery = new WildcardQuery(term);
        System.out.println("Query:" + wildcardQuery);

        searchDocs(searcher, wildcardQuery);

        IOUtils.closeIO(dir, reader);
    }

    @Test
    public void highlighterTest() throws IOException, InvalidTokenOffsetsException, ParseException {
        String field = "title";

        QueryParser parser = new QueryParser(field, analyzer);
        Query query = parser.parse("北大");
        System.out.println("Query:" + query);

        // 查询高亮
        QueryScorer score = new QueryScorer(query, field);
        // 定制高亮标签
        SimpleHTMLFormatter fors = new SimpleHTMLFormatter("<span style=\"color:red;\">", "</span>");
        Highlighter highlighter = new Highlighter(fors, score);// 高亮分析器

        // 返回前10条
        TopDocs tds = searcher.search(query, 10);
        for (ScoreDoc sd : tds.scoreDocs) {
            Document doc = searcher.doc(sd.doc);
            System.out.println("id:" + doc.get("id"));
            System.out.println("title:" + doc.get("title"));

            TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), sd.doc, field, analyzer);
//            TokenStream tokenStream = TokenSources.getTokenStream(field, null, doc.get(field), analyzer, -1);
            Fragmenter fragment = new SimpleSpanFragmenter(score);
            highlighter.setTextFragmenter(fragment);
            String str = highlighter.getBestFragment(tokenStream, doc.get(field));// 获取高亮的片段
            System.out.println("高亮的片段:" + str);
        }

        IOUtils.closeIO(dir, reader);
    }
}
