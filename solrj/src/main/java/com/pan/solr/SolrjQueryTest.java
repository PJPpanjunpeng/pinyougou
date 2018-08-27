package com.pan.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class SolrjQueryTest {

    //条件查询
    @Test
    public void test() throws Exception {
        //创建HttpSolrServer
        HttpSolrServer httpSolrServer = new HttpSolrServer("http://127.0.0.1:8080/solr/");

        //创建查询对象
        SolrQuery solrQuery = new SolrQuery();

        //设置查询关键字
        solrQuery.setQuery("花儿");
        //solrQuery.set("q", "花儿");
        //设置默认查询的区域
        //solrQuery.set("df", "product_name");
        //设置过滤条件
        solrQuery.setFilterQueries("product_price:[* TO 20]");

        //设置排序
        solrQuery.setSort("product_price", SolrQuery.ORDER.asc);

        //设置分页
        solrQuery.setStart(0);
        solrQuery.setRows(10);

        //设置要显示的域
        solrQuery.setFields("id,product_name,product_price,product_catalog_name");

        //设置默认要搜索的域
        solrQuery.set("df", "product_name");

        //设置响应格式
        solrQuery.set("wt", "json");

        //设置高亮
        solrQuery.setHighlight(true);//开启高亮显示关键字
        solrQuery.addHighlightField("product_name");//高亮域名
        solrQuery.setHighlightSimplePre("<font style='color:red'>");//高亮起始标签
        solrQuery.setHighlightSimplePost("</font>");//高亮结束标签

        //设置分片（分组）
        /*solrQuery.setFacet(true);
        solrQuery.addFacetQuery("product_name:花儿");//分片的查询表达式
        solrQuery.addFacetField("product_catalog_name");//分片的域
        */

        //查询
        QueryResponse queryResponse = httpSolrServer.query(solrQuery);

        System.out.println("本次查询的总数量为：" + queryResponse.getResults().getNumFound());

        //处理分片结果
        /*List<FacetField> facetFields = queryResponse.getFacetFields();
        for (FacetField facetField : facetFields) {
            System.out.println("分片域名为：" + facetField.getName() + "；总分片数为：" + facetField.getValueCount());
            List<FacetField.Count> values = facetField.getValues();
            for (FacetField.Count count : values) {
                System.out.println("分片-分类名称为：" + count.getName() + "；分类对应的统计数为：" + count.getCount());
            }
        }*/
        System.out.println("----------良好的分割线-----------");

        //处理高亮并显示
        Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();

        //一般的查询数据
        SolrDocumentList results = queryResponse.getResults();
        for (SolrDocument solrDocument : results) {
            System.out.println("id为：" + solrDocument.get("id"));
            System.out.println("product_name为：" + solrDocument.get("product_name"));

            //获取高亮标题

            System.out.println("高亮的product_name为：" + highlighting.get(solrDocument.get("id").toString()).get("product_name").get(0));

            System.out.println("product_price为：" + solrDocument.get("product_price"));
            System.out.println("product_catalog_name为：" + solrDocument.get("product_catalog_name"));

            System.out.println("---------------------");
        }
    }
}


//课件代码
/*
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class SolrjQueryTest {

    // 条件查询
    @Test
    public void test() throws Exception {
        //创建HttpSolrServer
        HttpSolrServer httpSolrServer = new HttpSolrServer("http://localhost:8080/solr");

        //创建查询对象
        SolrQuery solrQuery = new SolrQuery();

        //设置查询关键字
        solrQuery.setQuery("花儿");

        //设置过滤条件
        solrQuery.setFilterQueries("product_price:[* TO 20]");

        //设置排序
        solrQuery.setSort("product_price", ORDER.asc);

        //设置分页
        solrQuery.setStart(0);
        solrQuery.setRows(10);

        //设置要显示的域
        solrQuery.setFields("id,product_name,product_price,product_catalog_name");

        //设置默认要搜索的域
        solrQuery.set("df", "product_name");

        //设置响应格式
        solrQuery.set("wt", "json");

        //设置高亮
        solrQuery.setHighlight(true);//开启高亮显示关键字
        solrQuery.addHighlightField("product_name");//高亮域名
        solrQuery.setHighlightSimplePre("<font style='color:red'>");//高亮起始标签
        solrQuery.setHighlightSimplePost("</font>");//高亮结束标签

        //设置分片（分组）
        solrQuery.setFacet(true);
        solrQuery.addFacetQuery("product_name:花儿");//分片的查询表达式
        solrQuery.addFacetField("product_catalog_name");//分片的域

        //查询
        QueryResponse queryResponse = httpSolrServer.query(solrQuery);

        System.out.println("本次查询的总数量为：" + queryResponse.getResults().getNumFound());

        //处理分片结果
        List<FacetField> facetFields = queryResponse.getFacetFields();
        for (FacetField facetField : facetFields) {
            System.out.println("分片域名为：" + facetField.getName() + "；总分片数为：" + facetField.getValueCount());
            List<Count> values = facetField.getValues();
            for (Count count : values) {
                System.out.println("分片-分类名称为：" + count.getName() + "；分类对应的统计数为：" + count.getCount());
            }
        }
        System.out.println("---------------------------------------------------");

        //处理高亮并显示
        Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();

        //一般的查询数据
        SolrDocumentList results = queryResponse.getResults();
        for (SolrDocument solrDocument : results) {
            System.out.println("id为：" + solrDocument.get("id"));
            System.out.println("product_name为：" + solrDocument.get("product_name"));
            //获取高亮标题
            */
/**
             * "highlighting": {
             "1": {
             "product_name": [
             "<font style='color:red'>花儿</font>朵朵彩色金属门后挂 8钩免钉门背挂钩2066"
             ]
             }
             *//*

            System.out.println("高亮的product_name为：" + highlighting.get(solrDocument.get("id").toString()).get("product_name").get(0));

            System.out.println("product_price为：" + solrDocument.get("product_price"));
            System.out.println("product_catalog_name为：" + solrDocument.get("product_catalog_name"));
            System.out.println("-----------------------------------------------");
        }

    }
}
*/

