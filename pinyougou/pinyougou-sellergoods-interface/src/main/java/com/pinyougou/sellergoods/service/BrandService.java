package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 品牌接口
 */
public interface BrandService {
    List<TbBrand> findAll();

    /**
     *分页
     * @param pageNum  当前页
     * @param size     每页记录数
     * @return
     */
    PageResult findPage(int pageNum,int size);

    void addBrand(TbBrand tbBrand);

    TbBrand findOne(Long id);

    void updateBrand(TbBrand tbBrand);

    void deleteBrand(Long[] ids);

    PageResult queryPage(TbBrand tbBrand,int pageNum,int size);

    /**
     * select2数据源
     */
    public List<Map> selectOptionList();
}
