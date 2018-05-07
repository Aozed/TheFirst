package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

/**
 * 信息消费者监听器:数据导入solr
 */
@Component
public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {

        try {

            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            System.out.println("消费者获取数据"+text);

            List<TbItem> list = JSON.parseArray(text,TbItem.class);
            itemSearchService.importList(list);
            System.out.println("数据导入成功...");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
