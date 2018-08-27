package com.pan.jd.service.impl;

import com.pan.jd.pojo.Product;
import com.pan.jd.pojo.Result;
import com.pan.jd.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service("searchService")
public class SeachServiceImpl implements SearchService {

    @Autowired
    private HttpSolrServer httpSolrServer;

    /**
     * 到solr中根据搜索关键字和其它信息进行搜索数据
     *
     * @param queryString  搜索关键字
     * @param catalog_name 过滤条件：分类
     * @param price        过滤条件：价格，格式为：0-9
     * @param page         页号
     * @param sort         排序，1为升序，0为降序
     * @return 搜索结果
     */
    public Result search(String queryString, String catalog_name, String price, Integer page, String sort) throws Exception {


        Result result = new Result();
        //创建查询对象
        SolrQuery solrQuery = new SolrQuery();

        //如果搜索关键字为空则查询全部
        if (StringUtils.isNotBlank(queryString)) {
            solrQuery.setQuery(queryString);
        } else {
            solrQuery.setQuery("*:*");
        }
//设置默认的搜索域名
        solrQuery.set("df", "product_keywords");

        //根据分类进行过滤
        if(StringUtils.isNotBlank(catalog_name)) {
            catalog_name = "product_catalog_name:" + catalog_name;
        }
        //根据价格进行过滤
        if(StringUtils.isNotBlank(price)) {
            String[] prices = price.split("-");

            price = "product_price:[" + prices[0] + " TO " + prices[1] + "]";
        }

        solrQuery.setFilterQueries(catalog_name, price);

        //设置分页
        if(page == null || page==0) {
            page = 1;
        }
        //默认页大小为10
        int pageSize = 10;
        solrQuery.setStart((page-1)*pageSize);
        solrQuery.setRows(pageSize);

        //设置排序
        if("1".equals(sort)) {
            solrQuery.setSort("product_price", SolrQuery.ORDER.asc);
        } else {
            solrQuery.setSort("product_price", SolrQuery.ORDER.desc);
        }

        //设置高亮
        solrQuery.setHighlight(true);
        solrQuery.addHighlightField("product_name");//高亮域名
        solrQuery.setHighlightSimplePre("<font style='color:red'>");//高亮起始标签
        solrQuery.setHighlightSimplePost("</font>");//高亮结束标签

        //查询
        QueryResponse queryResponse = httpSolrServer.query(solrQuery);

        //获取搜索结果
        SolrDocumentList solrDocumentList = queryResponse.getResults();

        //当前页号
        result.setCurPage(page);

        //总记录数
        long total = solrDocumentList.getNumFound();
        result.setRecordCount(total);

        //计算总页数:总记录数对页大小求余数，整除则为总记录数整除页大小，不能整除则加1
        Integer pageCount = (int) (total/pageSize);
        if(total%pageSize != 0) {
            pageCount++;
        }
        result.setPageCount(pageCount);

        //获取高亮信息
        Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();

        //处理商品结果记录
        List<Product> productList = new ArrayList<Product>();
        Product product = null;
        for (SolrDocument solrDocument : solrDocumentList) {
            product = new Product();
            //商品id
            String id = solrDocument.get("id").toString();
            product.setPid(id);

            //商品名称
            String productName = solrDocument.get("product_name").toString();
            //处理高亮标题
            List<String> list = highlighting.get(id).get("product_name");
            if(list != null && list.size() > 0) {
                productName = list.get(0);
            }
            product.setName(productName);

            product.setPicture(solrDocument.get("product_picture").toString());
            product.setPrice(solrDocument.get("product_price").toString());

            productList.add(product);
        }
        result.setProductList(productList);

        return result;
    }

}
