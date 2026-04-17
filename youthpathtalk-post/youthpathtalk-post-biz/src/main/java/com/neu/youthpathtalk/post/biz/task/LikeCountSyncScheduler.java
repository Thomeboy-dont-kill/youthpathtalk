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
 * @time 2026/03/24 14:21
 * @description 点赞数定时落库
 */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class LikeCountSyncScheduler {
//    private final RedisService redisService;
//    private final PostMapper postMapper;
//
//    @Scheduled(fixedDelay = 60*1000)
//    public void syncLikeCountToDb(){
//        log.debug("开始同步点赞计数到数据库");
//        long start=System.currentTimeMillis();
//
//        redisService.scanLikeCounterKeys(entry->{
//            String key= entry.getKey();
//            Long delta=entry.getValue();
//            Long postId=Long.parseLong(key.substring(PostRedisKey.allLikeCounter().length()-1));
//            try {
//                int rows= postMapper.updateLikeCountById(postId,delta);
//                if (rows>0){
//                    log.debug("同步成功:postId={},delta={}",postId,delta);
//                }else {
//                    log.warn("同步失败：帖子状态异常,postId={}",postId);
//                }
//            } catch (Exception e) {
//                log.error("更新数据库统计失败:postId={},delta={}",postId,delta,e);
//            }
//        });
//        long elapsed=System.currentTimeMillis()-start;
//        log.info("同步点赞计数完成，耗时{}ms",elapsed);
//    }
//}
