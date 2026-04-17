package com.neu.youthpathtalk.user.biz.service.impl;

import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.neu.youthpathtalk.constant.redis.PostRedisKey;
import com.neu.youthpathtalk.constant.redis.UserRedisKey;
import com.neu.youthpathtalk.enums.CommonResponseErrorCode;
import com.neu.youthpathtalk.enums.UserType;
import com.neu.youthpathtalk.exception.BizException;
import com.neu.youthpathtalk.holder.LoginUserContextHolder;
import com.neu.youthpathtalk.post.api.vo.PostListVO;
import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.user.biz.cache.RedisService;
import com.neu.youthpathtalk.user.biz.constants.UserRoleConstants;
import com.neu.youthpathtalk.user.biz.dto.UserInfoDTO;
import com.neu.youthpathtalk.user.biz.dto.UserPostInfoDTO;
import com.neu.youthpathtalk.user.biz.dto.UserWeeklyRankInfoDTO;
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
import com.neu.youthpathtalk.user.biz.vo.rep.*;
import com.neu.youthpathtalk.user.biz.vo.req.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public Response<LoginRepVO> addUser(AddUserReqVO addUserReqVO) {
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
        List<String> paths=permissionMapper.selectPathsByRoleId(UserRoleConstants.REGULAR_USER_ROLE_ID);
        LoginRepVO loginRepVO = new LoginRepVO();
        loginRepVO.setUserId(userId);
        loginRepVO.setPaths(paths);
        return Response.ok(loginRepVO);
    }

    @Override
    public Response<Boolean> checkPhoneRegistered(CheckPhoneRegisteredReqVO checkPhoneRegisteredReqVO) {
        String phone= checkPhoneRegisteredReqVO.getPhone();
        return Response.ok(userMapper.existsByPhone(phone));
    }

    @Override
    public Response<LoginRepVO> getUserIdByPasswordLogin(GetUserIdByPwdLoginReqVO getUserIdByPwdLoginReqVO) {
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
        List<String> paths=permissionMapper.selectPathsByRoleId(roleId);
        LoginRepVO loginRepVO = new LoginRepVO();
        loginRepVO.setUserId(userId);
        loginRepVO.setPaths(paths);
        return Response.ok(loginRepVO);
    }

    @Override
    public Response<LoginRepVO> getUserIdByPhone(GetUserIdByPhoneReqVO getUserIdByPhoneReqVO) {
        Long userId=userMapper.selectUserIdByPhone(getUserIdByPhoneReqVO.getPhone());
        if (userId==null){
            throw new BizException(BizResponseErrorCode.USER_STATUS_ABNORMAL);
        }
        Long roleId=userRoleMapper.selectRoleIdByUserId(userId);
        List<String> paths=permissionMapper.selectPathsByRoleId(roleId);
        LoginRepVO loginRepVO = new LoginRepVO();
        loginRepVO.setUserId(userId);
        loginRepVO.setPaths(paths);
        return Response.ok(loginRepVO);
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
    public Response<List<BrowseHistoryVO>> getBrowseHistory() {
        Long userId= LoginUserContextHolder.getUserId();
        if (userId==null){
            throw new BizException(BizResponseErrorCode.USER_STATUS_ABNORMAL);
        }
        String viewHistoryKey= UserRedisKey.viewHistory(userId);
        Set<ZSetOperations.TypedTuple<String>> tuples;
        try {
            tuples=redisService.getRecentViewHistory(viewHistoryKey);
        } catch (Exception e) {
            log.error("获取最近浏览的帖子的ID列表失败");
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }
        if (tuples.isEmpty()){
            return Response.ok(Collections.emptyList());
        }
        List<Long> postIds=new ArrayList<>();
        Map<Long,Long> postIdToTimestamp=new HashMap<>();
        for (ZSetOperations.TypedTuple<String> tuple:tuples){
            Long postId=Long.parseLong(tuple.getValue());
            Long timestamp=tuple.getScore().longValue();
            postIds.add(postId);
            postIdToTimestamp.put(postId,timestamp);
        }
        List<PostListVO> postList=postRpcService.batchGetPostList(postIds);
        if (Objects.isNull(postList)){
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }
        Map<Long,PostListVO> postIdToPostListVO=postList.stream()
                .collect(Collectors.toMap(PostListVO::getId, Function.identity()));
        List<BrowseHistoryVO> result=new ArrayList<>();
        for (Long postId:postIds){
            PostListVO postListVO=postIdToPostListVO.get(postId);
            if (Objects.isNull(postListVO)){
                log.debug("浏览记录中帖子不存在或状态异常,postId={},userId={}",postId,userId);
                continue;
            }
            Long timestamp=postIdToTimestamp.get(postId);
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
                    .updateTime(postListVO.getUpdateTime())
                    .browseTime(browseTime)
                    .build();
            result.add(browseHistoryVO);
        }
        return Response.ok(result);
    }

    @Override
    public Response<PageRespVO<PostListVO>> getLikeHistory(PageReqVO pageReqVO) {
        Long userId=LoginUserContextHolder.getUserId();
        if (Objects.isNull(userId)){
            throw new BizException(BizResponseErrorCode.AUTH_LOGIN_FAILED);
        }
        String likeHistoryKey=UserRedisKey.likeHistory(userId);
        Long total;
        try {
            total = redisService.zCard(likeHistoryKey);
        } catch (Exception e) {
            log.error("获取用户维度点赞历史ZSet集合大小失败",e);
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }
        int pageNo=pageReqVO.getPageNo();
        int pageSize=pageReqVO.getPageSize();
        if (total==null||total==0){
            return Response.ok(new PageRespVO<>(0L,
                    pageNo, pageSize, Collections.emptyList()));
        }
        long start=(long) (pageNo-1)*pageSize;
        long end=start+pageSize-1;

        Set<ZSetOperations.TypedTuple<String>> tuples;
        try {
            tuples = redisService
                    .zRevRangeWithScores(likeHistoryKey, start, end);
        } catch (Exception e) {
            log.error("获取用户维度点赞历史分页失败",e);
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }
        if (tuples==null||tuples.isEmpty()){
            return Response.ok(new PageRespVO<>(total,
                    pageNo,pageSize,Collections.emptyList()));
        }
        List<Long> postIds=new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> tuple:tuples){
            Long postId=Long.parseLong(tuple.getValue());
            Long timestamp=tuple.getScore().longValue();
            postIds.add(postId);
        }
        List<PostListVO> postList=postRpcService.batchGetPostList(postIds);
        Map<Long,PostListVO> postMap=postList.stream()
                .collect(Collectors.toMap(PostListVO::getId,Function.identity()));
        List<PostListVO> sortedList=new ArrayList<>();
        for (Long postId:postIds){
            PostListVO vo=postMap.get(postId);
            if (Objects.isNull(vo)){
                log.warn("用户点赞记录中帖子不存在或状态异常, userId={}, postId={}",userId,postId);
            }else{
                sortedList.add(vo);
            }
        }
        return Response.ok(new PageRespVO<>(total,pageNo,pageSize,sortedList));
    }

    @Override
    public Response<List<CreatorWeeklyRankRespVO>> getWeeklyRank(int limit) {
        Preconditions.checkArgument(limit > 0 && limit <= 100, "limit必须在1~100之间");
        String weeklyRankKey=UserRedisKey.weeklyRank();

        Set<ZSetOperations.TypedTuple<String>> tuples;
        try {
            tuples = redisService
                    .zRevRangeWithScores(weeklyRankKey,0,limit-1);
        } catch (Exception e) {
            log.error("从ZSet获取创作者周榜失败",e);
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }

        if (Objects.isNull(tuples)||tuples.isEmpty()){
            return Response.ok(Collections.emptyList());
        }

        List<Long> userIds=new ArrayList<>();
        Map<Long,Double> scoreMap=new HashMap<>();
        for (ZSetOperations.TypedTuple<String> tuple:tuples){
            Long userId=Long.parseLong(tuple.getValue());
            userIds.add(userId);
            scoreMap.put(userId,tuple.getScore());
        }

        List<UserWeeklyRankInfoDTO> userInfos= userMapper.selectUserWeeklyRankInfoByIds(userIds);
        Map<Long,UserWeeklyRankInfoDTO> userMap=userInfos.stream()
                .collect(Collectors.toMap(UserWeeklyRankInfoDTO::getId,Function.identity()));

        List<CreatorWeeklyRankRespVO> result=new ArrayList<>();
        for (Long userId:userIds){
            UserWeeklyRankInfoDTO userInfo=userMap.get(userId);
            if (Objects.isNull(userInfo)){
                log.warn("用户已删除或状态异常");
                continue;
            }
            String universityName=universityMapper.selectUniversityNameById(userInfo.getUniversityId());
            CreatorWeeklyRankRespVO vo=CreatorWeeklyRankRespVO.builder()
                    .userId(userId)
                    .username(userInfo.getUsername())
                    .userAvatar(userInfo.getAvatar())
                    .universityName(universityName)
                    .type(UserType.getUserTypeName(userInfo.getType()))
                    .score(scoreMap.get(userId))
                    .build();
            result.add(vo);
        }
        return Response.ok(result);
    }
}
