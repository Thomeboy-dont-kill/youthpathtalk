package com.neu.youthpathtalk.youthpathtalkdistributedidgenerator.leaf;

import com.neu.youthpathtalk.youthpathtalkdistributedidgenerator.leaf.common.Result;

public interface IDGen {
    Result get(String key);
    boolean init();
}
