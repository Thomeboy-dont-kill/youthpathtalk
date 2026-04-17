package com.neu.youthpathtalk.distributed.id.generator.client;

import com.neu.youthpathtalk.distributed.id.generator.constant.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Julien
 * @time 2026/03/08 10:15
 * @description 供远程调用美团开源leaf获取分布式ID的FeignClient
 */
@FeignClient(
        name = ApiConstants.SERVICE_NAME,
        path = "/id"
)
public interface DistributedIdGenFeignClient {
    /**
     * 获取号段模式的分布式ID
     * @param key 业务标识
     * @return 分布式ID
     */
    @RequestMapping(value = "/segment/get/{key}")
    String getSegmentId(@PathVariable("key") String key);
//    /**
//     * 获取雪花算法的分布式ID
//     * @param key 雪花算法不需要传key
//     * @return 分布式ID
//     * 如果需要，取消注释即可使用
//     */
//    @RequestMapping(value = "/snowflake/get/{key}")
//    String getSnowflakeId(@PathVariable("key") String key);
}
