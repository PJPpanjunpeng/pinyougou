package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ctc.wstx.sr.ElemCallback;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.Map.Entry;

@Service(interfaceClass = GoodsService.class)
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;


    @Autowired
    private GoodsDescMapper goodsDescMapper;

    @Autowired
    private ItemCatMapper itemCatMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbGoods goods) {

        PageHelper.startPage(page, rows);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();

        //商家限定
        if(!StringUtils.isEmpty(goods.getSellerId())){
            criteria.andEqualTo("sellerId",  goods.getSellerId());
        }
        if(!StringUtils.isEmpty(goods.getAuditStatus())){
            criteria.andEqualTo("auditStatus",  goods.getAuditStatus());
        }
        if(!StringUtils.isEmpty(goods.getGoodsName())){
            criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
        }

        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 新增商品
     * @param goods
     */
    @Override
    public void addGoods(Goods goods) {
        //1、保存基本信息；在mybatis中如果在保存成功后主键可以回填到保存时候的那个对象中
        goodsMapper.insertSelective(goods.getGoods());

        //2、新增商品描述信息
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insertSelective(goods.getGoodsDesc());

        //3、保存商品SKU列表
        saveItemList(goods);


        //3、新增商品SKU列表
        /*if (goods.getItemList() != null && goods.getItemList().size() > 0) {
            for (TbItem item : goods.getItemList()) {
                String title = goods.getGoods().getGoodsName();

                //组合规格选项形成SKU标题
                Map<String, Object> map = JSON.parseObject(item.getSpec());
                Set<Map.Entry<String, Object>> entries = map.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    title += " " + entry.getValue().toString();
                }

                item.setTitle(title);

                //图片
                List<Map> imgList = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
                if (imgList != null && imgList.size() > 0) {
                    //将商品的第一张图作为sku的图片
                    item.setImage(imgList.get(0).get("url").toString());
                }
                //商品分类id
                item.setCategoryid(goods.getGoods().getCategory3Id());
                //商品分类名称
                TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
                item.setCategory(itemCat.getName());

                //创建时间
                item.setCreateTime(new Date());
                //更新时间
                item.setUpdateTime(item.getCreateTime());
                //SPU商品id
                item.setGoodsId(goods.getGoods().getId());
                //商家id
                item.setSellerId(goods.getGoods().getSellerId());
                //商家名称
                TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
                item.setSeller(seller.getName());
                //品牌名称
                TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
                item.setBrand(brand.getName());

                itemMapper.insertSelective(item);

            }
        }*/
    }

    /**
     * 方法抽取（3.保存商品SKU列表：是否启用规格）
     */
    private void saveItemList(Goods goods) {

        /**
         * 如果是不启用规格：应该根据商品基本信息生成一条sku数据保存到tb_item中；
         * 因为tb_item才是以后展示在页面中让用户购买的商品；
         * 在页面中entity.goods.isEnableSpec的值为0；如果启动则为1
         */
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            if (goods.getItemList() != null && goods.getItemList().size() > 0) {
                for (TbItem item: goods.getItemList()) {
                    //商品的标题应该为：spu商品名称+所有规格选项值
                    String title = goods.getGoods().getGoodsName();

                    //将sku对于的规格及选项数据转换为一个map;获取对应规格的选项
                    Map<String, Object> map = JSON.parseObject(item.getSpec());
                    Set<Entry<String, Object>> entries = map.entrySet();
                    for (Entry entry: entries) {
                        title += " " + entry.getValue();
                    }
                    item.setTitle(title);

                    setItemValue(item, goods);
                    //保存tbItem
                    itemMapper.insertSelective(item);
                }
            }
        } else {
            //不启用规格
            TbItem item = new TbItem();

            item.setPrice(goods.getGoods().getPrice());
            item.setNum(9999);
            item.setIsDefault("1");//表示默认
            item.setStatus("0");//未审核

            item.setTitle(goods.getGoods().getGoodsName());
            setItemValue(item, goods);

            itemMapper.insertSelective(item);
        }
    }





    private void setItemValue(TbItem item, Goods goods) {
        //查询品牌
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());

        //商品分类第3级的中文名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        //商品分类id
        item.setCategoryid(itemCat.getId());

        item.setCreateTime(new Date());
        item.setGoodsId(goods.getGoods().getId());

        //获取spu的第一张图片
        if (!StringUtils.isEmpty(goods.getGoodsDesc().getItemImages())) {
            //将图片json格式字符串转换为一个Json对象
            List<Map> images = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);

            item.setImage(images.get(0).toString());
        }

        //设置商家数据
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());

        item.setSeller(seller.getName());
        item.setSellerId(seller.getSellerId());

        item.setUpdateTime(item.getCreateTime());
    }


    /**
     * 商品修改
     * 1.商品基本信息，描述信息，sku列表回显
     * @param id
     * @return
     */
    @Override
    public Goods findGoodsById(Long id) {
        Goods goods = new Goods();
        //查询商品SPU
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);

        //查询商品描述
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);

        //查询商品SKU列表
        Example example = new Example(TbItem.class);
        example.createCriteria().andEqualTo("goodsId",id);
        List<TbItem> itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);

        return goods;
    }

    /**
     * 商品修改
     * 2.保存修改
     * @param goods
     */
    @Override
    public void updateGoods(Goods goods) {
        //更新商品信息
        //修改过则重新设置未审核
        goods.getGoods().setAuditStatus("0");
        goodsMapper.updateByPrimaryKeySelective(goods.getGoods());

        //更新商品描述信息
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());

        //删除原有的SKU列表，防止数据冗沉
        TbItem param = new TbItem();
        param.setGoodsId(goods.getGoods().getId());
        itemMapper.delete(param);

        //调用方法，保存商品SKU数据
        saveItemList(goods);

    }

    /**
     * 提交审核申请，更新商品状态
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {
        TbGoods goods = new TbGoods();
        goods.setAuditStatus(status);

        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));

        //批量更新商品的审核状态
        goodsMapper.updateByExampleSelective(goods, example);
    }


}
