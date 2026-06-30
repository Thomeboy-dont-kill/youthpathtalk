package com.neu.youthpathtalk.post.biz.task;

import com.neu.youthpathtalk.constant.redis.PostRedisKey;
import com.neu.youthpathtalk.constant.redis.RedisConstants;
import com.neu.youthpathtalk.enums.CommonResponseErrorCode;
import com.neu.youthpathtalk.exception.BizException;
import com.neu.youthpathtalk.post.biz.cache.RedisService;
import com.neu.youthpathtalk.post.biz.config.HotBoardProperties;
import com.neu.youthpathtalk.post.biz.constants.CacheConstants;
import com.neu.youthpathtalk.post.biz.dto.PostHotScoreDTO;
import com.neu.youthpathtalk.post.biz.enums.BoardType;
import com.neu.youthpathtalk.post.biz.mapper.PostMapper;
import com.neu.youthpathtalk.post.biz.util.JsonUtils;
import com.neu.youthpathtalk.post.biz.vo.resp.HotBoardItemVO;
import com.neu.youthpathtalk.post.biz.vo.resp.PostDetailRespVO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Julien
 * @time 2026/04/09 11:01
 * @description 定时全量同步热榜，注意多实例并发问题
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HotBoardScheduler {
    private final JsonUtils jsonUtils;
    private final PostMapper postMapper;
    private final RedisService redisService;
    private final HotBoardProperties hotBoardProperties;
    @Resource(name="taskExecutor")
    private Executor taskExecutor;

    @Scheduled(cron = "#{@hotBoardProperties.cron}")
    public void refreshHotBoard() {
        LocalDateTime thresholdDate = LocalDateTime.now()
                .minusHours(hotBoardProperties.getTimeWindowHours());

        long now = System.currentTimeMillis();
        int batchSize = hotBoardProperties.getBatchSize();
        Long lastId = 0L;
        PriorityQueue<Map.Entry<Long, Double>> minHeap = new PriorityQueue<>(
                hotBoardProperties.getBoardSize(), Comparator.comparingDouble(Map.Entry::getValue)
        );

        while (true) {
            List<PostHotScoreDTO> batch = postMapper.selectByCreateTimeAndIdCursor(thresholdDate, lastId, batchSize);
            if (batch.isEmpty()) {
                break;
            }
            for (PostHotScoreDTO dto : batch) {
                lastId = dto.getId();
                double score = calculateHotScore(dto, now);
                if (minHeap.size() < hotBoardProperties.getBoardSize()) {
                    minHeap.offer(new AbstractMap.SimpleEntry<>(dto.getId(), score));
                } else if (score > minHeap.peek().getValue()) {
                    minHeap.poll();
                    minHeap.offer(new AbstractMap.SimpleEntry<>(dto.getId(), score));
                }
            }
        }

        List<Map.Entry<Long, Double>> topEntriesDesc = minHeap.stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .collect(Collectors.toList());
        if (topEntriesDesc.isEmpty()) {
            return;
        }
        List<Long> topIds=topEntriesDesc.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<HotBoardItemVO> hotBoardItems=postMapper.selectHotBoardByIds(topIds);
        Map<Long, HotBoardItemVO> itemMap=hotBoardItems.stream()
                .collect(Collectors.toMap(HotBoardItemVO::getId, Function.identity()));
        List<HotBoardItemVO> sortedList=topIds.stream()
                .map(itemMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        String hotBoardJson= jsonUtils.toJsonString(sortedList);
        String hotBoardTempKey = PostRedisKey.hotBoardTemp();
        try {
            redisService.deleteLenient(hotBoardTempKey);
            redisService.set(hotBoardTempKey,hotBoardJson);
            redisService.rename(hotBoardTempKey, PostRedisKey.hotBoard());
            log.debug("热榜刷新完成，共{}条", sortedList.size());
        } catch (Exception e) {
            log.error("添加热榜失败，放弃本次更新，旧数据保持不变", e);
            redisService.deleteLenient(hotBoardTempKey);
        }
        taskExecutor.execute(()->{
            List<PostDetailRespVO> details =
                    postMapper.selectByIds(topIds);

            Map<Long, PostDetailRespVO> detailMap =
                    details.stream()
                            .collect(
                                    Collectors.toMap(
                                            PostDetailRespVO::getId,
                                            Function.identity()
                                    )
                            );
            for(Long postId:topIds){
                try {
                    PostDetailRespVO detail =
                            detailMap.get(postId);
                    if(detail == null){
                        continue;
                    }
                    String viewHotKey=PostRedisKey.viewHot(postId);
                    if (!redisService.exists(viewHotKey)) {

                        long randomExtra = ThreadLocalRandom.current().nextLong(0, RedisConstants.MAX_RANDOM_OFFSET);
                        long timeout=PostRedisKey.POST_VIEW_HOT_TTL + randomExtra;
                        TimeUnit unit=PostRedisKey.POST_VIEW_HOT_TTL_UNIT;
                        detail.setBoardTypeName(BoardType.getBoardTypeName(detail.getBoardType()));
                        redisService.setJson(
                                viewHotKey,
                                detail,
                                timeout,
                                unit
                        );
                        String viewHourlyKey = PostRedisKey.viewHourly(postId);
                        redisService.set(
                                viewHourlyKey,
                                String.valueOf(CacheConstants.HOT_THRESHOLD),
                                timeout,
                                unit
                        );
                    }
                } catch (Exception e) {
                    log.error("预热帖子详情失败，postId={}",postId,e);
                }
            }
        });
    }

    private double calculateHotScore(PostHotScoreDTO dto, long now) {
        ZoneId zone = ZoneId.systemDefault();
        long createTime = dto.getCreateTime().atZone(zone).toInstant().toEpochMilli();
        long ageHours = (now - createTime) / (1000 * 3600);
        double decay = Math.pow(hotBoardProperties.getDecayBase(), ageHours);
        HotBoardProperties.ScoreWeight weight = hotBoardProperties.getWeight();
        double baseScore = dto.getViewCount() * weight.getView()
                + dto.getLikeCount() * weight.getLike()
                + dto.getCommentCount() * weight.getComment()
                + dto.getFavoriteCount() * weight.getFavorite();
        return baseScore * decay;
    }
}