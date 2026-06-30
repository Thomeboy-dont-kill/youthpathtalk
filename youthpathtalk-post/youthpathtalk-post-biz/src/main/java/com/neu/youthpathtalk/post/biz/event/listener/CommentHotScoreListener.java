package com.neu.youthpathtalk.post.biz.event.listener;

import com.neu.youthpathtalk.post.biz.dto.CommentHotScoreInfoDTO;
import com.neu.youthpathtalk.post.biz.event.CommentHotScoreChangedEvent;
import com.neu.youthpathtalk.post.biz.event.CommentHotScoreRebuildEvent;
import com.neu.youthpathtalk.post.biz.mapper.CommentMapper;
import com.neu.youthpathtalk.post.biz.util.CommentHotScoreUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;

/**
 * @author Julien
 * @time 2026/06/04 17:12
 * @description
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentHotScoreListener {

    private final CommentMapper commentMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    //后面压力大可以考虑异步和去重
    @TransactionalEventListener(
            phase =
                    TransactionPhase.AFTER_COMMIT
    )
    public void onRebuild(
            CommentHotScoreRebuildEvent event
    ) {
        try {

            rebuildHotScore(
                    event
            );

        } catch (Exception e) {

            log.error(
                    "更新评论热度失败, rootCommentId={}",
                    event.rootCommentId(),
                    e
            );
        }
    }

    @Transactional
    public void rebuildHotScore(
            CommentHotScoreRebuildEvent event
    ) {
        Long commentId= event.rootCommentId();

        CommentHotScoreInfoDTO dto =
                commentMapper
                        .selectHotScoreInfoById(
                                commentId
                        );

        if(dto == null){
            return;
        }

        BigDecimal score =
                CommentHotScoreUtils.calculate(
                        dto.getLikeCount(),
                        dto.getReplyCount(),
                        dto.getCreateTime()
                );

        commentMapper.updateHotScore(
                commentId,
                score
        );

        applicationEventPublisher.publishEvent(
                new CommentHotScoreChangedEvent(
                        event.postId(),
                        score,
                        event.rootCommentId()
                )
        );
    }
}