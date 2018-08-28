package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.vo.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/findAll.do")
    public List<TbBrand> findAll() {
        return  brandService.queryAll();
        //return brandService.findAll();
    }
}
