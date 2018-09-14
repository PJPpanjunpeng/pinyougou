package com.pinyougou.search.activemq.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

public class ItemImportMessageListener extends AbstractAdaptableMessageListener {

    @Autowired
    private ItemSearchService itemSearchService;


    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        try {
            //1.接受消息
            TextMessage textMessage = (TextMessage) message;

            //将json格式转换为Java集合对象
            List<TbItem> itemList = JSONArray.parseArray(textMessage.getText(),
                    TbItem.class);

            //2.处理消息
            itemSearchService.importItemList(itemList);

            System.out.println("同步索引库数据完成。 ");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
