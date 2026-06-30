package com.neu.youthpathtalk.user.biz.service;

import com.neu.youthpathtalk.post.api.vo.PostListVO;
import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.user.biz.vo.resp.*;
import com.neu.youthpathtalk.user.biz.vo.req.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Julien
 * @time 2026/03/05 19:29
 * @description 用户service层
 */
public interface UserService {
    Response<LoginRespVO> addUser(AddUserReqVO addUserReqVO);
    Response<Boolean> checkPhoneRegistered(CheckPhoneRegisteredReqVO checkPhoneRegisteredReqVO);
    Response<LoginRespVO> getUserIdByPasswordLogin(GetUserIdByPwdLoginReqVO getUserIdByPwdLoginReqVO);
    Response<LoginRespVO> getUserIdByPhone(GetUserIdByPhoneReqVO getUserIdByPhoneReqVO);
    Response<UserInfoRespVO> getUserInfo(Long userId);
    Response<Map<Long, String>> getMentionInfoBatch(Set<Long> userIds);
//    Response<List<Long>> getIdsByUsernames(List<String> usernames);
    Response<List<BrowseHistoryVO>> getBrowseHistory();
    Response<Void> deleteBrowseHistory(Long postId);
    Response<Void> clearBrowseHistory();
    Response<PageRespVO<PostListVO>> getLikeHistory(int pageNo,int pageSize);
    Response<PageRespVO<PostListVO>> getFavoriteHistory(int pageNo,int pageSize);
    Response<List<CreatorWeeklyRankRespVO>> getWeeklyRank(int limit);
}
