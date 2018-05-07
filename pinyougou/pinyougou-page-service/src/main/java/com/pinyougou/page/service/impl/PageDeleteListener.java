package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

/**
 * 订阅模式:获取商品详情页id
 */
@Component
public class PageDeleteListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        try {
            //获取数据
            ObjectMessage objectMessage = (ObjectMessage) message;
            Long[] goodsIds = (Long[]) objectMessage.getObject();
            System.out.println(goodsIds);

            //调用方法,删除数据
            boolean b = itemPageService.deleteItemHtml(goodsIds);
            System.out.println("删除商品详情页结果:"+b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
