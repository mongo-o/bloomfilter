package com.ayl.util.hash;/**
 * Created by 20160701006 on 18/6/26.
 */

import java.util.List;

/**
 * User: AYL
 * Data: 18/6/26 14:24
 * Comment:HashFunction
 */
public abstract class HashFunction {

    /**
     * 计算hash值
     * @param value 进行计算hash的输入源对象的二进制数组
     * @param m hash散列长度范围
     * @param k hash函数次数
     * @return
     */
    public abstract List<Integer> hash(byte[] value, int m, int k);

    /**
     * 计算64位hash值
     * @param value 进行计算hash的输入源对象的二进制数组
     * @param m hash散列长度范围
     * @param k hash函数次数
     * @return
     */
    public abstract List<Integer> hash64(byte[] value, long m, int k);

    /**
     *
     * @param random int
     * @param m integer output range [1,size]
     * @return
     */
    protected int rejectionSample(int random, int m) {
        //返回random的绝对值
        random = Math.abs(random);
        //2147483647 = 2^31 - 1 = Integer.MAX_VALUE
        //redis中的一个bitmap的最大容量是512M，也就是 (2^32) = 4294967296 bit
        //这个if的判断条件是在过滤什么数据呢？
        if (random > (2147483647 - 2147483647 % m) || random == Integer.MIN_VALUE)
            return -1;
        else
            return random % m;
    }
}
