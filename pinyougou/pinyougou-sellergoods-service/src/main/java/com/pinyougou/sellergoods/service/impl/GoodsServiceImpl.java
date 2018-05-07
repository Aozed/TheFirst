package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbBrandMapper brandMapper;

    @Autowired
    private TbSellerMapper sellerMapper;

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        //设置goods状态
        TbGoods tbGoods = goods.getGoods();
        tbGoods.setAuditStatus("0");
        goodsMapper.insert(tbGoods);
        //设置goodsdesc的id
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDesc.setGoodsId(tbGoods.getId());
        goodsDescMapper.insert(goodsDesc);

        insertItemList(goods, tbGoods, goodsDesc);

    }

    private void insertItemList(Goods goods, TbGoods tbGoods, TbGoodsDesc goodsDesc) {
        if (tbGoods.getIsEnableSpec() == "1") {
            //获取item的集合
            List<TbItem> itemList = goods.getItemList();
            for (TbItem tbItem : itemList) {
                System.out.println(tbItem.getSpec());
                /**
                 * 封装数据
                 */
                Map<String, Object> map = JSON.parseObject(tbItem.getSpec());
                String title = tbGoods.getGoodsName();
                for (String s : map.keySet()) {
                    title += " " + map.get(s);
                }
                //设置标题
                tbItem.setTitle(title);

                setItemValues(tbGoods, goodsDesc, tbItem);
                //保存
                itemMapper.insert(tbItem);
            }
        } else {
            TbItem tbItem = new TbItem();
            tbItem.setTitle(tbGoods.getGoodsName());
            tbItem.setIsDefault("1");
            tbItem.setStatus("1");
            tbItem.setPrice(tbGoods.getPrice());
            tbItem.setNum(99999);
            setItemValues(tbGoods, goodsDesc, tbItem);

            itemMapper.insert(tbItem);
        }
    }

    private void setItemValues(TbGoods tbGoods, TbGoodsDesc goodsDesc, TbItem tbItem) {
        //设置三级分类id
        tbItem.setCategoryid(tbGoods.getCategory3Id());
        //设置分类名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
        tbItem.setCategory(itemCat.getName());

        //设置日期
        tbItem.setCreateTime(new Date());
        tbItem.setUpdateTime(new Date());

        //设置商品id
        tbItem.setGoodsId(tbGoods.getId());

        //设置商家id与商家店铺名称
        tbItem.setSellerId(tbGoods.getSellerId());
        TbSeller seller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
        tbItem.setSeller(seller.getNickName());
        //设置品牌
        TbBrand brand = brandMapper.selectByPrimaryKey(tbGoods.getBrandId());
        tbItem.setBrand(brand.getName());

        //设置图片信息
        List<Map> list = JSON.parseArray(goodsDesc.getItemImages(), Map.class);
        if (list.size() > 0) {
            tbItem.setImage(list.get(0).get("url").toString());
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {

        TbGoods tbGoods = goods.getGoods();
        //重新设置审核状态
        tbGoods.setAuditStatus("0");
        goodsMapper.updateByPrimaryKey(tbGoods);

        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDescMapper.updateByPrimaryKey(goodsDesc);
        //清空之前的关联item
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(tbGoods.getId());
        itemMapper.deleteByExample(example);

        //存储数据
        insertItemList(goods, tbGoods, goodsDesc);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        //获取goods
        Goods goods = new Goods();
        goods.setGoods(goodsMapper.selectByPrimaryKey(id));
        //获取goodsDesc
        goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));
        //获取所有item
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> itemList = itemMapper.selectByExample(example);
        System.out.println(itemList);
        goods.setItemList(itemList);
        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //获取数据
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            //修改删除信息
            tbGoods.setIsDelete("1");
            //保存修改后的数据
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();

        //删除信息必须为空
        criteria.andIsDeleteIsNull();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                //criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    //状态修改
    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            //查询到goods
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            //修改状态
            tbGoods.setAuditStatus(status);
            //保存修改后的数据
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

    /**
     * 通过更新的数据获取对应sku集合
     * @param goodsId
     * @param status
     * @return
     */
    @Override
    public List<TbItem> findItemListByGoodsIdsListAndStatus(Long[] goodsId,String status){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(goodsId));
        criteria.andStatusEqualTo(status);

        return itemMapper.selectByExample(example);
    }
}
