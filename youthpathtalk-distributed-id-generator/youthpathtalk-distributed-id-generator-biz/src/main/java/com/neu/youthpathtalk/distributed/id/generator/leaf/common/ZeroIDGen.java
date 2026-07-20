package com.neu.youthpathtalk.distributed.id.generator.leaf.common;

import com.neu.youthpathtalk.distributed.id.generator.leaf.IDGen;

public class ZeroIDGen implements IDGen {
    @Override
    public Result get(String key) {
        return new Result(0, Status.SUCCESS);
    }

    @Override
    public boolean init() {
        return true;
    }
}
