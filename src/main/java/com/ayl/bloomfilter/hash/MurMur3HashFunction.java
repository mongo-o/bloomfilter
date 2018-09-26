package com.ayl.bloomfilter.hash;/**
 * Created by 20160701006 on 18/6/26.
 */

import redis.clients.util.MurmurHash;

import java.util.ArrayList;
import java.util.List;

/**
 * User: AYL
 * Data: 18/6/26 15:20
 * Comment:MurMur3HashFunction
 */
public class MurMur3HashFunction extends HashFunction {
    @Override
    public List<Integer> hash(byte[] value, int m, int k) {
        List<Integer> hashlist = new ArrayList<>();
        int seed = 0;
        int pos = 0;
        while (pos < k) {
            seed = MurmurHash.hash(value, seed);
            int hash = rejectionSample(seed, m);
            if (hash != -1) {
                hashlist.add(hash);
                pos++;
            }
        }
        return hashlist;
    }
}
