package com.ayl.bloomfilter;/**
 * Created by 20160701006 on 18/6/26.
 */

import com.ayl.bloomfilter.builder.RedisBloomFilterBuilder;
import com.ayl.bloomfilter.impl.RedisBloomFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPool;

/**
 * User: AYL
 * Data: 18/6/26 19:59
 * Comment:RedisBloomFilterTest
 */
public class RedisBloomFilterTest {
    RedisBloomFilter<String> filter;
    int insertions = 10000;

    @Before
    public void before() {
        JedisPool jedisPool = new JedisPool("192.168.11.89", 6389);
        RedisBloomFilterBuilder builder = new RedisBloomFilterBuilder(insertions, 0.099, "stringFilter", jedisPool);
        filter = builder.builder();
    }

    @Test
    public void filterTest() {
        String prefix = "element";
        for (int i = 0; i< insertions; i++) {
            filter.add(prefix + i);
        }
        Assert.assertTrue(filter.contains(prefix + 0));
        Assert.assertTrue(filter.contains(prefix + (insertions -1) ));
        Assert.assertTrue(filter.contains(prefix + 888));

        Assert.assertFalse(filter.contains(prefix + -1));
        Assert.assertFalse(filter.contains(prefix + 100000));
        Assert.assertFalse(filter.contains(prefix + 10001));
    }

}
