package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = ItemSearchService.class)//为了引用事务才说明类，这里可以不用
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 根据搜索关键字搜索商品列表
     *
     * @param searchMap 搜索条件
     * @return 搜索结果
     */
    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        //创建查询对象
        //SimpleQuery query = new SimpleQuery();
        //创建高亮查询对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();

        //设置查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //设置高亮域
        HighlightOptions highlightOptions = new HighlightOptions();
        //高亮域名称
        highlightOptions.addField("item_title");

        //设置高亮的起始标签
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //设置高亮的结束标签
        highlightOptions.setSimplePostfix("</em>");

        query.setHighlightOptions(highlightOptions);


        //查询
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //获取高亮标题
        List<HighlightEntry<TbItem>> highlighted = highlightPage.getHighlighted();

        //对每个商品的标题获取高亮标题并回填

        for (HighlightEntry<TbItem> entry : highlighted) {
            if (entry.getHighlights() != null && entry.getHighlights().get(0).getSnipplets() != null) {
                entry.getEntity().setTitle(entry.getHighlights().get(0).getSnipplets().get(0));
            }
        }

        //设置返回的商品列表
        resultMap.put("rows", highlightPage.getContent());
        return resultMap;
    }
}
