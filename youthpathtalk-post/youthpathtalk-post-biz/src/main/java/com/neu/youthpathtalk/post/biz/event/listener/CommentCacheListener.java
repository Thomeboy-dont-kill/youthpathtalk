package com.neu.youthpathtalk.post.biz.event.listener;

import com.neu.youthpathtalk.constant.redis.PostRedisKey;
import com.neu.youthpathtalk.post.biz.cache.RedisService;
import com.neu.youthpathtalk.post.biz.enums.PageSizeEnum;
import com.neu.youthpathtalk.post.biz.event.CommentPageCacheEvictEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Arrays;
import java.util.List;

/**
 * @author Julien
 * @time 2026/06/08 20:18
 * @description
 */
@Component
@RequiredArgsConstructor
public class CommentCacheListener {
    private final RedisService redisService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void onEvict(CommentPageCacheEvictEvent event) {
        List<String> keys =
                Arrays.stream(PageSizeEnum.values())
                        .map(size ->
                                PostRedisKey.firstCommentPage(
                                        event.postId(),
                                        size.getCode()
                                )
                        )
                        .toList();

        redisService.deleteLenient(keys);
    }
}
