package com.pan.jd.controller;

import com.pan.jd.pojo.Result;
import com.pan.jd.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/search")
@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;


    @RequestMapping("/list")
    public ModelAndView list(String queryString, String catalog_name, String price, Integer page, String sort){
        ModelAndView mv = new ModelAndView("product_list");

        try {
            Result result = searchService.search(queryString, catalog_name, price, page, sort);
            mv.addObject("result", result);

            //回显查询条件
            mv.addObject("queryString", queryString);
            mv.addObject("catalog_name", catalog_name);
            mv.addObject("price", price);
            mv.addObject("page", page);
            mv.addObject("sort", sort);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mv;
    }
}

/*
@RequestMapping("/search")
@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    *//**
     * 到solr中根据搜索关键字和其它信息进行搜索数据
     * @param queryString 搜索关键字
     * @param catalog_name 过滤条件：分类
     * @param price 过滤条件：价格，格式为：0-9
     * @param page 页号
     * @param sort 排序，1为升序，0为降序
     * @return 搜索结果
     *//*
    @RequestMapping("/list")
    public ModelAndView list(String queryString, String catalog_name, String price, Integer page, String sort) {
        ModelAndView mv = new ModelAndView("product_list");
        try {
            Result result = searchService.search(queryString, catalog_name, price, page, sort);
            mv.addObject("result", result);

            //回显查询条件
            mv.addObject("queryString", queryString);
            mv.addObject("catalog_name", catalog_name);
            mv.addObject("price", price);
            mv.addObject("page", page);
            mv.addObject("sort", sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mv;
    }
}*/

