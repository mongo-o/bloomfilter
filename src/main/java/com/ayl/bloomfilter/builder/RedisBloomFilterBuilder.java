package com.ayl.bloomfilter.builder;/**
 * Created by 20160701006 on 18/6/26.
 */


import com.ayl.util.hash.HashFunction;
import com.ayl.util.hash.MurMur3HashFunction;
import com.ayl.bloomfilter.impl.RedisBloomFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.JedisPool;

/**
 * User: AYL
 * Data: 18/6/26 11:15
 * Comment:RedisBloomFilterBuilder
 */
public class RedisBloomFilterBuilder {
    private static Logger logger = LogManager.getLogger();
    /**
     * redis的bitmap的最大长度
     */
    private static final long MAX_SIZE = Integer.MAX_VALUE * 2L ;

    /**
     * 过滤器的N的个数
     */
    private int insertions;

    /**
     * 过滤器的错误率
     */
    private double falseProbability;

    /**
     * 过滤器使用的bitMap的key名称
     */
    private String keyName;

    /**
     *过滤器的M的位数int
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

    public RedisBloomFilterBuilder(int insertions, double falseProbability, String keyName, JedisPool jedisPool) {
        this.insertions = insertions;
        this.falseProbability = falseProbability;
        this.keyName = keyName;
        this.jedisPool = jedisPool;
    }

    public RedisBloomFilter builder() throws Exception{
        checkParamValid();
        return new RedisBloomFilter(this);
    }

    private void checkParamValid() throws Exception{
        totalSize = optimalNumsOfBits();
        hashFuncNums = optimalNumOfHashFunctions();
        buildDone = true;
    }

    /**
     * 计算过滤器的M的位数
     * @return
     */
    private int optimalNumsOfBits() throws Exception{
        if (falseProbability == 0) {
            falseProbability = Double.MIN_VALUE;
        }

        int totalSize = (int) (-insertions * Math.log(falseProbability) / (Math.log(2) * Math.log(2)));
        logger.debug("计算出总位数M为:" + totalSize);
        if (totalSize > MAX_SIZE) {
            String errorMsg = "过滤器的M最大位数为：" + MAX_SIZE + ",而您所计算出来的所需的M的最大位数为：" + totalSize;
            logger.debug(errorMsg);
            throw new Exception(errorMsg);
        }
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
