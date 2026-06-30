package com.neu.youthpathtalk.post.biz.event.listener;

import com.neu.youthpathtalk.constant.redis.PostRedisKey;
import com.neu.youthpathtalk.post.biz.cache.RedisService;
import com.neu.youthpathtalk.post.biz.constants.CacheConstants;
import com.neu.youthpathtalk.post.biz.event.CommentHotScoreBatchChangedEvent;
import com.neu.youthpathtalk.post.biz.event.RootCommentCreatedEvent;
import com.neu.youthpathtalk.post.biz.event.RootCommentDeletedEvent;
import com.neu.youthpathtalk.post.biz.event.CommentHotScoreChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

/**
 * @author Julien
 * @time 2026/06/09 11:23
 * @description
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentHotRankListener {

    private final RedisService redisService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onScoreChanged(CommentHotScoreChangedEvent event) {

        String key = PostRedisKey.hotCommentRank(event.postId());

        redisService.updateHotCommentRank(
                key,
                event.commentId(),
                event.hotScore().doubleValue()
        );
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onRootCreated(RootCommentCreatedEvent event) {
        String key = PostRedisKey.hotCommentRank(event.postId());

        redisService.updateHotCommentRank(
                key,
                event.commentId(),
                0L
        );
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onRootDeleted(RootCommentDeletedEvent event) {
        String key = PostRedisKey.hotCommentRank(event.postId());

        redisService.zRem(
                key,
                event.commentId().toString()
        );
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onBatchChanged(
            CommentHotScoreBatchChangedEvent batchEvent
    ) {

        redisService.batchUpdateZSetTopN(
                batchEvent.events(),
                event -> PostRedisKey.hotCommentRank(
                        event.postId()
                ),
                event -> String.valueOf(
                        event.commentId()
                ),
                event -> event.hotScore().doubleValue(),
                CacheConstants.HOT_COMMENT_RANK_SIZE
        );
    }
}