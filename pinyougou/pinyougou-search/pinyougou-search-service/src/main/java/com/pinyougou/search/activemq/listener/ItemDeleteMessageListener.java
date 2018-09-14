package com.pinyougou.search.activemq.listener;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.Arrays;

public class ItemDeleteMessageListener extends AbstractAdaptableMessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        try {
            ObjectMessage objectMessage = (ObjectMessage) message;

            //获取消息（商品id集合）
            Long[] goodsIds = (Long[]) objectMessage.getObject();

            //2、将solr商品数据删除
            itemSearchService.deleteItemByGoodsIdList(Arrays.asList(goodsIds));
            System.out.println("同步删除索引库中数据完成。 ");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
