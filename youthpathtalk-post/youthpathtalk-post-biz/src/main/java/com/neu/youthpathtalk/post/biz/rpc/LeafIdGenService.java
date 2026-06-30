package com.neu.youthpathtalk.post.biz.rpc;

import com.neu.youthpathtalk.constant.LeafConstants;
import com.neu.youthpathtalk.distributed.id.generator.client.DistributedIdGenFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Julien
 * @time 2026/03/09 20:46
 * @description 远程调用Leaf分布式ID生成服务
 */
@Component
@RequiredArgsConstructor
public class LeafIdGenService {
    private final DistributedIdGenFeignClient distributedIdGenFeignClient;
    public Long generatePostId(){
        String idStr= distributedIdGenFeignClient.getSegmentId(LeafConstants.LEAF_SEGMENT_POST);
        return Long.parseLong(idStr);
    }
    public Long generateCommentId(){
        String idStr= distributedIdGenFeignClient.getSegmentId(LeafConstants.LEAF_SEGMENT_COMMENT);
        return Long.parseLong(idStr);
    }
}
