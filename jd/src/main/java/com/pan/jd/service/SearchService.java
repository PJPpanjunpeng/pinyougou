package com.pan.jd.service;

import com.pan.jd.pojo.Result;

public interface SearchService {

    /**
     * 到solr中根据搜索关键字和其它信息进行搜索数据
     * @param queryString 搜索关键字
     * @param catalog_name 过滤条件：分类
     * @param price 过滤条件：价格，格式为：0-9
     * @param page 页号
     * @param sort 排序，1为升序，0为降序
     * @return 搜索结果
     */
    Result search(String queryString, String catalog_name, String price, Integer page, String sort) throws Exception;

}

