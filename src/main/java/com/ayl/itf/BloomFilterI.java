package com.ayl.itf;/**
 * Created by 20160701006 on 18/6/26.
 */

import java.util.List;
import java.util.Map;

/**
 * User: AYL
 * Data: 18/6/26 8:50
 * Comment:BloomFilterI
 */
public interface BloomFilterI<T> {
    boolean add(T object);

    Map<T, Boolean> batchAdd(List<T> targetObjects);

    boolean contains(T object);
}
