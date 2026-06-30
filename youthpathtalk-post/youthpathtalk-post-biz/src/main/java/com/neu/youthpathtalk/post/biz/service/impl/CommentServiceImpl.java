package com.neu.youthpathtalk.post.biz.service.impl;

import com.google.common.base.Preconditions;
import com.neu.youthpathtalk.constant.redis.CommentRedisKey;
import com.neu.youthpathtalk.constant.redis.PostRedisKey;
import com.neu.youthpathtalk.constant.redis.RedisConstants;
import com.neu.youthpathtalk.constant.redis.UserRedisKey;
import com.neu.youthpathtalk.enums.CommonResponseErrorCode;
import com.neu.youthpathtalk.exception.BizException;
import com.neu.youthpathtalk.holder.LoginUserContextHolder;
import com.neu.youthpathtalk.post.biz.cache.LocalCacheManager;
import com.neu.youthpathtalk.post.biz.cache.RedisService;
import com.neu.youthpathtalk.post.biz.config.CommentConversationProperties;
import com.neu.youthpathtalk.post.biz.constants.CacheConstants;
import com.neu.youthpathtalk.post.biz.constants.MQConstants;
import com.neu.youthpathtalk.post.biz.constants.WeeklyRankConstants;
import com.neu.youthpathtalk.post.biz.dto.*;
import com.neu.youthpathtalk.post.biz.enums.NotificationType;
import com.neu.youthpathtalk.post.biz.enums.PageSizeEnum;
import com.neu.youthpathtalk.post.biz.enums.TargetType;
import com.neu.youthpathtalk.post.biz.event.*;
import com.neu.youthpathtalk.post.biz.entity.CommentDO;
import com.neu.youthpathtalk.post.biz.enums.BizResponseErrorCode;
import com.neu.youthpathtalk.post.biz.mapper.CommentLikeRecordMapper;
import com.neu.youthpathtalk.post.biz.mapper.CommentMapper;
import com.neu.youthpathtalk.post.biz.mapper.PostMapper;
import com.neu.youthpathtalk.post.biz.message.NotificationMessage;
import com.neu.youthpathtalk.post.biz.richtext.model.RichTextDoc;
import com.neu.youthpathtalk.post.biz.richtext.service.RichTextService;
import com.neu.youthpathtalk.post.biz.rpc.LeafIdGenService;
import com.neu.youthpathtalk.post.biz.rpc.UserRpcService;
import com.neu.youthpathtalk.post.biz.service.CommentService;
import com.neu.youthpathtalk.post.biz.util.JsonUtils;
import com.neu.youthpathtalk.post.biz.vo.cursor.CommentCursor;
import com.neu.youthpathtalk.post.biz.vo.cursor.CreateTimeIdCursor;
import com.neu.youthpathtalk.post.biz.vo.req.*;
import com.neu.youthpathtalk.post.biz.vo.resp.*;
import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.user.api.vo.resp.UserInfoRespVO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * @author Julien
 * @time 2026/06/03 10:47
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    @Value("${comment.cache.retry-sleep-millis:100}")
    private Integer retrySleepMillis;
    private final JsonUtils jsonUtils;
    @Value("${comment.edit-window-minutes:5}")
    private Integer editWindowMinutes;
    private final PostMapper postMapper;
    private final RedisService redisService;
    private final CommentMapper commentMapper;
    private final UserRpcService userRpcService;
    private final RichTextService richTextService;
    private final LeafIdGenService leafIdGenService;
    private final RocketMQTemplate rocketMQTemplate;
    private final LocalCacheManager localCacheManager;
    private final CommentLikeRecordMapper commentLikeRecordMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CommentConversationProperties conversationProperties;
    @Resource(name="taskExecutor")
    private Executor taskExecutor;

    @Override
    public Response<String> getPlainText(Long id) {
        if (id == null) {
            return Response.ok(null);
        }
        String plainText = commentMapper.selectPlainTextById(id);
        if (plainText == null) {
            return Response.ok(null);
        }
        return Response.ok(plainText);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<Void> create(CommentCreateReqVO req) {
        RichTextService.ProcessResult result = richTextService.process(req.getContent());
        String plainText = result.getPlainText();
        Preconditions.checkArgument(plainText.length() <= 500,"评论内容不能超过500字");
        String content= jsonUtils.toJsonString(result.getDoc());

        Long postId = req.getPostId();
        checkPostExists(postId);

        Long currentUserId =getCurrentUserId();

        Long commentId=leafIdGenService.generateCommentId();
        UserInfoRespVO userInfoRespVO=userRpcService.getUserInfo(currentUserId);
        CommentDO commentDO= CommentDO.builder()
                .id(commentId)
                .postId(postId)
                .userId(currentUserId)
                .userName(userInfoRespVO.getUsername())
                .userAvatar(userInfoRespVO.getUserAvatar())
                .universityId(userInfoRespVO.getUniversityId())
                .universityName(userInfoRespVO.getUniversityName())
                .content(content)
                .plainText(plainText)
                .build();
        //这里似乎不会出现实际执行失败却不抛异常的情况？
        commentMapper.insert(commentDO);
        int rows = postMapper.updateCommentCountById(postId,1L);
        if (rows!=1){
            throw new BizException(BizResponseErrorCode.POST_NOT_EXISTS_OR_ABNORMAL);
        }
        applicationEventPublisher.publishEvent(
                new CommentPageCacheEvictEvent(postId)
        );

        applicationEventPublisher.publishEvent(
                new RootCommentCreatedEvent(
                        postId,
                        commentId
                )
        );
        applicationEventPublisher.publishEvent(
                MentionEvent.builder()
                        .targetType(TargetType.COMMENT)
                        .postId(postId)
                        .commentId(commentId)
                        .senderId(currentUserId)
                        .senderName(userInfoRespVO.getUsername())
                        .senderAvatar(userInfoRespVO.getUserAvatar())
                        .mentionedUserIds(result.getMentionedUserIds())
                        .content(plainText)
                        .createTime(LocalDateTime.now())
                        .build()
        );
        Long authorId = getPostAuthorId(postId);
        if (authorId == null) {
            log.warn("帖子作者缺失,跳过发送评论通知和创作者周榜计分, postId={}", postId);
        }else {
            if (!Objects.equals(authorId,currentUserId)) {
                NotificationMessage notification = NotificationMessage.builder()
                        .eventId(UUID.randomUUID().toString())
                        .receiverId(authorId)
                        .senderId(currentUserId)
                        .senderName(userInfoRespVO.getUsername())
                        .senderAvatar(userInfoRespVO.getUserAvatar())
                        .type(NotificationType.POST_COMMENT.getCode())
                        .targetType(TargetType.COMMENT.getCode())
                        .postId(postId)
                        .commentId(commentId)//评论的ID
                        .content(plainText)
                        .createTime(LocalDateTime.now())
                        .build();
                sendNotification(notification);
            }
            asyncUpdateWeeklyRank(
                    authorId,
                    WeeklyRankConstants.WEEKLY_RANK_COMMENT_SCORE
            );
        }
        return Response.ok();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Response<Void> reply(CommentReplyReqVO req) {
        RichTextService.ProcessResult result = richTextService.process(req.getContent());
        String plainText = result.getPlainText();
        Preconditions.checkArgument(plainText.length() <= 500,"评论内容不能超过500字");
        String content= jsonUtils.toJsonString(result.getDoc());

        Long parentId=req.getParentId();
        ReplyTargetDTO targetDTO = commentMapper.selectReplyTarget(parentId);
        if (Objects.isNull(targetDTO)){
            throw new BizException(BizResponseErrorCode.COMMENT_NOT_EXISTS);
        }

        Long postId = targetDTO.getPostId();
        checkPostExists(postId);

        Long currentUserId =getCurrentUserId();
        UserInfoRespVO userInfoRespVO=userRpcService.getUserInfo(currentUserId);

        Long commentId=leafIdGenService.generateCommentId();
        Long rootId=targetDTO.getRootId();
        if (Objects.isNull(rootId)){
            rootId=parentId;
        }
        CommentDO commentDO= CommentDO.builder()
                .id(commentId)
                .postId(postId)
                .userId(currentUserId)
                .userName(userInfoRespVO.getUsername())
                .userAvatar(userInfoRespVO.getUserAvatar())
                .universityId(userInfoRespVO.getUniversityId())
                .universityName(userInfoRespVO.getUniversityName())
                .rootId(rootId)
                .parentId(parentId)
                .replyUserId(targetDTO.getUserId())
                .replyUserName(targetDTO.getUserName())
                .content(content)
                .plainText(plainText)
                .build();
        commentMapper.insert(commentDO);
        int rows = postMapper.updateCommentCountById(postId,1L);
        if (rows!=1){
            throw new BizException(BizResponseErrorCode.POST_NOT_EXISTS_OR_ABNORMAL);
        }
        rows = commentMapper.updateReplyCountById(rootId,1L);

        if(rows != 1){
            throw new BizException(
                    BizResponseErrorCode.COMMENT_NOT_EXISTS
            );
        }
        applicationEventPublisher.publishEvent(
                new CommentPageCacheEvictEvent(postId)
        );

        applicationEventPublisher.publishEvent(
                new CommentHotScoreRebuildEvent(rootId,postId)
        );

        applicationEventPublisher.publishEvent(
                MentionEvent.builder()
                        .targetType(TargetType.COMMENT)
                        .postId(postId)
                        .rootId(rootId)
                        .commentId(commentId)
                        .senderId(currentUserId)
                        .senderName(userInfoRespVO.getUsername())
                        .senderAvatar(userInfoRespVO.getUserAvatar())
                        .mentionedUserIds(result.getMentionedUserIds())
                        .content(plainText)
                        .createTime(LocalDateTime.now())
                        .build()
        );
        if (!Objects.equals(targetDTO.getUserId(),currentUserId)) {
            NotificationMessage notification = NotificationMessage.builder()
                    .eventId(UUID.randomUUID().toString())
                    .receiverId(targetDTO.getUserId())
                    .senderId(currentUserId)
                    .senderName(userInfoRespVO.getUsername())
                    .senderAvatar(userInfoRespVO.getUserAvatar())
                    .type(NotificationType.COMMENT_REPLY.getCode())
                    .targetType(TargetType.COMMENT.getCode())
                    .postId(postId)
                    .rootId(rootId)
                    .commentId(commentId)//回复评论的ID
                    .targetContent(targetDTO.getPlainText())
                    .content(plainText)
                    .createTime(LocalDateTime.now())
                    .build();
            sendNotification(notification);
        }
        Long authorId = getPostAuthorId(postId);
        if (authorId == null) {
            log.warn("帖子作者缺失,跳过创作者周榜计分, postId={}", postId);
        }else {
            asyncUpdateWeeklyRank(
                    authorId,
                    WeeklyRankConstants.WEEKLY_RANK_COMMENT_SCORE
            );
        }
        return Response.ok();
    }

    private Long getCurrentUserId() {

        Long userId =
                LoginUserContextHolder.getUserId();

        if (Objects.isNull(userId)) {

            throw new BizException(
                    BizResponseErrorCode.AUTH_NOT_LOGIN
            );
        }

        return userId;
    }

    private void checkPostExists(Long postId) {

        if (!existsPost(postId)) {

            throw new BizException(
                    BizResponseErrorCode.POST_NOT_EXISTS_OR_ABNORMAL
            );
        }
    }

    private boolean existsPost(Long id){
        Boolean localExists=localCacheManager.getExists(id);
        if (localExists!=null){
            return localExists;
        }
        //本地缓存没有命中，走redis
        String existsKey= PostRedisKey.exists(id);
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

    @Override
    public Response<
            CursorPageRespVO<
                    CommentRespVO,
                    CommentCursor
                    >
            > list(CommentListReqVO req)
    {
        validateCursor(req.getCursor());

        Long postId = req.getPostId();

        checkPostExists(postId);

        if (req.getCursor() == null) {

            return Response.ok(
                    getFirstPageWithCache(
                            postId,
                            req.getPageSize()
                    )
            );
        }

        return Response.ok(
                queryRootCommentPage(
                        postId,
                        req.getCursor(),
                        req.getPageSize()
                )
        );
    }

    private CursorPageRespVO<
            CommentRespVO,
            CommentCursor
            > getFirstPageWithCache(
            Long postId,
            PageSizeEnum pageSize
    )
    {
        String cacheKey =
                PostRedisKey.firstCommentPage(postId,pageSize.getCode());

        CursorPageRespVO<
                CommentRespVO,
                CommentCursor
                > cache =
                redisService.getCursorPage(
                        cacheKey,
                        CommentRespVO.class,
                        CommentCursor.class
                );

        if (cache != null) {
            return cache;
        }

        String lockKey = PostRedisKey.commentFirstPageLock(postId,pageSize.getCode());

        String lockValue = null;
        try {
            lockValue = redisService.tryLock(
                    lockKey,
                    PostRedisKey.POST_COMMENT_FIRST_PAGE_LOCK_TTL,
                    PostRedisKey.POST_COMMENT_FIRST_PAGE_LOCK_TTL_UNIT
            );

            if (Objects.nonNull(lockValue)) {
                cache =
                        redisService.getCursorPage(
                                cacheKey,
                                CommentRespVO.class,
                                CommentCursor.class
                        );

                if (cache != null) {
                    return cache;
                }

                CursorPageRespVO<
                        CommentRespVO,
                        CommentCursor
                        > dbResult =
                        queryRootCommentPage(
                                postId,
                                null,
                                pageSize
                        );

                redisService.setCursorPage(
                        cacheKey,
                        dbResult,
                        PostRedisKey.COMMENT_FIRST_PAGE_TTL,
                        PostRedisKey.COMMENT_FIRST_PAGE_TTL_UNIT
                );

                return dbResult;
            }else {
                try {

                    Thread.sleep(retrySleepMillis);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    return queryRootCommentPage(
                            postId,
                            null,
                            pageSize
                    );
                }

                cache =
                        redisService.getCursorPage(
                                cacheKey,
                                CommentRespVO.class,
                                CommentCursor.class
                        );

                if (cache != null) {
                    return cache;
                }

                return queryRootCommentPage(
                        postId,
                        null,
                        pageSize
                );
            }
        } catch (Exception e) {
            log.error("重建帖子第一页评论失败,postId={}",postId,e);

            return queryRootCommentPage(
                    postId,
                    null,
                    pageSize
            );
        } finally {
            if (Objects.nonNull(lockValue)) {

                redisService.unLock(
                        lockKey,
                        lockValue
                );
            }
        }
    }
    private CursorPageRespVO<
            CommentRespVO,
            CommentCursor
            > queryRootCommentPage(
            Long postId,
            CommentCursor cursor,
            PageSizeEnum pageSizeEnum
    )
    {
        int pageSize =
                pageSizeEnum.getCode();

        List<CommentRespVO> comments =
                commentMapper.selectRootComments(
                        postId,
                        cursor,
                        pageSize + 1
                );

        boolean hasNext =
                comments.size() > pageSize;

        if (hasNext) {

            comments =
                    comments.subList(
                            0,
                            pageSize
                    );
        }

        CommentCursor nextCursor = null;

        if (hasNext && !comments.isEmpty()) {

            CommentRespVO last =
                    comments.get(
                            comments.size() - 1
                    );

            nextCursor =
                    new CommentCursor(
                            last.getHotScore(),
                            last.getId()
                    );
        }

        return new CursorPageRespVO<>(
                comments,
                hasNext,
                nextCursor
        );
    }
    private void validateCursor(CommentCursor cursor){

        if(cursor == null){
            return;
        }

        Preconditions.checkArgument(
                cursor.getHotScore() != null&& cursor.getId() != null,
                "非法游标"
        );
    }

    @Override
    public Response<
            CursorPageRespVO<
                    ReplyCommentRespVO,
                    CreateTimeIdCursor
                    >
            > listReply(ReplyCommentListReqVO req)
    {

        Long rootId =
                req.getRootId();

        int pageSize =
                req.getPageSize().getCode();

        List<ReplyCommentRespVO> replies =
                commentMapper.selectReplyList(
                        rootId,
                        req.getCursor(),
                        pageSize + 1
                );

        fillShowReplyUser(replies,rootId);
        boolean hasNext =
                replies.size() > pageSize;

        if (hasNext) {

            replies.remove(pageSize);
        }

        CreateTimeIdCursor nextCursor = null;

        if (hasNext && !replies.isEmpty()) {

            ReplyCommentRespVO last =
                    replies.get(
                            replies.size() - 1
                    );

            nextCursor =
                    new CreateTimeIdCursor(
                            last.getCreateTime(),
                            last.getId()
                    );
        }

        return Response.ok(
                new CursorPageRespVO<>(
                        replies,
                        hasNext,
                        nextCursor
                )
        );
    }
    private void fillShowReplyUser(
            List<ReplyCommentRespVO> replies,
            Long rootId
    ) {
        for (ReplyCommentRespVO reply : replies) {

            reply.setShowReplyUser(
                    !Objects.equals(
                            rootId,
                            reply.getParentId()
                    )
            );
        }
    }
    @Override
    public Response<List<ReplyCommentRespVO>> conversation(
            Long startId
    ) {

        //注意可能的根评论“查看对话”的恶意请求
        List<ReplyCommentRespVO> conversations =
                commentMapper.selectConversation(
                        startId,
                        conversationProperties.getMaxDepth(),
                        conversationProperties.getMaxNodeCount()
                );

        if (CollectionUtils.isEmpty(conversations)) {
            throw new BizException(
                    BizResponseErrorCode.COMMENT_NOT_EXISTS
            );
        }

        ReplyCommentRespVO start=conversations.get(0);
        if (Objects.equals(start.getRootId(),start.getParentId())){
            start.setShowReplyUser(Boolean.FALSE);
        }

        return Response.ok(conversations);
    }

    //不删缓存，接受短暂不一致
    @Override
    public Response<Void> update(CommentUpdateReqVO req) {
        RichTextService.ProcessResult result = richTextService.process(req.getContent());
        String plainText=result.getPlainText();
        Preconditions.checkArgument(plainText.length() <= 500,"评论内容不能超过500字");
        String content = jsonUtils.toJsonString(result.getDoc());

        CommentEditDTO comment = commentMapper.selectEditInfoById(req.getCommentId());

        if (comment == null) {
            throw new BizException(BizResponseErrorCode.COMMENT_NOT_EXISTS);
        }

        Long currentUserId = getCurrentUserId();

        if (!Objects.equals(comment.getUserId(), currentUserId)) {
            throw new BizException(BizResponseErrorCode.COMMENT_NOT_OWNER);
        }

        if (comment.getCreateTime() != null) {

            LocalDateTime expireTime =
                    comment.getCreateTime().plusMinutes(editWindowMinutes);

            if (LocalDateTime.now().isAfter(expireTime)) {
                throw new BizException(BizResponseErrorCode.COMMENT_EDIT_EXPIRED);
            }
        }

        if (Objects.equals(comment.getContent(), content)) {
            return Response.ok();
        }

        int rows = commentMapper.updateContent(
                req.getCommentId(),
                content,
                plainText
        );

        if (rows != 1) {
            throw new BizException(BizResponseErrorCode.COMMENT_NOT_EXISTS);
        }

        return Response.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Void> delete(Long id) {

        CommentDeleteDTO comment =
                commentMapper.selectDeleteInfoById(id);

        if (comment == null) {
            throw new BizException(
                    BizResponseErrorCode.COMMENT_NOT_EXISTS
            );
        }

        Long currentUserId = getCurrentUserId();

        if (!Objects.equals(
                comment.getUserId(),
                currentUserId
        )) {
            throw new BizException(
                    BizResponseErrorCode.COMMENT_NOT_OWNER
            );
        }

        int rows =
                commentMapper.logicalDelete(id);

        if (rows != 1) {
            throw new BizException(
                    BizResponseErrorCode.COMMENT_NOT_EXISTS
            );
        }

        Long postId=comment.getPostId();

        rows =
                postMapper.updateCommentCountById(
                        postId,
                        -1L
                );

        if (rows != 1) {
            throw new BizException(
                    BizResponseErrorCode.POST_NOT_EXISTS_OR_ABNORMAL
            );
        }

        applicationEventPublisher.publishEvent(
                new CommentPageCacheEvictEvent(postId)
        );

        Long rootId = comment.getRootId();

        // 回复评论
        if (rootId != null) {

            commentMapper.updateReplyCountById(
                    rootId,
                    -1L
            );

            applicationEventPublisher.publishEvent(
                    new CommentHotScoreRebuildEvent(
                            rootId,
                            postId
                    )
            );
        }else {


            applicationEventPublisher.publishEvent(
                    new RootCommentDeletedEvent(
                            postId,
                            id
                    )
            );
        }

        Long authorId = getPostAuthorId(postId);
        if (authorId == null) {
            log.warn("帖子作者缺失,跳过创作者周榜计分, postId={}", postId);
        }else {
            asyncUpdateWeeklyRank(
                    authorId,
                    -WeeklyRankConstants.WEEKLY_RANK_COMMENT_SCORE
            );
        }
        return Response.ok();
    }

    @Override
    public Response<InteractRespVO> likeComment(Long commentId) {

        Long userId = LoginUserContextHolder.getUserId();

        if (Objects.isNull(userId)) {
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }

        CommentLikeTargetDTO target =
                commentMapper.selectLikeTarget(commentId);

        if (target == null) {

            throw new BizException(
                    BizResponseErrorCode.COMMENT_NOT_EXISTS
            );
        }

        String bitKey = CommentRedisKey.like(commentId);
        String countKey = CommentRedisKey.likeCount(commentId);

        int retryCount = 0;
        ToggleResultDTO result = null;

        do {
            try {
                result = redisService.toggleBitmapCounter(bitKey, countKey, userId);
            } catch (Exception e) {
                log.error("评论点赞失败", e);
            }

            if (Objects.isNull(result)) {
                throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
            }

            if (result.getState() == -2) {

                initInteractCacheWithLock(
                        bitKey,
                        countKey,
                        CommentRedisKey.likeLock(commentId),
                        Duration.ofDays(CommentRedisKey.COMMENT_LIKE_BIT_TTL_DAYS),
                        Duration.ofDays(CommentRedisKey.COMMENT_LIKE_COUNT_TTL_DAYS),
                        () -> commentLikeRecordMapper.selectUserIdsByCommentId(commentId)
                );

                if (retryCount < CacheConstants.MAX_RETRIES) {
                    sleepWithBackoff(retryCount);
                }
            } else {
                break;
            }

            retryCount++;

        } while (retryCount <= CacheConstants.MAX_RETRIES && result.getState() == -2);

        if (Objects.isNull(result) || result.getState() == -2) {
            throw new BizException(BizResponseErrorCode.COMMENT_LIKE_CACHE_EXPIRED);
        }

        Boolean interacted = result.getState() == 1L;

        sendInteractEvent(
                MQConstants.TOPIC_COMMENT_LIKE_EVENT,
                userId,
                commentId,
                interacted
        );

        if (target.getRootId() == null) {

            applicationEventPublisher.publishEvent(
                    new CommentHotScoreRebuildEvent(
                            commentId,
                            target.getPostId()
                    )
            );

            applicationEventPublisher.publishEvent(
                    new CommentPageCacheEvictEvent(
                            target.getPostId()
                    )
            );
        }

        InteractRespVO vo = new InteractRespVO();
        vo.setInteracted(interacted);
        vo.setCount(result.getCount());

        return Response.ok(vo);
    }


    private void sendInteractEvent(String destination,Long userId,Long commentId,Boolean interacted){
        log.info("准备发送评论点赞事件: userId={}, commentId={}, interacted={}",userId, commentId, interacted);
        try {
            CommentLikeEvent event=new CommentLikeEvent(UUID.randomUUID().toString(),userId,commentId,interacted);
            String hashKey=userId.toString();
            Message<CommentLikeEvent> message = MessageBuilder.withPayload(event).build();

            // 异步发送，并注册回调
            rocketMQTemplate.asyncSendOrderly(destination, message, hashKey, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("评论点赞事件发送成功: userId={}, commentId={}, msgId={}",
                            userId, commentId, sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {
                    log.error("评论点赞事件发送失败: userId={}, commentId={}, error: {}",
                            userId, commentId, e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            log.error("发送评论点赞事件异常",e);
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
                    redisService.initBitmapCounterPipeline(
                            bitKey,
                            countKey,
                            userLoader.get(),
                            bitTtl,
                            countTtl
                    );
                }
            }  catch (Exception e) {
                log.error("初始化互动缓存失败", e);
                try {
                    redisService.deleteStrict(bitKey);
                    redisService.deleteStrict(countKey);
                } catch (Exception ex) {
                    log.error("清理互动缓存失败，可能产生脏数据",ex);
                }
            }finally {
                boolean result= redisService.unLock(lockKey,value);
                if (!result) {
                    log.warn("释放锁失败,lockKey;{}", lockKey);
                }
            }
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
    public void incrementWeeklyScore(Long userId,double increment){
        if (userId==null||increment==0){
            return;
        }
        String key= UserRedisKey.weeklyRank();
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

    private void sendNotification(NotificationMessage message){
        rocketMQTemplate.asyncSend(
                MQConstants.TOPIC_NOTIFICATION,
                message,
                new SendCallback() {

                    @Override
                    public void onSuccess(SendResult sendResult) {

                        log.debug(
                                "通知发送成功, commentId={}, receiverId={}",
                                message.getCommentId(),
                                message.getReceiverId()
                        );
                    }

                    @Override
                    public void onException(Throwable e) {

                        log.error(
                                "通知发送失败, commentId={}, receiverId={}",
                                message.getCommentId(),
                                message.getReceiverId(),
                                e
                        );
                    }
                }
        );
    }
}
