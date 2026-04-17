package com.neu.youthpathtalk.post.biz.task;

import com.neu.youthpathtalk.post.biz.config.IdempotentCleanupProperties;
import com.neu.youthpathtalk.post.biz.mapper.IdempotentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/04/01 16:38
 * @description 定时清理post_like_count_msg_idempotent
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotentCleanupScheduler {
    private final IdempotentMapper idempotentMapper;
    private final IdempotentCleanupProperties cleanupProperties;

    @Scheduled(cron = "${idempotent.cleanup.cron:0 0 4 * * ?}")
    public void cleanExpiredIdempotentRecords(){
        if (!cleanupProperties.isEnabled()){
            log.info("幂等表清理定时任务未启用");
            return;
        }

        LocalDateTime threshold=LocalDateTime.now()
                .minusDays(cleanupProperties.getRetainDays());

        log.info("开始清理幂等表过期数据，保留天数:{},批次大小:{}",
                cleanupProperties.getRetainDays(),cleanupProperties.getBatchSize());

        int totalDeleted=0;
        int batchDeleted;
        do{
            try {
                batchDeleted=idempotentMapper.deleteExpiredRecords(threshold,cleanupProperties.getBatchSize());
                totalDeleted+=batchDeleted;
                if (batchDeleted>0){
                    log.debug("删除幂等记录批次{}条，累计删除{}",batchDeleted,totalDeleted);
                }
                Thread.sleep(100);
            }catch (Exception e){
                log.error("清理幂等表失败",e);
                break;
            }
        }while (batchDeleted>=cleanupProperties.getBatchSize());
        log.info("清理幂等表完成，共删除{}条记录",totalDeleted);
    }
}
