package com.neu.youthpathtalk.user.biz.task;

import com.neu.youthpathtalk.user.biz.config.IdempotentCleanupProperties;
import com.neu.youthpathtalk.user.biz.mapper.IdempotentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/04/01 16:31
 * @description 定时清理user_like_count_msg_idempotent
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotentCleanupScheduler {
    private final IdempotentMapper idempotentMapper;
    private final IdempotentCleanupProperties cleanupProperties;

    @Scheduled(cron = "${idempotent.cleanup.cron:0 0 3 * * ?}")
    public void cleanExpiredIdempotentRecords(){
        if (!cleanupProperties.isEnabled()){
            log.info("幂等表定时清理任务未启用");
            return;
        }

        LocalDateTime threshold=LocalDateTime.now()
                .minusDays(cleanupProperties.getRetainDays());
        int batchSize=cleanupProperties.getBatchSize();

        log.info("开始清理幂等表过期数据，保留天数:{},批次大小:{}",
                cleanupProperties.getRetainDays(),batchSize);

        int totalDeleted=0;
        int batchDeleted;
        do{
            try {
                batchDeleted= idempotentMapper.deleteExpiredRecords(threshold,batchSize);
                totalDeleted+=batchDeleted;
                if (batchDeleted>0){
                    log.debug("删除幂等记录批次{}条，累计删除{}",batchDeleted,totalDeleted);
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.error("清理幂等表失败",e);
                break;
            }
        }while (batchDeleted>=batchSize);
        log.info("清理幂等表完成，共删除{}条记录",totalDeleted);
    }
}
