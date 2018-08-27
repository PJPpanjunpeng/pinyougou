package com.pan.lucene;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.jupiter.api.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

public class IndexSearchTest {

    //检索索引数据
    private void search(Query query) throws IOException {
        //打印查询语法
        System.out.println("查询的Query语法为：" + query);

        //1、创建分词器Analyzer
        //Analyzer analyzer = new StandardAnalyzer();
        //使用ik分词器
        Analyzer analyzer = new IKAnalyzer();

        //2、创建查询对象Query

        //3.创建存放索引目录Direcory,指定索引存放路径
        File file = new File("D:\\JAVAEE\\test\\workplace\\lucene");
        Directory directory = FSDirectory.open(file);
        //4、创建索引读对象IndexReader
        IndexReader indexReader = DirectoryReader.open(directory);
        //5、创建索引搜索对象IndexSearcher，执行搜索，返回结果
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        /**
         * 参数1：查询对象
         * 参数2：查询前n个文档
         * 返回结果：得分文档（包含文档数组，总的命中数）
         */
        TopDocs topDocs = indexSearcher.search(query,10);
        System.out.println("符合本次查询的总命中文档数为：" + topDocs.totalHits);

        //6.处理搜索结果
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc: scoreDocs) {
            System.out.println("文档在Lucene中的id为：" + scoreDoc.doc + "；文档分值为：" + scoreDoc.score);
            //根据lucene中的文档id查询到文档
            Document document = indexSearcher.doc(scoreDoc.doc);

            System.out.println("文档id为：" + document.get("bookId"));
            System.out.println("名称为：" + document.get("bookName"));
            System.out.println("价格为：" + document.get("bookPrice"));
            System.out.println("图片为：" + document.get("bookPic"));
            System.out.println("描述为：" + document.get("bookDesc"));
            System.out.println("---------------------------------------");
        }
        //7、释放资源
        indexReader.close();
    }

    /**
     * 使用TermQuery
     * 需求：查询图书名称域中包含有java的图书。
     */
    @Test
    public void termQuery() throws Exception {
        TermQuery termQuery = new TermQuery(new Term("bookName", "java"));
        search(termQuery);
    }

    /**
     * 使用NumericRangeQuery
     *	需求：查询图书价格在80到100之间的图书。不包含边界值80和100
     */
    @Test
    public void numericRangeQuery() throws IOException {
        /**
         * 参数1：域名
         * 参数2：数值范围的下限
         * 参数3：数值范围的下限
         * 参数4：是否包含数值范围的下限（端点）
         * 参数5：是否包含数值范围的上限（端点）
         */
        NumericRangeQuery<Double> query = NumericRangeQuery.newDoubleRange("bookPrice", 80d, 100d, false, false);

        search(query);
    }

    /**
     * 使用BooleanQuery
     * 需求：查询图书名称中包含有lucene，并且图书价格在80到100之间的图书。包含边界值
     */
    @Test
    public void booleanQuery() throws IOException {
        BooleanQuery query = new BooleanQuery();
        /**
         * 参数1：域名
         * 参数2：数值范围的下限
         * 参数3：数值范围的下限
         * 参数4：是否包含数值范围的下限（端点）
         * 参数5：是否包含数值范围的上限（端点）
         */
        NumericRangeQuery<Double> query1 = NumericRangeQuery.newDoubleRange("bookPrice", 80d, 100d, true, true);
        query.add(query1, BooleanClause.Occur.MUST);

        TermQuery query2 = new TermQuery(new Term("bookName", "lucene"));
        query.add(query2, BooleanClause.Occur.MUST);

        search(query);
    }

    /**
     * 使用QueryParser
     * 需求：查询图书名称域中，包含有java，并且包含有lucene的图书。
     */
    @Test
    public void queryParser() throws Exception {
        //1.创建分词器
        Analyzer analyzer = new IKAnalyzer();
        //2.创建queryParser对象
        QueryParser queryParser = new QueryParser("bookName",analyzer);
        //3.设置查询条件
        Query query = queryParser.parse("bookName:java AND bookName:lucene");

        search(query);
    }


    //设置权重
    @Test
    public void updateIndexBoost() throws IOException {
        //1.创建分词器analyzer
        Analyzer analyzer = new IKAnalyzer();
        //2.创建文档索引配置对象IndexWriterConfig
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        //3.创建文档索引目录对象
        File file = new File("D:\\JAVAEE\\test\\workplace\\lucene");
        FSDirectory directory = FSDirectory.open(file);
        //4.创建索引编写器IndexWriter
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

        //5.创建文档Document
        Document doc = new Document();
        doc.add(new StringField("bookId","5", Field.Store.YES));
        TextField textField = new TextField("bookeName", "Lucene Java精华版", Field.Store.YES);
        //设置权重，默认为1
        textField.setBoost(2);
        doc.add(textField);

        doc.add(new DoubleField("bookPrice", 80d, Field.Store.YES));
        doc.add(new StoredField("bookPic", "5.jpg"));
        doc.add(new TextField("bookDesc", "test", Field.Store.NO));

        //6.创建条件对象Term
        Term term = new Term("bookId", "5");
        //7.根据词条更新；如果存在则更新，不存在则新增
        indexWriter.updateDocument(term, doc);
        //8.释放资源
        indexWriter.close();
    }





    //检索索引数据
    private void searchHighLighter(Query query) throws Exception {
        //打印查询语法
        System.out.println("查询的Query语法为：" + query);

        //1、创建分词器Analyzer
        //Analyzer analyzer = new StandardAnalyzer();
        //使用ik分词器
        Analyzer analyzer = new IKAnalyzer();

        //2、创建查询对象Query

        //3、创建存放索引目录Directory，指定索引存放路径
        Directory directory = FSDirectory.open(new File("D:\\JAVAEE\\test\\workplace\\lucene"));
        //4、创建索引读对象IndexReader
        IndexReader indexReader = DirectoryReader.open(directory);
        //5、创建索引搜索对象IndexSearcher，执行搜索，返回结果
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        /**
         * 参数1：查询对象
         * 参数2：查询前n个文档
         * 返回结果：得分文档（包含文档数组，总的命中数）
         */
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("符合本次查询的总命中文档数为：" + topDocs.totalHits);

        //创建查询分值对象QueryScore
        QueryScorer queryScorer = new QueryScorer(query);

        //创建高亮分片对象
        Fragmenter fragmenter = new SimpleSpanFragmenter(queryScorer);

        //创建高亮处理对象HighLighter
        //Highlighter highlighter = new Highlighter(queryScorer);
        //highlighter.setTextFragmenter(fragmenter);

        //创建自定义的Html高亮显示标签
        //参数1：高亮的起始标签
        //参数2：高亮的结束标签
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font style='color:red'>", "</font>");

        //创建高亮处理对象HighLighter
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, queryScorer);




        //6、处理搜索结果
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("文档在Lucene中的id为：" + scoreDoc.doc + "；文档分值为：" + scoreDoc.score);
            //根据lucene中的文档id查询到文档
            Document document = indexSearcher.doc(scoreDoc.doc);

            System.out.println("文档id为：" + document.get("bookId"));

            String bookName = document.get("bookName");
            System.out.println("原名称为：" + bookName);

            /**
             * 使用TokenSources将文档对象转换为流对象TokenStream
             * 参数1：文档
             * 参数2：要高亮的域名
             * 参数3：分词器
             */
            TokenStream tokenStream = TokenSources.getTokenStream(document, "bookName", analyzer);

            bookName = highlighter.getBestFragment(tokenStream, bookName);

            System.out.println("高亮之后的名称为：" + bookName);

            System.out.println("价格为：" + document.get("bookPrice"));
            System.out.println("图片为：" + document.get("bookPic"));
            System.out.println("描述为：" + document.get("bookDesc"));
            System.out.println("---------------------------------------");
        }
        //7、释放资源
        indexReader.close();
    }

    /**
     * 测试高亮显示搜索关键字
     *
     */
    @Test
    public void highLightQuery() throws Exception {
        TermQuery termQuery = new TermQuery(new Term("bookName", "java"));

        searchHighLighter(termQuery);
    }
}
