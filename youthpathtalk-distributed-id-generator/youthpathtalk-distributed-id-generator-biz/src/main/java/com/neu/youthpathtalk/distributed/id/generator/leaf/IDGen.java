package com.neu.youthpathtalk.distributed.id.generator.leaf;

import com.neu.youthpathtalk.distributed.id.generator.leaf.common.Result;

public interface IDGen {
    Result get(String key);
    boolean init();
}
