package com.ayl.bloomfilter.builder;/**
 * Created by 20160701006 on 18/6/26.
 */


import com.ayl.bloomfilter.hash.HashFunction;
import com.ayl.bloomfilter.hash.MurMur3HashFunction;
import com.ayl.bloomfilter.impl.RedisBloomFilter;
import redis.clients.jedis.JedisPool;

/**
 * User: AYL
 * Data: 18/6/26 11:15
 * Comment:RedisBloomFilterBuilder
 */
public class RedisBloomFilterBuilder {
    /**
     * redis的bitmap的最大长度
     */
    private static final long MAX_SIZE = Integer.MAX_VALUE * 2 ;

    /**
     * 过滤器的N的个数
     */
    private long insertions;

    /**
     * 过滤器的错误率
     */
    private double falseProbability;

    /**
     * 过滤器使用的bitMap的key名称
     */
    private String keyName;

    /**
     *过滤器的M的位数
     */
    private int totalSize;

    /**
     * 过滤器的哈希函数的个数K
     */
    private int hashFuncNums;

    /**
     * redis的连接池
     */
    private JedisPool jedisPool;

    private volatile boolean buildDone = false;

    private HashFunction hashFunction = new MurMur3HashFunction();

    public RedisBloomFilterBuilder(long insertions, double falseProbability, String keyName, JedisPool jedisPool) {
        this.insertions = insertions;
        this.falseProbability = falseProbability;
        this.keyName = keyName;
        this.jedisPool = jedisPool;
    }

    public RedisBloomFilter builder() {
        checkParamValid();
        return new RedisBloomFilter(this);
    }

    private void checkParamValid() {
        totalSize = optimalNumsOfBits();
        hashFuncNums = optimalNumOfHashFunctions();
        buildDone = true;
    }

    /**
     * 计算过滤器的M的位数
     * @return
     */
    private int optimalNumsOfBits() {
        if (falseProbability == 0) {
            falseProbability = Double.MIN_VALUE;
        }

        return (int) (-insertions * Math.log(falseProbability) / (Math.log(2) * Math.log(2)));
    }

    /**
     * 计算过滤器的哈希函数的个数K
     * @return
     */
    private int optimalNumOfHashFunctions() {
        return Math.max(1, (int) Math.round((double) totalSize/ insertions * Math.log(2)));
    }

    public long getInsertions() {
        return insertions;
    }

    public double getFalseProbability() {
        return falseProbability;
    }

    public String getKeyName() {
        return keyName;
    }

    public int getTotalSize() {
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

    public RedisBloomFilterBuilder setHashFunction(HashFunction hashFunction) {
        this.hashFunction = hashFunction;
        return this;
    }
}
