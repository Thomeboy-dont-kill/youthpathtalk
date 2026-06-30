package com.neu.youthpathtalk.post.biz.task;

import com.neu.youthpathtalk.post.biz.config.CommentHotScoreProperties;
import com.neu.youthpathtalk.post.biz.dto.CommentHotScoreRecalculateDTO;
import com.neu.youthpathtalk.post.biz.dto.CommentHotScoreUpdateDTO;
import com.neu.youthpathtalk.post.biz.event.CommentHotScoreBatchChangedEvent;
import com.neu.youthpathtalk.post.biz.event.CommentHotScoreChangedEvent;
import com.neu.youthpathtalk.post.biz.mapper.CommentMapper;
import com.neu.youthpathtalk.post.biz.util.CommentHotScoreUtils;
import com.neu.youthpathtalk.post.biz.vo.cursor.CreateTimeIdCursor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Julien
 * @time 2026/06/04 18:25
 * @description
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentHotScoreScheduler {
    private final CommentMapper commentMapper;
    private final CommentHotScoreProperties properties;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Scheduled(cron = "#{@commentHotScoreProperties.cron}")
    @Transactional
    public void recalculateHotScore() {

        LocalDateTime startTime =
                LocalDateTime.now()
                        .minusDays(
                                properties.getRecalculateDays()
                        );

        CreateTimeIdCursor cursor = null;

        int batchSize =
                properties.getBatchSize();

        while (true) {

            List<CommentHotScoreRecalculateDTO> comments =
                    commentMapper.selectForRecalculate(
                            startTime,
                            cursor,
                            batchSize
                    );

            if (CollectionUtils.isEmpty(comments)) {
                break;
            }

            List<CommentHotScoreUpdateDTO> updates =
                    comments.stream()
                            .map(comment -> {

                                BigDecimal score =
                                        CommentHotScoreUtils.calculate(
                                                comment.getLikeCount(),
                                                comment.getReplyCount(),
                                                comment.getCreateTime()
                                        );

                                return CommentHotScoreUpdateDTO.builder()
                                        .id(comment.getId())
                                        .hotScore(score)
                                        .build();

                            })
                            .toList();

            if (!updates.isEmpty()) {
                commentMapper.batchUpdateHotScore(updates);
            }
            List<CommentHotScoreChangedEvent> events=new ArrayList<>(comments.size());
            for (int i = 0; i < comments.size(); i++) {

                events.add(
                        new CommentHotScoreChangedEvent(
                                comments.get(i).getPostId(),
                                updates.get(i).getHotScore(),
                                comments.get(i).getId()
                        )
                );
            }
            applicationEventPublisher.publishEvent(
                    new CommentHotScoreBatchChangedEvent(
                            events
                    )
            );
            CommentHotScoreRecalculateDTO last =
                    comments.get(comments.size() - 1);

            cursor = new CreateTimeIdCursor(
                    last.getCreateTime(),
                    last.getId()
            );
        }
    }
}
