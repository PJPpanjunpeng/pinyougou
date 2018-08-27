package com.pan.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class solrjTest {

    private HttpSolrServer httpSolrServer;

    @Before
    public void test() {
        //solr服务器地址
        String baseURL = "http://127.0.0.1:8080/solr";
        httpSolrServer = new HttpSolrServer(baseURL);
    }

    //新增或更新solr中数据
    @Test
    public void testAddOrUpdate() throws IOException, SolrServerException {
        //1.创建文档
        SolrInputDocument solrInputDocument = new SolrInputDocument();

        solrInputDocument.addField("id",123);
        solrInputDocument.setField("name","你好！solr and lucene");
        //2.添加文档
        httpSolrServer.add(solrInputDocument);
        //3.提交
        httpSolrServer.commit();
    }

    //根据id删除索引
    @Test
    public void testDeleteById() throws Exception {
        //1.删除
        httpSolrServer.deleteById("123");
        //2.提交
        httpSolrServer.commit();
    }

    //根据条件删除索引
    @Test
    public void testDeleteByWhere() throws Exception {
        //1.条件删除
        httpSolrServer.deleteByQuery("name:solr");
        //2.提交
        httpSolrServer.commit();
    }

    //条件查询索引
    @Test
    public void testQuery() throws Exception {
        //1.创建solrQuery对象
        SolrQuery solrQuery = new SolrQuery("*:*");

        //2.使用httpSolrServer查询
        QueryResponse queryResponse = httpSolrServer.query(solrQuery);

        //3.查询并获取结果
        Long total = queryResponse.getResults().getNumFound();
        System.out.println("本次查询数据总数为：" + total);

        //4、处理查询结果
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        for (SolrDocument solrDocument : solrDocumentList) {
            System.out.println("id为：" + solrDocument.get("id"));
            System.out.println("name为：" + solrDocument.get("name"));
        }
    }

    //条件查询不同的solrCore内容
    @Test
    public void testQueryBySolrCore() throws Exception {
        //重新创建HttpSolrServer，指定要查询的solrCore名称

        httpSolrServer = new HttpSolrServer("http://127.0.0.1:8080/solr/collection2");

        //1、创建solrQuery对象
        SolrQuery solrQuery = new SolrQuery("*:*");

        //2、使用httpSolrServer查询
        QueryResponse queryResponse = httpSolrServer.query(solrQuery);

        //3、查询并获取查询结果
        long total = queryResponse.getResults().getNumFound();
        System.out.println("本次查询数据总数为：" + total);

        //4、处理查询结果
        SolrDocumentList solrDocumentList = queryResponse.getResults();
        for (SolrDocument solrDocument : solrDocumentList) {
            System.out.println("id为：" + solrDocument.get("id"));
            System.out.println("name为：" + solrDocument.get("name"));
        }
    }

}

