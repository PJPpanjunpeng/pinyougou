package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/brand")
@RestController
public class BrandController {
    @Reference
    private BrandService brandService;

    @GetMapping("/testPage")
    public List<TbBrand> testPage(@RequestParam(value="page", defaultValue = "1") Integer page,
                                   @RequestParam(value="rows", defaultValue = "5") Integer rows) {
        //return brandService.testPage(page, rows);
        return (List<TbBrand>) brandService.findPage(page, rows).getRows();

    }

    /**
     * 使用angularjs设计前端
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return brandService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbBrand brand) {
        try {
            brandService.add(brand);
            return Result.ok("新增品牌成功");
        } catch (Exception e) {
            //出现异常，保存失败
            e.printStackTrace();
        }
        return Result.fail("新增品牌失败");
    }

    //根据主键查询
    @GetMapping("/findOne")
    public TbBrand findOne(Long id) {
        return brandService.findOne(id);
    }
    //修改后保存
    @PostMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand) {
        try {
            brandService.update(tbBrand);
            return Result.ok("修改保存成功");
        } catch (Exception e) {
            //出现异常，保存失败
            e.printStackTrace();
        }
        return Result.fail("异常，修改保存失败");
    }

    //批量删除
    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            brandService.deleteByIds(ids);
            return Result.ok("删除品牌成功");
        } catch (Exception e) {
            //出现异常，删除失败
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 根据条件分页查询
     * @param brand 查询条件
     * @param page 页号
     * @param rows 页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody TbBrand brand,
                             @RequestParam(value = "page", defaultValue = "1")Integer page,
                             @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return brandService.search(brand, page, rows);
    }


    @GetMapping("/findAll.do")
    public List<TbBrand> findAll() {
        return  brandService.queryAll();
        //return brandService.findAll();
    }
}
