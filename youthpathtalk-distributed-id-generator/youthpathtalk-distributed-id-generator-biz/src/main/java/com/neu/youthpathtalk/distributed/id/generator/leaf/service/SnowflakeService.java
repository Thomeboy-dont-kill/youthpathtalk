package com.neu.youthpathtalk.distributed.id.generator.leaf.service;

import com.google.common.base.Preconditions;
import com.neu.youthpathtalk.distributed.id.generator.leaf.exception.InitException;
import com.neu.youthpathtalk.distributed.id.generator.leaf.snowflake.SnowflakeIDGenImpl;
import com.neu.youthpathtalk.distributed.id.generator.leaf.IDGen;
import com.neu.youthpathtalk.distributed.id.generator.leaf.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnowflakeService {
    private Logger logger = LoggerFactory.getLogger(SnowflakeService.class);

    private IDGen idGen;
    public SnowflakeService(String zkpath,int port) throws InitException {
            Preconditions.checkNotNull(zkpath,"zookeeper path can not be null");
            Preconditions.checkNotNull(port,"zookeeper port  can not be null");
            idGen = new SnowflakeIDGenImpl(zkpath, port);
            if(idGen.init()) {
                logger.info("Snowflake Service Init Successfully");
            } else {
                throw new InitException("Snowflake Service Init Fail");
            }
    }

    public Result getId(String key) {
        return idGen.get(key);
    }
}
