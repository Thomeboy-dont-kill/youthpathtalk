package com.neu.youthpathtalk.post.biz.service.impl;

import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.neu.youthpathtalk.constant.redis.PostRedisKey;
import com.neu.youthpathtalk.constant.redis.RedisConstants;
import com.neu.youthpathtalk.constant.redis.UserRedisKey;
import com.neu.youthpathtalk.enums.CommonResponseErrorCode;
import com.neu.youthpathtalk.exception.BizException;
import com.neu.youthpathtalk.holder.LoginUserContextHolder;
import com.neu.youthpathtalk.post.biz.cache.LocalCacheManager;
import com.neu.youthpathtalk.post.biz.cache.RedisService;
import com.neu.youthpathtalk.post.biz.cache.pubsub.CacheInvalidatePublisher;
import com.neu.youthpathtalk.post.biz.constants.CacheConstants;
import com.neu.youthpathtalk.post.biz.constants.CommentHotScoreConstants;
import com.neu.youthpathtalk.post.biz.constants.MQConstants;
import com.neu.youthpathtalk.post.biz.constants.WeeklyRankConstants;
import com.neu.youthpathtalk.post.biz.dto.CommentHotDTO;
import com.neu.youthpathtalk.post.biz.dto.ToggleResultDTO;
import com.neu.youthpathtalk.post.biz.dto.PostBasicInfoDTO;
import com.neu.youthpathtalk.post.biz.entity.PostDO;
import com.neu.youthpathtalk.post.biz.enums.BizResponseErrorCode;
import com.neu.youthpathtalk.post.biz.enums.BoardType;
import com.neu.youthpathtalk.post.biz.enums.PageSizeEnum;
import com.neu.youthpathtalk.post.biz.event.PostFavoriteEvent;
import com.neu.youthpathtalk.post.biz.mapper.CommentMapper;
import com.neu.youthpathtalk.post.biz.mapper.FavoriteRecordMapper;
import com.neu.youthpathtalk.post.biz.mapper.PostLikeRecordMapper;
import com.neu.youthpathtalk.post.biz.mapper.PostMapper;
import com.neu.youthpathtalk.post.biz.message.InteractCompensateMessage;
import com.neu.youthpathtalk.post.biz.event.PostLikeEvent;
import com.neu.youthpathtalk.post.biz.richtext.model.RichTextDoc;
import com.neu.youthpathtalk.post.biz.richtext.service.RichTextService;
import com.neu.youthpathtalk.post.biz.rpc.LeafIdGenService;
import com.neu.youthpathtalk.post.biz.rpc.UserRpcService;
import com.neu.youthpathtalk.post.biz.service.PostService;
import com.neu.youthpathtalk.post.biz.util.JsonUtils;
import com.neu.youthpathtalk.post.biz.vo.req.PostListReqVO;
import com.neu.youthpathtalk.post.biz.vo.req.PostReqVO;
import com.neu.youthpathtalk.post.biz.vo.req.PostUpdateReqVO;
import com.neu.youthpathtalk.post.biz.vo.resp.*;
import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.user.api.vo.resp.UserInfoRespVO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Julien
 * @time 2026/03/21 11:55
 * @description PostService实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final LeafIdGenService leafIdGenService;
    private final UserRpcService userRpcService;
    private final PostMapper postMapper;
    private final JsonUtils jsonUtils;
    private final RedisService redisService;
    private final CommentMapper commentMapper;
    private final RichTextService richTextService;
    private final RocketMQTemplate rocketMQTemplate;
    private final PostLikeRecordMapper postLikeRecordMapper;
    private final FavoriteRecordMapper favoriteRecordMapper;
    private final LocalCacheManager localCacheManager;
    private final CacheInvalidatePublisher cacheInvalidatePublisher;
    @Resource(name="taskExecutor")
    private Executor taskExecutor;

    private final AtomicBoolean isDeleting = new AtomicBoolean(false);

    @Override
    public Response<String> getPostTitle(Long id) {

        if (id == null) {
            return Response.ok();
        }

        return Response.ok(
                postMapper.selectTitleById(id)
        );
    }

    @Override
    public Response<?> addPost(PostReqVO postReqVO) {
        Long id=leafIdGenService.generatePostId();
        Long userId= LoginUserContextHolder.getUserId();
        if (Objects.isNull(userId)){
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }
        UserInfoRespVO userInfoRespVO=userRpcService.getUserInfo(userId);
        RichTextDoc doc =
                jsonUtils.parseObject(postReqVO.getContent(), RichTextDoc.class);
        RichTextService.ProcessResult result =
                richTextService.process(doc);
        String contentJson =
                jsonUtils.toJsonString(result.getDoc());
        PostDO postDO = new PostDO()
                .setId(id)
                .setUserId(userId)
                .setUsername(userInfoRespVO.getUsername())
                .setUserAvatar(userInfoRespVO.getUserAvatar())
                .setUniversityId(userInfoRespVO.getUniversityId())
                .setUniversityName(userInfoRespVO.getUniversityName())
                .setBoardType(postReqVO.getBoardType().getType())
                .setTitle(postReqVO.getTitle())
                .setContent(contentJson)
                .setPlainText(result.getPlainText());
        int rows=postMapper.insertSelective(postDO);
        if (rows>0) {
            deleteAllFirstPageCachesAsync();
            String existsKey=PostRedisKey.exists(id);
            try {
                redisService.deleteStrict(existsKey);
            } catch (Exception e) {
                log.error("删除帖子存在性缓存失败，existsKey:{}",existsKey,e);
            }
            asyncUpdateWeeklyRank(
                    userId,
                    WeeklyRankConstants.WEEKLY_RANK_POST_SCORE
            );
            localCacheManager.invalidateExists(id);
            cacheInvalidatePublisher.publishExistsInvalidate(Collections.singletonList(id));
        }
        return Response.ok();
    }

    @Override
    public Response<CursorPageRespVO<PostListVO,Void>> getPostList(PostListReqVO postListReqVO) {
        int size= postListReqVO.getSize().getCode();
        if(isFirstPage(postListReqVO)){
            String firstPageKey= PostRedisKey.firstPage(size);
            CursorPageRespVO<PostListVO,Void> cacheResult=null;
            try {
                cacheResult=redisService.getCursorPage(firstPageKey,PostListVO.class, Void.class);
            } catch (Exception e) {
                log.error("获取第一页帖子列表缓存失败,降级走数据库查询",e);
            }
            if (!Objects.isNull(cacheResult)){
                log.info("帖子分页第一页缓存命中，size={}",cacheResult.getList().size());
                fillExtraInfo(
                        cacheResult.getList()
                );
                return Response.ok(cacheResult);
            }
            String lockKey =
                    PostRedisKey.firstPageLock(size);
            String lockValue = null;

            try {

                lockValue = redisService.tryLock(
                        lockKey,
                        PostRedisKey.POST_FIRST_PAGE_LOCK_TTL,
                        PostRedisKey.POST_FIRST_PAGE_LOCK_TTL_UNIT
                );

                if (Objects.nonNull(lockValue)) {
                    cacheResult=redisService.getCursorPage(firstPageKey,PostListVO.class, Void.class);
                    if (Objects.nonNull(cacheResult)){
                        fillExtraInfo(
                                cacheResult.getList()
                        );
                        return Response.ok(cacheResult);
                    }
                    CursorPageRespVO<PostListVO,Void> dbResult =
                            buildFirstPage(postListReqVO);

                    redisService.setCursorPage(
                            firstPageKey,
                            dbResult,
                            PostRedisKey.FIRST_PAGE_TTL,
                            PostRedisKey.FIRST_PAGE_TTL_UNIT
                    );

                    fillExtraInfo(
                            dbResult.getList()
                    );

                    return Response.ok(dbResult);
                }else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        log.warn("Thread.sleep()被打断");
                    }

                    cacheResult =
                            redisService.getCursorPage(
                                    firstPageKey,
                                    PostListVO.class,
                                    Void.class
                            );

                    if (cacheResult != null) {

                        fillExtraInfo(
                                cacheResult.getList()
                        );

                        return Response.ok(cacheResult);
                    }
                }
            } catch (Exception e) {
                log.error("重建第一页缓存失败", e);
            }finally {

                if (Objects.nonNull(lockValue)) {

                    redisService.unLock(
                            lockKey,
                            lockValue
                    );
                }
            }
            log.warn("帖子分页第一页未命中，走数据库查询");
            CursorPageRespVO<PostListVO,Void> dbResult=buildFirstPage(postListReqVO);
            fillExtraInfo(
                    dbResult.getList()
            );
            return Response.ok(dbResult);
        }
        return Response.ok(queryByCursor(postListReqVO));
    }
    private CursorPageRespVO<PostListVO,Void> buildFirstPage(
            PostListReqVO postListReqVO
    ) {

        int size = postListReqVO.getSize().getCode();

        List<PostListVO> list =
                postMapper.selectByCursor(
                        postListReqVO,
                        size + 1
                );

        list.forEach(vo -> vo.setBoardTypeName(BoardType.getBoardTypeName(vo.getBoardType())));

        boolean hasNext = list.size() > size;

        if (hasNext) {
            list = list.subList(0, size);
        }

        CursorPageRespVO<PostListVO,Void> result =
                new CursorPageRespVO<>();

        result.setList(list);
        result.setHasNext(hasNext);

        return result;
    }
    private void fillInteractStatus(
            List<PostListVO> list
    ) {

        Long userId =
                LoginUserContextHolder.getUserId();

        if (Objects.isNull(userId)
                || CollectionUtils.isEmpty(list)) {
            return;
        }

        try {
            List<Long> postIds = list.stream()
                    .map(PostListVO::getId)
                    .toList();

            Map<Long, Boolean> likedMap = redisService.batchExistsInZSet(
                    UserRedisKey.likeHistory(userId),
                    postIds
            );

            Map<Long, Boolean> favoritedMap =
                    redisService.batchExistsInZSet(
                            UserRedisKey.favoriteHistory(userId),
                            postIds
                    );

            for (PostListVO vo : list) {

                Long postId = vo.getId();

                vo.setLiked(
                        likedMap.getOrDefault(
                                postId,
                                false
                        )
                );

                vo.setFavorited(
                        favoritedMap.getOrDefault(
                                postId,
                                false
                        )
                );
            }
        } catch (Exception e) {

            log.error(
                    "填充互动状态失败,userId={}",
                    userId,
                    e
            );
        }
    }
    private void fillHotComment(List<PostListVO> list) {

        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        List<String> keys = list.stream()
                .map(vo -> PostRedisKey.hotCommentRank(vo.getId()))
                .toList();

        List<Object> results = redisService.zRevRangePipeline(keys, 0, 0);

        List<Long> commentIds = new ArrayList<>(list.size());

        for (int i = 0; i < list.size(); i++) {

            Set<String> raw = (Set<String>) results.get(i);

            if (raw == null || raw.isEmpty()) {

                commentIds.add(null);
                continue;
            }

            commentIds.add(
                    Long.valueOf(
                            raw.iterator().next()
                    )
            );
        }
        List<Long> validIds =
                commentIds.stream()
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();
        if (validIds.isEmpty()) {
            return;
        }

        List<CommentHotDTO> hotList =
                commentMapper.selectHotInfoByIds(validIds, CommentHotScoreConstants.HOT_COMMENT_MIN_SCORE);

        Map<Long, CommentHotDTO> map =
                hotList.stream()
                        .collect(Collectors.toMap(CommentHotDTO::getId, v -> v));

        for (int i = 0; i < list.size(); i++) {

            Long commentId = commentIds.get(i);

            if (commentId == null) {
                continue;
            }

            list.get(i)
                    .setHotComment(
                            map.get(commentId)
                    );
        }
    }
    private void fillExtraInfo(List<PostListVO> list){

        fillInteractStatus(list);

        fillHotComment(list);
    }
    private boolean isFirstPage(PostListReqVO postListReqVO) {
        // 关键：必须所有游标字段都为null才是第一页
        return postListReqVO.getLastCreateTime() == null
                && postListReqVO.getLastId() == null
                && postListReqVO.getLastIsTop() == null
                && postListReqVO.getLastIsEssence() == null;
    }
    private CursorPageRespVO<PostListVO,Void> queryByCursor(PostListReqVO postListReqVO){
        Preconditions.checkArgument(!Objects.isNull(postListReqVO.getLastIsTop()), "lastIsTop 不能为空");
        Preconditions.checkArgument(!Objects.isNull(postListReqVO.getLastIsEssence()), "lastIsEssence 不能为空");
        Preconditions.checkArgument(!Objects.isNull(postListReqVO.getLastCreateTime()), "lastUpdateTime 不能为空");
        Preconditions.checkArgument(!Objects.isNull(postListReqVO.getLastId()), "lastId 不能为空");
        CursorPageRespVO<PostListVO,Void> dbResult=new CursorPageRespVO<>();
        int size= postListReqVO.getSize().getCode();
        List<PostListVO> list=postMapper.selectByCursor(postListReqVO,size+1);
        list.forEach(vo -> vo.setBoardTypeName(BoardType.getBoardTypeName(vo.getBoardType())));
        boolean hasNext = list.size() > size;
        if (hasNext) {
            list = list.subList(0, size);
        }
        fillExtraInfo(list);
        dbResult.setList(list);
        dbResult.setHasNext(hasNext);
        return dbResult;
    }

    @Override
    public Response<PostDetailRespVO> getPostDetail(Long id) {
        if (!existsPost(id)){
            throw new BizException(BizResponseErrorCode.POST_NOT_EXISTS_OR_ABNORMAL);
        }
        Long userId=LoginUserContextHolder.getUserId();
        PostDetailRespVO postDetailRespVO=getPostDetailRespVO(id,isHot(id));
        String viewCountKey=PostRedisKey.viewCount(id);
        Long viewCount=null;
        try {
            viewCount=redisService.incrementViewCount(viewCountKey);
            postDetailRespVO.setViewCount(viewCount.intValue());
        } catch (Exception e) {
            log.error("浏览计数失败，降级返回数据库记录");
        }
        if (userId!=null){
            String viewHistoryKey= UserRedisKey.viewHistory(userId);
            try {
                redisService.addRecentView(viewHistoryKey,id,System.currentTimeMillis(),
                        RedisConstants.MAX_HISTORY_SIZE,PostRedisKey.POST_VIEW_HISTORY_TTL_SECONDS);
            } catch (Exception e) {
                log.error("记录用户浏览历史失败, userId={}, postId={}", userId, id, e);
            }
            fillInteractStatus(userId,id,postDetailRespVO);
        }
        return Response.ok(postDetailRespVO);
    }

    private void fillInteractStatus(
            Long userId,
            Long postId,
            PostDetailRespVO vo
    ) {

        try {

            vo.setLiked(
                    redisService.zScore(
                            UserRedisKey.likeHistory(userId),
                            String.valueOf(postId)
                    ) != null
            );

        } catch (Exception e) {

            log.warn(
                    "查询点赞状态失败,userId={},postId={}",
                    userId,
                    postId,
                    e
            );
        }

        try {

            vo.setFavorited(
                    redisService.zScore(
                            UserRedisKey.favoriteHistory(userId),
                            String.valueOf(postId)
                    ) != null
            );

        } catch (Exception e) {

            log.warn(
                    "查询收藏状态失败,userId={},postId={}",
                    userId,
                    postId,
                    e
            );
        }
    }

    private Boolean isHot(Long id){
        String viewHourlyKey=PostRedisKey.viewHourly(id);
        Long viewHourly=null;
        try {
            viewHourly=redisService.incrementAndExpire(viewHourlyKey,
                    PostRedisKey.POST_VIEW_HOURLY_TTL,
                    PostRedisKey.POST_VIEW_HOURLY_TTL_UNIT);
        } catch (Exception e) {
            log.error("帖子每小时浏览量自增失败",e);
            log.warn("注意可能产生僵尸key");
        }
        return Objects.isNull(viewHourly)?null:viewHourly>= CacheConstants.HOT_THRESHOLD;
    }
    private PostDetailRespVO getPostDetailRespVO(Long id,Boolean isHot){
        PostDetailRespVO result=null;
        if (Objects.isNull(isHot)||Boolean.FALSE.equals(isHot)){
            result=postMapper.selectById(id);
            if (Objects.isNull(result)){
                throw new BizException(BizResponseErrorCode.POST_NOT_EXISTS_OR_ABNORMAL);
            }
            result.setBoardTypeName(BoardType.getBoardTypeName(result.getBoardType()));
        }else {
            String viewHotKey=PostRedisKey.viewHot(id);
            int retryCount=0;
            do {
                try {
                    result = redisService.get(viewHotKey, PostDetailRespVO.class);
                } catch (Exception e) {
                    log.error("获取帖子详情缓存异常，降级走数据库查询");
                }
                if (result == null) {
                    initHotCacheWithLock(viewHotKey,id);
                    if (retryCount<CacheConstants.MAX_RETRIES){
                        sleepWithBackoff(retryCount);
                    }
                }
                retryCount++;
            }while (Objects.isNull(result)&&retryCount<=CacheConstants.MAX_RETRIES);
            if (!Objects.isNull(result)){
                log.info("热点帖子详情缓存命中");
                return result;
            }
            log.warn("热点帖子详情缓存未命中: viewHotKey:{}，降级走数据库查询",viewHotKey);
            result=postMapper.selectById(id);
            if (Objects.isNull(result)){
                throw new BizException(BizResponseErrorCode.POST_NOT_EXISTS_OR_ABNORMAL);
            }
            result.setBoardTypeName(BoardType.getBoardTypeName(result.getBoardType()));
        }
        return result;
    }

    @Override
    public Response<?> updatePost(PostUpdateReqVO postUpdateReqVO) {
        Long currentUserId=LoginUserContextHolder.getUserId();
        if (Objects.isNull(currentUserId)){
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }
        PostBasicInfoDTO postBasicInfoDTO=postMapper.selectBasicInfoById(postUpdateReqVO.getId());
        if (Objects.isNull(postBasicInfoDTO)||postBasicInfoDTO.getStatus()==2){
            throw new BizException(BizResponseErrorCode.POST_NOT_EXISTS_OR_DELETED);
        }
        if (!Objects.equals(currentUserId,postBasicInfoDTO.getUserId())){
            throw new BizException(BizResponseErrorCode.POST_NOT_OWNER);
        }
        RichTextDoc doc =
                jsonUtils.parseObject(postUpdateReqVO.getContent(), RichTextDoc.class);

        RichTextService.ProcessResult result =
                richTextService.process(doc);
        String contentJson =
                jsonUtils.toJsonString(result.getDoc());
        String plainText = result.getPlainText();
        postUpdateReqVO.setContent(contentJson);
        postUpdateReqVO.setPlainText(plainText);
        int rows=postMapper.updatePostById(postUpdateReqVO);
        if (rows==0){
            throw new BizException(BizResponseErrorCode.POST_NOT_EXISTS_OR_DELETED);
        }else {
            deleteAllFirstPageCachesAsync();
            String titleKey = PostRedisKey.title(postUpdateReqVO.getId());
            redisService.deleteLenient(titleKey);
        }
        return Response.ok();
    }

    @Override
    public Response<?> deletePost(Long id) {
        Long currentUserId=LoginUserContextHolder.getUserId();
        if (Objects.isNull(currentUserId)){
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }
        PostBasicInfoDTO postBasicInfoDTO=postMapper.selectBasicInfoById(id);
        if (Objects.isNull(postBasicInfoDTO)||postBasicInfoDTO.getStatus()==2){
            throw new BizException(BizResponseErrorCode.POST_NOT_EXISTS_OR_DELETED);
        }
        if (!Objects.equals(currentUserId,postBasicInfoDTO.getUserId())){
            throw new BizException(BizResponseErrorCode.POST_NOT_OWNER);
        }
        int rows=postMapper.softDeletePostById(id);
        if (rows==0){
            throw new BizException(BizResponseErrorCode.POST_NOT_EXISTS_OR_DELETED);
        }else {
            deleteAllFirstPageCachesAsync();
            String existsKey=PostRedisKey.exists(id);
            try {
                redisService.deleteStrict(existsKey);
            } catch (Exception e) {
                log.error("删除帖子存在性缓存失败，existsKey:{}",existsKey,e);
            }
            String bitKey = PostRedisKey.like(id);
            String countKey = PostRedisKey.likeCount(id);
            redisService.deleteLenient(bitKey);
            redisService.deleteLenient(countKey);
            String viewCountKey=PostRedisKey.viewCount(id);
            redisService.deleteLenient(viewCountKey);
            localCacheManager.invalidateExists(id);
            cacheInvalidatePublisher.publishExistsInvalidate(Collections.singletonList(id));
        }
        return Response.ok();
    }

    public void deleteAllFirstPageCachesAsync() {
        // 避免短时间内重复提交多个删除任务
        if (!isDeleting.compareAndSet(false, true)) {
            log.debug("已有删除任务排队，跳过本次提交");
            return;
        }
        taskExecutor.execute(() -> {
            try {
                for (PageSizeEnum sizeEnum : PageSizeEnum.values()) {
                    String key = PostRedisKey.firstPage(sizeEnum.getCode());
                    redisService.deleteLenient(key);
                }
                log.info("异步删除第一页缓存完成");
            } catch (Exception e) {
                log.error("异步删除第一页缓存失败", e);
            } finally {
                isDeleting.set(false);
            }
        });
    }

    @Override
    public Response<InteractRespVO> likePost(Long id) {
        Long userId=LoginUserContextHolder.getUserId();
        //用户必须先登录，这里暂时没有处理
        if (Objects.isNull(userId)){
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }
        if (!existsPost(id)){
            throw new BizException(BizResponseErrorCode.POST_NOT_EXISTS_OR_ABNORMAL);
        }
        String bitKey=PostRedisKey.like(id);
        String countKey=PostRedisKey.likeCount(id);

        int retryCount=0;

        ToggleResultDTO result=null;
        do {
            try {
                result=redisService.toggleBitmapCounter(bitKey,countKey,userId);
            } catch (Exception e) {
                log.error("获取点赞结果失败",e);
            }
            if (Objects.isNull(result)){
                throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
            }
            if (result.getState()==-2){
                //缓存不存在，利用分布式锁保证串行(同一时刻只有一个线程)初始化缓存，防止缓存击穿
                initInteractCacheWithLock(
                        bitKey,
                        countKey,
                        PostRedisKey.likeLock(id),
                        Duration.ofDays(PostRedisKey.POST_LIKE_BIT_TTL_DAYS),
                        Duration.ofDays(PostRedisKey.POST_LIKE_COUNT_TTL_DAYS),
                        () -> postLikeRecordMapper.selectUserIdsByPostId(id)
                );
                if (retryCount<CacheConstants.MAX_RETRIES){
                    sleepWithBackoff(retryCount);
                }
            }else {
                break;
            }
            retryCount++;
        }while (retryCount<=CacheConstants.MAX_RETRIES&&result.getState()==-2);
        if (Objects.isNull(result)||result.getState()==-2){
            throw new BizException(BizResponseErrorCode.POST_LIKE_CACHE_EXPIRED);
        }
        Boolean interacted=result.getState()==1;
        Long authorId = getPostAuthorId(id);
        if (authorId == null) {
            log.warn("帖子作者缺失,跳过点赞落库和创作者周榜计分, postId={}", id);
        } else {
            sendOrderlyEvent(
                    MQConstants.TOPIC_POST_LIKE_RECORD,
                    userId.toString(),
                    new PostLikeEvent(
                            UUID.randomUUID().toString(),
                            userId,
                            id,
                            authorId,
                            interacted
                    )
            );
            //后续新增了收藏/评论功能之后同样的方式埋点
            asyncUpdateWeeklyRank(
                    authorId,
                    interacted
                            ? WeeklyRankConstants.WEEKLY_RANK_LIKE_SCORE
                            : -WeeklyRankConstants.WEEKLY_RANK_LIKE_SCORE
            );
        }
        asyncUpdateInteractionHistory(
                UserRedisKey.likeHistory(userId),
                id,
                interacted,
                MQConstants.TOPIC_USER_LIKE_HISTORY_COMPENSATE,
                userId
        );
        InteractRespVO interactRespVO = new InteractRespVO();
        interactRespVO.setInteracted(interacted);
        interactRespVO.setCount(result.getCount());
        return Response.ok(interactRespVO);
    }

    @Override
    public Response<InteractRespVO> favoritePost(Long id) {
        Long userId=LoginUserContextHolder.getUserId();
        //用户必须先登录，这里暂时没有处理
        if (Objects.isNull(userId)){
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }
        if (!existsPost(id)){
            throw new BizException(BizResponseErrorCode.POST_NOT_EXISTS_OR_ABNORMAL);
        }
        String bitKey=PostRedisKey.favorite(id);
        String countKey=PostRedisKey.favoriteCount(id);

        int retryCount=0;

        ToggleResultDTO result=null;
        do {
            try {
                result=redisService.toggleBitmapCounter(bitKey,countKey,userId);
            } catch (Exception e) {
                log.error("获取点赞结果失败",e);
            }
            if (Objects.isNull(result)){
                throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
            }
            if (result.getState()==-2){
                //缓存不存在，利用分布式锁保证串行(同一时刻只有一个线程)初始化缓存，防止缓存击穿
                initInteractCacheWithLock(
                        bitKey,
                        countKey,
                        PostRedisKey.favoriteLock(id),
                        Duration.ofDays(PostRedisKey.POST_FAVORITE_BIT_TTL_DAYS),
                        Duration.ofDays(PostRedisKey.POST_FAVORITE_COUNT_TTL_DAYS),
                        () -> favoriteRecordMapper.selectUserIdsByPostId(id)
                );
                if (retryCount<CacheConstants.MAX_RETRIES){
                    sleepWithBackoff(retryCount);
                }
            }else {
                break;
            }
            retryCount++;
        }while (retryCount<=CacheConstants.MAX_RETRIES&&result.getState()==-2);
        if (Objects.isNull(result)||result.getState()==-2){
            throw new BizException(BizResponseErrorCode.POST_FAVORITE_CACHE_EXPIRED);
        }
        Boolean interacted=result.getState()==1L;
        Long authorId = getPostAuthorId(id);
        if (authorId == null) {
            log.warn("帖子作者缺失,跳过创作者周榜计分, postId={}", id);
        }else {
            //后续新增了收藏/评论功能之后同样的方式埋点
            asyncUpdateWeeklyRank(
                    authorId,
                    interacted
                            ? WeeklyRankConstants.WEEKLY_RANK_FAVORITE_SCORE
                            : -WeeklyRankConstants.WEEKLY_RANK_FAVORITE_SCORE
            );
        }
        sendOrderlyEvent(
                MQConstants.TOPIC_POST_FAVORITE_EVENT,
                userId.toString(),
                new PostFavoriteEvent(
                        UUID.randomUUID().toString(),
                        userId,
                        id,
                        interacted
                )
        );
        asyncUpdateInteractionHistory(
                UserRedisKey.favoriteHistory(userId),
                id,
                interacted,
                MQConstants.TOPIC_USER_FAVORITE_HISTORY_COMPENSATE,
                userId
        );
        InteractRespVO interactRespVO = new InteractRespVO();
        interactRespVO.setInteracted(interacted);
        interactRespVO.setCount(result.getCount());
        return Response.ok(interactRespVO);
    }
    private void asyncUpdateWeeklyRank(
            Long authorId,
            int scoreDelta
    ) {

        if (Objects.isNull(authorId)) {
            return;
        }

        taskExecutor.execute(() -> {

            try {

                incrementWeeklyScore(authorId, scoreDelta);

            } catch (Exception e) {

                log.error(
                        "更新创作者周榜失败,authorId={},delta={}",
                        authorId,
                        scoreDelta,
                        e
                );
            }
        });
    }
    private void asyncUpdateInteractionHistory(
            String historyKey,
            Long targetId,
            Boolean interacted,
            String compensateTopic,
            Long userId
    ) {

        taskExecutor.execute(() -> {

            long now = System.currentTimeMillis();

            try {

                if (Boolean.TRUE.equals(interacted)) {

                    redisService.zAdd(
                            historyKey,
                            now,
                            String.valueOf(targetId)
                    );

                } else {

                    redisService.zRem(
                            historyKey,
                            String.valueOf(targetId)
                    );
                }

            } catch (Exception e) {

                log.error(
                        "维护互动历史失败,key={},targetId={},interacted={},MQ异步补偿",
                        historyKey,
                        targetId,
                        interacted,
                        e
                );

                sendInteractCompensateMessage(
                        compensateTopic,
                        userId,
                        targetId,
                        interacted,
                        now
                );
            }
        });
    }
    //本来可以复用的，点赞/收藏/评论消息聚合的时候就不用再批量查询作者ID，后续再改
    private Long getPostAuthorId(Long id){
        if (id==null) return null;
        String authorKey=PostRedisKey.author(id);
        try {
            String cached= redisService.get(authorKey);
            if (cached!=null){
                if (cached.equals(RedisConstants.NULL_PLACEHOLDER)){
                    return null;
                }
                return Long.parseLong(cached);
            }
        } catch (NumberFormatException e) {
            log.error("Redis获取帖子作者缓存失败,postId={}",id,e);
        }
        //缓存未命中或异常，查询数据库
        Long authorId=postMapper.selectAuthorIdById(id);
        try {
            if (Objects.isNull(authorId)){
                //缓存空值，防止穿透
                redisService.set(authorKey,RedisConstants.NULL_PLACEHOLDER,
                        RedisConstants.NULL_VALUE_TTL,RedisConstants.NULL_VALUE_TTL_UNIT);
            }else {
                redisService.set(authorKey,String.valueOf(authorId),
                        PostRedisKey.POST_AUTHOR_TTL,PostRedisKey.POST_AUTHOR_TTL_UNIT);
            }
        } catch (Exception e) {
            log.error("写入帖子作者缓存失败,postId={}",id,e);
        }
        return authorId;
    }
    private boolean existsPost(Long id){
        Boolean localExists=localCacheManager.getExists(id);
        if (localExists!=null){
            return localExists;
        }
        //本地缓存没有命中，走redis
        String existsKey=PostRedisKey.exists(id);
        Boolean exists;
        try {
            exists=redisService.getBoolean(existsKey);
        } catch (Exception e) {
            log.error("检查帖子存在性失败, existsKey: {}", existsKey, e);
            exists=null;
        }
        if (!Objects.isNull(exists)){
            //回填本地缓存
            localCacheManager.putExists(id,exists);
            return exists;
        }
        boolean result=postMapper.existsNormalById(id);

        if (result){
            redisService.setLenient(existsKey,true,
                    PostRedisKey.POST_EXISTS_TTL,
                    PostRedisKey.POST_EXISTS_TTL_UNIT);
        }else {
            //防止缓存穿透
            try {
                redisService.setStrict(existsKey,false,
                        RedisConstants.NULL_VALUE_TTL,
                        RedisConstants.NULL_VALUE_TTL_UNIT);
            } catch (Exception e) {
                log.error("空值缓存设置失败，注意可能发生缓存穿透",e);
            }
        }
        localCacheManager.putExists(id, result);

        return result;
    }

    private void initHotCacheWithLock(String targetKey,Long id){
        String lockKey=PostRedisKey.viewHotLock(id);
        String value=null;
        try {
            value=redisService.tryLock(lockKey,
                    PostRedisKey.POST_VIEW_HOT_LOCK_TTL,
                    PostRedisKey.POST_VIEW_HOT_LOCK_TTL_UNIT);
        } catch (Exception e) {
            log.error("获得锁异常",e);
        }
        if (!Objects.isNull(value)){
            try {
                if (!redisService.exists(targetKey)){
                    initHotCache(targetKey,id);
                }
            } catch (Exception e) {
                throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
            } finally {
                boolean result= redisService.unLock(lockKey,value);
                if (!result) {
                    log.warn("释放锁失败,lockKey;{}", lockKey);
                }
            }
        }
    }
    private void initHotCache(String targetKey,Long id){
        PostDetailRespVO result=postMapper.selectById(id);
        try {
            if (result == null) {
                throw new BizException(BizResponseErrorCode.POST_NOT_EXISTS_OR_ABNORMAL);
            }else {
                result.setBoardTypeName(BoardType.getBoardTypeName(result.getBoardType()));
                redisService.setJson(targetKey, result,
                        PostRedisKey.POST_VIEW_HOT_TTL,PostRedisKey.POST_VIEW_HOT_TTL_UNIT);
            }
        } catch (Exception e) {
            log.error("设置热点帖子详情缓存失败",e);
            throw new RuntimeException(e);
        }
    }

    private void initInteractCacheWithLock(
            String bitKey,
            String countKey,
            String lockKey,
            Duration bitTtl,
            Duration countTtl,
            Supplier<List<Long>> userLoader
    ){
        String value=null;
        try {
            value=redisService.tryLock(lockKey,
                    PostRedisKey.INIT_CACHE_LOCK_TTL,
                    PostRedisKey.INIT_CACHE_LOCK_TTL_UNIT);
        } catch (Exception e) {
            log.error("获取锁异常",e);
        }
        if (!Objects.isNull(value)){
            try {
                if (!redisService.exists(countKey)||!redisService.exists(bitKey)){
                    initInteractCache(
                            bitKey,
                            countKey,
                            bitTtl,
                            countTtl,
                            userLoader
                    );
                }
            }  catch (Exception e) {
                try {
                    redisService.deleteStrict(bitKey);
                } catch (Exception ex) {
                    log.error("清理互动记录位图失败，注意可能产生僵尸bitKey", ex);
                }
            }finally {
                boolean result= redisService.unLock(lockKey,value);
                if (!result) {
                    log.warn("释放锁失败,lockKey;{}", lockKey);
                }
            }
        }
    }

    private void initInteractCache(
            String bitKey,
            String countKey,
            Duration bitTtl,
            Duration countTtl,
            Supplier<List<Long>> userLoader
    ){
        List<Long> userIds=userLoader.get();
        int count=userIds.size();
        int batchSize=1000;

        if (count>0) {
            for (int i = 0; i < count; i += batchSize) {
                int end = Math.min(i + batchSize, count);
                List<Long> batch = userIds.subList(i, end);

                Object[] args = batch.stream().map(Object::toString).toArray();

                try {
                    redisService.initPostInteractBitmap(bitKey, args);
                } catch (Exception e) {
                    log.error("批量设置帖子互动记录位图失败", e);
                    throw new RuntimeException(e);
                }
            }
        }else {
            redisService.initEmptyBitmap(bitKey);
        }

        try {
            Boolean isSuccess= redisService.expire(bitKey,bitTtl);
            if (Boolean.FALSE.equals(isSuccess)){
                //似乎不会出现这种请况
                log.warn("初始化帖子互动记录位图和设置帖子互动记录位图过期时间顺序颠倒");
                try {
                    redisService.deleteStrict(bitKey);
                } catch (Exception e) {
                    log.error("删除帖子互动记录位图缓存失败，注意可能产生僵尸key",e);
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            log.error("设置帖子互动记录位图过期时间失败",e);
            throw new RuntimeException(e);
        }
        try {
            redisService.setStrict(countKey,count,countTtl);
        } catch (Exception e) {
            log.error("设置帖子互动计数器失败");
        }
    }

    private void sleepWithBackoff(int retryCount){
        long waitMs=Math.min(100L*(1L<<retryCount),3000L);
        try {
            Thread.sleep(waitMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }
    }

    public <T> void sendOrderlyEvent(
            String topic,
            String hashKey,
            T event
    ) {
        try {
            Message<T> message = MessageBuilder.withPayload(event).build();

            rocketMQTemplate.asyncSendOrderly(
                    topic,
                    message,
                    hashKey,
                    new SendCallback() {

                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.info("互动事件发送成功, msgId={}", sendResult.getMsgId());
                        }

                        @Override
                        public void onException(Throwable e) {
                            log.error("互动事件发送失败", e);
                        }
                    }
            );

        } catch (Exception e) {
            log.error("互动发送异常", e);
        }
    }

    private void sendInteractCompensateMessage(String destination,Long userId,Long id,Boolean interacted,long now){
        log.info("准备发送用户维度互动记录补偿消息: userId={}, postId={}, interacted={}", userId, id, interacted);
        try {
            InteractCompensateMessage message=new InteractCompensateMessage(userId,id,interacted,now);
            String hashKey=String.valueOf(userId);

            // 外层异步，内层同步
            SendResult sendResult = rocketMQTemplate.syncSendOrderly(destination, message, hashKey, 3000);

            if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
                log.info("用户维度互动记录补偿消息发送成功: userId={}, postId={}, msgId={}",
                        userId, id, sendResult.getMsgId());
            } else {
                log.error("用户维度互动记录补偿消息发送失败: sendStatus={},userId={}, postId={}, interacted={}",
                        sendResult.getSendStatus(),userId, id, interacted);
                // 可抛出业务异常或记录到本地重试表
            }
        } catch (Exception e) {
            log.error("发送用户维度互动记录补偿消息异常",e);
            // 可抛出业务异常或记录到本地重试表
        }
    }

    @Override
    public Response<List<PostListVO>> batchGetPostList(List<Long> ids) {
        if (Objects.isNull(ids)||ids.isEmpty()){
            return Response.ok(Collections.emptyList());
        }
        List<PostListVO> list = postMapper.selectPostListByIds(ids);
        list.forEach(vo -> vo.setBoardTypeName(BoardType.getBoardTypeName(vo.getBoardType())));
        fillExtraInfo(list);
        return Response.ok(list);
    }

    @Override
    public Response<List<HotBoardItemVO>> getHotBoard(int limit) {
        String hotBoardKey=PostRedisKey.hotBoard();
        String json;
        try {
            json=redisService.get(hotBoardKey);
        } catch (Exception e) {
            log.error("获取热榜帖子列表失败",e);
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }
        if (StringUtils.isBlank(json)){
            //降级：返回空列表
            return Response.ok(Collections.emptyList());
        }
        List<HotBoardItemVO> all;
        try {
            all=jsonUtils.parseList(json, HotBoardItemVO.class);
        } catch (JsonProcessingException e) {
            log.error("热榜帖子列表序列化失败");
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }
        if (Objects.isNull(all)||all.isEmpty()){
            return Response.ok(Collections.emptyList());
        }
        List<HotBoardItemVO> result=all.stream().limit(limit).collect(Collectors.toList());
        return Response.ok(result);
    }

    public void incrementWeeklyScore(Long userId,double increment){
        if (userId==null||increment==0){
            return;
        }
        String key=UserRedisKey.weeklyRank();
        try {
            Boolean exists =redisService.exists(key);
            Double newScore=redisService.zIncrBy(key,increment,String.valueOf(userId));
            if (!Boolean.TRUE.equals(exists)){
                redisService.expireAt(
                        key,
                        UserRedisKey.getWeeklyRankExpireTime()
                );
            }
            log.debug("创作者周榜更新:userId={},increment={},newScore={}",userId,increment,newScore);
        } catch (Exception e) {
            log.error("创作者更新周榜失败，userId={},increment={}",userId,increment,e);
            //一致性要求不高暂时不做MQ补偿消息
        }
    }
}
