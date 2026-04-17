package com.neu.youthpathtalk.post.biz.task;

import com.neu.youthpathtalk.constant.redis.PostRedisKey;
import com.neu.youthpathtalk.post.biz.cache.RedisService;
import com.neu.youthpathtalk.post.biz.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Julien
 * @time 2026/04/05 13:49
 * @description 浏览计数定时落库
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountSyncScheduler {
    private final RedisService redisService;
    private final PostMapper postMapper;

    @Scheduled(fixedDelay = 60*1000)
    public void syncViewCountToDb(){
        log.debug("开始同步浏览计数到数据库");
        long start=System.currentTimeMillis();

        redisService.scanViewCountKeys(entry->{
            String key= entry.getKey();
            Long viewCount=entry.getValue();
            Long postId=Long.parseLong(key.substring(PostRedisKey.allViewCounter().length()-1));
            try {
                int rows= postMapper.updateViewCountById(postId,viewCount);
                if (rows>0){
                    log.debug("同步成功:postId={},viewCount={}",postId,viewCount);
                }else {
                    log.warn("同步失败：帖子状态异常或浏览量没有新增,postId={}",postId);
                }
            } catch (Exception e) {
                log.error("更新数据库统计失败:postId={},viewCount={}",postId,viewCount,e);
            }
        });
        long elapsed=System.currentTimeMillis()-start;
        log.info("同步浏览计数完成，耗时{}ms",elapsed);
    }
}
