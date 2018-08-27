package com.pan.lucene;

import com.pan.dao.BookDao;
import com.pan.dao.impl.BookDaoImpl;
import com.pan.pojo.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.jupiter.api.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import org.junit.Test;

public class IndexManagerTest {


    @Test
    public void testCreateIndex() throws IOException {
        //1.采集数据
        BookDao bookDao = new BookDaoImpl();
        List<Book> bookList = bookDao.queryBookList();

        //2.创建文档对象
        List<Document> docList = new ArrayList<Document>();
        for (Book book : bookList) {
            Document doc = new Document();
            /**
             * IntField整型类型域，TextField文本类型域，FloatField浮点型类型域
             * 参数1：域名--对应数据库中字段名
             * 参数2：域值
             * 参数3：是否存储--是否需要将该域对应的值存储到文档中
             */

            doc.add(new TextField("bookId", book.getId() + "", Store.YES));
            doc.add(new TextField("bookName", book.getBookname(), Store.YES));
            doc.add(new TextField("bookPrice", book.getPrice() + "", Store.YES));
            doc.add(new TextField("bookPic", book.getPic(), Store.YES));
            doc.add(new TextField("bookDesc", book.getBookdesc(), Store.YES));

            docList.add(doc);
        }
        //3.创建分词器analyzer
        Analyzer analyzer = new IKAnalyzer();

        //4、创建文档索引配置对象IndexWriterConfig
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);

        //5、创建存放索引目录Directory，指定索引存放路径
        File file = new File("D:\\JAVAEE\\test\\workplace\\lucene");
        Directory directory = FSDirectory.open(file);

        //6、创建索引编写器IndexWriter
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

        //7、利用索引编写器写入文档到索引目录
        for (Document document : docList) {
            //把文档对象写入到索引库中
            indexWriter.addDocument(document);
        }

        //8、释放资源
        indexWriter.close();

    }


    //检索索引数据
    @Test
    public void testSearchIndex() throws Exception {
        //1.创建分词器Anakyzer
        Analyzer analyzer = new StandardAnalyzer();
        //2.创建查询对象Query
        //2.1.创建分析器：参数1：默认查询的域，参数2：分词器
        QueryParser queryParser = new QueryParser("bookName",analyzer);
        //2.2.创建Query对象
        Query query = queryParser.parse("java");

        //3.创建存放索引目录Directory,指定存放路径
        Directory directory = FSDirectory.open(new File("D:\\JAVAEE\\test\\workplace\\lucene"));

        //4.创建索引读对象IndexReader
        IndexReader indexReader = DirectoryReader.open(directory);

        //5.创建索引搜索对象IndexSearcher,执行搜索，返回结果

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        /**
         * 参数1：查询对象
         * 参数2：查询前n个文档
         * 返回结果：得分文档（包含文档数组，总的命中数）
         */
        TopDocs topDocs = indexSearcher.search(query,10);

        System.out.println("符合本次产需的总命中文档书为:" + topDocs.totalHits);

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
            System.out.println("--------------------------");
        }
        //7.释放资源
        indexReader.close();
    }

    @Test
    public void testSearchIndex2() throws Exception {
        //1.创建ik分词器
        Analyzer analyzer = new IKAnalyzer();

        //2.创建查询对象Query
        //2.1.创建查询分析器；参数1：默认查询的域，参数2：分词器
        QueryParser queryParser = new QueryParser("bookName", analyzer);
        //2.2.创建Query对象
        Query query = queryParser.parse("java");

        //3、创建存放索引目录Directory，指定索引存放路径
        Directory directory = FSDirectory.open(new File("D:\\JAVAEE\\test\\workplace\\lucene"));

        //4、创建索引读对象IndexReader
        IndexReader indexReader = DirectoryReader.open(directory);

        //5、创建索引搜索对象IndexSearcher，执行搜索，返回结果
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        //页号
        int pageNo = 2;
        //页大小
        int pageSize = 2;
        //文档分页起始索引号
        int start = (pageNo - 1) * pageSize;
        int end = start + pageSize;

        /**
         * 参数1：查询对象
         * 参数2：查询前n个文档
         * 返回结果：得分文档（包含文档数组，总的命中数）
         */
        TopDocs topDocs = indexSearcher.search(query, end);
        System.out.println("符合本次查询的总命中文档数为：" + topDocs.totalHits);

        if (end > topDocs.totalHits) {
            end = topDocs.totalHits;
        }

        //6.处理搜索结果
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (int i = start; i < end; i++) {
            ScoreDoc scoreDoc = scoreDocs[i];
            System.out.println("文档在Lucene中的id为：" + scoreDoc.doc + "；文档分值为：" + scoreDoc.score);

            //根据lucene中文档id查询到文档
            Document document = indexSearcher.doc(scoreDoc.doc);
            System.out.println("文档id为：" + document.get("bookId"));
            System.out.println("名称为：" + document.get("bookName"));
            System.out.println("价格为：" + document.get("bookPrice"));
            System.out.println("图片为：" + document.get("bookPic"));
            System.out.println("描述为：" + document.get("bookDesc"));
            System.out.println("---------------------------------------");

        }
        //7.释放资源
        indexReader.close();

    }
}
