package com.ayl.distributedbloomfilter;/**
 * Created by 20160701006 on 18/6/26.
 */

import com.ayl.BaseBloomFilterTest;
import com.ayl.bloomfilter.builder.RedisBloomFilterBuilder;
import com.ayl.bloomfilter.impl.RedisBloomFilter;
import com.ayl.distributedbloomfilter.builder.DistributedRedisBloomFilterBuilder;
import com.ayl.distributedbloomfilter.impl.DistributedRedisBloomFilter;
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
public class DistributedRedisBloomFilterTest extends BaseBloomFilterTest {

    @Before
    public void before() throws Exception {
        initialize();
    }

    @Override
    public void initialize() throws Exception {
        JedisPool jedisPool = new JedisPool("192.168.11.89", 6389);
        DistributedRedisBloomFilterBuilder builder = new DistributedRedisBloomFilterBuilder(super.getInsertions(),
                0.099, "bf", jedisPool, Integer.MAX_VALUE);
        super.setFilter(builder.builder());
    }

    /**
     *  //得到的结果是：
     * C:\java\jdk1.8\bin\java -ea -Didea.test.cyclic.buffer.size=1048576 "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA 2018.1\lib\idea_rt.jar=56045:C:\Program Files\JetBrains\IntelliJ IDEA 2018.1\bin" -Dfile.encoding=UTF-8 -classpath "C:\Program Files\JetBrains\IntelliJ IDEA 2018.1\lib\idea_rt.jar;C:\Program Files\JetBrains\IntelliJ IDEA 2018.1\plugins\junit\lib\junit-rt.jar;C:\Program Files\JetBrains\IntelliJ IDEA 2018.1\plugins\junit\lib\junit5-rt.jar;C:\java\jdk1.8\jre\lib\charsets.jar;C:\java\jdk1.8\jre\lib\deploy.jar;C:\java\jdk1.8\jre\lib\ext\access-bridge-32.jar;C:\java\jdk1.8\jre\lib\ext\cldrdata.jar;C:\java\jdk1.8\jre\lib\ext\dnsns.jar;C:\java\jdk1.8\jre\lib\ext\jaccess.jar;C:\java\jdk1.8\jre\lib\ext\jfxrt.jar;C:\java\jdk1.8\jre\lib\ext\localedata.jar;C:\java\jdk1.8\jre\lib\ext\nashorn.jar;C:\java\jdk1.8\jre\lib\ext\sunec.jar;C:\java\jdk1.8\jre\lib\ext\sunjce_provider.jar;C:\java\jdk1.8\jre\lib\ext\sunmscapi.jar;C:\java\jdk1.8\jre\lib\ext\sunpkcs11.jar;C:\java\jdk1.8\jre\lib\ext\zipfs.jar;C:\java\jdk1.8\jre\lib\javaws.jar;C:\java\jdk1.8\jre\lib\jce.jar;C:\java\jdk1.8\jre\lib\jfr.jar;C:\java\jdk1.8\jre\lib\jfxswt.jar;C:\java\jdk1.8\jre\lib\jsse.jar;C:\java\jdk1.8\jre\lib\management-agent.jar;C:\java\jdk1.8\jre\lib\plugin.jar;C:\java\jdk1.8\jre\lib\resources.jar;C:\java\jdk1.8\jre\lib\rt.jar;D:\git\demo\BloomFilter\target\test-classes;D:\git\demo\BloomFilter\target\classes;C:\Users\Dora\.m2\repository\redis\clients\jedis\2.9.0\jedis-2.9.0.jar;C:\Users\Dora\.m2\repository\org\apache\commons\commons-pool2\2.4.2\commons-pool2-2.4.2.jar;C:\Users\Dora\.m2\repository\junit\junit\4.11\junit-4.11.jar;C:\Users\Dora\.m2\repository\org\hamcrest\hamcrest-core\1.3\hamcrest-core-1.3.jar;C:\Users\Dora\.m2\repository\org\apache\logging\log4j\log4j-core\2.11.0\log4j-core-2.11.0.jar;C:\Users\Dora\.m2\repository\org\apache\logging\log4j\log4j-api\2.11.0\log4j-api-2.11.0.jar" com.intellij.rt.execution.junit.JUnitStarter -ideVersion5 -junit4 com.ayl.distributedbloomfilter.DistributedRedisBloomFilterTest,filterTest
     * 20:47:35.814 [main] DEBUG com.ayl.distributedbloomfilter.builder.DistributedRedisBloomFilterBuilder - 计算出总位数M为:48134
     * 20:47:35.817 [main] DEBUG com.ayl.distributedbloomfilter.builder.DistributedRedisBloomFilterBuilder - 计算出子哈希函数个数为:5
     * 20:47:35.817 [main] DEBUG com.ayl.distributedbloomfilter.builder.DistributedRedisBloomFilterBuilder - 计算出哈希函数个数K为：3
     * add耗时：3326ms
     * 判断一个总共耗时：47ms
     * 总共耗时：53ms
     *  // 首先，这个单个元素的响应时间相比集中式，增加了50倍的响应时间，这里设置的单个子过滤器的长度为10000
     *  // 其次，看上去判断多个和判断一个的时间差别不大。这里的原因是因为什么？
     *
     */
    @Test
    public void filterTest() {
        super.filterTest();
    }

}
