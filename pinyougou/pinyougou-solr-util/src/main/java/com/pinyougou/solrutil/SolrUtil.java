package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 批量导入工具类
 */
@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    //批量导入
    public void importItemData(){
        //设置数据库查询条件
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        //已审核通过
        criteria.andStatusEqualTo("1");
        //查询所有符合条件数据
        List<TbItem> list = itemMapper.selectByExample(example);

        for (TbItem tbItem : list) {
            System.out.println(tbItem.getId()+" "+tbItem.getTitle());
            //将spec解析为map
            Map map = JSON.parseObject(tbItem.getSpec(), Map.class);
            //赋给item对象
            tbItem.setSpecMap(map);
        }
        //添加到solr
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
        System.out.println("--------分割线----------");
    }

    //调用工具类完成导入
    public static void main(String[] args) {
        //读取配置文件
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        //注入工具类
        SolrUtil solrUtil = (SolrUtil) applicationContext.getBean("solrUtil");

        //调用
        solrUtil.importItemData();
    }
}
