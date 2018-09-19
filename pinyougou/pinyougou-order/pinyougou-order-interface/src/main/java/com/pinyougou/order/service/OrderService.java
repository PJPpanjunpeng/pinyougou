package com.pinyougou.order.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

public interface OrderService extends BaseService<TbOrder> {

    PageResult search(Integer page, Integer rows, TbOrder order);

    /**
     * 将购物车列表中的商品保存成订单基本、明细信息和支付日志信息
     * @param order 订单基本信息
     * @return 支付业务id
     */
    String addOrder(TbOrder order);
}