package com.ayl.distributedbloomfilter.builder;/**
 * Created by 20160701006 on 18/6/26.
 */

import com.ayl.distributedbloomfilter.impl.DistributedRedisBloomFilter;
import com.ayl.util.hash.HashFunction;
import com.ayl.util.hash.MurMur3HashFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.JedisPool;

/**
 * User: AYL
 * Data: 18/6/26 11:15
 * Comment:RedisBloomFilterBuilder
 */
public class DistributedRedisBloomFilterBuilder {
    private final Logger logger = LogManager.getLogger();
    /**
     * 每个redis的bitmap的长度
     */
    private int eachKeySize = 10000;

    /**
     * 过滤器中要判断的N的个数
     */
    private long insertions;

    /**
     * 过滤器的错误率
     */
    private double falseProbability;

    /**
     * 过滤器使用的bitMap的key名称的前缀
     */
    private String redisKeyNamePrefix = "bf";

    /**
     *过滤器的M的位数
     */
    private long totalSize;

    /**
     * 过滤器的哈希函数的个数K
     */
    private int hashFuncNums;

    /**
     * 子布隆过滤器的个数
     */
    private int childFilterNums;

    /**
     * redis的连接池
     */
    private JedisPool jedisPool;

    private volatile boolean buildDone = false;

    private HashFunction hashFunction = new MurMur3HashFunction();

    public DistributedRedisBloomFilterBuilder(long insertions, double falseProbability,
                                              String redisKeyNamePrefix, JedisPool jedisPool,
                                              int eachKeySize) {
        this.insertions = insertions;
        this.falseProbability = falseProbability;
        this.redisKeyNamePrefix = redisKeyNamePrefix;
        this.jedisPool = jedisPool;
        this.eachKeySize = eachKeySize;
    }

    public DistributedRedisBloomFilter builder() {
        checkParamValid();
        return new DistributedRedisBloomFilter(this);
    }

    private void checkParamValid() {
        if (!buildDone) {
            totalSize = optimalNumsOfBits();
            childFilterNums = (int) (totalSize / eachKeySize) + 1;
            logger.debug("计算出子哈希函数个数为:" + childFilterNums);
            hashFuncNums = optimalNumOfHashFunctions();
            buildDone = true;
        }
    }

    /**
     * 计算过滤器的M的位数
     * @return
     */
    private long optimalNumsOfBits() {
        if (falseProbability == 0) {
            falseProbability = Double.MIN_VALUE;
        }

        //如果这里计算的totalSize超过long的表示范围呢？这个情况有没有可能呢？
        long totalSize = (long) (-insertions * Math.log(falseProbability) / (Math.log(2) * Math.log(2)));
        logger.debug("计算出总位数M为:" + totalSize);
        return totalSize;
    }

    /**
     * 计算过滤器的哈希函数的个数K
     * @return
     */
    private int optimalNumOfHashFunctions() {
        int hashNum = Math.max(1, (int) Math.round((double) totalSize/ insertions * Math.log(2)));
        logger.debug("计算出哈希函数个数K为：" + hashNum);
        return hashNum;
    }

    public long getInsertions() {
        return insertions;
    }

    public double getFalseProbability() {
        return falseProbability;
    }

    public String getRedisKeyNamePrefix() {
        return redisKeyNamePrefix;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public int getHashFuncNums() {
        return hashFuncNums;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public HashFunction getHashFunction() {
        return hashFunction;
    }

    public DistributedRedisBloomFilterBuilder setHashFunction(HashFunction hashFunction) {
        this.hashFunction = hashFunction;
        return this;
    }

    public int getChildFilterNums() {
        return childFilterNums;
    }

    public int getEachKeySize() {
        return eachKeySize;
    }
}
