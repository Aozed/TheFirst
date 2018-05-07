package com.pinyougou.search.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

/**
 * 信息消费者监听器:删除solr数据
 */
@Component
public class ItemDeleteListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {

        try {
            //使用objectmessage接收数据
            ObjectMessage objectMessage = (ObjectMessage) message;
            Long[] ids = (Long[]) objectMessage.getObject();
            System.out.println(ids);

            //调用方法
            itemSearchService.delete(Arrays.asList(ids));
            System.out.println("删除成功");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
