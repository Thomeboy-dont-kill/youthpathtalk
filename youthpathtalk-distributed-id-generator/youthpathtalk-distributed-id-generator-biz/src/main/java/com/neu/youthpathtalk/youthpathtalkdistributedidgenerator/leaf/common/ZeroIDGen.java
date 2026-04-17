package com.neu.youthpathtalk.youthpathtalkdistributedidgenerator.leaf.common;

import com.neu.youthpathtalk.youthpathtalkdistributedidgenerator.leaf.IDGen;

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
