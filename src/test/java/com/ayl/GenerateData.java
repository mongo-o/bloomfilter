package com.ayl;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author AYL    2018/7/3 8:14
 */
public class GenerateData {
    /**
     * 批量创建100000条测试数据到redis
     * @param args
     */
    public static void main(String[] args) {
        JedisPoolConfig config = new JedisPoolConfig();

        JedisPool jedisPool = new JedisPool(config,"192.168.11.89",6389);
        Jedis jedis = jedisPool.getResource();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 50000; i++) {
            jedis.set("String" + i, String.valueOf(i));
        }
        System.out.println("set total millis：" + (System.currentTimeMillis() - startTime));

        long startTime2 = System.currentTimeMillis();
        String[] msetKeyValues = new String[100000];
        for (int i = 0; i < 100000; i = i + 2 ) {
            msetKeyValues[i] = "mString" + i;
            msetKeyValues[i +1] = Integer.toString(i);
        }
        jedis.mset(msetKeyValues);
        System.out.println("mset total millis：" + (System.currentTimeMillis() - startTime2));

        jedis.close();

        /**
         * test result 500 key-values
         * set total millis：89
         * mset total millis：2
         *
         * test result 50000 key-values
         * set total millis：7940
         * mset total millis：153
         *
         * so:mset is more efficient
         */

    }

}
