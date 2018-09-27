# bloomfilter
问题1：<br>
```Java
    /**
     * 计算过滤器的M的位数
     * @return
     */
    private long optimalNumsOfBits() {
        if (falseProbability == 0) {
            falseProbability = Double.MIN_VALUE;
        }

        //如果这里计算的totalSize超过long的表示范围呢？这个情况有没有可能呢？
        long totalSize = (long) (-insertions * Math.log(falseProbability) / (Math.log(2) * Math.log(2)));
        return totalSize;
    }
```
