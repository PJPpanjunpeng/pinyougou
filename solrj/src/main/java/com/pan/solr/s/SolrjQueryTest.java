package com.pan.solr.s;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

import java.util.List;
import java.util.Map;


/*
 * 注意：
 *     1）查询的时候，要加查询区域，不加默认为test区域。
 *     2）设置高亮时，还要处理高亮让其显示
 *     3)设置分页时，还要处理分页的结果让其显示
 * */

public class SolrjQueryTest {
    @Test
    public void test() throws Exception {
        //1、建立HttpSolrService服务对象
        HttpSolrServer httpSolrServer = new HttpSolrServer("http://127.0.0.1:8080/solr");
        //创建SolrQuery索引查询对象
        SolrQuery solrQuery = new SolrQuery();
        //设置搜素条件
        solrQuery.set("q", "花儿");
        //设置默认查询的区域
        solrQuery.set("df", "product_name");
        //设置过滤的条件
        solrQuery.setFilterQueries("product_price:[* TO 20]");
        //设置排序
        solrQuery.setSort("product_price", SolrQuery.ORDER.asc);
        //设置返回要显示的数据
        solrQuery.setFields("id,product_name,product_price,product_catalog_name");
        //设置分页
        solrQuery.setStart(0);
        solrQuery.setRows(10);
        //设置响应的格式
        solrQuery.set("wt", "json");
        //设置高亮
        solrQuery.setHighlight(true);
        //设置要显示的高亮
        solrQuery.addHighlightField("product_name");
        //设置高亮的样式
        solrQuery.setHighlightSimplePre("<font style='color:red'>");
        solrQuery.setHighlightSimplePost("</font>");
        //分组
        solrQuery.setFacet(true);
        //设置要查询的名称（分片查询表达式）
        solrQuery.addFacetQuery("product_name:花儿");
        //设置按什么分组（分片的域）
        solrQuery.addFacetField("product_catalog_name");
        //2、使用HttpSolrService对象，执行搜素，返回查询响应对象（QueryResponse）
        QueryResponse queryResponse = httpSolrServer.query(solrQuery);
        //3、使用QueryResponse对象，获取查询的结果集数据
        System.out.println("本次查询的总数量为：" + queryResponse.getResults().getNumFound());
        //处理分页的结果
        List<FacetField> facetFields = queryResponse.getFacetFields();
        for (FacetField facetField : facetFields) {
            System.out.println("分片域名为："+facetField.getName()+";总分片数为："+facetField.getValueCount());
            List<FacetField.Count> values = facetField.getValues();
            for (FacetField.Count count : values) {
                System.out.println("分片-分类名称为："+count.getName()+"分类对象的统计数为："+count.getCount());
            }
        }

        System.out.println("------------------------");
        //处理高亮并显示
        Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
        SolrDocumentList results = queryResponse.getResults();
        //4、处理数据
        for (SolrDocument solrDocument : results) {
            System.out.println("id为" + solrDocument.get("id"));
            System.out.println("product_name为：" + solrDocument.get("product_name"));
            System.out.println("product_price为：" + solrDocument.get("product_price"));
            System.out.println("product_catalog_name为：" + solrDocument.get("product_catalog_name"));
            //获取高亮标题
            System.out.println("高亮的product_name为："+highlighting.get(solrDocument.get("id").toString()).get("product_name").get(0));
            System.out.println("-----------------------------------------------");
        }
    }
}
