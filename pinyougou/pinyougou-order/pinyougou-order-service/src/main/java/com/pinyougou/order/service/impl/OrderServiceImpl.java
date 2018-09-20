package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Transactional
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl extends BaseServiceImpl<TbOrder> implements OrderService {

    //redis中购物车数据的key
    private static final String REDIS_CART_LIST = "CART_LIST";

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Override
    public PageResult search(Integer page, Integer rows, TbOrder order) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(order.get***())){
            criteria.andLike("***", "%" + order.get***() + "%");
        }*/

        List<TbOrder> list = orderMapper.selectByExample(example);
        PageInfo<TbOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public String addOrder(TbOrder order) {

        //支付日志id，若非微信支付可以为空
        String outTradeNo = "";

        //1、获取用户对应的购物车列表
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(order.getUserId());

        if (cartList != null && cartList.size() > 0) {
            //2、遍历购物车列表的每个购物车对应生成一个订单和多个其对应的订单明细
            double totalFee = 0.0;//本次应该支付总金额
            String orderIds = "";//本次交易的订单id集合
            for (Cart cart : cartList) {
                long orderId = idWorker.nextId();
                TbOrder tbOrder = new TbOrder();
                tbOrder.setOrderId(orderId);
                tbOrder.setSourceType(order.getSourceType());//订单来源
                tbOrder.setUserId(order.getUserId());//购买者
                tbOrder.setStatus("1");//状态：未付款
                tbOrder.setPaymentType(order.getPaymentType());//支付类型
                tbOrder.setReceiverMobile(order.getReceiverMobile());//收货人手机号
                tbOrder.setReceiverAreaName(order.getReceiverAreaName());//收货人地址
                tbOrder.setReceiver(order.getReceiver());//收货人
                tbOrder.setCreateTime(new Date());//订单创建时间
                tbOrder.setUpdateTime(tbOrder.getCreateTime());//订单更新时间
                tbOrder.setSellerId(cart.getSeller());//卖家

                //本笔订单的支付总金额
                double payment = 0.0;
                //本笔订单的明细
                for (TbOrderItem orderItem : cart.getOrderItemList()) {
                    orderItem.setId(idWorker.nextId());
                    orderItem.setOrderId(orderId);
                    //累计本笔订单的总金额
                    payment += orderItem.getTotalFee().doubleValue();
                    orderItemMapper.insertSelective(orderItem);
                }

                tbOrder.setPayment(new BigDecimal(payment));
                orderMapper.insertSelective(tbOrder);

                //记录订单id
                if (orderIds.length() > 0) {
                    orderIds += "," + orderId;
                } else {
                    orderIds = orderId + "";
                }
                //累计本次所有订单的总金额
                totalFee += payment;

            }

            //3、如果是微信支付的话则需要生成支付日志保存到数据库
            if ("1".equals(order.getPaymentType())) {
                outTradeNo = idWorker.nextId() + "";
                TbPayLog tbPayLog = new TbPayLog();
                tbPayLog.setOutTradeNo(outTradeNo);
                tbPayLog.setTradeState("0");//未支付
                tbPayLog.setUserId(order.getUserId());
                tbPayLog.setCreateTime(new Date());

                //本次购物要支付的总金额 = 所有订单的总金额累加
                //因为微信、支付宝等支付接口要求支付的金额精度为分
                tbPayLog.setTotalFee((long) (totalFee * 100));//总金额，取整
                tbPayLog.setOrderList(orderIds);//本次订单 id 集合

                payLogMapper.insertSelective(tbPayLog);
            }

            //4、删除用户对应的购物车列表
            redisTemplate.boundHashOps(REDIS_CART_LIST).delete(order.getUserId());

            //5、返回支付日志 id；如果不是微信支付则返回空
            return outTradeNo;
        }

        return null;
    }

    @Override
    public TbPayLog findPayLogByOutTradeNo(String outTradeNo) {
        return payLogMapper.selectByPrimaryKey(outTradeNo);
    }

    @Override
    public void updateOrderStatus(String outTradeNo, String transactionId) {
        //1、更新支付日志支付状态
        TbPayLog payLog = findPayLogByOutTradeNo(outTradeNo);
        payLog.setTradeState("1");//已支付
        payLog.setPayTime(new Date());
        payLog.setTransactionId(transactionId);
        payLogMapper.updateByPrimaryKeySelective(payLog);

        //2、更新支付日志中对应的每一笔订单的支付状态
        String[] orderIds = payLog.getOrderList().split(",");

        TbOrder order = new TbOrder();
        order.setPaymentTime(new Date());
        order.setStatus("2");

        Example example = new Example(TbOrder.class);
        example.createCriteria().andIn("orderId", Arrays.asList(orderIds));

        orderMapper.updateByExampleSelective(order, example);
    }
}
