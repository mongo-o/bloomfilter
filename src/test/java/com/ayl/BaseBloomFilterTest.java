package com.ayl;/**
 * Created by 20160701006 on 18/6/26.
 */

import com.ayl.bloomfilter.builder.RedisBloomFilterBuilder;
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
public abstract class BaseBloomFilterTest {
    private BloomFilterI<String> filter;
    private int insertions = 10000;

    public abstract void initialize() throws Exception;

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
    public void filterTest() {
        long startTime = new Date().getTime();
        //添加insertions个目标元素到过滤器中
        String prefix = "element";
        for (int i = 0; i< insertions; i++) {
            filter.add(prefix + i);
        }
        //查看添加过程耗时
        long entTime2 = new Date().getTime();
        System.out.println("add耗时：" + (entTime2 - startTime) + "ms");

        long startTime1 = new Date().getTime();
        Assert.assertTrue(filter.contains(prefix + 0));
        //查看判断一个元素的耗时
        long entTime1 = new Date().getTime();
        System.out.println("判断一个总共耗时：" + (entTime1 - startTime1) + "ms");

        Assert.assertTrue(filter.contains(prefix + (insertions -1) ));
        Assert.assertTrue(filter.contains(prefix + 888));

        Assert.assertFalse(filter.contains(prefix + -1));
        Assert.assertFalse(filter.contains(prefix + insertions));
        Assert.assertFalse(filter.contains(prefix + (insertions + 1)));
        //查看判断多个元素的耗时
        long entTime = new Date().getTime();
        System.out.println("总共耗时：" + (entTime - startTime1) + "ms");
    }

    public BloomFilterI<String> getFilter() {
        return filter;
    }

    public void setFilter(BloomFilterI<String> filter) {
        this.filter = filter;
    }

    public int getInsertions() {
        return insertions;
    }
}
