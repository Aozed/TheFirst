package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController         //包含@Controller  @ResponseBody
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    //查询所有
    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(int page,int size){
        return brandService.findPage(page, size);
    }

    //添加品牌
    @RequestMapping("/add")
    public Result addBrand(@RequestBody  TbBrand tbBrand){  //requestBody接收数据
        //使用trycatch,进行不同结果的返回
        try {
            brandService.addBrand(tbBrand);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
    //获取对应id的品牌
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    //保存修改品牌
    @RequestMapping("/update")
    public Result updateBrand(@RequestBody  TbBrand tbBrand){  //requestBody接收数据
        //使用trycatch,进行不同结果的返回
        try {
            brandService.updateBrand(tbBrand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    //获取对应id的品牌
    @RequestMapping("/delete")
    public Result deleteBrand(Long[] ids){
        //使用trycatch,进行不同结果的返回
        try {
            brandService.deleteBrand(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    //分页查询
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand tbBrand,int page,int size){
        return brandService.queryPage(tbBrand,page, size);
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}
