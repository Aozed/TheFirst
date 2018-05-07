package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 订阅模式:获取商品详情页id
 */
@Component
public class PageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        try {
            //获取商品id
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            System.out.println(text);

            //生成商品详情页
            boolean b = itemPageService.genItemHtml(Long.parseLong(text));
            System.out.println("页面生成结果"+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
