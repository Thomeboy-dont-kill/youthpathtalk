package com.neu.youthpathtalk.user.biz.service.impl;

import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.neu.youthpathtalk.constant.redis.RedisConstants;
import com.neu.youthpathtalk.constant.redis.UserRedisKey;
import com.neu.youthpathtalk.enums.CommonResponseErrorCode;
import com.neu.youthpathtalk.enums.UserType;
import com.neu.youthpathtalk.exception.BizException;
import com.neu.youthpathtalk.holder.LoginUserContextHolder;
import com.neu.youthpathtalk.post.api.vo.PostListVO;
import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.user.biz.cache.RedisService;
import com.neu.youthpathtalk.user.biz.constants.UserRoleConstants;
import com.neu.youthpathtalk.user.biz.dto.*;
import com.neu.youthpathtalk.user.biz.entity.UserRoleDO;
import com.neu.youthpathtalk.user.biz.enums.UserStatus;
import com.neu.youthpathtalk.user.biz.mapper.PermissionMapper;
import com.neu.youthpathtalk.user.biz.mapper.UniversityMapper;
import com.neu.youthpathtalk.user.biz.mapper.UserRoleMapper;
import com.neu.youthpathtalk.user.biz.rpc.LeafIdGenService;
import com.neu.youthpathtalk.user.biz.entity.UserDO;
import com.neu.youthpathtalk.user.biz.enums.BizResponseErrorCode;
import com.neu.youthpathtalk.user.biz.mapper.UserMapper;
import com.neu.youthpathtalk.user.biz.rpc.PostRpcService;
import com.neu.youthpathtalk.user.biz.service.UserService;
import com.neu.youthpathtalk.user.biz.util.JsonUtils;
import com.neu.youthpathtalk.user.biz.vo.resp.*;
import com.neu.youthpathtalk.user.biz.vo.req.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Julien
 * @time 2026/03/05 19:30
 * @description UserService实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final JsonUtils jsonUtils;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LeafIdGenService leafIdGenService;
    private final UserRoleMapper userRoleMapper;
    private final PermissionMapper permissionMapper;
    private final UniversityMapper universityMapper;
    private final RedisService redisService;
    private final PostRpcService postRpcService;
    /**
     * 用户注册服务:添加用户并设置用户角色
     * @param addUserReqVO 添加用户请求VO
     * @return 响应数据(带userId)
     */
    @Override
    @Transactional
    public Response<LoginRespVO> addUser(AddUserReqVO addUserReqVO) {
        Long userId= leafIdGenService.generateUserId();
        UserDO userDO=new UserDO()
                .setId(userId)
                .setUsername(addUserReqVO.getUsername())
                .setPassword(passwordEncoder.encode(addUserReqVO.getPassword()))
                .setPhone(addUserReqVO.getPhone())
                .setType(addUserReqVO.getUserType().getType());
        try {
            userMapper.insertSelective(userDO);
        } catch (DuplicateKeyException e) {
            String message=e.getMessage();
            if (message.contains("uk_username")){
                throw new BizException(BizResponseErrorCode.USER_USERNAME_REGISTERED);
            }else if (message.contains("uk_phone")){
                throw new BizException(BizResponseErrorCode.USER_PHONE_REGISTERED);
            }else{
                throw e;
            }
        }
        UserRoleDO userRoleDO=new UserRoleDO()
                .setUserId(userId)
                .setRoleId(UserRoleConstants.REGULAR_USER_ROLE_ID);
        try {
            userRoleMapper.insertSelective(userRoleDO);
        } catch (DuplicateKeyException e) {
            throw new BizException(BizResponseErrorCode.USER_ROLE_INIT_ERROR);
        }
        List<String> paths=permissionMapper.selectPermissionsByRoleId(UserRoleConstants.REGULAR_USER_ROLE_ID);
        LoginRespVO loginRespVO = new LoginRespVO();
        loginRespVO.setUserId(userId);
        loginRespVO.setPaths(paths);
        return Response.ok(loginRespVO);
    }

    @Override
    public Response<Boolean> checkPhoneRegistered(CheckPhoneRegisteredReqVO checkPhoneRegisteredReqVO) {
        String phone= checkPhoneRegisteredReqVO.getPhone();
        return Response.ok(userMapper.existsByPhone(phone));
    }

    @Override
    public Response<LoginRespVO> getUserIdByPasswordLogin(GetUserIdByPwdLoginReqVO getUserIdByPwdLoginReqVO) {
        String username=getUserIdByPwdLoginReqVO.getUsername();
        UserInfoDTO userInfoDTO=userMapper.selectUserInfoByUsername(username);
        if (userInfoDTO==null){
            throw new BizException(BizResponseErrorCode.AUTH_LOGIN_FAILED);
        }
        if (userInfoDTO.getStatus()==UserStatus.ABNORMAL.getStatus()){
            throw new BizException(BizResponseErrorCode.USER_STATUS_ABNORMAL);
        }
        if (!passwordEncoder.matches(getUserIdByPwdLoginReqVO.getPassword(),userInfoDTO.getPassword())){
            throw new BizException(BizResponseErrorCode.AUTH_LOGIN_FAILED);
        }
        Long userId=userInfoDTO.getId();
        Long roleId=userRoleMapper.selectRoleIdByUserId(userId);
        List<String> paths=permissionMapper.selectPermissionsByRoleId(roleId);
        LoginRespVO loginRespVO = new LoginRespVO();
        loginRespVO.setUserId(userId);
        loginRespVO.setPaths(paths);
        return Response.ok(loginRespVO);
    }

    @Override
    public Response<LoginRespVO> getUserIdByPhone(GetUserIdByPhoneReqVO getUserIdByPhoneReqVO) {
        Long userId=userMapper.selectUserIdByPhone(getUserIdByPhoneReqVO.getPhone());
        if (userId==null){
            throw new BizException(BizResponseErrorCode.USER_STATUS_ABNORMAL);
        }
        Long roleId=userRoleMapper.selectRoleIdByUserId(userId);
        List<String> paths=permissionMapper.selectPermissionsByRoleId(roleId);
        LoginRespVO loginRespVO = new LoginRespVO();
        loginRespVO.setUserId(userId);
        loginRespVO.setPaths(paths);
        return Response.ok(loginRespVO);
    }

    @Override
    public Response<UserInfoRespVO> getUserInfo(Long userId) {
        Preconditions.checkArgument(Objects.nonNull(userId), "用户ID不能为空");
        UserPostInfoDTO userPostInfoDTO=userMapper.selectUserInfoById(userId);
        Long universityId=userPostInfoDTO.getUniversityId();
        String universityName=universityMapper.selectUniversityNameById(universityId);
        UserInfoRespVO userInfoRespVO= UserInfoRespVO.builder()
                .username(userPostInfoDTO.getUsername())
                .userAvatar(userPostInfoDTO.getAvatar())
                .universityId(universityId)
                .universityName(universityName)
                .build();
        return Response.ok(userInfoRespVO);
    }

    @Override
    public Response<Map<Long, String>> getMentionInfoBatch(
            Set<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Response.ok(Collections.emptyMap());
        }
        List<UserMentionDTO> users =
                userMapper.selectMentionInfoByIds(userIds);
        Map<Long, String> result = users.stream()
                .collect(Collectors.toMap(
                        UserMentionDTO::getId,
                        UserMentionDTO::getUsername
                ));
        return Response.ok(result);
    }
