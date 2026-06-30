package com.neu.youthpathtalk.post.biz.task;

import com.neu.youthpathtalk.constant.redis.PostRedisKey;
import com.neu.youthpathtalk.post.biz.cache.RedisService;
import com.neu.youthpathtalk.post.biz.config.PostCleanupProperties;
import com.neu.youthpathtalk.post.biz.constants.MQConstants;
import com.neu.youthpathtalk.post.biz.dto.PostDeleteInfoDTO;
import com.neu.youthpathtalk.post.biz.mapper.*;
import com.neu.youthpathtalk.post.biz.message.UserLikeCountDecrMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Julien
 * @time 2026/03/23 14:07
 * @description 定时清理软删除状态的帖子
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostCleanupScheduler {
    private final PostMapper postMapper;
    private final RedisService redisService;
    private final CommentMapper commentMapper;
    private final RocketMQTemplate rocketMQTemplate;
    private final TransactionTemplate transactionTemplate;
    private final PostLikeRecordMapper postLikeRecordMapper;
    private final FavoriteRecordMapper favoriteRecordMapper;
    private final PostCleanupProperties cleanupProperties;
    private final CommentLikeRecordMapper commentLikeRecordMapper;

    @Scheduled(cron = "${post.cleanup.cron:0 0 3 * * ?}")
    public void cleanSoftDeletedPosts(){
        if (!cleanupProperties.isEnabled()){
            log.info("帖子清理定时任务未启用");
            return;
        }
        log.info("开始清理软删除帖子，保留天数:{},批次大小:{}",
                cleanupProperties.getRetainDays(),cleanupProperties.getBatchSize());
        LocalDateTime thresholdDate=LocalDateTime.now()
                .minusDays(cleanupProperties.getRetainDays());
        int batchSize=cleanupProperties.getBatchSize();
        int totalDeleted=0;
        List<Long> postIdsBatch=new ArrayList<>(batchSize);
        Map<Long,Long> userDeltaBatch=new HashMap<>();
        try(Cursor<PostDeleteInfoDTO> cursor= postMapper.selectExpiredPostsStream(thresholdDate)) {
            for (PostDeleteInfoDTO dto:cursor){
                postIdsBatch.add(dto.getId());
                userDeltaBatch.merge(dto.getUserId(), dto.getLikeCount()==null?0L: dto.getLikeCount(), Long::sum);
                if (postIdsBatch.size()>=batchSize){
                    executeBatch(postIdsBatch,userDeltaBatch);
                    totalDeleted+=postIdsBatch.size();
                    log.debug("已删除{}条，累计删除{}", postIdsBatch.size(),totalDeleted);
                    postIdsBatch.clear();
                    userDeltaBatch.clear();
                }
            }
            if (!postIdsBatch.isEmpty()){
                executeBatch(postIdsBatch,userDeltaBatch);
                totalDeleted+=postIdsBatch.size();
            }
        }catch (Exception e){
            log.error("清除软删除帖子失败",e);
        }
        log.info("清理软删除帖子完成，共删除{}条",totalDeleted);
    }
    private void executeBatch(List<Long> postIds,Map<Long,Long> userDeltas){
        transactionTemplate.execute(status -> {
            try {
                commentLikeRecordMapper.deleteByPostIds(postIds);
                commentMapper.deleteByPostIds(postIds);
                postLikeRecordMapper.deleteByPostIds(postIds);
                favoriteRecordMapper.deleteByPostIds(postIds);
                //删除收藏记录，后续添加了收藏记录表再补充
                int postsDeleted=postMapper.physicalDeleteByIds(postIds);
                if (postsDeleted!=postIds.size()){
                    log.error("批量删除帖子数量不一致,expected={},provided={}",postIds.size(),postsDeleted);
                    throw new RuntimeException("批量删除帖子数量不一致");
                }
                return null;
            } catch (RuntimeException e) {
                log.error("批量删除失败，事务回滚",e);
                status.setRollbackOnly();
                throw e;
            }
        });
        List<String> keys = new ArrayList<>();
        keys.addAll(
                postIds.stream()
                        .map(PostRedisKey::viewCount)
                        .toList()
        );
        keys.addAll(
                postIds.stream()
                        .map(PostRedisKey::hotCommentRank)
                        .toList()
        );
        redisService.deleteLenient(keys);
        if (!userDeltas.isEmpty()){
            try {
                String messageId= UUID.randomUUID().toString();
                UserLikeCountDecrMessage message=new UserLikeCountDecrMessage(messageId,userDeltas);
                String destination= MQConstants.TOPIC_USER_LIKE_COUNT_DECREMENT;
                rocketMQTemplate.asyncSend(destination, message, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.debug("用户获赞数减少消息发送成功，userDeltas:{}",userDeltas);
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        log.error("用户获赞数减少消息发送失败,userDeltas:{}",userDeltas,throwable);
                        //可写入本地失败表，定时重试，暂时不实现
                    }
                });
            } catch (Exception e) {
                log.error("发送消息异常",e);
            }
        }
    }
}
