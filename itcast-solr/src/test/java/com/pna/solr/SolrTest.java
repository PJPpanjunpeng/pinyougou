package com.pna.solr;

import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sun.management.snmp.jvmmib.JvmThreadInstanceTableMeta;
import sun.plugin2.message.ShowDocumentMessage;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-solr.xml")
public class SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;


    @Test
    public void testAdd() {
        //创建要添加到solr中的对象，该对象中的属性应该添加solr的注解
        TbItem item = new TbItem();
        item.setId(1L);
        item.setTitle("中兴天机Axon M 折叠双屏智能手机");
        item.setBrand("中兴");
        item.setPrice(new BigDecimal(3888));
        item.setGoodsId(123L);
        item.setSeller("中兴旗舰店");
        item.setCategory("手机");

        /*item.setId(5424543L);
        item.setTitle("222 魅族 魅蓝 Note6 4GB+32GB 全网通公开版 香槟金 移动联通电信4G手机 双卡双待");
        item.setPrice(new BigDecimal(899));
        item.setUpdateTime(new Date());
        item.setSellerId("meizu");
        item.setImage("https://item.jd.com/5424543.html");*/


        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }

    //根据主键删除
    @Test
    public void testDeleteById() {
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }

    //根据条件删除
    @Test
    public void testDeleteByQuery() {
        SimpleQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    //查询关键字分页查询
    @Test
    public void testQueryInPage() {
        SimpleQuery query = new SimpleQuery("*:*");

        //设置分页
        query.setOffset(0);//分页起始索引号默认为0；
        query.setRows(10); //分页页大小默认为10

        //查询；参数1：查询对象，参数2：是返回结果中的每个文档封装的实体类
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);

        showPage(scoredPage);
    }

    private void showPage(ScoredPage<TbItem> scoredPage) {
        System.out.println("总记录数为：" + scoredPage.getTotalElements());
        System.out.println("总页数为：" + scoredPage.getTotalPages());
        List<TbItem> itemList = scoredPage.getContent();
        for (TbItem item: itemList) {
            System.out.println("id=" + item.getId());
            System.out.println("title=" + item.getTitle());
            System.out.println("price=" + item.getPrice());
            System.out.println("image=" + item.getImage());
            System.out.println("更新时间=" + item.getUpdateTime());
        }
    }

    //多条件查询
    @Test
    public void testMultiQuery() {
        SimpleQuery query = new SimpleQuery();

        //参数1：在schema.xml文件中对应的编写了的域名；contains表示查询该域包含相应关键字的那些文档是不会分词的；is会分词
        Criteria criteria1 = new Criteria("item_title").contains("中兴");
        query.addCriteria(criteria1);

        Criteria criteria2 = new Criteria("item_price").greaterThanEqual(1000);
        query.addCriteria(criteria2);

        //查询；参数1：查询对象，参数2：是返回结果中的每个文档封装的实体类
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);

        showPage(scoredPage);



    }



}
