package com.ayl.bloomfilter;/**
 * Created by 20160701006 on 18/6/26.
 */

import com.ayl.BaseBloomFilterTest;
import com.ayl.bloomfilter.builder.RedisBloomFilterBuilder;
import com.ayl.bloomfilter.impl.RedisBloomFilter;
import com.ayl.itf.BloomFilterI;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPool;

import java.util.Date;

/**
 * User: AYL
 * Data: 18/6/26 19:59
 * Comment:RedisBloomFilterTest
 */
public class RedisBloomFilterTest extends BaseBloomFilterTest {

    @Before
    public void before() throws Exception {
        initialize();
    }

    @Override
    public void initialize() throws Exception {
        JedisPool jedisPool = new JedisPool("192.168.11.89", 6389);
        RedisBloomFilterBuilder builder = new RedisBloomFilterBuilder(super.getInsertions(), 0.099, "stringFilter", jedisPool);
        super.setFilter(builder.builder());
    }

    /**
     *  //得到的结果是：
     *  Connected to the target VM, address: '127.0.0.1:64606', transport: 'socket'
     *  15:44:41.508 [main] DEBUG com.ayl.bloomfilter.builder.RedisBloomFilterBuilder - 计算出总位数M为:48134
     *  15:44:41.512 [main] DEBUG com.ayl.bloomfilter.builder.RedisBloomFilterBuilder - 计算出哈希函数个数K为：3
     *  add耗时：3561ms
     *  判断一个总共耗时：2ms
     *  总共耗时：7ms
     *  // 首先，这个响应时间并不怎么理想
     *  // 其次，看上去判断一个和判断多个的时间几乎没有区别。很可能是因为redis是部署在本地，没有tcp延迟的缘故，如果是远程机器，应该差别会很明显
     *
     */
    @Test
    public void filterTest() {
        super.filterTest();
    }

}
