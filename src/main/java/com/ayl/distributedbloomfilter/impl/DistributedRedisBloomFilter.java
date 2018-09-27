package com.ayl.distributedbloomfilter.impl;/**
 * Created by 20160701006 on 18/6/26.
 */

import com.ayl.distributedbloomfilter.builder.DistributedRedisBloomFilterBuilder;
import com.ayl.itf.BloomFilterI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * User: AYL
 * Data: 18/6/26 9:12
 * Comment:RedisBloomFilter
 */
public class DistributedRedisBloomFilter<T> implements BloomFilterI<T> {
    private Logger logger = LogManager.getLogger();

    private DistributedRedisBloomFilterBuilder config;

    public DistributedRedisBloomFilter(DistributedRedisBloomFilterBuilder builder) {
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
        String keyName = getRedisKeyName(targetObject);

        Jedis jedis = config.getJedisPool().getResource();
        for (Integer i : offsetList) {
            jedis.setbit(keyName, i, true);
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
        Map<String, List<Integer>> indexMap = new HashMap<>(config.getChildFilterNums());
        Map<T, Boolean> resultMap = new HashMap<>(targetObjects.size());

        //计算得到每个需要添加进过滤器的元素的所在key及其bit位图
        targetObjects.forEach(
                (k) -> {
                    indexMap.put(getRedisKeyName(k), hash(k));
                }
        );

        Jedis jedis = config.getJedisPool().getResource();
        Pipeline pipeline = jedis.pipelined();
        try {
            //开始批量将setbit操作到redis
            //循环map，这个循环单次操作对象是一个redisKey
            indexMap.forEach(
                    (redisKeyName, listBits) -> {
                        //循环单个对象的位图
                        listBits.forEach(
                                (bitIndex) -> {
                                    pipeline.setbit(redisKeyName, bitIndex, true);
                                }
                        );
                    }
            );
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return resultMap;
    }

    /**
     * 判断object是否在过滤器的目标对象中， 是：返回true， 否：返回false
     * @param object
     * @return
     */
    public boolean contains(T object) {
        String redisKeyName = getRedisKeyName(object);
        List<Integer> bitMap = hash(object);

        Jedis jedis = config.getJedisPool().getResource();
        Pipeline pipeline = jedis.pipelined();
        bitMap.forEach(
                (bitIndex) -> {
                    pipeline.getbit(redisKeyName, bitIndex);
                }
        );

        List<Object> bitResultList = pipeline.syncAndReturnAll();

        for (Object bitResultItem : bitResultList) {
            //只要有一个bit位没有匹配上，则表示不存在
            if (!(Boolean) bitResultItem) {
                return false;
            }
        }
        return true;
    }

    /**
     * 第二次hash，使用hash函数计算object的位图
     * @param object
     * @return
     */
    private List<Integer> hash(T object) {
        //如何得到object对象的byte数组？
        byte[] bytes;

        bytes = object.toString().getBytes();
        return config.getHashFunction().hash(bytes, config.getEachKeySize(), config.getHashFuncNums());
    }

    /**
     *第一次hash，获取子哈希的index
     * @param object
     * @return
     */
    private int getWhere(T object) {
        //如何得到object对象的byte数组？
        byte[] bytes;

        bytes = object.toString().getBytes();
        List<Integer> indexList = config.getHashFunction().hash(bytes, config.getChildFilterNums(), 1);
        //因为传递的参数K为1，所以不可能出现size > 1的情况，但是这里还是做判断
        //size < 0的情况是有可能出现的
        //如果出现异常情况，此时使用随机方式落到某个子key中
        if (indexList.size() > 1 || indexList.size() < 0) {
            return new Random().nextInt(config.getChildFilterNums());
        }
        return indexList.get(0);
    }

    private String getRedisKeyName(T object) {
        return config.getRedisKeyNamePrefix() + getWhere(object);
    }
}
