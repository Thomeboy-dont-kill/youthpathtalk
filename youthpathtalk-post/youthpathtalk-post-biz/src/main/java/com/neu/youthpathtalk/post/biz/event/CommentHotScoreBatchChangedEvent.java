package com.neu.youthpathtalk.post.biz.event;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Julien
 * @time 2026/06/09 17:45
 * @description
 */
public record CommentHotScoreBatchChangedEvent(
        List<CommentHotScoreChangedEvent> events
) {

}