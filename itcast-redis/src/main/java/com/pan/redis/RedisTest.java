package com.pan.redis;

import jdk.management.resource.ResourceId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    //测试字符串objing
    @Test
    public void testString() {
        redisTemplate.boundValueOps("string_key").set("i_am_pjp");
        Object obj = redisTemplate.boundValueOps("string_key").get();
        System.out.println(obj);
    }

    //测试散列hash
    @Test
    public void testHash() {
        redisTemplate.boundHashOps("hash_key").put("fi", "v1");
        redisTemplate.boundHashOps("hash_key").put("f2", "v2");
        List list = redisTemplate.boundHashOps("hash_key").values();
        System.out.println(list);
    }

    //测试集合list
    @Test
    public void testList() {
        redisTemplate.boundListOps("list_key").leftPush(1);
        redisTemplate.boundListOps("list_key").leftPush(2);
        List list_key = redisTemplate.boundListOps("list_key").range(0, -1);
        System.out.println(list_key);
    }

    //测试集合set
    @Test
    public void testSet() {
        redisTemplate.boundSetOps("set_key").add(1, 3, 5,"itcast", 7);
        Set set_key = redisTemplate.boundSetOps("set_key").members();
        System.out.println(set_key);
    }

    //测试有序集合 sorted set
    @Test
    public void testSortedSet() {
        redisTemplate.boundZSetOps("zset_key").add("aa", 20);
        redisTemplate.boundZSetOps("zset_key").add("bb", 10);
        redisTemplate.boundZSetOps("zset_key").add("cc", 30);
        Set zset_key = redisTemplate.boundZSetOps("zset_key").range(0, -1);
        System.out.println(zset_key);

    }
}
