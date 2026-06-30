package com.neu.youthpathtalk.post.biz.cache;

import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.neu.youthpathtalk.constant.redis.PostRedisKey;
import com.neu.youthpathtalk.post.biz.constants.CacheConstants;
import com.neu.youthpathtalk.post.biz.dto.ToggleResultDTO;
import com.neu.youthpathtalk.post.biz.util.JsonUtils;
import com.neu.youthpathtalk.post.biz.vo.resp.CursorPageRespVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

/**
 * @author Julien
 * @time 2026/03/22 14:45
 * @description redis缓存服务
 */
@Slf4j
@Service
public class RedisService {
    private final JsonUtils jsonUtils;
    private final DefaultRedisScript<List> bitmapCounterToggleScript;
    private final DefaultRedisScript<Void> initPostInteractBitmapScript;
    private final DefaultRedisScript<Long> releaseLockScript;
    private final DefaultRedisScript<Long> incrementAndExpireScript;
    private final DefaultRedisScript<Void> addRecentViewScript;
    private final DefaultRedisScript<Long> addCommentScript;
    private final RedisTemplate<String,String> redisTemplate;

    public RedisService(JsonUtils jsonUtils,
                        @Qualifier("bitmapCounterToggleScript")DefaultRedisScript<List> bitmapCounterToggleScript,
                        @Qualifier("initPostInteractBitmapScript")DefaultRedisScript<Void> initPostInteractBitmapScript,
                        @Qualifier("releaseLockScript")DefaultRedisScript<Long> releaseLockScript,
                        @Qualifier("incrementAndExpireScript")DefaultRedisScript<Long> incrementAndExpireScript,
                        @Qualifier("addRecentViewScript")DefaultRedisScript<Void> addRecentViewScript,
                        @Qualifier("addCommentScript")DefaultRedisScript<Long> addCommentScript,
                        RedisTemplate<String, String> redisTemplate) {
        this.jsonUtils = jsonUtils;
        this.bitmapCounterToggleScript = bitmapCounterToggleScript;
        this.initPostInteractBitmapScript=initPostInteractBitmapScript;
        this.releaseLockScript = releaseLockScript;
        this.incrementAndExpireScript=incrementAndExpireScript;
        this.addRecentViewScript=addRecentViewScript;
        this.addCommentScript=addCommentScript;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 存储分页响应对象
     */
    public <T,C> void setCursorPage(String key, CursorPageRespVO<T,C> pageResp, long timeout, TimeUnit unit) {
        try {
            String json = jsonUtils.toJsonString(pageResp);
            redisTemplate.opsForValue().set(key, json, timeout, unit);
        } catch (Exception e) {
            log.error("RedisService setCursorPage失败, key: {}", key, e);
        }
    }

    /**
     * 获取分页响应对象（需要知道T的具体类型）
     */
    public <T,C> CursorPageRespVO<T,C> getCursorPage(String key,Class<T> elementType,Class<C> cursorType){
        String json = redisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return jsonUtils.parseGeneric(json,CursorPageRespVO.class,elementType,cursorType);
        } catch (Exception e) {
            log.error("RedisService getCursorPage反序列化失败, key: {}", key, e);
            throw new RuntimeException("RedisService getCursorPage error", e);
        }
    }

    public Long deleteLenient(List<String> keys){
        try {
            return redisTemplate.delete(keys);
        } catch (Exception e) {
            log.error("RedisService deleteLenient失败,keys:{}",keys,e);
            return null;
        }
    }
    public void deleteLenient(String key) {
        try {
            if (key == null || key.isEmpty()) {
                log.warn("RedisService deleteLenient 传入空 key，跳过");
                return;
            }
            Boolean result = redisTemplate.delete(key);
            if (Boolean.TRUE.equals(result)) {
                log.debug("RedisService deleteLenient 成功, key: {}", key);
            } else {
                log.warn("RedisService deleteLenient key 不存在: {}", key);
            }
        } catch (Exception e) {
            log.error("RedisService deleteLenient 失败, key: {}", key, e);
        }
    }

