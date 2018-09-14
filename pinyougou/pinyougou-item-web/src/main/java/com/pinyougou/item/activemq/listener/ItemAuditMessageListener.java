package com.pinyougou.item.activemq.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 在运营商管理后台中如果审核通过商品之后应该发送商品id集合到MQ,
 * 然后商品详情系统接收商品id并根据商品id生成详情系统的静态页面到指定路径下
 */
public class ItemAuditMessageListener extends AbstractAdaptableMessageListener {

    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;


    @Override
    public void onMessage(Message message, Session session) throws JMSException {

        //1、接收消息
        ObjectMessage objectMessage = (ObjectMessage) message;

        Long[] ids = (Long[]) objectMessage.getObject();

        //2、处理消息（根据商品spu id生成具体的静态页面）
        if (ids != null && ids.length > 0) {
            for (long id : ids) {
                genHtml(id);
            }
        }
    }

    //生成商品静态页面
    private void genHtml(Long goodsId) {

        try {
            //模版
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");

            Map<String, Object> dataModel = new HashMap<>();

            //数据
            Goods goods = goodsService.findGoodsByIdAndStatus(goodsId, "1");

            //goods 商品基本信息
            dataModel.put("goods", goods.getGoods());

            //goodsDesc 商品描述信息
            dataModel.put("goodsDesc", goods.getGoodsDesc());
            //itemList sku商品列表
            dataModel.put("itemList", goods.getItemList());
            //itemCat1 一级商品分类中文名称
            TbItemCat itemCat1 = itemCatService.findOne(goods.getGoods().getCategory1Id());
            dataModel.put("itemCat1", itemCat1.getName());
            //itemCat2 二级商品分类中文名称
            TbItemCat itemCat2 = itemCatService.findOne(goods.getGoods().getCategory2Id());
            dataModel.put("itemCat2", itemCat2.getName());
            //itemCat3 三级商品分类中文名称
            TbItemCat itemCat3 = itemCatService.findOne(goods.getGoods().getCategory3Id());
            dataModel.put("itemCat3", itemCat3.getName());

            //输出
            FileWriter fileWriter = new FileWriter(ITEM_HTML_PATH + goodsId + ".html");

            template.process(dataModel, fileWriter);

            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
