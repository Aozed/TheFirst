package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojogroup.Specification;
import com.pinyougou.sellergoods.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
	private TbSpecificationOptionMapper tbSpecificationOptionMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		//获取对象
		TbSpecification tbSpecification = specification.getSpecification();
		//保存数据
		specificationMapper.insert(tbSpecification);
		//获取主表主键
		Long id = tbSpecification.getId();
		//获取集合
		List<TbSpecificationOption> list = specification.getSpecificationOptionList();
		//遍历集合
		for (TbSpecificationOption tbSpecificationOption : list) {
			//设置关联id
			tbSpecificationOption.setSpecId(id);
			//保存数据
			tbSpecificationOptionMapper.insert(tbSpecificationOption);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		//获取规格对象
		TbSpecification tbSpecification = specification.getSpecification();
		//直接更新主表数据
		specificationMapper.updateByPrimaryKey(tbSpecification);
		//获取id
		Long id = tbSpecification.getId();
		//清空关联数据
		//创建example
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		//创建criteria
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		//设置条件
		criteria.andSpecIdEqualTo(id);
		//清除
		tbSpecificationOptionMapper.deleteByExample(example);

		//获取集合
		List<TbSpecificationOption> list = specification.getSpecificationOptionList();
		//遍历集合
		for (TbSpecificationOption tbSpecificationOption : list) {
			//设置关联id
			tbSpecificationOption.setSpecId(id);
			//保存数据
			tbSpecificationOptionMapper.insert(tbSpecificationOption);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		//创建对象
		Specification specification = new Specification();
		//获取tbspecification
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		/**
		 * 获取tbspecificationoption
		 */
		//创建example
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		//创建criteria
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		//设置条件
		criteria.andSpecIdEqualTo(id);
		List<TbSpecificationOption> list = tbSpecificationOptionMapper.selectByExample(example);

		//数据封装
		specification.setSpecification(tbSpecification);
		specification.setSpecificationOptionList(list);
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
			//创建example
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			//创建criteria
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			//设置条件
			criteria.andSpecIdEqualTo(id);
			//清除
			tbSpecificationOptionMapper.deleteByExample(example);
		}

	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
		return specificationMapper.selectOptionList();
	}

}
