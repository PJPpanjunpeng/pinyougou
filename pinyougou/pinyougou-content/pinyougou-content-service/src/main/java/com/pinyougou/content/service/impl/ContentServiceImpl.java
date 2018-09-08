package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增
     *
     * @param tbContent 实体类对象
     */
    @Override
    public void add(TbContent tbContent) {
        super.add(tbContent);
        //同步缓存中数据
        updateContentInRedisByCategoryId(tbContent.getCategoryId());
    }

    /**
     * 将分类id对应的redis数据删除
     * @param categoryId 分类id
     */
    private void updateContentInRedisByCategoryId(Long categoryId) {
        redisTemplate.boundHashOps("content").delete(categoryId);
    }

    /**
     * 根据主键更新
     *
     * @param tbContent
     */
    @Override
    public void update(TbContent tbContent) {
        super.update(tbContent);

        //查询原来这个内容对应的分类id
        TbContent oldContent = findOne(tbContent.getId());
        if (!oldContent.getCategoryId().equals(tbContent.getCategoryId())) {
            //修改内容的时候已经修改过内容分类；所以要将原来分类的数据更新
            updateContentInRedisByCategoryId(oldContent.getCategoryId());
        }
        //更新当前对于的分类的redis数据
        updateContentInRedisByCategoryId(tbContent.getCategoryId());

    }

    /**
     * 批量删除
     *
     * @param ids 主键集合
     */
    @Override
    public void deleteByIds(Serializable[] ids) {

        //查询所有的内容；并且要更新这些每一个内容对应的分类在redis中的缓存数据
        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("id", Arrays.asList(ids));

        List<TbContent> contentList = contentMapper.selectByExample(example);
        for (TbContent tbContent: contentList) {
            updateContentInRedisByCategoryId(tbContent.getCategoryId());
        }

        super.deleteByIds(ids);
    }

    //在redis中内容对应的key
    //private static  final String REDIS_CONTENT = "content";

    @Override
    public PageResult search(Integer page, Integer rows, TbContent content) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(content.get***())){
            criteria.andLike("***", "%" + content.get***() + "%");
        }*/

        List<TbContent> list = contentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {

        List<TbContent> contentList = null;

        //就算缓存有误，也不向上抛异常
        try {
            //先从缓存中查找
            contentList = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
            if (contentList != null) {
                return contentList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //根据内容分类id查询该分类下的所有有效内容并且按照排序字段降序排序
        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();

        //内容分类
        criteria.andEqualTo("categoryId", categoryId);

        //启用状态的
        criteria.andEqualTo("status", "1");

        ////排序；设置排序属性，desc降序
        example.orderBy("sortOrder").desc();

        contentList = contentMapper.selectByExample(example);

        //就算缓存设置有误，也不向上抛异常
        try {
            //设置缓存
            redisTemplate.boundHashOps("content").put(categoryId, contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contentList;
    }
}
