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

package com.zfoo.record.search.lucene.analyzer;

import com.zfoo.protocol.util.ClassUtils;
import com.zfoo.protocol.util.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;

/**
 * @author jaysunxiao
 * @version 1.0
 * @since 2019-06-25 11:03
 */
@Ignore
public class LanguageAnalyzerTest {

    private static String chStr = "中华人民共和国简称中国, 是一个有14亿人口的国家";
    private static String enStr = "Dogs can not achieve a place,eyes can reach; ";

    private void printAnalyzer(Analyzer analyzer, String str) throws IOException {
        StringReader reader = new StringReader(str);
        TokenStream toStream = analyzer.tokenStream(str, reader);
        toStream.reset();// 清空流
        CharTermAttribute teAttribute = toStream.getAttribute(CharTermAttribute.class);
        System.out.println("分词结果:");
        while (toStream.incrementToken()) {
            System.out.print(teAttribute.toString() + "|");
        }
        System.out.println(FileUtils.LS);
        analyzer.close();
    }


    // 标准分词对英文很友好
    @Test
    public void standardTest() throws IOException {
        System.out.println("StandardAnalyzer对中文分词:");
        printAnalyzer(new StandardAnalyzer(), chStr);
        System.out.println("StandardAnalyzer对英文分词:");
        printAnalyzer(new StandardAnalyzer(), enStr);
    }


    @Test
    public void variousAnalyzerTest() throws IOException {
        // 标准分词
        Analyzer analyzer = new StandardAnalyzer();
        System.out.println("标准分词:" + analyzer.getClass());
        printAnalyzer(analyzer, chStr);


        // 空格分词
        analyzer = new WhitespaceAnalyzer();
        System.out.println("空格分词:" + analyzer.getClass());
        printAnalyzer(analyzer, chStr);


        // 简单分词
        analyzer = new SimpleAnalyzer();
        System.out.println("简单分词:" + analyzer.getClass());
        printAnalyzer(analyzer, chStr);


        // 二分法分词
        analyzer = new CJKAnalyzer();
        System.out.println("二分法分词:" + analyzer.getClass());
        printAnalyzer(analyzer, chStr);


        // 关键字分词
        analyzer = new KeywordAnalyzer();
        System.out.println("关键字分词:" + analyzer.getClass());
        printAnalyzer(analyzer, chStr);


        // 停用词分词
        Reader reader = new BufferedReader(new InputStreamReader(ClassUtils.getFileFromClassPath("search/analyzer/stopword.dic")));
        analyzer = new StopAnalyzer(reader);
        System.out.println("停用词分词:" + analyzer.getClass());
        printAnalyzer(analyzer, chStr);


        // 中文智能分词
        analyzer = new SmartChineseAnalyzer();
        System.out.println("中文智能分词:" + analyzer.getClass());
        printAnalyzer(analyzer, chStr);

        // IK分词器只能和elastic search配合使用
//        analyzer = new IKAnalyzer();
//        System.out.println("aaaaa:" + analyzer.getClass());
//        printAnalyzer(analyzer, chStr);
    }

}
