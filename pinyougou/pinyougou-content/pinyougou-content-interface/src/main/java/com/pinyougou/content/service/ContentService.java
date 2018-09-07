package com.pinyougou.content.service;

import com.pinyougou.pojo.TbContent;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface ContentService extends BaseService<TbContent> {

    PageResult search(Integer page, Integer rows, TbContent content);

    /**
     * 根据内容id查询启用的内容列表并降序排序
     * @param categoryId
     * @return
     */
    List<TbContent> findContentListByCategoryId(Long categoryId);
}