/*

    @Override
    public Response<List<Long>> getIdsByUsernames(List<String> usernames) {
        if (CollectionUtils.isEmpty(usernames)) {
            return Response.ok(Collections.emptyList());
        }
        List<Long> userIds=userMapper.selectIdsByUsernames(usernames);
        return Response.ok(userIds);
    }
*/

    @Override
    public Response<List<BrowseHistoryVO>> getBrowseHistory() {
        Long userId= LoginUserContextHolder.getUserId();
        if (userId==null){
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }
        String viewHistoryKey= UserRedisKey.viewHistory(userId);
        List<ZSetItemDTO> items =
                redisService.listZSet(viewHistoryKey, 0, RedisConstants.MAX_HISTORY_SIZE-1);
        if (CollectionUtils.isEmpty(items)){
            return Response.ok(Collections.emptyList());
        }
        List<Long> postIds =
                items.stream()
                        .map(ZSetItemDTO::getId)
                        .toList();
        List<PostListVO> postList=postRpcService.batchGetPostList(postIds);
        Map<Long,PostListVO> postIdToPostListVO=postList.stream()
                .collect(Collectors.toMap(PostListVO::getId, Function.identity()));
        List<BrowseHistoryVO> result=new ArrayList<>();
        for (ZSetItemDTO item:items){
            PostListVO postListVO=postIdToPostListVO.get(item.getId());
            if (Objects.isNull(postListVO)){
                log.debug("浏览记录中帖子不存在或状态异常,item.getId()={},userId={}",item.getId(),userId);
                redisService.zRem(viewHistoryKey,String.valueOf(item.getId()));
                continue;
            }
            Long timestamp=item.getScore().longValue();
            LocalDateTime browseTime=LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
            BrowseHistoryVO browseHistoryVO = BrowseHistoryVO.builder()
                    .id(postListVO.getId())
                    .userId(postListVO.getUserId())
                    .username(postListVO.getUsername())
                    .userAvatar(postListVO.getUserAvatar())
                    .universityName(postListVO.getUniversityName())
                    .boardType(postListVO.getBoardType())
                    .boardTypeName(postListVO.getBoardTypeName())
                    .title(postListVO.getTitle())
                    .preview(postListVO.getPreview())
                    .viewCount(postListVO.getViewCount())
                    .likeCount(postListVO.getLikeCount())
                    .commentCount(postListVO.getCommentCount())
                    .favoriteCount(postListVO.getFavoriteCount())
                    .isTop(postListVO.getIsTop())
                    .isEssence(postListVO.getIsEssence())
                    .updateTime(postListVO.getCreateTime())
                    .browseTime(browseTime)
                    .build();
            result.add(browseHistoryVO);
        }
        return Response.ok(result);
    }

    @Override
    public Response<PageRespVO<PostListVO>> getLikeHistory(int pageNo,int pageSize) {
        Long userId=LoginUserContextHolder.getUserId();
        if (Objects.isNull(userId)){
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }
        return getInteractHistory(
                userId,
                UserRedisKey.likeHistory(userId),
                pageNo,pageSize
        );
    }

    @Override
    public Response<PageRespVO<PostListVO>> getFavoriteHistory(int pageNo,int pageSize) {
        Long userId=LoginUserContextHolder.getUserId();
        if (Objects.isNull(userId)){
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }
        return getInteractHistory(
                userId,
                UserRedisKey.favoriteHistory(userId),
                pageNo,pageSize
        );
    }
    private Response<PageRespVO<PostListVO>> getInteractHistory(
            Long userId,
            String InteractHistoryKey,
            int pageNo,
            int pageSize
    ){
        Long total;
        try {
            total = redisService.zCard(InteractHistoryKey);
        } catch (Exception e) {
            log.error("获取用户维度互动历史ZSet集合大小失败",e);
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }
        if (total==null||total==0){
            return Response.ok(new PageRespVO<>(0L,
                    pageNo, pageSize, Collections.emptyList()));
        }
        long start=(long) (pageNo-1)*pageSize;
        long end=start+pageSize-1;

        List<ZSetItemDTO> items =
                redisService.listZSet(InteractHistoryKey, start, end);

        if (CollectionUtils.isEmpty(items)) {

            return Response.ok(
                    new PageRespVO<>(
                            total,
                            pageNo,
                            pageSize,
                            Collections.emptyList()
                    )
            );
        }

        List<Long> postIds =
                items.stream()
                        .map(ZSetItemDTO::getId)
                        .toList();

        List<PostListVO> postList=postRpcService.batchGetPostList(postIds);
        Map<Long,PostListVO> postMap=postList.stream()
                .collect(Collectors.toMap(PostListVO::getId,Function.identity()));
        List<PostListVO> sortedList=new ArrayList<>();
        for (Long postId:postIds){
            PostListVO vo=postMap.get(postId);
            if (Objects.isNull(vo)){
                log.warn("用户互动记录中帖子不存在或状态异常, userId={}, postId={}",userId,postId);
                redisService.zRem(
                        InteractHistoryKey,
                        String.valueOf(postId)
                );
                total--;
            }else{
                sortedList.add(vo);
            }
        }
        return Response.ok(new PageRespVO<>(total,pageNo,pageSize,sortedList));
    }

    @Override
    public Response<List<CreatorWeeklyRankRespVO>> getWeeklyRank(int limit) {
        Preconditions.checkArgument(limit > 0 && limit <= 20, "limit必须在1~20之间");
        String weeklyRankVOKey =
                UserRedisKey.weeklyRankVO(limit);

        List<CreatorWeeklyRankRespVO> cacheResult = getWeeklyRankFromCache(weeklyRankVOKey);
        if (cacheResult != null) {
            return Response.ok(cacheResult);
        }

        String lockKey=UserRedisKey.weeklyRankVOLock(limit);
        String lockValue = null;
        try {
            lockValue = redisService.tryLock(lockKey,
                    UserRedisKey.USER_WEEKLY_RANK_VO_LOCK_TTL,
                    UserRedisKey.USER_WEEKLY_RANK_VO_LOCK_TTL_UNIT
            );
            if (Objects.nonNull(lockValue)&& !redisService.exists(weeklyRankVOKey)){
                List<CreatorWeeklyRankRespVO> result =
                        buildWeeklyRank(limit);
                String weeklyRankVOJson=jsonUtils.toJsonString(result);
                redisService.set(weeklyRankVOKey,
                        weeklyRankVOJson,
                        UserRedisKey.USER_WEEKLY_RANK_VO_TTL,
                        UserRedisKey.USER_WEEKLY_RANK_VO_TTL_UNIT
                );
            }
        } catch (Exception e) {
            log.error("重建创作者周榜缓存失败,weeklyRankVOKey={}",weeklyRankVOKey,e);
        }finally {
            if (Objects.nonNull(lockValue)) {
                redisService.unLock(lockKey, lockValue);
            }else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.warn("Thread.sleep()被打断");
                }
                cacheResult = getWeeklyRankFromCache(weeklyRankVOKey);
                if (cacheResult != null) {
                    return Response.ok(cacheResult);
                }
            }
        }
        List<CreatorWeeklyRankRespVO> result =
                buildWeeklyRank(limit);
        return Response.ok(result);
    }
    private List<CreatorWeeklyRankRespVO> getWeeklyRankFromCache(
            String cacheKey
    ) {

        try {

            String json =
                    redisService.get(cacheKey);

            if (StringUtils.isBlank(json)) {
                return null;
            }

            return jsonUtils.parseList(
                    json,
                    CreatorWeeklyRankRespVO.class
            );

        } catch (JsonProcessingException e) {

            log.error(
                    "创作者周榜缓存反序列化失败, cacheKey={}",
                    cacheKey,
                    e
            );

            return null;

        } catch (Exception e) {

            log.error(
                    "获取创作者周榜缓存失败, cacheKey={}",
                    cacheKey,
                    e
            );

            return null;
        }
    }
    private List<CreatorWeeklyRankRespVO> buildWeeklyRank(int limit) {
        String weeklyRankKey=UserRedisKey.weeklyRank();

        List<ZSetItemDTO> items =
                redisService.listZSet(
                        weeklyRankKey,
                        0,
                        limit - 1
                );

        if (CollectionUtils.isEmpty(items)) {
            return Collections.emptyList();
        }

        List<Long> userIds =
                items.stream()
                        .map(ZSetItemDTO::getId)
                        .toList();

        List<UserWeeklyRankInfoDTO> userInfos= userMapper.selectUserWeeklyRankInfoByIds(userIds);
        Map<Long,UserWeeklyRankInfoDTO> userMap=userInfos.stream()
                .collect(Collectors.toMap(UserWeeklyRankInfoDTO::getId,Function.identity()));

        List<CreatorWeeklyRankRespVO> result=new ArrayList<>();
        for (ZSetItemDTO item : items){
            UserWeeklyRankInfoDTO userInfo=userMap.get(item.getId());
            if (Objects.isNull(userInfo)){
                log.warn("用户已删除或状态异常");
                continue;
            }
            //N+1次查询
            CreatorWeeklyRankRespVO vo=CreatorWeeklyRankRespVO.builder()
                    .userId(item.getId())
                    .username(userInfo.getUsername())
                    .userAvatar(userInfo.getAvatar())
                    .universityName(userInfo.getUniversityName())
                    .type(UserType.getUserTypeName(userInfo.getType()))
                    .score(item.getScore())
                    .build();
            result.add(vo);
        }
        return result;
    }
    @Override
    public Response<Void> deleteBrowseHistory(Long postId) {
        Long userId=LoginUserContextHolder.getUserId();
        if (Objects.isNull(userId)){
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }
        String viewHistoryKey=UserRedisKey.viewHistory(userId);
        try {
            redisService.zRem(viewHistoryKey,String.valueOf(postId));
        } catch (Exception e) {
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }
        return Response.ok();
    }

    @Override
    public Response<Void> clearBrowseHistory() {
        Long userId=LoginUserContextHolder.getUserId();
        if (Objects.isNull(userId)){
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }
        String viewHistoryKey=UserRedisKey.viewHistory(userId);
        try {
            redisService.delete(viewHistoryKey);
        } catch (Exception e) {
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }
        return null;
    }
}
