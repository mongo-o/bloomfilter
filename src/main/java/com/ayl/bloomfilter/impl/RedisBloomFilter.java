package com.ayl.bloomfilter.impl;/**
 * Created by 20160701006 on 18/6/26.
 */

import com.ayl.bloomfilter.BloomFilterI;
import com.ayl.bloomfilter.builder.RedisBloomFilterBuilder;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: AYL
 * Data: 18/6/26 9:12
 * Comment:RedisBloomFilter
 */
public class RedisBloomFilter<T> implements BloomFilterI<T> {
    private Logger logger = LogManager.getLogger();

    private RedisBloomFilterBuilder config;

    public RedisBloomFilter(RedisBloomFilterBuilder builder) {
        this.config = builder;
    }

    /**
     * 向布隆过滤器添加需要过滤的目标对象
     * @param targetObject
     * @return
     */
    public boolean add(T targetObject) {

        //计算hash值
        List<Integer> offsetList = hash(targetObject);

        if (offsetList == null || offsetList.isEmpty()) {
            return false;
        }

        Jedis jedis = config.getJedisPool().getResource();
        for (Integer i : offsetList) {
            jedis.setbit(config.getKeyName(), i, true);
        }

        //归还jedis回到连接池中
        jedis.close();
        return true;
    }

    /**
     * 向布隆过滤器添加需要过滤的目标对象
     * @return
     */
    @Override
    public Map<T, Boolean> batchAdd(List<T> targetObjects) {
        if (targetObjects == null || targetObjects.size() == 0) {
            return null;
        }
        Map<T, Boolean> resultMap = new HashMap<>(targetObjects.size());
        for (T item : targetObjects) {
            Boolean result = add(item);
            resultMap.put(item, result);
        }
        return resultMap;
    }

    /**
     * 判断object是否在过滤器的目标对象中， 是：返回true， 否：返回false
     * @param object
     * @return
     */
    public boolean contains(T object) {
        //计算hash值
        List<Integer> offsetList = hash(object);
        if (offsetList == null || offsetList.isEmpty()) {
            return false;
        }
        Jedis jedis = config.getJedisPool().getResource();
        for (Integer i : offsetList) {
            if (!jedis.getbit(config.getKeyName(), i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 使用hash函数计算object的位图
     * @param object
     * @return
     */
    private List<Integer> hash(T object) {
        //如何得到object对象的byte数组？
        byte[] bytes;

        bytes = object.toString().getBytes();
        return config.getHashFunction().hash(bytes, config.getTotalSize(), config.getHashFuncNums());
    }
}