    @Retryable(
            retryFor = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 1.5
            )
    )
    public void deleteStrict(String key) {
        try {
            if (key == null || key.isEmpty()) {
                log.warn("RedisService deleteStrict 传入空 key，跳过");
                return;
            }
            Boolean result = redisTemplate.delete(key);
            if (Boolean.TRUE.equals(result)) {
                log.debug("RedisService deleteStrict 成功, key: {}", key);
            } else {
                log.warn("RedisService deleteStrict key 不存在: {}", key);
            }
        } catch (Exception e) {
            log.error("RedisService deleteStrict 失败, key: {}", key, e);
            throw new RuntimeException("RedisService deleteStrict error",e);
        }
    }

    @Retryable(
            retryFor = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 1.5
            )
    )

    public ToggleResultDTO toggleBitmapCounter(String bitKey, String countKey, Long userId){
        Preconditions.checkArgument(Objects.nonNull(bitKey)&&Objects.nonNull(countKey)&&Objects.nonNull(userId),"参数不能为空");
        List<String> keys= Arrays.asList(bitKey,countKey);
        try {
            List<Long> result=redisTemplate.execute(bitmapCounterToggleScript,keys,userId.toString());
            if (result==null||result.size()<2){
                return null;
            }
            Long state=result.get(0);
            Long count=result.get(1);
            return new ToggleResultDTO(state,count);
        } catch (Exception e) {
            log.error("RedisService interact 失败, error: {}", e.getMessage(), e);
            throw new RuntimeException("RedisService interact error",e);
        }
    }

    @Retryable(
            retryFor = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 1.5
            )
    )
    public void initPostInteractBitmap(String bitKey,Object[] args){
        Preconditions.checkArgument(Objects.nonNull(bitKey)&&Objects.nonNull(args),"参数不能为空");
        if (args.length==0){
            return;
        }
        try {
            redisTemplate.execute(initPostInteractBitmapScript,Collections.singletonList(bitKey),args);
        } catch (Exception e) {
            log.error("RedisService initPostInteractBitmap 失败, bitKey={}, userIds数量={}", bitKey, args.length, e);
            throw new RuntimeException("RedisService initPostInteractBitmap error", e);
        }
    }
    public void initEmptyBitmap(String bitKey) {
        try {
            // 设置偏移量 0 的位为 0，这会在 Redis 中创建该 key（如果不存在）
            redisTemplate.opsForValue().setBit(bitKey, 0, false);
        } catch (Exception e) {
            log.error("RedisService initEmptyBitmap 失败, bitKey={}", bitKey, e);
            throw new RuntimeException("RedisService initEmptyBitmap error", e);
        }
    }
    public void incrementLikeCount(String counterKey,int delta){
        try {
            Long newCount=redisTemplate.opsForValue().increment(counterKey,delta);
            redisTemplate.expire(counterKey, PostRedisKey.POST_LIKE_COUNTER_TTL,PostRedisKey.POST_LIKE_COUNTER_TTL_UNIT);
            log.debug("Redis 点赞计数器更新: counterKey={}, delta={}, newCount={}", counterKey, delta, newCount);
        } catch (Exception e) {
            log.error("Redis 点赞计数器更新失败: counterKey={}, delta={}", counterKey, delta, e);
        }
    }

    public Long incrementViewCount(String viewCountKey){
        try {
            Long newCount=redisTemplate.opsForValue().increment(viewCountKey);
            log.debug("RedisService incrementViewCount: viewCountKey={}, newCount={}", viewCountKey, newCount);
            return newCount;
        } catch (Exception e) {
            log.error("RedisService incrementViewCount 失败, viewCountKey={}",viewCountKey,e);
            throw new RuntimeException("RedisService incrementViewCount error", e);
        }
    }

    @Retryable(
            retryFor = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 1.5
            )
    )
    public Long incrementAndExpire(String key,long timeout,TimeUnit unit){
        try {
            long ttlSeconds = unit.toSeconds(timeout);
            return redisTemplate.execute(incrementAndExpireScript,Collections.singletonList(key),String.valueOf(ttlSeconds));
        } catch (Exception e) {
            log.error("RedisService incrementAndExpireScript 失败: key={}",key,e);
            throw new RuntimeException("RedisService incrementAndExpireScript error",e);
        }
    }
    public Long getAndDeleteLikeCount(String counterKey){
        try {
            String value=redisTemplate.opsForValue().getAndDelete(counterKey);
            return value==null?null:Long.parseLong(value);
        } catch (NumberFormatException e) {
            log.error("RedisService getAndDeleteLikeCount 失败: counterKey={}", counterKey, e);
            return null;
        }
    }

    public void scanLikeCounterKeys(Consumer<Map.Entry<String,Long>> consumer){
        ScanOptions options=ScanOptions.scanOptions()
                .match(PostRedisKey.allLikeCounter())
                .count(1000)
                .build();
        try(Cursor<String> cursor=redisTemplate.scan(options)){
            while (cursor.hasNext()){
                String key=cursor.next();
                Long value=getAndDeleteLikeCount(key);
                if (value!=null&&value!=0){
                    consumer.accept(new AbstractMap.SimpleEntry<>(key,value));
                }else {
                    log.warn("扫描到值为null的点赞计数键,key:{}",key);
                }
            }
        }catch (Exception e){
            log.error("扫描点赞计数键失败",e);
        }
    }

    public void scanViewCountKeys(Consumer<Map.Entry<String,Long>> consumer){
        ScanOptions options=ScanOptions.scanOptions()
                .match(PostRedisKey.allViewCounter())
                .count(1000)
                .build();
        try(Cursor<String> cursor=redisTemplate.scan(options)){
            while (cursor.hasNext()){
                String key=cursor.next();
                Long value=getLong(key);
                if (value!=null){
                    consumer.accept(new AbstractMap.SimpleEntry<>(key,value));
                }else {
                    log.warn("扫描到值为null的浏览计数键,key:{}",key);
                }
            }
        }catch (Exception e){
            log.error("扫描浏览计数键失败",e);
        }
    }

    @Retryable(
            retryFor = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 1.5
            )
    )
    public String tryLock(String lockKey,long timeout,TimeUnit unit){
        try {
            //唯一标识，防止误删
            String value=UUID.randomUUID().toString();
            Boolean success=redisTemplate.opsForValue().setIfAbsent(lockKey,value,timeout,unit);
            if (Boolean.TRUE.equals(success)){
                return value;
            }
            return null;
        } catch (Exception e) {
            log.error("RedisService tryLock 异常, lockKey: {}", lockKey, e);
            throw new RuntimeException("RedisService tryLock error", e);
        }
    }
    public boolean unLock(String lockKey,String value){
        try {
            Long result=redisTemplate.execute(releaseLockScript,Collections.singletonList(lockKey),value);
            return Long.valueOf(1).equals(result);
        } catch (Exception e) {
            log.error("RedisService unLock 异常, lockKey: {}, value: {}", lockKey, value, e);
            return false;
        }
    }

    public Boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("RedisService exists 失败, key: {}", key, e);
            throw new RuntimeException("RedisService exists error", e);
        }
    }

    public void set(String key,String value){
        try {
            redisTemplate.opsForValue().set(key,value);
        } catch (Exception e) {
            log.error("RedisService set 失败, key: {},value: {}", key, value, e);
            throw new RuntimeException("RedisService set error", e);
        }
    }

    // 重载，自动将任意对象转为字符串存储
    public void setStrict(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, String.valueOf(value));
        } catch (Exception e) {
            log.error("RedisService set 失败, key: {},value: {}", key, value, e);
            throw new RuntimeException("RedisService set error", e);
        }
    }
    public void setLenient(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, String.valueOf(value));
        } catch (Exception e) {
            log.error("RedisService set 失败, key: {},value: {}", key, value, e);
        }
    }

    //重载：带过期时间
    public void set(String key,String value,long timeout,TimeUnit unit){
        try {
            redisTemplate.opsForValue().set(key,value,timeout,unit);
        } catch (Exception e) {
            log.error("RedisService set 失败, key: {},value: {}", key, value, e);
            throw new RuntimeException("RedisService set error", e);
        }
    }

    // 重载，自动将任意对象转为字符串存储
    @Retryable(
            retryFor = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 1.5
            )
    )
    public void setStrict(String key, Object value,long timeout,TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, String.valueOf(value),timeout,unit);
        } catch (Exception e) {
            log.error("RedisService setStrict 失败, key: {},value: {}", key, value, e);
            throw new RuntimeException("RedisService setStrict error", e);
        }
    }

    // 重载，自动将任意对象转为字符串存储
    @Retryable(
            retryFor = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 1.5
            )
    )
    public void setStrict(String key, Object value,Duration duration) {
        try {
            redisTemplate.opsForValue().set(key, String.valueOf(value),duration);
        } catch (Exception e) {
            log.error("RedisService setStrict 失败, key: {},value: {}", key, value, e);
            throw new RuntimeException("RedisService setStrict error", e);
        }
    }

    public void setLenient(String key, Object value,long timeout,TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, String.valueOf(value),timeout,unit);
        } catch (Exception e) {
            log.error("RedisService setLenient 失败, key: {},value: {}", key, value, e);
        }
    }

    @Retryable(
            retryFor = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 1.5
            )
    )
    public Boolean expire(String key,long timeout,TimeUnit unit){
        try {
            return redisTemplate.expire(key,timeout,unit);
        } catch (Exception e) {
            log.error("RedisService expire 失败, key: {}", key, e);
            throw new RuntimeException("RedisService expire error", e);
        }
    }

    @Retryable(
            retryFor = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 1.5
            )
    )
    public Boolean expire(String key, Duration duration){
        try {
            return redisTemplate.expire(key,duration);
        } catch (Exception e) {
            log.error("RedisService expire 失败, key: {}", key, e);
            throw new RuntimeException("RedisService expire error", e);
        }
    }

    public String get(String key){
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("RedisService get 失败, key: {}", key, e);
            throw new RuntimeException("RedisService get error", e);
        }
    }

    public Boolean getBoolean(String key){
        try {
            String result = redisTemplate.opsForValue().get(key);
            return result ==null? null : Boolean.parseBoolean(result);
        } catch (Exception e) {
            log.error("RedisService getBoolean 失败, key: {}", key, e);
            throw new RuntimeException("RedisService getBoolean error", e);
        }
    }

    public Long getLong(String key){
        try {
            String result = redisTemplate.opsForValue().get(key);
            return result ==null? null : Long.parseLong(result);
        } catch (Exception e) {
            log.error("RedisService getLong 失败, key: {}", key, e);
            throw new RuntimeException("RedisService getLong error", e);
        }
    }

    public <T> T get(String key,Class<T> valueType){
        String json = redisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return jsonUtils.parseObject(json, valueType);
        } catch (Exception e) {
            log.error("RedisService get 反序列化失败, key: {}", key, e);
            throw new RuntimeException("RedisService get error", e);
        }
    }

    public void setJson(String key, Object value,long timeout,TimeUnit unit) {
        try {
            String json= jsonUtils.toJsonString(value);

            redisTemplate.opsForValue().set(key, json,timeout,unit);
        } catch (Exception e) {
            log.error("RedisService setJson 序列化失败, key: {},value: {}", key, value, e);
            throw new RuntimeException("RedisService setJson error", e);
        }
    }

    public void addRecentView(String viewHistoryKey, Long id, long timestamp, int limit, long ttlSeconds) {
        try {
            List<String> keys=Collections.singletonList(viewHistoryKey);
            List<Object> args=Arrays.asList(
                    String.valueOf(id),
                    String.valueOf(timestamp),
                    String.valueOf(limit),
                    String.valueOf(ttlSeconds)
            );
            redisTemplate.execute(addRecentViewScript,keys,args.toArray());
            log.debug("添加最近浏览记录成功,key:{},postId:{}",viewHistoryKey,id);
        } catch (Exception e) {
            log.error("RedisService addRecentView 失败, viewHistoryKey: {}", viewHistoryKey, e);
            throw new RuntimeException("RedisService addRecentView error", e);
        }
    }

    public void updateHotCommentRank(String hotCommentRankKey, Long id, double hotScore) {
        try {
            List<String> keys=Collections.singletonList(hotCommentRankKey);
            List<String> args=Arrays.asList(
                    String.valueOf(id),
                    String.valueOf(hotScore),
                    String.valueOf(CacheConstants.HOT_COMMENT_RANK_SIZE)
            );
            redisTemplate.execute(addCommentScript,keys,args.toArray());
            log.debug("添加评论成功,key:{},commentId:{}",hotCommentRankKey,id);
        } catch (Exception e) {
            log.error("RedisService addComment 失败, hotCommentRankKey: {}", hotCommentRankKey, e);
            throw new RuntimeException("RedisService addComment error", e);
        }
    }

    public Boolean zAdd(String key,double score,String value){
        try {
            return redisTemplate.opsForZSet().add(key,value,score);
        } catch (Exception e) {
            log.error("RedisService zAdd 失败,key:{}",key);
            throw new RuntimeException("RedisService zAdd error", e);
        }
    }

    public Long zRem(String key,String... values){
        try {
            return redisTemplate.opsForZSet().remove(key,values);
        } catch (Exception e) {
            log.error("RedisService zRem 失败,key:{},values:{}",key,Arrays.toString(values));
            throw new RuntimeException("RedisService zRem error", e);
        }
    }

    public void rename(String oldKey,String newKey){
        try {
            redisTemplate.rename(oldKey, newKey);
        } catch (Exception e) {
            log.error("RedisService rename 失败,oldKey:{},newKey:{}",oldKey,newKey);
            throw new RuntimeException("RedisService rename error", e);
        }
    }

    public Set<String> zRevRange(String key, long start, long end){
        try {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        } catch (Exception e) {
            log.error("RedisService zRevRange 失败,key:{},start:{},end:{}",key,start,end);
            throw new RuntimeException("RedisService zRevRange error",e);
        }
    }

    public Boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("RedisService setIfAbsent 失败, key: {}", key, e);
            throw new RuntimeException("RedisService setIfAbsent error", e);
        }
    }

    public Double zIncrBy(String key,double delta,String value){
        try {
            return redisTemplate.opsForZSet().incrementScore(key, value, delta);
        } catch (Exception e) {
            log.error("RedisService zIncrBy 失败, key: {},value:{},delta:{}", key,value,delta, e);
            throw new RuntimeException("RedisService zIncrBy error", e);
        }
    }

    public Double zScore(String key, String member) {
        try {
            return redisTemplate.opsForZSet().score(key, member);
        } catch (Exception e) {
            log.error("RedisService zScore失败,key:{},member:{}", key, member, e);
            throw new RuntimeException("RedisService zScore error", e);
        }
    }

    public Map<Long, Boolean> batchExistsInZSet(
            String key,
            List<Long> ids
    ) {

        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }

        List<Object> results =
                redisTemplate.executePipelined(
                        (RedisCallback<Object>) connection -> {

                            byte[] redisKey =
                                    redisTemplate.getStringSerializer()
                                            .serialize(key);

                            for (Long id : ids) {

                                connection.zScore(
                                        redisKey,
                                        redisTemplate
                                                .getStringSerializer()
                                                .serialize(String.valueOf(id))
                                );
                            }

                            return null;
                        }
                );

        Map<Long, Boolean> result =
                new HashMap<>(ids.size());

        for (int i = 0; i < ids.size(); i++) {

            result.put(
                    ids.get(i),
                    results.get(i) != null
            );
        }

        return result;
    }

    public List<Object> zRevRangePipeline(List<String> keys, long start, long end) {

        try {
            return redisTemplate.executePipelined((RedisCallback<Object>) connection -> {

                for (String key : keys) {
                    connection.zRevRange(
                            redisTemplate.getStringSerializer()
                                    .serialize(key),
                            start,
                            end
                    );
                }

                return null;
            });

        } catch (Exception e) {
            log.error("RedisService zRevRangePipeline失败, keys:{}", keys, e);
            throw new RuntimeException("RedisService zRevRangePipeline error", e);
        }
    }

    public <T> void batchUpdateZSetTopN(
            List<T> data,
            Function<T, String> keyFunc,
            Function<T, String> memberFunc,
            ToDoubleFunction<T> scoreFunc,
            long topN
    ) {

        if (CollectionUtils.isEmpty(data)) {
            return;
        }

        Map<String, List<T>> grouped =
                data.stream()
                        .collect(
                                Collectors.groupingBy(keyFunc)
                        );

        try {

            redisTemplate.executePipelined(
                    (RedisCallback<Object>) connection -> {

                        for (Map.Entry<String, List<T>> entry
                                : grouped.entrySet()) {

                            byte[] keyBytes =
                                    redisTemplate
                                            .getStringSerializer()
                                            .serialize(entry.getKey());

                            for (T item : entry.getValue()) {

                                connection.zAdd(
                                        keyBytes,
                                        scoreFunc.applyAsDouble(item),
                                        redisTemplate
                                                .getStringSerializer()
                                                .serialize(
                                                        memberFunc.apply(item)
                                                )
                                );
                            }

                            connection.zRemRange(
                                    keyBytes,
                                    0,
                                    -(topN + 1)
                            );
                        }

                        return null;
                    }
            );

        } catch (Exception e) {

            log.error("batchUpdateZSetTopN失败", e);

            throw new RuntimeException(e);
        }
    }
    public void expireAt(
            String key,
            Date expireTime
    ) {

        redisTemplate.expireAt(
                key,
                expireTime
        );
    }

    public void initBitmapCounterPipeline(
            String bitKey,
            String countKey,
            List<Long> userIds,
            Duration bitTtl,
            Duration countTtl
    ) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] bitKeyBytes = serializer.serialize(bitKey);
            byte[] countKeyBytes = serializer.serialize(countKey);
            if (CollectionUtils.isEmpty(userIds)) {
                connection.setBit(
                        bitKeyBytes,
                        0,
                        false
                );
            } else {
                for (Long userId : userIds) {

                    connection.setBit(
                            bitKeyBytes,
                            userId,
                            true
                    );
                }
            }
            connection.set(
                    countKeyBytes,
                    serializer.serialize(
                            String.valueOf(userIds.size())
                    )
            );
            connection.expire(bitKeyBytes, bitTtl.getSeconds());
            connection.expire(countKeyBytes, countTtl.getSeconds());
            return null;
        });
    }
}
