package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * search实现类
 */
@Service(timeout = 5000)    //超时设置
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map search(Map searchMap) {
        Map map = new HashMap();
        //获取keywords
        String keywords = (String) searchMap.get("keywords");
        //去除空格
        searchMap.put("keywords", keywords.replace(" ", ""));
        //查询列表
        map.putAll(searchList(searchMap));

        //分类列表显示
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList );

        //品牌与规格
        String category = (String) searchMap.get("category");
        //是否为空
        if (category.equals("")){
            //是否size为0
            if (categoryList.size()>0){
                map.putAll(searchSpecAndBrandList(categoryList.get(0)));
            }
        }else{
            map.putAll(searchSpecAndBrandList(category));
        }


        return map;
    }

    /**
     * 批量导入
     * @param list
     */
    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
     * 批量删除
     * @param ids
     */
    @Override
    public void delete(List ids) {
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_goodsid").in(ids);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 关键字高亮查询
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap) {
        Map map = new HashMap();
        //创建高亮查询对象
        HighlightQuery query = new SimpleHighlightQuery();

        //设置高亮域
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");

        //添加高亮标签
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");

        //注入query
        query.setHighlightOptions(highlightOptions);

        //设置附加条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //添加条件
        query.addCriteria(criteria);

        /**
         *  过滤条件设置
         */

        //分类过滤
        if (!"".equals(searchMap.get("category"))){
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //品牌过滤
        if (!"".equals(searchMap.get("brand"))){
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //规格过滤
        if (searchMap.get("spec")!=null){
            Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //价格过滤,判断条件
        if(!"".equals(searchMap.get("price"))){
            //切分价格
            String[] prices = ((String) searchMap.get("price")).split("-");
            //起始价格不为0时
            if (!prices[0].equals("0")){
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price").greaterThan(prices[0]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            //上限价格不为*时
            if (!prices[1].equals("*")){
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        /**
         * 排序
         */
        //获取排序方式
        String sortValue = (String) searchMap.get("sort");
        //获取排序字段
        String sortField = (String) searchMap.get("sortField");
        //进行排序判断
        if (sortValue!=null&&!"".equals(sortField)){
            if (sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
            }
        }

        /**
         *  分页查询
         */
        //获取前端数据
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo==null){
            pageNo = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize==null){
            pageSize=20;
        }

        //设置分页条件
        query.setOffset((pageNo-1)*pageSize);
        query.setRows(pageSize);

        //获取高亮集合对象
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //获取所有包含高亮的记录
        List<HighlightEntry<TbItem>> entryList = page.getHighlighted();

        //遍历获取包含高亮域每条记录
        for (HighlightEntry<TbItem> entry : entryList) {

            //获取记录中的包含高亮的所有域
            List<HighlightEntry.Highlight> highlights = entry.getHighlights();

            /*for (HighlightEntry.Highlight highlight : highlights) {
                //由于一个域可以对应多个域
                List<String> snipplets = highlight.getSnipplets();
                System.out.println(snipplets);
            }*/

            //判断是否存在高亮
            if (highlights.size()>0 && highlights.get(0).getSnipplets().get(0).length()>0){
                //获取高亮的对应对象
                TbItem item = entry.getEntity();
                //注入包含高亮的域
                item.setTitle(highlights.get(0).getSnipplets().get(0));
            }
        }
        //map返回数据
        map.put("rows", page.getContent());
        map.put("totalPages", page.getTotalPages());
        map.put("total", page.getTotalElements());
        return map;
    }

    /**
     * 分类列表
     * @param searchMap
     * @return
     */
    private List<String> searchCategoryList(Map searchMap){

        List<String> list = new ArrayList<>();

        //创建query
        Query query = new SimpleQuery();

        //设置分组域名
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");

        //添加条件
        query.setGroupOptions(groupOptions);

        //设置基础条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));

        //添加条件
        query.addCriteria(criteria);

        //获取分组页
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);

        //获取所有分组记录
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");

        //获取分组的入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();

        //获取入口页的所有记录
        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        for (GroupEntry<TbItem> entry : content) {
            System.out.println(entry.getGroupValue());
            //获取每个记录的值,添加到集合
            list.add(entry.getGroupValue());
        }
        return list;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取品牌与规格的缓存
     * @param category
     * @return
     */
    private Map searchSpecAndBrandList(String category){
        Map map = new HashMap();

        //获取模板id
        Long templateId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        //判断是否cunz
        if (templateId!=null){
            //获取品牌
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(templateId);
            map.put("brandList", brandList);
            //获取规格
            List specList = (List) redisTemplate.boundHashOps("specList").get(templateId);
            map.put("specList", specList);

        }
        return map;
    }
}
